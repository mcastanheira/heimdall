(ns heimdall.core
  (:require 
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [heimdall.config :refer [load-config config]]
    [heimdall.web :refer [start-server]])
  (:gen-class))

(def cli-options [["-c" "--config-file PATH" "Config file path" :default "cfg/heimdall.clj"]])

(defn -main [& args]
  (let [options (:options (parse-opts args cli-options))]
    (try 
      (load-config (:config-file options))
      (log/info (str "Loaded configuration from file " (:config-file options)))
      (start-server (:port @config))
      (catch Exception e
        (log/error e)))))
