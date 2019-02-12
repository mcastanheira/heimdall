(ns heimdall.config)

(defn load-config 
  "Loads global configurations from path"
  [path]
  (read-string (slurp path)))