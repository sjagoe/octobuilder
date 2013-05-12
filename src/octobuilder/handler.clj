(ns octobuilder.handler
  (:use compojure.core
        [ring.middleware.session :only [wrap-session]]
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]]
        [clj-oauth2.ring :only [wrap-oauth2]])
  (:require [octobuilder.oauth2 :as octo-auth]
            [octobuilder.views :as views]
            [compojure.handler :as handler]
            [compojure.route :as route]))


(defroutes app-routes
  (GET "/" [] (views/base-view))
  (GET "/projects" [views/project-list])
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (-> app-routes
      (wrap-oauth2 octo-auth/github-com-oauth2)
      wrap-session
      wrap-keyword-params
      wrap-params))
