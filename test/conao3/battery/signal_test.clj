;; Original: Lib/test/test_signal.py

(ns conao3.battery.signal-test
  (:require
   [clojure.test :as t]
   [conao3.battery.signal :as signal-m]))

(t/deftest test-constants
  (t/is (= :SIG_DFL signal-m/SIG_DFL))
  (t/is (= :SIG_IGN signal-m/SIG_IGN))
  (t/is (= 15 signal-m/SIGTERM))
  (t/is (= 2 signal-m/SIGINT)))

(t/deftest test-getsignal-default
  (t/is (= :SIG_DFL (signal-m/getsignal signal-m/SIGTERM))))

(t/deftest test-signal-and-getsignal
  (let [handler (fn [_sig] nil)
        old (signal-m/signal signal-m/SIGTERM handler)]
    (t/is (= :SIG_DFL old))
    (t/is (= handler (signal-m/getsignal signal-m/SIGTERM)))
    (signal-m/signal signal-m/SIGTERM signal-m/SIG_DFL)))

(t/deftest test-raise-signal
  (let [received (atom nil)
        handler (fn [sig] (reset! received sig))]
    (signal-m/signal signal-m/SIGTERM handler)
    (signal-m/raise-signal signal-m/SIGTERM)
    (t/is (= signal-m/SIGTERM @received))
    (signal-m/signal signal-m/SIGTERM signal-m/SIG_DFL)))

(t/deftest test-raise-signal-ign
  (signal-m/signal signal-m/SIGTERM signal-m/SIG_IGN)
  (t/is (nil? (signal-m/raise-signal signal-m/SIGTERM)))
  (signal-m/signal signal-m/SIGTERM signal-m/SIG_DFL))

(t/deftest test-strsignal
  (t/is (= "Terminated" (signal-m/strsignal 15)))
  (t/is (= "Interrupt" (signal-m/strsignal 2))))

(t/deftest test-valid-signals
  (let [sigs (signal-m/valid-signals)]
    (t/is (set? sigs))
    (t/is (contains? sigs signal-m/SIGTERM))
    (t/is (contains? sigs signal-m/SIGINT))))

(t/deftest test-set-wakeup-fd
  (t/is (= -1 (signal-m/set-wakeup-fd -1))))
