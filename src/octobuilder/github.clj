(ns octobuilder.github
  (:use [octobuilder.database :only [conn]]
        [datomic.api :only [db q] :as d]))


(defn get-existing-entity-id [github-id]
  (let [existing-entity (q '[:find ?entity :in $ ?github-id :where
                             [?entity :github.general/id]]
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


(defn pull-request-to-tx
  "Returns a datomic transaction constructed from a github pull request"
  [pull-request]
  (let [base (:base pull-request)
        head (:head pull-request)
        user (:user pull-request)]
    (let [id-cache (assoc-id {} pull-request)
          id-cache (assoc-id id-cache user)
          id-cache (assoc-id id-cache base identify-head)
          id-cache (assoc-id id-cache head identify-head)
          pull-request-id (id-cache (:id pull-request))
          base-id (id-cache (identify-head base))
          head-id (id-cache (identify-head head))
          user-id (id-cache (:id user))]
      (let [parts [(head-to-tx id-cache base base-id)
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
        (vals (:tx (merge-txs parts)))))))
