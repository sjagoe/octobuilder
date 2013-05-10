(ns octobuilder.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-oauth2.ring :as oauth2-ring]))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (handler/site app-routes))
