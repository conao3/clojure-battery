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

(t/deftest test-suppress-returns-nil-on-exception
  (let [ctx (contextlib/suppress RuntimeException)]
    (t/is (nil? (ctx (fn [] (throw (RuntimeException. "test"))))))
    (t/is (= "ok" (ctx (fn [] "ok"))))))

(t/deftest test-closing-calls-close-on-exception
  (let [closed (atom false)
        resource (reify java.io.Closeable
                   (close [_] (reset! closed true)))]
    (try
      (contextlib/closing resource (fn [_] (throw (RuntimeException. "error"))))
      (catch RuntimeException _))
    (t/is (true? @closed))))

(t/deftest test-exit-stack-close-clears-stack
  (let [stack (contextlib/exit-stack)
        count (atom 0)]
    ((:push stack) (fn [] (swap! count inc)))
    ((:push stack) (fn [] (swap! count inc)))
    ((:close stack))
    (t/is (= 2 @count))
    ((:close stack))
    (t/is (= 2 @count))))

(t/deftest test-exit-stack-enter
  (let [stack (contextlib/exit-stack)
        closed (atom false)
        resource (reify java.io.Closeable
                   (close [_] (reset! closed true)))
        entered ((:enter stack) resource)]
    (t/is (identical? resource entered))
    ((:close stack))
    (t/is (true? @closed))))

(t/deftest test-chdir-restores-on-exception
  (let [original (System/getProperty "user.dir")]
    (try
      (contextlib/chdir "/tmp" (fn [] (throw (RuntimeException. "error"))))
      (catch RuntimeException _))
    (t/is (= original (System/getProperty "user.dir")))))

(t/deftest test-contextmanager-passthrough
  (let [f (fn [thunk] (thunk))]
    (t/is (identical? f (contextlib/contextmanager f)))))

(t/deftest test-redirect-stream-calls-thunk
  (let [called (atom false)]
    (contextlib/redirect-stream nil nil (fn [] (reset! called true)))
    (t/is (true? @called))))

(t/deftest test-suppress-nested-exception
  (let [ctx (contextlib/suppress ArithmeticException)
        outer-called (atom false)]
    (ctx (fn []
           (ctx (fn [] (/ 1 0)))
           (reset! outer-called true)))
    (t/is (true? @outer-called))))

(t/deftest test-exit-stack-exception-in-close
  (let [stack (contextlib/exit-stack)
        order (atom [])]
    ((:push stack) (fn [] (swap! order conj :last)))
    ((:push stack) (fn [] (throw (RuntimeException. "ignored"))))
    ((:push stack) (fn [] (swap! order conj :first)))
    ((:close stack))
    (t/is (= [:first :last] @order))))
