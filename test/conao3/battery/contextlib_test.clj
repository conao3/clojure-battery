;; Original: Lib/test/test_contextlib.py

(ns conao3.battery.contextlib-test
  (:require
   [clojure.test :as t]
   [conao3.battery.contextlib :as contextlib])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-suppress-basic
  (let [ctx (contextlib/suppress ArithmeticException)]
    (t/is (nil? (ctx (fn [] (/ 1 0)))))
    (t/is (= 42 (ctx (fn [] 42))))))

(t/deftest test-suppress-multiple
  (let [ctx (contextlib/suppress ArithmeticException IllegalArgumentException)]
    (t/is (nil? (ctx (fn [] (/ 1 0)))))
    (t/is (nil? (ctx (fn [] (throw (IllegalArgumentException. "test"))))))
    (t/is (thrown? ExceptionInfo (ctx (fn [] (throw (ex-info "not suppressed" {}))))))))

(t/deftest test-with-suppress
  (t/is (nil? (contextlib/with-suppress [ArithmeticException] (fn [] (/ 1 0)))))
  (t/is (= 42 (contextlib/with-suppress [ArithmeticException] (fn [] 42))))
  (t/is (thrown? ExceptionInfo
                 (contextlib/with-suppress [ArithmeticException]
                                           (fn [] (throw (ex-info "not suppressed" {})))))))

(t/deftest test-closing
  (let [closed (atom false)
        resource (reify java.io.Closeable
                   (close [_] (reset! closed true)))
        result (contextlib/closing resource (fn [r] (t/is (identical? resource r)) :done))]
    (t/is (= :done result))
    (t/is (true? @closed))))

(t/deftest test-nullcontext
  (t/is (nil? (contextlib/nullcontext)))
  (t/is (= 42 (contextlib/nullcontext 42)))
  (t/is (= "hello" (contextlib/nullcontext "hello"))))

(t/deftest test-exit-stack
  (let [stack (contextlib/exit-stack)
        order (atom [])]
    ((:push stack) (fn [] (swap! order conj 1)))
    ((:push stack) (fn [] (swap! order conj 2)))
    ((:close stack))
    (t/is (= [2 1] @order))))

(t/deftest test-chdir
  (let [original (System/getProperty "user.dir")
        result (contextlib/chdir "/tmp" (fn [] (System/getProperty "user.dir")))]
    (t/is (= "/tmp" result))
    (t/is (= original (System/getProperty "user.dir")))))
