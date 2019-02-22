(ns heimdall.core
  (:require 
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [heimdall.config :refer [load-config]]
    [heimdall.web :refer [start-server]]
    [heimdall.scheduler :refer [start-scheduler]])
  (:gen-class))

(def cli-options [["-c" "--config-file PATH" "Config file path" :default "cfg/heimdall.clj"]])

(defn -main [& args]
  (let [options (:options (parse-opts args cli-options))]
    (try 
      (let [config-file (:config-file options) config (load-config config-file)]
        (log/info (str "Loaded configuration from file " config-file))
        (start-server config)
;        (start-scheduler config)
        (log/info "Scheduler started"))
      (catch Exception e
        (log/error e)))))
