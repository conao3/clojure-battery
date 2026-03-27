;; Original: Lib/test/test_graphlib.py

(ns conao3.battery.graphlib-test
  (:require
   [clojure.test :as t]
   [conao3.battery.graphlib :as graphlib])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- static-order-groups
  "Returns groups of nodes that become ready at the same time."
  [sorter]
  (graphlib/prepare! sorter)
  (loop [groups []]
    (if-not (graphlib/active? sorter)
      groups
      (let [ready (graphlib/get-ready sorter)]
        (apply graphlib/done! sorter ready)
        (recur (conj groups (set ready)))))))

(defn- test-graph
  "Returns true if static-order groups match expected (as sets)."
  [graph expected]
  (let [sorter (graphlib/topological-sorter graph)
        groups (static-order-groups sorter)
        expected-sets (mapv set expected)]
    (= groups expected-sets)))

(t/deftest test-simple-cases
  (t/is (test-graph
          {2 #{11}, 9 #{11 8}, 10 #{11 3}, 11 #{7 5}, 8 #{7 3}}
          [#{3 5 7} #{8 11} #{2 9 10}]))
  (t/is (test-graph {1 #{}} [#{1}]))
  (t/is (test-graph
          (into {} (for [x (range 10)] [x #{(inc x)}]))
          (mapv hash-set (range 10 -1 -1))))
  (t/is (test-graph
          {2 #{3}, 3 #{4}, 4 #{5}, 5 #{1}, 11 #{12}, 12 #{13}, 13 #{14}, 14 #{15}}
          [#{1 15} #{5 14} #{4 13} #{3 12} #{2 11}]))
  (t/is (test-graph
          {0 [1 2], 1 [3], 2 [5 6], 3 [4], 4 [9], 5 [3], 6 [7], 7 [8], 8 [4], 9 []}
          [#{9} #{4} #{3 8} #{1 5 7} #{6} #{2} #{0}]))
  (t/is (test-graph
          {0 [1 2], 1 [], 2 [3], 3 []}
          [#{1 3} #{2} #{0}]))
  (t/is (test-graph
          {0 [1 2], 1 [], 2 [3], 3 [], 4 [5], 5 [6], 6 []}
          [#{1 3 6} #{2 5} #{0 4}])))

(t/deftest test-no-dependencies
  (t/is (test-graph {1 #{2}, 3 #{4}, 5 #{6}} [#{2 4 6} #{1 3 5}]))
  (t/is (test-graph {1 #{}, 3 #{}, 5 #{}} [#{1 3 5}])))

(t/deftest test-the-node-multiple-times
  (t/is (test-graph {1 #{2}, 3 #{4}, 0 [2 4 4 4 4 4]} [#{2 4} #{0 1 3}]))
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 1 2)
    (graphlib/add! sorter 1 2)
    (graphlib/add! sorter 1 2)
    (t/is (= (set (graphlib/static-order sorter)) #{1 2}))
    (t/is (= (last (graphlib/static-order (graphlib/topological-sorter {1 #{2}}))) 1))))

(t/deftest test-graph-with-iterables
  (let [dependson (map #(* 2 %) (range 1 6))
        sorter (graphlib/topological-sorter {0 dependson})
        order (graphlib/static-order sorter)]
    (t/is (= (last order) 0))
    (t/is (= (set order) #{0 2 4 6 8 10}))))

(t/deftest test-add-dependencies-for-same-node-incrementally
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 1 2)
    (graphlib/add! sorter 1 3)
    (graphlib/add! sorter 1 4)
    (graphlib/add! sorter 1 5)
    (let [sorter2 (graphlib/topological-sorter {1 #{2 3 4 5}})
          order1 (graphlib/static-order sorter)
          order2 (graphlib/static-order sorter2)]
      (t/is (= (set order1) (set order2)))
      (t/is (= (last order1) 1))
      (t/is (= (last order2) 1)))))

(t/deftest test-empty
  (t/is (test-graph {} [])))

(t/deftest test-cycle
  (t/is (thrown? ExceptionInfo
                 (graphlib/prepare! (doto (graphlib/topological-sorter)
                                      (graphlib/add! 1 1)))))
  (t/is (thrown? ExceptionInfo
                 (graphlib/prepare! (doto (graphlib/topological-sorter)
                                      (graphlib/add! 1 2)
                                      (graphlib/add! 2 1)))))
  (t/is (thrown? ExceptionInfo
                 (graphlib/prepare! (doto (graphlib/topological-sorter)
                                      (graphlib/add! 1 2)
                                      (graphlib/add! 2 3)
                                      (graphlib/add! 3 1)))))
  (t/is (thrown? ExceptionInfo
                 (graphlib/prepare! (doto (graphlib/topological-sorter)
                                      (graphlib/add! 1 2)
                                      (graphlib/add! 2 3)
                                      (graphlib/add! 3 1)
                                      (graphlib/add! 5 4)
                                      (graphlib/add! 4 6))))))

(t/deftest test-calls-before-prepare
  (let [sorter (graphlib/topological-sorter)]
    (t/is (thrown? ExceptionInfo (graphlib/get-ready sorter)))
    (t/is (thrown? ExceptionInfo (graphlib/done! sorter 3)))
    (t/is (thrown? ExceptionInfo (graphlib/active? sorter)))))

(t/deftest test-prepare-multiple-times
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/prepare! sorter)
    (graphlib/prepare! sorter)
    (t/is (= :prepared (:state @sorter)))))

(t/deftest test-prepare-after-pass-out
  (let [sorter (graphlib/topological-sorter {"a" ["b" "c"]})]
    (graphlib/prepare! sorter)
    (t/is (= #{"b" "c"} (set (graphlib/get-ready sorter))))
    (t/is (thrown? ExceptionInfo (graphlib/prepare! sorter)))))

(t/deftest test-prepare-cycleerror-each-time
  (let [sorter (graphlib/topological-sorter {"a" ["b"] "b" ["a"]})]
    (dotimes [_ 3]
      (t/is (thrown? ExceptionInfo (graphlib/prepare! sorter))))))

(t/deftest test-invalid-nodes-in-done
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 1 2 3 4)
    (graphlib/add! sorter 2 3 4)
    (graphlib/prepare! sorter)
    (graphlib/get-ready sorter)
    (t/is (thrown? ExceptionInfo (graphlib/done! sorter 2)))
    (t/is (thrown? ExceptionInfo (graphlib/done! sorter 24)))))

(t/deftest test-done
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 1 2 3 4)
    (graphlib/add! sorter 2 3)
    (graphlib/prepare! sorter)
    (t/is (= #{3 4} (set (graphlib/get-ready sorter))))
    (t/is (= [] (graphlib/get-ready sorter)))
    (graphlib/done! sorter 3)
    (t/is (= #{2} (set (graphlib/get-ready sorter))))
    (t/is (= [] (graphlib/get-ready sorter)))
    (graphlib/done! sorter 4)
    (graphlib/done! sorter 2)
    (t/is (= #{1} (set (graphlib/get-ready sorter))))
    (t/is (= [] (graphlib/get-ready sorter)))
    (graphlib/done! sorter 1)
    (t/is (= [] (graphlib/get-ready sorter)))
    (t/is (not (graphlib/active? sorter)))))

(t/deftest test-is-active
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 1 2)
    (graphlib/prepare! sorter)
    (t/is (graphlib/active? sorter))
    (t/is (= #{2} (set (graphlib/get-ready sorter))))
    (t/is (graphlib/active? sorter))
    (graphlib/done! sorter 2)
    (t/is (graphlib/active? sorter))
    (t/is (= #{1} (set (graphlib/get-ready sorter))))
    (t/is (graphlib/active? sorter))
    (graphlib/done! sorter 1)
    (t/is (not (graphlib/active? sorter)))))

(t/deftest test-order-of-insertion-does-not-matter-between-groups
  (let [sorter (graphlib/topological-sorter)]
    (graphlib/add! sorter 3 2 1)
    (graphlib/add! sorter 1 0)
    (graphlib/add! sorter 4 5)
    (graphlib/add! sorter 6 7)
    (graphlib/add! sorter 4 7)
    (let [sorter2 (graphlib/topological-sorter)]
      (graphlib/add! sorter2 1 0)
      (graphlib/add! sorter2 3 2 1)
      (graphlib/add! sorter2 4 7)
      (graphlib/add! sorter2 6 7)
      (graphlib/add! sorter2 4 5)
      (t/is (= (static-order-groups sorter)
               (static-order-groups sorter2))))))
