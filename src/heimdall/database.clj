(ns heimdall.database
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as string]))

(def db {
  :classname   "org.sqlite.JDBC"
  :subprotocol "sqlite"
  :subname     "db/heimdall.db"})

(defn- create-db []
  (try 
    (jdbc/db-do-commands db
      (jdbc/create-table-ddl :checks [
        [:uuid :text] 
        [:name :text] 
        [:port :integer] 
        [:status :text] 
        [:message :text] 
        [:timestamp :datetime]]))
    (catch Exception e
      (println (.getMessage e)))))
  

(defn add-checks [checks]
  (try 
    (jdbc/insert-multi! db :checks checks) 
      (catch Exception e 
        (println (.getMessage e)))))

(defn get-checks-by-uuid-and-port 
  ([uuid port limit]
    (jdbc/query db ["select * from checks where uuid = ? and port = ? order by timestamp desc limit ?" uuid port limit]))
  ([uuid port]
    (get-checks-by-uuid-and-port uuid port 1)))

(defn- get-checks-by-service [service]
  (map #(get-checks-by-uuid-and-port (:uuid service) %) (:ports service)))

(defn get-checks [services]
  (flatten (map get-checks-by-service services)))

(create-db)