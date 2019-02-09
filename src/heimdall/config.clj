(ns heimdall.config)

(defn contains-required-keys?
  "Checks if a configuration contains all the required keys"
  [config] 
  (let [required-keys #{:port :check-interval :database :services}]
    (every? #(% config) required-keys)))

(defn load-config 
  "Loads global configurations from path"
  [path]
  (let [loaded-config (read-string (slurp path))]
    (if (contains-required-keys? loaded-config)
      loaded-config
      (throw (Exception. "Attempted to load an invalid configuration!")))))