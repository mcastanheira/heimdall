(ns heimdall.web
  (:require 
    [heimdall.database :as database]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [hiccup.core :as hiccup])
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
                [:a {:class "nav-link" :href "/configurations"} "Configurations"]]
              [:li {:class "nav-item"}
                [:a {:class "nav-link" :href "/services"} "Services"]]]]]
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

(defn- service-row [service-check timestamp-mask]
  [:tr
    [:td {:class "text-center"} 
      (if (= (:status service-check) ":ok") 
        [:i {:class "far fa-check-circle"}] 
        [:i {:class "far fa-times-circle"}])]
    [:td (:name service-check)]
    [:td (:port service-check)]
    [:td (.format (SimpleDateFormat. timestamp-mask) (:timestamp service-check))]
    [:td (:message service-check)]
    [:td {:class "text-center"} 
      [:a {:class "btn btn-default" :href (str "/services/" (:uuid service-check) "/" (:port service-check))} "view last 10 checks"]]])

(defn- services-page [services timestamp-mask]
  (hiccup/html
    [:h1 {:class "title"} "Services"]
    [:table {:class "table table-bordered"}
        [:theader
          [:tr
            [:th "Status"]
            [:th "Service"]
            [:th "Port"]
            [:th "Last Check"]
            [:th "Message"]
            [:th]]]
        [:tbody (map #(service-row % timestamp-mask) (database/get-checks services))]]
    [:script {:src "/js/reload.js"}]))

(defn- check-row [check timestamp-mask]
  [:tr
    [:td {:class "text-center"} 
      (if (= (:status check) ":ok") 
        [:i {:class "far fa-check-circle"}] 
        [:i {:class "far fa-times-circle"}])]
    [:td (:name check)]
    [:td (:port check)]
    [:td (.format (SimpleDateFormat. timestamp-mask) (:timestamp check))]
    [:td (:message check)]])

(defn- checks-page [uuid port timestamp-mask]
  (hiccup/html
    [:h1 {:class "title"} "Checks"]
    [:table {:class "table table-bordered"}
      [:theader
        [:tr
          [:th "Status"]
          [:th "Service"]
          [:th "Port"]
          [:th "Timestamp"]
          [:th "Message"]]]
        [:tbody (map #(check-row % timestamp-mask) (database/get-checks-by-uuid-and-port uuid port 10))]]))

(defn- not-found-page []
  (hiccup/html [:div {:class "alert alert-danger"} "Page not found!"]))

(defn start-server 
  "Start the web server" 
  [config]
  (let [
    port (:port config)
    check-interval (:check-interval config)
    services (:services config)
    timestamp-mask (:timestamp-mask config)]
    (jetty/run-jetty 
      (routes 
        (GET "/" [] 
          (generate-page 
            (configurations-page port check-interval timestamp-mask)))
        (GET "/configurations" [] 
          (generate-page 
            (configurations-page port check-interval timestamp-mask)))
        (GET "/services" [] (generate-page (services-page services timestamp-mask)))
        (GET "/services/:uuid/:port" [uuid port] (generate-page (checks-page uuid port timestamp-mask)))
        (route/resources "/")
        (route/not-found (generate-page (not-found-page)))) 
      {:port (:port config) :join? false})))