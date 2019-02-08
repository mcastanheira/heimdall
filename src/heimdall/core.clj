(ns heimdall.core
  (:require 
    [clojure.tools.cli :refer [parse-opts]]
    [heimdall.config :refer [load-config config]])
  (:gen-class))

(def cli-options [
  ["-p" "--port PORT" "Port number" :default 3000 :parse-fn #(Integer/parseInt %)]
  ["-c" "--config-file PATH" "Config file path" :default "cfg/heimdall.clj"]])

(defn -main [& args]
  (let [options (:options (parse-opts args cli-options))]
    (println options)
    (load-config (:config-file options)))
  (println @config))
