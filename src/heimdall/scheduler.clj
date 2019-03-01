(ns heimdall.scheduler
  (:require 
    [heimdall.database :as database]
    [clojure.tools.logging :as log]
    [overtone.at-at :as at]
    [clj-http.client :as http])
  (:import (java.util Date)))

(defn- make-request [url]
  (log/info (str "Making request to " url))
  (:status (http/get url {:conn-timeout 3000})))

(defn- check-service [service]
    (let [{:keys [id host port heartbeat]} service url (str "http://" host ":" port heartbeat)]
      (try
        (let [response (make-request url)]
          (if (= response 200)
            {:status :ok :message "" :timestamp (Date.) :service_id id}
            {:status :error :message response :timestamp (Date.) :service_id id}))
        (catch Exception e
          {:status :error :message (.getMessage e) :timestamp (Date.) :service_id id}))))

(defn start-scheduler
  "Starts a scheduler for check each registered service"
  [config]
  (let [pool (at/mk-pool) check-interval (:check-interval config)]
    (at/every (* check-interval 1000) #(database/save-checks (flatten (map check-service (database/get-services)))) pool)))