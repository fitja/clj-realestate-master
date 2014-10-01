(defproject Realestate "1.0.0"
  :description "Realestate"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.9"]
                 [ring/ring-core "1.3.1"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [hiccup "1.0.5"]
                 [congomongo "0.4.1"]
                 [amalloy/mongo-session "0.0.2"]
                 [lib-noir "0.9.0"]
                 [clj-time "0.6.0"]]
  
  :main core)