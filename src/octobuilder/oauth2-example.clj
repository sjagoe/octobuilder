(ns octobuilder.oauth2)


(def login-uri "https://github.com/login/oauth")


(def oath2-settings
  {:authorization-uri (str login-uri "/authorize")
   :access-token-uri (str login-uri "/access_token")
   :redirect-uri "https://simonjagoe.com/login"
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
