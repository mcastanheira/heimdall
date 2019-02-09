(ns heimdall.config-test
  (:require [clojure.test :refer :all]
            [heimdall.config :refer :all]))

(deftest contains-required-keys?-with-valid-configuration-test
  (testing "contains-required-keys? should returns true if all the required keys are present"
    (let [valid-configuration {:port 3000 :check-interval 300 :database {} :services []}]
      (is (contains-required-keys? valid-configuration)))))

(deftest contains-required-keys?-with-invalid-configuration-test
  (testing "contains-required-keys? should returns false if any required key is absent"
    (let [invalid-configuration {:port 3000 :database {} :services []}]
      (is (false? (contains-required-keys? invalid-configuration))))))

(deftest load-config-with-valid-configuration-test
  (testing "load-config should load valid configurations"
    (let [valid-configuration {:port 3000 :check-interval 300 :database {} :services []}]
      (is (= valid-configuration (load-config "cfg/heimdall.clj"))))))

(deftest load-config-with-invalid-configuration-test
  (testing "load-config should refuse to load invalid configurations" 
    (is (thrown? Exception (load-config "cfg/invalid-config.clj")))))

(deftest load-config-with-innexistent-path-test
  (testing "load-config should thrown an exception if an innexistent path is provided"
    (is (thrown? java.io.FileNotFoundException (load-config "cfg/innexistent-config.clj")))))