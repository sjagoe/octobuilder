(ns octobuilder.database
  (:use [datomic.api :only [q db] :as d])
  (:require [octobuilder.secrets :as secrets]))


;; (def database-uri "datomic:free://localhost:4334/octobuilder")
(def database-uri "datomic:mem://octobuilder")


(def db-tx (d/create-database database-uri))


(def conn (d/connect database-uri))


(def schema
  [;; projects
   ; owner name; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :project/owner
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The name of the project's owner"
    :db.install/_attribute :db.part/db}
   ; project name; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :project/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The name of the project repository"
    :db.install/_attribute :db.part/db}
   ; project user; ref
   {:db/id (d/tempid :db.part/db)
    :db/ident :project/user
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "Reference to the user for logging in to github"
    :db.install/_attribute :db.part/db}

   ;; Users
   {:db/id (d/tempid :db.part/db)
    :db/ident :github/access-token
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The user's access token"
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :user/username
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The user's login name"
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :jenkins/password
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The user's password"
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :user/service
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The service for which this user is used"
    :db.install/_attribute :db.part/db}

   ;; services
   {:db/id (d/tempid :db.part/db)
    :db/ident :service/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The name of this service"
    :db.install/_attribute :db.part/db}

   ;;; pull request components
   ;; github.general/id; long
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.general/id
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The internal GitHub object ID"
    :db.install/_attribute :db.part/db}

   ;; user types
   [:db/add (d/tempid :db.part/user) :db/ident :github.usertype/user]
   [:db/add (d/tempid :db.part/user) :db/ident :github.usertype/organization]

   ;; github.user
   ; github.general/id
   ; type; [organization user]
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.user/type
    :db/valueType :db.type/ref ; :github.usertype/*
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The type of the user"
    :db.install/_attribute :db.part/db}
   ; login; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.user/login
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The login name of the user"
    :db.install/_attribute :db.part/db}

   ;; github.repository
   ; github.general/id
   ; name; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.repository/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The name of the repository"
    :db.install/_attribute :db.part/db}
   ; full-name; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.repository/full-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The full name of the repository"
    :db.install/_attribute :db.part/db}
   ; owner
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.repository/owner
    :db/valueType :db.type/ref ; github.user
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The owner of the repository"
    :db.install/_attribute :db.part/db}

   ;; github.head
   ; label; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.head/label
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The label of the HEAD"
    :db.install/_attribute :db.part/db}
   ; ref; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.head/ref
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The ref pointing to the HEAD"
    :db.install/_attribute :db.part/db}
   ; sha; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.head/sha
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The sha of the HEAD"
    :db.install/_attribute :db.part/db}
   ; user; github.user
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.head/user
    :db/valueType :db.type/ref ; github.user
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The user that created the commit"
    :db.install/_attribute :db.part/db}
   ; repo
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.head/repository
    :db/valueType :db.type/ref ; github.repository
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "THe repository to which the HEAD belongs"
    :db.install/_attribute :db.part/db
    }

   ;;; pull request states
   [:db/add (d/tempid :db.part/user) :db/ident :github.pullrequest/open]
   [:db/add (d/tempid :db.part/user) :db/ident :github.pullrequest/closed]


   ;; pull requests
   ; github.general/id
   ; state ; [open closed]
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/state
    :db/valueType :db.type/ref ; github.pullrequest/{open,closed}
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The state of the pull request"
    :db.install/_attribute :db.part/db}
   ; merge_commit_sha ; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/merge-sha
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The sha of the merge commit"
    :db.install/_attribute :db.part/db}
   ; html_url; string
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/html-url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The html URL of the pull request"
    :db.install/_attribute :db.part/db}
   ; number; long
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/number
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The numeric ID of the pull request"
    :db.install/_attribute :db.part/db}
   ; user; ref
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/user
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The user ... ?"
    :db.install/_attribute :db.part/db}
   ; base; ref
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/base
    :db/valueType :db.type/ref ; guthub.repp
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The base repositorty against which the pull request was made"
    :db.install/_attribute :db.part/db}
   ; head; ref
   {:db/id (d/tempid :db.part/db)
    :db/ident :github.pullrequest/head
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/fulltext false
    :db/doc "The HEAD of the pull request"
    :db.install/_attribute :db.part/db}
   ])


(defn make-github-user-tx [github-id users]
  (fn [coll user-name]
    (let [user (users user-name)
          user-id (:id user)
          token (:token user)]
      (conj coll {:db/id user-id
                  :github/access-token token
                  :user/username user-name
                  :user/service github-id}))))


(defn make-project-tx [users]
  (fn [project]
    {:db/id (d/tempid :db.part/user)
     :project/owner (:owner project)
     :project/name (:project project)
     :project/user (:id (users (:user project)))}))


(defn bootstrap-contents [users projects]
  (let [github-id (d/tempid :db.part/user)
        jenkins-id (d/tempid :db.part/user)
        user-ids-tokens (reduce #(assoc %1 %2 {:id (d/tempid :db.part/user) :token (users %2)})
                                {} (keys users))
        github-user-txs (reduce (make-github-user-tx github-id user-ids-tokens)
                                [] (keys user-ids-tokens))
        project-txs (map (make-project-tx user-ids-tokens) projects)
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
