(ns heimdall.config-test
  (:require [clojure.test :refer :all]
            [heimdall.config :refer :all]))

(deftest load-config-with-valid-configuration-test
  (testing "load-config should load valid configurations"
    (let [valid-configuration {:port 3000 :check-interval 300 :services [{:uuid "uuid" :name "name" :ports [] :heart-beat-url "/"}]}]
      (is (= valid-configuration (load-config "test-files/valid-config.clj"))))))

(deftest load-config-with-innexistent-path-test
  (testing "load-config should thrown an exception if an innexistent path is provided"
    (is (thrown? java.io.FileNotFoundException (load-config "test-files/innexistent-config.clj")))))