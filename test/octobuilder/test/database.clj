(ns octobuilder.test.database
  (:use clojure.test
        conjure.core
        octobuilder.database)
  (:require [datomic.api :as d]))


(def ^:dynamic *state*)


(defn tempid-stub [part & rest]
  (dosync
   (let [value (inc @*state*)]
     (ref-set *state* value)
     value)))


(deftest test-create-user-tx
  (let [users {:user1 {:name "name" :token "token"}
               :user2 {:name "user" :token "foo"}}
        github-user-ids {:user1 1 :user2 2}]
    (testing "allocate-user-db-ids"
      (binding [*state* (ref 0)]
        (stubbing [d/tempid tempid-stub
                   ;; fixme: Looks ... wrong
                   keys [:user1 :user2]]
                  (is (= (allocate-user-db-ids users) github-user-ids)))))
    (testing "make-github-user-tx"
      (let [github-id 0]
        (is (= (sort-by :db/id (reduce (make-github-user-tx github-id github-user-ids users)
                                       [] (keys users)))
               [{:db/id 1
                 :github/access-token "token"
                 :user/username "name"
                 :user/service 0}
                {:db/id 2
                 :github/access-token "foo"
                 :user/username "user"
                 :user/service 0}]))))))


(deftest test-bootstrap-contents
  (let [minimum-output [{:db/id 1
                         :service/name "github"}
                        {:db/id 2
                         :service/name "jenkins"}]
        user-part {:db/id 3
                   :github/access-token "token"
                   :user/username "name"
                   :user/service 1}
        project-part {:db/id 4
                      :project/owner "me!"
                      :project/name "project"
                      :project/user 3}
        one-user-output (cons user-part minimum-output)
        one-project-output (concat [user-part project-part] minimum-output)]
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [users {}
                      projects []]
                  (testing "no users or projects"
                    (is (= (bootstrap-contents users projects)
                           minimum-output))))))
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [users {:user {:name "name" :token "token"}}
                      projects []]
                  (testing "one user, no projects"
                    (is (= (bootstrap-contents users projects)
                           one-user-output))))))
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [users {:user {:name "name" :token "token"}}
                      projects [{:owner "me!"
                                 :project "project"
                                 :user :user
                                 :jenkins-job nil}]]
                  (testing "one user, one project"
                    (is (= (bootstrap-contents users projects)
                           one-project-output))))))))
