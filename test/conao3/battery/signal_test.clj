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

(t/deftest test-signal-override-multiple
  (let [calls (atom [])
        h1 (fn [sig] (swap! calls conj [:h1 sig]))
        h2 (fn [sig] (swap! calls conj [:h2 sig]))]
    (signal-m/signal signal-m/SIGTERM h1)
    (signal-m/signal signal-m/SIGTERM h2)
    (signal-m/raise-signal signal-m/SIGTERM)
    (t/is (= [[:h2 signal-m/SIGTERM]] @calls))
    (signal-m/signal signal-m/SIGTERM signal-m/SIG_DFL)))

(t/deftest test-getsignal-after-ign
  (signal-m/signal signal-m/SIGTERM signal-m/SIG_IGN)
  (t/is (= signal-m/SIG_IGN (signal-m/getsignal signal-m/SIGTERM)))
  (signal-m/signal signal-m/SIGTERM signal-m/SIG_DFL))

(t/deftest test-valid-signals-contains-standard
  (let [sigs (signal-m/valid-signals)]
    (t/is (contains? sigs signal-m/SIGABRT))
    (t/is (contains? sigs signal-m/SIGFPE))
    (t/is (contains? sigs signal-m/SIGILL))
    (t/is (contains? sigs signal-m/SIGSEGV))))

(t/deftest test-strsignal-unknown
  (t/is (string? (signal-m/strsignal 99))))

(t/deftest test-strsignal-standard-signals
  (doseq [sig [signal-m/SIGTERM signal-m/SIGINT signal-m/SIGABRT signal-m/SIGSEGV]]
    (let [desc (signal-m/strsignal sig)]
      (t/is (string? desc))
      (t/is (pos? (count desc))))))

(t/deftest test-valid-signals-minimum-count
  (t/is (>= (count (signal-m/valid-signals)) 4)))

(t/deftest test-valid-signals-all-positive
  (t/is (every? pos? (signal-m/valid-signals))))
