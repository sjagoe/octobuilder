(defproject octobuilder "0.1.0-SNAPSHOT"
  :description "octobuilder is a bridge between Jenkins and GitHub to
  facilitate automatic building of pull requests"
  :url "https://github.com/sjagoe/octobuilder"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [com.datomic/datomic-free "0.8.3941"]
                 [tentacles "0.2.4"]
                 [bwhmather/clj-oauth2 "0.5.1"]
                 [hiccup "1.0.3"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler octobuilder.handler/app}
  :profiles {:dev {:dependencies [[ring-mock "0.1.3"]
                                  [org.clojars.runa/conjure "2.1.3"]]
                   :source-paths ["dev-src"]}})
