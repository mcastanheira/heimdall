(ns heimdall.web
  (:require 
    [heimdall.database :as database]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.params :refer :all]
    [ring.util.response :as response]
    [selmer.parser :as selmer]
    [selmer.filters :as filters])
  (:import [java.text SimpleDateFormat]))

(selmer/set-resource-path! (clojure.java.io/resource "./templates"))

(defn- load-configurations-page [port check-interval timestamp-mask]
  (selmer/render-file "configurations.html" {:port port :check-interval check-interval :timestamp-mask timestamp-mask}))

(defn- load-services-page [services timestamp-mask]
  (selmer/render-file "services.html" {:services services :timestamp-mask timestamp-mask}))

(defn- load-add-service-page []
  (selmer/render-file "service.html" {:title "Add Service" :service {:id 0}}))

(defn- load-edit-service-page [service]
  (selmer/render-file "service.html" {:title "Edit Service" :service service}))

(defn- transform-form-params-to-service [params]
  {
    :id (Integer/parseInt (get params "id"))
    :name (get params "name")
    :origin (get params "origin")
    :host (get params "host")
    :port (Integer/parseInt (get params "port"))
    :heartbeat (get params "heartbeat")
    :restart (Boolean/parseBoolean (get params "restart"))
    :command (get params "command")})

(defn- save-service [service]
  (database/save-service service)
  (response/redirect "/services"))

(defn- delete-service [id]
  (database/delete-service id)
  (response/redirect "/services"))

;(defn- check-row [check timestamp-mask show-links]
;  [:tr
;    [:td {:class "text-center"} 
;      (if (= (:status check) ":ok") 
;        [:i {:class "far fa-check-circle"}] 
;        [:i {:class "far fa-times-circle"}])]
;    [:td (:name check)]
;    [:td (.format (SimpleDateFormat. timestamp-mask) (:timestamp check))]
;    [:td (:message check)]
;    [:td {:class "text-right"} 
;      (if show-links [:a {:href (str "/checks/" (:service_id check)) :class "btn btn-default"} "view last 10 checks"])]])

;(defn- checks-page [checks timestamp-mask show-links]
;  (hiccup/html
;    [:h1 {:class "title"} "Checks"]
;    [:table {:class "table table-sm"}
;      [:theader
;        [:tr
;          [:th {:class "text-center"} "Status"]
;          [:th "Service"]
;          [:th "Timestamp"]
;          [:th "Message"]
;          [:th]]]
;      [:tbody (map #(check-row % timestamp-mask show-links) checks)]]
;      (if show-links [:script {:src "/js/reload.js"}])))

(defn- load-checks-page [checks timestamp-mask show-links]
  (selmer/render-file 
    "checks.html" 
    {:checks checks :df (SimpleDateFormat. timestamp-mask) :show-links show-links}))

;(defn- not-found-page []
;  (hiccup/html [:div {:class "alert alert-danger"} "Page not found!"]))

(defn start-server 
  "Start the web server" 
  [config]
  (let [
    {:keys [port check-interval timestamp-mask]} config 
    date-format (SimpleDateFormat. timestamp-mask)]
    (filters/add-filter! :format-timestamp (fn [timestamp] (.format date-format (java.util.Date. timestamp))))
    (jetty/run-jetty 
      (wrap-params
        (routes 
          (GET "/" [] (response/redirect "/checks"))
          (GET "/configurations" [] (load-configurations-page port check-interval timestamp-mask))
          (GET "/services" [] (load-services-page (database/get-services) timestamp-mask))
          (POST "/services" request  (save-service (transform-form-params-to-service (:form-params request))))
          (GET "/services/add" [] (load-add-service-page))
          (GET "/services/edit/:id" [id] (load-edit-service-page (database/get-service (Integer/parseInt id))))
          (POST "/services/delete" request (delete-service (Integer/parseInt (get (:form-params request) "id"))))
          (GET "/checks" [] (load-checks-page (database/get-last-checks) timestamp-mask true))
          (GET "/checks/:id" [id] (load-checks-page (database/get-last-checks-by-service (Integer/parseInt id) 10) timestamp-mask false))
          (route/resources "/")));          (route/not-found (generate-page (not-found-page)))))
      {:port (:port config) :join? false})))