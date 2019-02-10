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

(deftest is-database-struct-correct?-with-valid-database-test
  (testing "is-database-struct-correct? should returns true if all the required keys are present"
    (let [valid-database {:class-name "class-name" :url "url" :username "username" :password "password"}]
      (is (is-database-struct-correct? valid-database)))))
      
(deftest is-database-struct-correct?-with-invalid-database-test
  (testing "is-database-struct-correct? should returns false if any required key is absent"
    (let [invalid-database {:class-name "class-name" :url "url" :username "username"}]
      (is (false? (is-database-struct-correct? invalid-database))))))

(deftest is-service-struct-correct?-with-valid-service-test
  (testing "is-service-struct-correct? should returns true if all the required keys are present"
    (let [valid-service {:uuid "uuid" :name "name" :ports [] :heart-beat-url "/"}]
      (is (is-service-struct-correct? valid-service)))))
            
(deftest is-service-struct-correct?-with-invalid-service-test
  (testing "is-service-struct-correct? should returns false if any required key is absent"
    (let [invalid-service {:name "name" :ports [] :heart-beat-url "/"}]
      (is (false? (is-service-struct-correct? invalid-service))))))

(deftest load-config-with-valid-configuration-test
  (testing "load-config should load valid configurations"
    (let [
      valid-configuration 
      {
        :port 3000 :check-interval 300 :database {
          :class-name "class-name" :url "url" :username "username" :password "password"} 
        :services [{:uuid "uuid" :name "name" :ports [] :heart-beat-url "/"}]}]
      (is (= valid-configuration (load-config "test-files/valid-config.clj"))))))

(deftest load-config-with-invalid-configuration-test
  (testing "load-config should refuse to load invalid configurations" 
    (is (thrown? Exception (load-config "test-files/invalid-config.clj")))))

(deftest load-config-with-innexistent-path-test
  (testing "load-config should thrown an exception if an innexistent path is provided"
    (is (thrown? java.io.FileNotFoundException (load-config "test-files/innexistent-config.clj")))))