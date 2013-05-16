(ns octobuilder.database
  (:use [datomic.api :only [q db] :as d]))


;; (def database-uri "datomic:free://localhost:4334/octobuilder")
(def database-uri "datomic:mem://octobuilder")


(def db-tx (d/create-database database-uri))


(def conn (d/connect database-uri))


(def schema
  [;; Users
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


(def bootstrap-contents
  [;; github service
   {:db/id (d/tempid :db.part/user -100001)
    :service/name "github"}

   {:db/id (d/tempid :db.part/user -100002)
    :service/name "jenkins"}

   {:db/id (d/tempid :db.part/user -200003)
    :github/access-token "foo"
    :user/username "sjagoe"
    :user/service (d/tempid :db.part/user -100001)
    }

   {:db/id (d/tempid :db.part/user -200004)
    :jenkins/password "some-password"
    :user/username "pr-builder"
    :user/service (d/tempid :db.part/user -100002)
    }

   ])


(defn bootstrap [schema bootstrap-contents]
  (do
    @(d/transact conn schema)
    @(d/transact conn bootstrap-contents)))


(defn get-existing-entity-id [github-id]
  (let [existing-entity (q '[:find ?entity :in $ ?github-id :where
                             [?entity :github.general/id ]]
                           (db conn) github-id)]
    (if (not (empty? existing-entity))
      (first (first existing-entity)))))


(defn assoc-id
  ([id-cache github-item]
     (assoc-id id-cache github-item :id))
  ([id-cache github-item get-id]
     (let [github-id (get-id github-item)
           cached-id (id-cache github-id)]
       (if (nil? cached-id)
         (let [existing-id (get-existing-entity-id github-id)
               entity-id (if (nil? existing-id)
                           (d/tempid :db.part/user)
                           existing-id)]
           (assoc id-cache github-id entity-id))
         id-cache))))


(defn create-tx [id-cache items]
  {:id-cache id-cache
   :tx items})


(defn merge-txs [parts]
  {:id-cache (apply merge (map :id-cache parts))
   :tx (apply merge (map :tx parts))})


(defn user-to-tx [id-cache user user-id]
  (let [type (if (= (:type user) "Organization")
               :github.usertype/organization
               :github.usertype/user)]
    (create-tx id-cache
               {user-id {:db/id user-id
                         :github.general/id (:id user)
                         :github.user/type type
                         :github.user/login (:login user)}})))


(defn repo-to-tx [id-cache repo repo-id]
  (let [repo-owner (:owner repo)
        id-cache (assoc-id id-cache repo-owner)
        repo-owner-id (id-cache (:id repo-owner))]
    (let [parts [(user-to-tx id-cache repo-owner repo-owner-id)
                 (create-tx id-cache
                            {repo-id {:db/id repo-id
                                      :github.general/id (:id repo)
                                      :github.repository/name (:name repo)
                                      :github.repository/full-name (:full_name repo)
                                      :github.repository/owner repo-owner-id}})]]
      (merge-txs parts))))


(defn head-to-tx [id-cache head head-id]
  (let [head-user (:user head)
        head-repo (:repo head)
        id-cache (assoc-id id-cache head-user)
        id-cache (assoc-id id-cache head-repo)
        head-user-id (id-cache (:id head-user))
        head-repo-id (id-cache (:id head-repo))]
    (let [parts [(user-to-tx id-cache head-user head-user-id)
                 (repo-to-tx id-cache head-repo head-repo-id)
                 (create-tx id-cache
                            {head-id {:db/id head-id
                                      :github.head/label (:label head)
                                      :github.head/ref (:ref head)
                                      :github.head/sha (:sha head)
                                      :github.head/user head-user-id
                                      :github.head/repository head-repo-id}})]]
      (merge-txs parts))))


(defn identify-head [head]
  (let [repository (:id (:repo head))
        ref (:ref head)]
    (list repository ref)))


(defn- unravel [n coll]
  (map #(take-nth n (drop % coll)) (range n)))


(defn- get-tx [tx-map]
  (unravel 2 (concat (list tx-map))))


(defn pull-request-to-tx [pull-request]
  "Returns a datomic transaction constructed from a github pull request"
  (let [base (:base pull-request)
        head (:head pull-request)
        user (:user pull-request)]
    (let [id-cache (assoc-id {} pull-request)
          id-cache (assoc-id id-cache base)
          id-cache (assoc-id id-cache user)
          id-cache (assoc-id id-cache head identify-head)
          pull-request-id (id-cache (:id pull-request))
          base-id (id-cache (:id base))
          head-id (id-cache (identify-head head))
          user-id (id-cache (:id user))]
      (let [parts [(repo-to-tx id-cache base base-id)
                   (head-to-tx id-cache head head-id)
                   (user-to-tx id-cache user user-id)
                   (create-tx id-cache
                              {pull-request-id {:db/id pull-request-id
                                                :github.general/id (:id pull-request)
                                                :github.pullrequest/state :github.pullrequest/open
                                                :github.pullrequest/html-url (:html_url pull-request)
                                                :github.pullrequest/number (:number pull-request)
                                                :github.pullrequest/user user-id
                                                :github.pullrequest/base base-id
                                                :github.pullrequest/head head-id}})]]
        (merge-txs parts)))))
