(ns octobuilder.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-oauth2.ring :as oauth2-ring]))


(def login-uri "")


(def oath2-settings
  {:authorization-uri (str login-uri "")
   :access-token-uri (str login-uri "")
   :redirect-uri ""
   :client-id ""
   :client-secret ""
   :scope ["repo"]
   :grant-type "authorization_code"
   :force-https true
   :trace-messages false
   :get-state oauth2-ring/get-state-from-session
   :put-state oauth2-ring/put-state-in-session
   :get-target oauth2-ring/get-target-from-session
   :put-target oauth2-ring/put-target-in-session
   :get-oauth2-data oauth2-ring/get-oauth2-data-from-session
   :put-oauth2-data oauth2-ring/put-oauth2-data-in-session})



(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (handler/site app-routes))
