(ns heimdall.scheduler
  (:require 
    [heimdall.database :as database]
    [clojure.tools.logging :as log]
    [overtone.at-at :as at]
    [clj-http.client :as http])
  (:import (java.util Date)))

(defn- make-request [url]
  (:status (http/get url {:conn-timeout 3000})))

(defn- check-service 
  ([service port]
    (let [url (str "http://localhost:" port (:heart-beat-url service))]
      (try
        (let [response (make-request url)]
          (if (= response 200)
            {:uuid (:uuid service) :name (:name service) :port port :status :ok :message "" :timestamp (Date.)}
            {:uuid (:uuid service) :name (:name service) :port port :status :error :message response :timestamp (Date.)}))
        (catch Exception e
          {:uuid (:uuid service) :name (:name service) :port port :status :error :message (.getMessage e) :timestamp (Date.)}))))
  ([service]
    (map #(check-service service %) (:ports service))))

(defn start-scheduler
  "Starts a scheduler for check each registered service"
  [config]
  (let [pool (at/mk-pool) check-interval (:check-interval config) services (:services config)]
    (at/every (* check-interval 1000) #(database/add-checks (flatten (map check-service services))) pool)))