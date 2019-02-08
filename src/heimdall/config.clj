(ns heimdall.config)

(def config (atom {}))

(defn is-valid?
  "Checks if a configuration contains all the required keys"
  [config] 
  (let [required-keys #{:port :check-interval :services}]
    (every? #(% config) required-keys)))

(defn load-config 
  "Loads global configurations from path"
  [path]
  (let [loaded-config (read-string (slurp path))]
    (if (is-valid? loaded-config)
      (swap! config #(merge % loaded-config))
      (throw (Exception. "Attempted to load an invalid configuration!")))))