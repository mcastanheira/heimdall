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
        [:link {:rel "stylesheet" :href "css/bootstrap.min.css.map"}]
        [:link {:rel "stylesheet" :href "css/heimdall.css"}]]
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

(defn- configurations-page []
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
            [:td (:port @config)]]
          [:tr
            [:td "check interval"]
            [:td (:check-interval @config)]]]]]))

(defn- services-page []
  (hiccup/html
    [:h1 {:class "title"} "Services"]
    [:table {:class "table table-bordered"}
        [:theader
          [:tr
            [:th "Status"]
            [:th "Service"]
            [:th "Last Check"]]]
        [:tbody]]))

(defn- not-found-page []
  (hiccup/html [:div {:class "alert alert-danger"} "Page not found!"]))

(defroutes heimdall-routes
  (GET "/" [] (generate-page (configurations-page)))
  (GET "/configurations" [] (generate-page (configurations-page)))
  (GET "/services" [] (generate-page (services-page)))
  (route/resources "/")
  (route/not-found (generate-page (not-found-page))))

(defn start-server 
  "Start the web server" 
  [port]
  (jetty/run-jetty heimdall-routes {:port port}))