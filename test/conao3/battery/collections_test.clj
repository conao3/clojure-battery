;; Original: Lib/test/test_collections.py

(ns conao3.battery.collections-test
  (:require
   [clojure.test :as t]
   [conao3.battery.collections :as collections]))

;; Excluded by checklist:
;; TestUserObjects:
;; - test_str_protocol, test_list_protocol, test_dict_protocol,
;;   test_list_copy, test_dict_copy, test_dict_missing
;;   (Python UserString/UserList/UserDict wrapper classes, no Clojure equivalent)
;; TestOneTrickPonyABCs / TestCollectionABCs:
;;   All tests (Python ABC protocol hierarchy, no Clojure equivalent)
;; TestNamedTuple:
;;   All tests (Python namedtuple construct; Clojure defrecord is per-type, not generic)
;; TestChainMap:
;; - test_missing (Python __missing__ subclass protocol)
;; - test_iter_not_calling_getitem_on_maps (Python-specific __getitem__ call tracking)
;; - test_new_child (Python-specific new_child() API)
;; - test_union_operators (Python |= operators)
;; TestCounter:
;; - test_update_reentrant_add_clears_counter (Python-specific reentrant behavior)
;; - test_init (Python **kwargs Counter(a=42) idiom)
;; - test_update (Python **kwargs)
;; - test_copying (pickle/deepcopy)
;; - test_copy_subclass (subclassing)
;; - test_repr_nonsortable (Python repr format)
;; - test_helper_function (Python internal _count_elements)
;; - test_multiset_operations (random 1000-iteration test)
;; - test_inplace_operations (mutation/inplace ops)
;; - test_symmetric_difference (complex invariant with nested loops)
;; - test_multiset_operations_equivalent_to_set_operations (complex combinatorial)
;; - test_eq, test_le, test_lt, test_ge, test_gt (Counter comparison operators, Python-specific)

(t/deftest test-test-chain-map-basics
  (t/is (= {"a" 1 "b" 20 "c" 30}
           (collections/chain-map {"b" 20 "c" 30} {"a" 1 "b" 2}))))

(t/deftest test-ordering
  (let [baseline {"music" "bach" "art" "rembrandt"}
        adjustments {"art" "van gogh" "opera" "carmen"}]
    (t/is (= (merge baseline adjustments)
             (collections/chain-map adjustments baseline)))))

(t/deftest test-constructor
  (t/is (= {} (collections/chain-map)))
  (t/is (= {1 2} (collections/chain-map {1 2}))))

(t/deftest test-bool
  (t/is (empty? (collections/chain-map)))
  (t/is (empty? (collections/chain-map {} {})))
  (t/is (not (empty? (collections/chain-map {1 2} {}))))
  (t/is (not (empty? (collections/chain-map {} {1 2})))))

(t/deftest test-test-chain-map-order-preservation
  (let [c (collections/chain-map
            {"j" 0 "h" 88888}
            {}
            {"i" 9999 "d" 4444 "c" 3333}
            {"f" 666 "b" 222 "g" 777 "c" 333 "h" 888}
            {}
            {"e" 55 "b" 22}
            {"a" 1 "b" 2 "c" 3 "d" 4 "e" 5}
            {})]
    (t/is (= 1 (get c "a")))
    (t/is (= 222 (get c "b")))
    (t/is (= 3333 (get c "c")))
    (t/is (= 4444 (get c "d")))
    (t/is (= 55 (get c "e")))
    (t/is (= 666 (get c "f")))
    (t/is (= 777 (get c "g")))
    (t/is (= 88888 (get c "h")))
    (t/is (= 9999 (get c "i")))
    (t/is (= 0 (get c "j")))))

(t/deftest test-dict-coercion
  (t/is (= {"a" 1 "b" 2 "c" 30}
           (collections/chain-map {"a" 1 "b" 2} {"b" 20 "c" 30}))))

(t/deftest test-test-counter-basics
  (t/is (= {\a 3 \b 2 \c 1} (collections/counter "abcaba")))
  (t/is (= 3 (count (collections/counter "abcaba"))))
  (t/is (= 6 (reduce + (vals (collections/counter "abcaba")))))
  (t/is (= 2 (get (collections/counter "abcaba") \b 0)))
  (t/is (= 0 (get (collections/counter "abcaba") \z 0))))

(t/deftest test-total
  (t/is (= 15 (collections/counter-total {\a 10 \b 5 \c 0}))))

(t/deftest test-test-counter-order-preservation
  (let [c (collections/counter "abracadabra")]
    (t/is (= 5 (get c \a)))
    (t/is (= 2 (get c \b)))
    (t/is (= 2 (get c \r)))
    (t/is (= 1 (get c \c)))
    (t/is (= 1 (get c \d)))))

(t/deftest test-conversions
  (let [s "she sells sea shells by the sea shore"]
    (t/is (= (sort (seq s))
             (sort (collections/counter-elements (collections/counter s)))))
    (t/is (= (set (seq s))
             (set (keys (collections/counter s)))))))

(t/deftest test-invariant-for-the-in-operator
  (let [c {\a 10 \b -2 \c 0}]
    (doseq [elem (keys c)]
      (t/is (contains? c elem)))))

(t/deftest test-subtract
  (let [c {\a -5 \b 0 \c 5 \d 10 \e 15 \g 40}
        other {\a 1 \b 2 \c -3 \d 10 \e 20 \f 30 \h -50}]
    (t/is (= {\a -6 \b -2 \c 8 \d 0 \e -5 \f -30 \g 40 \h 50}
             (collections/counter-subtract c other)))))

(t/deftest test-unary
  (let [c {\a -5 \b 0 \c 5 \d 10 \e 15 \g 40}]
    (t/is (= {\c 5 \d 10 \e 15 \g 40} (collections/counter-pos c)))
    (t/is (= {\a 5} (collections/counter-neg c)))))
