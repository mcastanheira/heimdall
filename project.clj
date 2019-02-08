(defproject heimdall "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/tools.cli "0.4.1"]
    [org.clojure/tools.logging "0.4.1"]
    [compojure "1.6.1"]
    [ring/ring-core "1.6.3"]
    [ring/ring-jetty-adapter "1.6.3"]
    [hiccup "1.0.5"]
    [javax.servlet/servlet-api "2.5"]]
  :main ^:skip-aot heimdall.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
