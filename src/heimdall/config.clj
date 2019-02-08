(ns heimdall.config)

(def config (atom []))

(defn load-config 
  "Loads global configurations from path"
  [path]
  (reset! config (read-string (slurp path))))