(ns conao3.battery.graphlib)

(defn- normalize-graph [graph]
  (reduce-kv
    (fn [acc node deps]
      (let [dep-set (set deps)]
        (reduce (fn [a d] (update a d #(or % #{})))
                (update acc node #(into (or % #{}) dep-set))
                dep-set)))
    {}
    graph))

(defn- in-degrees [graph]
  (reduce-kv
    (fn [acc node deps]
      (assoc acc node (count deps)))
    {}
    graph))

(defn- reverse-graph [graph]
  (reduce-kv
    (fn [rg node deps]
      (reduce (fn [rg2 dep] (update rg2 dep (fnil conj #{}) node))
              (update rg node (fnil identity #{}))
              deps))
    {}
    graph))

(defn- dfs-find-cycle [graph]
  (let [visiting (java.util.HashSet.)
        visited (java.util.HashSet.)
        result (volatile! nil)]
    (letfn [(dfs [node path]
              (when-not @result
                (cond
                  (.contains visiting node)
                  (let [path-vec (vec path)
                        idx (.indexOf path-vec node)]
                    (vreset! result (conj (subvec path-vec idx) node)))

                  (not (.contains visited node))
                  (do
                    (.add visiting node)
                    (doseq [dep (get graph node #{})]
                      (dfs dep (conj path node)))
                    (.remove visiting node)
                    (.add visited node)))))]
      (doseq [node (keys graph)]
        (when-not (.contains visited node)
          (dfs node [])))
      @result)))

(defn topological-sorter
  ([] (atom {:graph {} :state :initial}))
  ([graph]
   (atom {:graph (normalize-graph graph) :state :initial})))

(defn add!
  [sorter node & predecessors]
  (swap! sorter
         (fn [s]
           (let [dep-set (set predecessors)
                 g (reduce (fn [g d] (update g d #(or % #{})))
                           (update (:graph s) node #(into (or % #{}) dep-set))
                           dep-set)]
             (assoc s :graph g)))))

(defn prepare!
  [sorter]
  (let [{:keys [graph passed-out]} @sorter]
    (when (not-empty passed-out)
      (throw (ex-info "cannot prepare() after starting sort" {:type :value-error})))
    (when-let [cycle (dfs-find-cycle graph)]
      (throw (ex-info "Dependency cycle detected" {:type :cycle-error :cycle cycle})))
    (let [degrees (in-degrees graph)
          rg (reverse-graph graph)
          ready (into #{} (for [[node deg] degrees :when (zero? deg)] node))]
      (swap! sorter assoc
             :state :prepared
             :n-predecessors degrees
             :rg rg
             :ready ready
             :passed-out #{}))))

(defn active?
  [sorter]
  (let [{:keys [state ready passed-out]} @sorter]
    (when (not= state :prepared)
      (throw (ex-info "prepare() must be called first" {:type :value-error})))
    (boolean (or (not-empty ready) (not-empty passed-out)))))

(defn get-ready
  [sorter]
  (let [{:keys [state ready]} @sorter]
    (when (not= state :prepared)
      (throw (ex-info "prepare() must be called first" {:type :value-error})))
    (let [result (vec ready)]
      (swap! sorter #(-> % (update :passed-out into ready) (assoc :ready #{})))
      result)))

(defn done!
  [sorter & nodes]
  (let [{:keys [state passed-out graph]} @sorter]
    (when (not= state :prepared)
      (throw (ex-info "prepare() must be called first" {:type :value-error})))
    (doseq [node nodes]
      (when-not (contains? graph node)
        (throw (ex-info (str "node " node " was not added using add()") {:type :value-error})))
      (when-not (contains? passed-out node)
        (throw (ex-info (str "node " node " was not passed out") {:type :value-error})))))
  (swap! sorter
         (fn [s]
           (reduce
             (fn [{:keys [rg] :as s2} node]
               (let [s3 (update s2 :passed-out disj node)
                     dependents (get rg node #{})]
                 (reduce
                   (fn [s4 dep]
                     (let [new-count (dec (get-in s4 [:n-predecessors dep] 0))]
                       (-> s4
                           (assoc-in [:n-predecessors dep] new-count)
                           (cond-> (zero? new-count) (update :ready conj dep)))))
                   s3
                   dependents)))
             s
             nodes))))

(defn static-order
  [sorter]
  (prepare! sorter)
  (loop [result []]
    (if-not (active? sorter)
      result
      (let [ready (get-ready sorter)]
        (apply done! sorter ready)
        (recur (into result ready))))))
