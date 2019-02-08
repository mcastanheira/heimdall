(ns heimdall.core
  (:require 
    [clojure.tools.cli :refer [parse-opts]]
    [heimdall.config :refer [load-config config]])
  (:gen-class))

(def cli-options [["-c" "--config-file PATH" "Config file path" :default "cfg/heimdall.clj"]])

(defn -main [& args]
  (let [options (:options (parse-opts args cli-options))]
    (println options)
    (load-config (:config-file options)))
  (println @config))
