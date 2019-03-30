(ns heimdall.web
  (:require 
    [heimdall.database :as database]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.params :refer :all]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.flash :refer [wrap-flash]]
    [ring.util.response :as response]
    [selmer.parser :as selmer]
    [selmer.filters :as filters])
  (:import [java.text SimpleDateFormat]))

(selmer/set-resource-path! (clojure.java.io/resource "./templates"))

(defn- load-configurations-page [port check-interval timestamp-mask]
  (selmer/render-file 
    "configurations.html" 
    {:port port :check-interval check-interval :timestamp-mask timestamp-mask}))

(defn- load-services-page [services timestamp-mask messages]
  (selmer/render-file 
    "services.html" 
    (merge {:services services :timestamp-mask timestamp-mask} messages)))

(defn- load-add-service-page []
  (selmer/render-file "service.html" {:title "Add Service" :service {:id 0}}))

(defn- load-edit-service-page [service]
  (selmer/render-file "service.html" {:title "Edit Service" :service service}))

(defn- transform-form-params-to-service [params]
  {
    :id (Integer/parseInt (get params "id"))
    :name (get params "name")
    :host (get params "host")
    :port (Integer/parseInt (get params "port"))
    :heartbeat (get params "heartbeat")})

(defn- save-service [service]
  (database/save-service service)
  (assoc 
    (response/redirect "/services") 
      :flash {:success-message "Service saved with success!"}))

(defn- delete-service [id]
  (database/delete-service id)
  (assoc 
    (response/redirect "/services") 
      :flash {:success-message "Service deleted with success!"}))

(defn- load-checks-page [checks timestamp-mask show-links]
  (selmer/render-file 
    "checks.html" 
    {:checks checks :df (SimpleDateFormat. timestamp-mask) :show-links show-links}))

(defn- load-not-found-page []
  (selmer/render-file "base.html" {:failure-message "Page not found"}))

(defn start-server 
  "Start the web server" 
  [config]
  (let [
    {:keys [port check-interval timestamp-mask]} config 
    date-format (SimpleDateFormat. timestamp-mask)]
    (filters/add-filter! :format-timestamp (fn [timestamp] (.format date-format (java.util.Date. timestamp))))
    (jetty/run-jetty 
      (wrap-session
        (wrap-flash
          (wrap-params
            (routes 
              (GET "/" [] 
                (response/redirect "/checks"))
              (GET "/configurations" [] 
                (load-configurations-page port check-interval timestamp-mask))
              (GET "/services" request 
                (load-services-page (database/get-services) timestamp-mask (:flash request)))
              (POST "/services" request  
                (save-service (transform-form-params-to-service (:form-params request))))
              (GET "/services/add" [] 
                (load-add-service-page))
              (GET "/services/edit/:id" [id] 
                (load-edit-service-page (database/get-service (Integer/parseInt id))))
              (POST "/services/delete" request 
                (delete-service (Integer/parseInt (get (:form-params request) "id"))))
              (GET "/checks" [] 
                (load-checks-page (database/get-last-checks) timestamp-mask true))
              (GET "/checks/:id" [id] 
                (load-checks-page (database/get-last-checks-by-service (Integer/parseInt id) 10) timestamp-mask false))
              (route/resources "/")
              (route/not-found (load-not-found-page))))))
      {:port (:port config) :join? false})))