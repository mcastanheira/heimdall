(ns heimdall.scheduler
  (:require 
    [clojure.tools.logging :as log]
    [overtone.at-at :as at]))

(defn- check-service [service]
  (log/info service))

(defn start-scheduler
  "Starts a scheduler for check each registered service"
  [services check-interval]
  (let [pool (at/mk-pool)]
    (at/every 
      (* check-interval 1000) 
      #(doseq [service services] (check-service service)) 
      pool)))