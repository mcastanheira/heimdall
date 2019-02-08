(ns heimdall.config-test
  (:require [clojure.test :refer :all]
            [heimdall.config :refer :all]))

(deftest is-valid-with-valid-configuration-test
  (testing "is-valid should returns true if all the required keys are present"
    (let [valid-configuration {:port 3000 :check-interval 300 :services {}}]
      (is (is-valid? valid-configuration)))))

(deftest is-valid-with-invalid-configuration-test
  (testing "is-valid should returns false if any required key is absent"
    (let [invalid-configuration {:port 3000 :services {}}]
      (is (false? (is-valid? invalid-configuration))))))

(deftest load-config-with-valid-configuration-test
  (testing "load-config should load valid configurations"
    (reset! config {})
    (load-config "cfg/heimdall.clj")
    (is (= {:port 3000 :check-interval 300 :services {}} @config))))

(deftest load-config-with-invalid-configuration-test
  (testing "load-config should refuse to load invalid configurations" 
    (reset! config {})
    (is (thrown? Exception (load-config "cfg/invalid-config.clj")))
    (is (empty? @config))))

(deftest load-config-with-innexistent-path-test
  (testing "load-config should thrown an exception if an innexistent path is provided"
    (reset! config {})
    (is (thrown? java.io.FileNotFoundException (load-config "cfg/innexistent-config.clj")))))