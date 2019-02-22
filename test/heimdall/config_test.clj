(ns heimdall.config-test
  (:require [clojure.test :refer :all]
            [heimdall.config :refer :all]))

(deftest load-config-with-valid-configuration-test
  (testing "load-config should load valid configurations"
    (let [valid-configuration {:port 3000 :check-interval 300 :timestamp-mask "dd/MM/yyyy HH:mm:ss"}]
      (is (= valid-configuration (load-config "cfg/heimdall.clj"))))))

(deftest load-config-with-innexistent-path-test
  (testing "load-config should thrown an exception if an innexistent path is provided"
    (is (thrown? java.io.FileNotFoundException (load-config "cfg/innexistent-config.clj")))))