(ns octobuilder.oauth2
  (:require [octobuilder.secrets :as secrets]
            [clj-oauth2.ring :as oauth2-ring]))


(def login-uri "https://github.com/login/oauth")


(def github-com-oauth2
  {:authorization-uri (str login-uri "/authorize")
   :access-token-uri (str login-uri "/access_token")
   :redirect-uri "http://simonjagoe.com:8000/login-complete"
   :client-id secrets/client-id
   :client-secret secrets/client-secret
   :scope ["repo"]
   :grant-type "authorization_code"
   :force-https false
   :trace-messages false
   :get-state oauth2-ring/get-state-from-session
   :put-state oauth2-ring/put-state-in-session
   :get-target oauth2-ring/get-target-from-session
   :put-target oauth2-ring/put-target-in-session
   :get-oauth2-data oauth2-ring/get-oauth2-data-from-session
   :put-oauth2-data oauth2-ring/put-oauth2-data-in-session})
