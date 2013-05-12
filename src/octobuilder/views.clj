(ns octobuilder.views
  (:require [octobuilder.secrets :as secrets]
            [clojure.string :as string]
            [clojure.pprint :as pprint])
  (:import [java.io StringWriter]))


(defn base-view []
  "Hello World")


(defn project-list []
  "List of projects")


(defn view-session [session]
  (let [writer (StringWriter.)]
    (pprint/pprint session writer)
    (pprint/pprint session)
    (str "<pre>" (string/replace (.toString writer) "\n" "<br/>") "</pre>")))
