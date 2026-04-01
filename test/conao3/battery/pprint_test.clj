;; Original: Lib/test/test_pprint.py

(ns conao3.battery.pprint-test
  (:require
   [clojure.test :as t]
   [conao3.battery.pprint :as pprint])
  (:import
   [clojure.lang ExceptionInfo]))

;; Excluded by checklist:
;; - test_lazy_import (support.import_helper)
;; - test_init (PrettyPrinter class instantiation, Python-specific)
;; - test_knotted (requires mutable circular references not available in Clojure)
;; - test_container_repr_override_called (Python subclass __repr__ override)
;; - test_basic_line_wrap (Python dict repr format {'key': val}, not Clojure format)
;; - test_nested_indentations (Python-specific indent param format)
;; - test_width (Python dict repr format with width)
;; - test_sorted_dict / test_sort_dict (sort_dicts option + Python dict repr format)
;; - test_ordered_dict (Python OrderedDict)
;; - test_mapping_proxy (Python MappingProxy type)
;; - test_dict_views / test_abc_views / test_nested_views / test_unorderable_items_views (Python dict views)
;; - test_mapping_subclass_repr (Python subclass repr)
;; - test_empty_simple_namespace through test_simple_namespace_subclass (Python SimpleNamespace)
;; - test_empty_dataclass through test_cyclic_dataclass (Python dataclass type)
;; - test_subclassing (Python subclassing)
;; - test_set_reprs / test_set_of_sets_reprs (non-deterministic set ordering)
;; - test_depth (requires :depth option implementation)
;; - test_sort_unorderable_values / test_sort_orderable_and_unorderable_values (Python-specific unorderable)
;; - test_compact / test_compact_width (Python compact format option)
;; - test_bytes_wrap / test_bytearray_wrap (Java byte array repr is JVM-specific)
;; - test_default_dict / test_counter / test_chainmap / test_deque (Python-specific collection types)
;; - test_user_dict / test_user_list / test_user_string (Python UserDict/UserList/UserString)

(t/deftest test-basic
  (t/is (false? (pprint/isrecursive 2)))
  (t/is (true? (pprint/isreadable 2)))
  (t/is (false? (pprint/isrecursive [1 2 3])))
  (t/is (true? (pprint/isreadable [1 2 3])))
  (t/is (false? (pprint/isrecursive nil)))
  (t/is (true? (pprint/isreadable nil))))

(t/deftest test-stdout-is-none
  (t/is (nil? (pprint/pprint "this should not fail"))))

(t/deftest test-unreadable
  (t/is (false? (pprint/isrecursive (Object.))))
  (t/is (false? (pprint/isreadable (Object.)))))

(t/deftest test-same-as-repr
  (t/is (= "42" (pprint/pformat 42)))
  (t/is (= "[]" (pprint/pformat [])))
  (t/is (= "[1 2 3]" (pprint/pformat [1 2 3])))
  (t/is (= "()" (pprint/pformat '())))
  (t/is (= "{}" (pprint/pformat {})))
  (t/is (= "true" (pprint/pformat true)))
  (t/is (= "false" (pprint/pformat false)))
  (t/is (= "nil" (pprint/pformat nil))))

(t/deftest test-integer
  (t/is (= "42" (pprint/pformat 42))))

(t/deftest test-mapping-view-subclass-no-mapping
  (t/is (thrown? ExceptionInfo (pprint/pformat (Object.)))))

(t/deftest test-str-wrap
  (t/is (= "\"a very long string\"" (pprint/pformat "a very long string"))))

(t/deftest test-pformat-string-with-quotes
  (t/is (= "\"say \\\"hello\\\"\"" (pprint/pformat "say \"hello\""))))

(t/deftest test-pformat-keyword
  (t/is (= ":foo" (pprint/pformat :foo)))
  (t/is (= ":bar/baz" (pprint/pformat :bar/baz))))

(t/deftest test-pformat-nested
  (t/is (= "[[1 2] [3 4]]" (pprint/pformat [[1 2] [3 4]])))
  (t/is (= "{:a [1 2]}" (pprint/pformat {:a [1 2]}))))

(t/deftest test-saferepr-no-throw
  (t/is (string? (pprint/saferepr (Object.))))
  (t/is (string? (pprint/saferepr 42)))
  (t/is (string? (pprint/saferepr nil))))

(t/deftest test-isreadable-collections
  (t/is (true? (pprint/isreadable {})))
  (t/is (true? (pprint/isreadable #{})))
  (t/is (true? (pprint/isreadable '(1 2))))
  (t/is (false? (pprint/isreadable {:key (Object.)}))))

(t/deftest test-pformat-numbers
  (t/is (= "0" (pprint/pformat 0)))
  (t/is (= "-1" (pprint/pformat -1)))
  (t/is (= "3.14" (pprint/pformat 3.14))))

(t/deftest test-pformat-empty-collections
  (t/is (= "{}" (pprint/pformat {})))
  (t/is (= "[]" (pprint/pformat [])))
  (t/is (= "()" (pprint/pformat '())))
  (t/is (= "#{}" (pprint/pformat #{}))))

(t/deftest test-saferepr-is-string
  ;; saferepr always returns a string even for unreadable objects
  (let [r (pprint/saferepr (Object.))]
    (t/is (string? r))
    (t/is (pos? (count r)))))

(t/deftest test-isrecursive-false-for-basic
  ;; Common collections are not recursive
  (t/is (false? (pprint/isrecursive {})))
  (t/is (false? (pprint/isrecursive [])))
  (t/is (false? (pprint/isrecursive '(1 2))))
  (t/is (false? (pprint/isrecursive #{1 2}))))

(t/deftest test-pformat-symbol
  (t/is (= "foo" (pprint/pformat 'foo)))
  (t/is (= "my-sym" (pprint/pformat 'my-sym)))
  (t/is (= "ns/fn" (pprint/pformat 'ns/fn))))

(t/deftest test-pformat-ratio
  (t/is (= "3/4" (pprint/pformat 3/4)))
  (t/is (= "1/3" (pprint/pformat 1/3))))

(t/deftest test-isreadable-symbol-consistent
  ;; isreadable and pformat should be consistent for symbols
  (t/is (true? (pprint/isreadable 'foo)))
  (t/is (string? (pprint/pformat 'foo))))

(t/deftest test-pformat-string-key-map
  (t/is (= "{\"a\" 1}" (pprint/pformat {"a" 1}))))
