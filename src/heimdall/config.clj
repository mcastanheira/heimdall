(ns heimdall.config)

(defn contains-required-keys?
  "Checks if a configuration contains all the required keys"
  [config] 
  (let [required-keys #{:port :check-interval :database :services}]
    (every? #(% config) required-keys)))

(defn is-database-struct-correct?
  "Checks if the database section is correct"
  [database] 
    (let [required-keys #{:class-name :url :username :password}]
      (every? #(% database) required-keys)))

(defn is-service-struct-correct?
  "Checks if the service struct is correct"
  [service] 
    (let [required-keys #{:uuid :name :ports :heart-beat-url}]
      (every? #(% service) required-keys)))

(defn load-config 
  "Loads global configurations from path"
  [path]
  (let [loaded-config (read-string (slurp path))]
    (if 
      (and 
        (contains-required-keys? loaded-config) 
        (is-database-struct-correct? (:database loaded-config))
        (every? is-service-struct-correct? (:services loaded-config)))
      loaded-config
      (throw (Exception. "Attempted to load an invalid configuration!")))))