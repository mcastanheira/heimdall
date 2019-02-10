(ns heimdall.scheduler
  (:require 
    [clojure.tools.logging :as log]
    [overtone.at-at :as at]
    [clj-http.client :as http]))

(defn- check-service [service]
  (log/info "sfsffsdfsdffdsfdsfsdf")
  (check-service service (:ports service)))

(defn- check-service [service ports]
  (loop [port ports]
    (log/info (str "Checking service " (:name service) " at port " port))
    ))

(defn start-scheduler
  "Starts a scheduler for check each registered service"
  [services check-interval]
  (let [pool (at/mk-pool)]
    (log/info (* check-interval 1000))
    (at/every 
      (* check-interval 1) 
      #(doseq [service services] (check-service service)) 
      pool)))