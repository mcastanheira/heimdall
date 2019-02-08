(ns heimdall.web
  (:require 
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [hiccup.core :as hiccup]
    [heimdall.config :refer [config]]))

(defn- generate-page [content]
  (hiccup/html 
    [:html
      [:head
        [:title "Heimdall"]
        [:link {:rel "stylesheet" :href "css/bootstrap.min.css"}]
        [:link {:rel "stylesheet" :href "css/bootstrap.min.css.map"}]]
      [:body
        [:nav
          [:a {:class "btn" :href "/configurations"} "Configurations"]
          [:a {:href "/services"} "Services"]]
        content]]))

(defn- generate-configurations-page []
  (hiccup/html
    [:div
      [:h1 "Configurations"]
      [:table
        [:theader
          [:tr
            [:th "Parameter"]
            [:th "Value"]]]
        [:tbody
          [:tr
            [:td "port"]
            [:td (:port @config)]]
          [:tr
            [:td "check interval"]
            [:td (:check-interval @config)]]]]]))

(defn- generate-services-page []
  (hiccup/html
    [:h1 "Services"]
    [:table
        [:theader
          [:tr
            [:th "Service"]
            [:th "Status"]
            [:th "Last Check"]]]
        [:tbody]]))

(defroutes heimdall-routes
  (GET "/" [] (generate-page (generate-configurations-page)))
  (GET "/configurations" [] (generate-page (generate-configurations-page)))
  (GET "/services" [] (generate-page (generate-services-page)))
  (route/resources "/")
  (route/not-found (generate-page [:div "Page not found!"])))

(defn start-server 
  "Start the web server" 
  [port]
  (jetty/run-jetty heimdall-routes {:port port}))