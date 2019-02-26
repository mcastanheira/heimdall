(ns heimdall.web
  (:require 
    [heimdall.database :as database]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.params :refer :all]
    [ring.util.response :as response]
    [hiccup.core :as hiccup]
    [hiccup.form :as form])
  (:import [java.text SimpleDateFormat]))

(defn- generate-page [content]
  (hiccup/html 
    [:html
      [:head
        [:title "Heimdall"]
        [:link {:rel "stylesheet" :href "/css/bootstrap.min.css"}]
        [:link {:rel "stylesheet" :href "/css/bootstrap.min.css.map"}]
        [:link {:rel "stylesheet" :href "/css/all.min.css"}]
        [:link {:rel "stylesheet" :href "/css/heimdall.css"}]]
      [:body
        [:nav {:class "navbar navbar-expand-lg"}
          [:a {:class "navbar-brand" :href "/"} "Heimdall"]
          [:div {:class "collapse navbar-collapse"}
            [:ul {:class "navbar-nav mr-auto"}
              [:li {:class "nav-item"}
                [:a {:class "nav-link" :href "/services"} "Services"]]
              [:li {:class "nav-item"}
                [:a {:class "nav-link" :href "/configurations"} "Configurations"]]]]]
        [:div {:class "container text-center"} content]]]))

(defn- configurations-page [port check-interval timestamp-mask]
  (hiccup/html
    [:div
      [:h1 {:class "title"} "Configurations"]
      [:table {:class "table table-bordered"}
        [:theader
          [:tr
            [:th "Parameter"]
            [:th "Value"]]]
        [:tbody
          [:tr
            [:td "port"]
            [:td port]]
          [:tr
            [:td "check interval"]
            [:td check-interval]]
          [:tr
            [:td "timestamp mask"]
            [:td timestamp-mask]]]]]))

(defn- service-row [service timestamp-mask]
  [:tr
    [:td (:name service)]
    [:td (:host service)]
    [:td (:port service)]])

(defn- services-page [timestamp-mask]
  (hiccup/html
    [:h1 {:class "title"} "Services"]
    [:table {:class "table table-bordered"}
        [:theader
          [:tr
            [:th "Name"]
            [:th "Host"]
            [:th "Port"]]]
        [:tbody (map #(service-row % timestamp-mask) (database/get-services))]]
    [:a {:href "/services/add" :class "btn btn-default"} "create new service"]
    [:script {:src "/js/reload.js"}]))

(defn- service-form [service]
  (let [{:keys [id name origin host port heartbeat restart command]} service]
    [:div {:class "container text-left"} 
      (form/form-to [:post "/services"] 
        [:div {:class "form-group"} 
          (form/hidden-field "id" id)]
        [:div {:class "form-group"} 
          (form/label "name" "Name")
          (form/text-field {:class "form-control" :placeholder "My Service Name"} "name" name)]
        [:div {:class "form-group"} 
          (form/label "origin" "Origin")
          [:div
            [:div {:class "form-check form-check-inline"} 
              (form/radio-button {:class "form-check-input"} "origin" (= origin "local") "local")
              (form/label {:class "form-check-label"} "local" "Local")]
            [:div {:class "form-check form-check-inline"} 
              (form/radio-button {:class "form-check-input"} "origin" (= origin "remote") "remote")
              (form/label {:class "form-check-label"} "remote" "Remote")]]]
        [:div {:class "form-group"} 
          (form/label "host" "Host")
          (form/text-field {:class "form-control" :placeholder "localhost"} "host" host)]
        [:div {:class "form-group"} 
          (form/label "port" "Port")
          [:input {:class "form-control" :type "number" :name "port" :value port :placeholder "8080"}]]
        [:div {:class "form-group"} 
          (form/label "heartbeat" "Heartbeat URL")
          (form/text-field {:class "form-control" :placeholder "/heartbeat"} "heartbeat" heartbeat)]
        [:div {:class "form-group"} 
          [:div
            [:div {:class "form-check form-check-inline"} 
              (form/check-box {:class "form-check-input"} "restart" restart)
              (form/label {:class "form-check-label"} "restart" "Should Restart")]]]
        [:div {:class "form-group"} 
          (form/label "command" "Command to Restart")
          (form/text-field {:class "form-control" :placeholder "java -jar myService.jar --port 8080"} "command" command)]
        [:div {:class "form-group"} 
          (form/submit-button {:class "btn btn-default"} "save")])]))

(defn- add-service-page []
  (hiccup/html
    [:h1 {:class "title"} "Add Service"]
    (service-form {:id 0})))

(defn- edit-service-page [id]
  (hiccup/html
    [:h1 {:class "title"} "Edit Service"]
    (service-form {})))

(defn- transform-form-params-to-service [params]
  {
    :id (Integer/parseInt (get params "id"))
    :name (get params "name")
    :origin (get params "origin")
    :host (get params "host")
    :port (Integer/parseInt (get params "port"))
    :heartbeat (get params "heartbeat")
    :restart (Boolean/parseBoolean (get params "restart"))
    :command (get params "command")
  })

(defn- save-service [service]
  (database/save-service service)
  (response/redirect "/services"))

;(defn- check-row [check timestamp-mask]
;  [:tr
;    [:td {:class "text-center"} 
;      (if (= (:status check) ":ok") 
;        [:i {:class "far fa-check-circle"}] 
;        [:i {:class "far fa-times-circle"}])]
;    [:td (:name check)]
;    [:td (:port check)]
;    [:td (.format (SimpleDateFormat. timestamp-mask) (:timestamp check))]
;    [:td (:message check)]])

;(defn- checks-page [uuid port timestamp-mask]
;  (hiccup/html
;    [:h1 {:class "title"} "Checks"]
;    [:table {:class "table table-bordered"}
;      [:theader
;        [:tr
;          [:th "Status"]
;          [:th "Service"]
;          [:th "Port"]
;          [:th "Timestamp"]
;          [:th "Message"]]]
;        [:tbody (map #(check-row % timestamp-mask) (database/get-checks-by-uuid-and-port uuid port 10))]]))

(defn- not-found-page []
  (hiccup/html [:div {:class "alert alert-danger"} "Page not found!"]))

(defn start-server 
  "Start the web server" 
  [config]
  (let [{:keys [port check-interval timestamp-mask]} config]
    (jetty/run-jetty 
      (wrap-params
        (routes 
          (GET "/" [] (generate-page (configurations-page port check-interval timestamp-mask)))
          (GET "/configurations" [] (generate-page (configurations-page port check-interval timestamp-mask)))
          (GET "/services" [] (generate-page (services-page timestamp-mask)))
          (POST "/services" request  
            (save-service (transform-form-params-to-service (:form-params request))))
          (GET "/services/add" [] (generate-page (add-service-page)))
          (GET "/services/edit/:id" [id] (generate-page (edit-service-page id)))
;          (GET "/services/:uuid/:port" [uuid port] (generate-page (checks-page uuid port timestamp-mask)))
          (route/resources "/")
          (route/not-found (generate-page (not-found-page)))))
      {:port (:port config) :join? false})))