(ns heimdall.database
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as string]))

(def db {
  :classname   "org.sqlite.JDBC"
  :subprotocol "sqlite"
  :subname     "db/heimdall.db"})

(defn- create-db []
  (let [db-dir (java.io.File. "db")] 
    (if (not (.exists db-dir)) (.mkdir db-dir)))
  (try 
    (jdbc/db-do-commands db
      [(jdbc/create-table-ddl :services [
        [:id :integer :primary :key :autoincrement] 
        [:name :text] 
        [:host :text]
        [:port :integer]
        [:heartbeat :text]])
      (jdbc/create-table-ddl :checks [
        [:id :integer :primary :key :autoincrement] 
        [:status :text] 
        [:message :text]
        [:timestamp :timestamp]
        [:service_id :integer]
        ["foreign key(service_id) references services(id)"]])])
    (catch Exception e
      (println (.getMessage e)))))

(defn get-services []
  (jdbc/query db ["select * from services order by name"]))

(defn get-service [id]
  (first (jdbc/query db ["select * from services where id = ?" id])))

(defn save-service [service]
  (if (= (:id service) 0)
    (jdbc/insert! db :services (dissoc service :id))
    (jdbc/update! db :services service ["id = ?" (:id service)])))

(defn delete-service [id]
  (jdbc/delete! db :services ["id = ?" id]))

(defn save-checks [checks]
  (try 
    (jdbc/insert-multi! db :checks checks) 
      (catch Exception e 
        (println (.getMessage e)))))

(defn get-last-checks []
    (jdbc/query db [
      (str 
        "select c.status, c.service_id, c.timestamp, c.message, s.name from checks c inner join services s on c.service_id = s.id " 
        "order by c.timestamp desc limit (select count(id) from services)")]))

(defn get-last-checks-by-service [id limit]
    (jdbc/query db [
      (str 
        "select c.status, c.service_id, c.timestamp, c.message, s.name from checks c inner join services s on c.service_id = s.id " 
        "where c.service_id = ? order by c.timestamp desc limit ?") id limit]))

(create-db)