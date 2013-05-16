(ns octobuilder.views
  (:require [clojure.string :as string]
            [clojure.pprint :as pprint]
            [ring.util.response :as ring-response]
            [tentacles.repos :as repos])
  (:import [java.io StringWriter]))


(defn view-session [session]
  (let [writer (StringWriter.)]
    (pprint/pprint session writer)
    (str "<pre>" (string/replace (.toString writer) "\n" "<br/>") "</pre>")))


(defn base-view [oauth2]
  (if oauth2
    (ring-response/redirect "/projects")
    "Not authenticated!"))


;; FIXME: How should I handle the redirect from github?
(defn login-complete []
  (ring-response/redirect "/projects"))


(defn project-list [oauth2]
  (let [oauth_token (:access-token oauth2)]
    (pprint/pprint oauth2)
    (view-session (repos/repos {:type "private" :oauth_token oauth_token}))))


(defn print-thing [real-handler thing]
  (pprint/pprint thing)
  (real-handler))
