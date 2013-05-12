(ns octobuilder.views
  (:require [octobuilder.secrets :as secrets]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [ring.util.response :as ring-response])
  (:import [java.io StringWriter]))


(defn base-view [oauth2]
  (if oauth2
    (ring-response/redirect "/projects")
    "Not authenticated!"))


;; FIXME: How should I handle the redirect from github?
(defn login-complete []
  (ring-response/redirect "/projects"))


(defn project-list []
  "List of projects")


(defn print-thing [real-handler thing]
  (pprint/pprint thing)
  (real-handler))


(defn view-session [session]
  (let [writer (StringWriter.)]
    (pprint/pprint session writer)
    (pprint/pprint session)
    (str "<pre>" (string/replace (.toString writer) "\n" "<br/>") "</pre>")))
