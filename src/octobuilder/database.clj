(ns octobuilder.database
  (:use [datomic.api :only [q db] :as d])
  (:require [octobuilder.secrets :as secrets]))


;; (def database-uri "datomic:free://localhost:4334/octobuilder")
(def database-uri "datomic:mem://octobuilder")


(def db-tx (d/create-database database-uri))


(def conn (d/connect database-uri))


(defn make-schema-part
  ([ident type cardinality doc]
     (make-schema-part ident type cardinality doc false))
  ([ident type cardinality doc fulltext]
     {:db/id (d/tempid :db.part/db)
      :db/ident name
      :db/valueType type
      :db/cardinality cardinality
      :db/fulltext fulltext
      :db/doc doc
      :db.install/_attribute :db.part/db}))


(def schema
  [;; Jenkins job
   (make-schema-part :jenkins.job/name
                     :db.type/string
                     :db.cardinality/one
                     "The name of a Job in Jenkins")
   (make-schema-part :jenkins.job/location
                     :db.type/string
                     :db.cardinality/one
                     "The URL of the job")
   (make-schema-part :jenkins.job/user
                     :db.type/ref
                     :db.cardinality/one
                     "The user used to authenticate to Jenkins to trigger builds of the job")

   ;; projects
   ; owner name; string
   (make-schema-part :project/owner
                     :db.type/string
                     :db.cardinality/one
                     "The name of the project's owner")
   ; project name; string
   (make-schema-part :project/name
                     :db.type/string
                     :db.cardinality/one
                     "The name of the project repository")
   ; project user; ref
   (make-schema-part :project/user
                     :db.type/ref
                     :db.cardinality/one
                     "Reference to the user for logging in to github")
   (make-schema-part :project/jenkins-job
                     :db.type/ref
                     :db.cardinality/one
                     "Jenkins job used to build pull requests of this project")

   ;; Users
   (make-schema-part :github/access-token
                     :db.type/string
                     :db.cardinality/one
                     "The user's access token")
   (make-schema-part :user/username
                     :db.type/string
                     :db.cardinality/one
                     "The user's login name")
   (make-schema-part :jenkins/password
                     :db.type/string
                     :db.cardinality/one
                     "The user's password")
   (make-schema-part :user/service
                     :db.type/ref
                     :db.cardinality/one
                     "The service for which this user is used")

   ;; services
   (make-schema-part :service/name
                     :db.type/string
                     :db.cardinality/one
                     "The name of this service")

   ;;; pull request components
   ;; github.general/id; long
   (make-schema-part :github.general/id
                     :db.type/long
                     :db.cardinality/one
                     "The internal GitHub object ID")

   ;; user types
   [:db/add (d/tempid :db.part/user) :db/ident :github.usertype/user]
   [:db/add (d/tempid :db.part/user) :db/ident :github.usertype/organization]

   ;; github.user
   ; github.general/id
   ; type; [organization user]
   (make-schema-part :github.user/type
                     :db.type/ref       ; :github.usertype/*
                     :db.cardinality/one
                     "The type of the user")
   ; login; string
   (make-schema-part :github.user/login
                     :db.type/string
                     :db.cardinality/one
                     "The login name of the user")

   ;; github.repository
   ; github.general/id
   ; name; string
   (make-schema-part :github.repository/name
                     :db.type/string
                     :db.cardinality/one
                     "The name of the repository")
   ; full-name; string
   (make-schema-part :github.repository/full-name
                     :db.type/string
                     :db.cardinality/one
                     "The full name of the repository")
   ; owner
   (make-schema-part :github.repository/owner
                     :db.type/ref       ; github.user
                     :db.cardinality/one
                     "The owner of the repository")

   ;; github.head
   ; label; string
   (make-schema-part :github.head/label
                     :db.type/string
                     :db.cardinality/one
                     "The label of the HEAD")
   ; ref; string
   (make-schema-part :github.head/ref
                     :db.type/string
                     :db.cardinality/one
                     "The ref pointing to the HEAD")
   ; sha; string
   (make-schema-part :github.head/sha
                     :db.type/string
                     :db.cardinality/one
                     "The sha of the HEAD")
   ; user; github.user
   (make-schema-part :github.head/user
                     :db.type/ref       ; github.user
                     :db.cardinality/one
                     "The user that created the commit")
   ; repo
   (make-schema-part :github.head/repository
                     :db.type/ref       ; github.repository
                     :db.cardinality/one
                     "THe repository to which the HEAD belongs" 
                      )

   ;;; pull request states
   [:db/add (d/tempid :db.part/user) :db/ident :github.pullrequest/open]
   [:db/add (d/tempid :db.part/user) :db/ident :github.pullrequest/closed]

   ;; pull requests
   ; github.general/id
   ; state ; [open closed]
   (make-schema-part :github.pullrequest/state
                     :db.type/ref   ; github.pullrequest/{open,closed}
                     :db.cardinality/one
                     "The state of the pull request")
   ; merge_commit_sha ; string
   (make-schema-part :github.pullrequest/merge-sha
                     :db.type/string
                     :db.cardinality/one
                     "The sha of the merge commit")
   ; html_url; string
   (make-schema-part :github.pullrequest/html-url
                     :db.type/string
                     :db.cardinality/one
                     "The html URL of the pull request")
   ; number; long
   (make-schema-part :github.pullrequest/number
                     :db.type/long
                     :db.cardinality/one
                     "The numeric ID of the pull request")
   ; user; ref
   (make-schema-part :github.pullrequest/user
                     :db.type/ref
                     :db.cardinality/one
                     "The user ... ?")
   ; base; ref
   (make-schema-part :github.pullrequest/base
                     :db.type/ref       ; guthub.repp
                     :db.cardinality/one
                     "The base repositorty against which the pull request was made")
   ; head; ref
   (make-schema-part :github.pullrequest/head
                     :db.type/ref
                     :db.cardinality/one
                     "The HEAD of the pull request")


   ;; builds
   {:db/id (d/tempid :db.part/db)
    :db/ident :jenkins.build/number
    :db/valueType :db.type/long}
   {:db/id (d/tempid :db.part/db)
    :db/ident :jenkins.build/url
    :db/valueType :db.type/string}
   ])


;; (defn make-github-user-tx [github-id users]
;;   (fn [coll user-name]
;;     (let [user (users user-name)
;;           user-id (:id user)
;;           token (:token user)]
;;       (conj coll {:db/id user-id
;;                   :github/access-token token
;;                   :user/username user-name
;;                   :user/service github-id}))))


(defn make-github-user-tx [github-id github-user-ids github-users]
  (fn [coll user-key]
    (let [user-id (github-user-ids user-key)
          user (github-users user-key)]
      (conj coll {:db/id user-id
                  :github/access-token (:token user)
                  :user/username (:name user)
                  :user/service github-id}))))


(defn make-project-tx [users]
  (fn [project]
    {:db/id (d/tempid :db.part/user)
     :project/owner (:owner project)
     :project/name (:project project)
     :project/user (users (:user project))}))


(defn allocate-user-db-ids [users]
  (reduce #(assoc %1 %2 (d/tempid :db.part/user)) {} (keys users)))


(defn bootstrap-contents [github-users projects]
  (let [github-id (d/tempid :db.part/user)
        jenkins-id (d/tempid :db.part/user)
        github-user-ids (allocate-user-db-ids github-users)
        github-user-txs (reduce (make-github-user-tx github-id github-user-ids github-users)
                                [] (keys github-user-ids))
        project-txs (map (make-project-tx github-user-ids) projects)
        ]
    (concat github-user-txs
            project-txs
            [{:db/id github-id
              :service/name "github"}
             {:db/id jenkins-id
              :service/name "jenkins"}])))


(defn bootstrap [schema users projects]
  (do
    @(d/transact conn schema)
    @(d/transact conn (bootstrap-contents users projects))))
