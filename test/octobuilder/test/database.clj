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
    (testing "allocate-db-ids"
      (binding [*state* (ref 0)]
        (stubbing [d/tempid tempid-stub
                   ;; fixme: Looks ... wrong
                   keys [:user1 :user2]]
                  (is (= (allocate-db-ids users) github-user-ids)))))
    (testing "make-user-tx"
      (let [github-id 0]
        (is (= (sort-by :db/id (reduce (make-user-tx github-id github-user-ids users)
                                       [] (keys users)))
               [{:db/id 1
                 :github/access-token "token"
                 :user/username "name"
                 :user/service 0}
                {:db/id 2
                 :github/access-token "foo"
                 :user/username "user"
                 :user/service 0}])))
      (let [jenkins-id 2
            user-ids {:user1 4}
            users {:user1 {:name "jenkins" :password "jkns"}}]
        (is (= (sort-by :db/id (reduce (make-user-tx jenkins-id user-ids users)
                                       [] (keys users)))
               [{:db/id 4
                 :user/username "jenkins"
                 :jenkins/password "jkns"
                 :user/service 2}]))))))


(deftest test-bootstrap-contents
  (let [minimum-output (list {:db/id 1
                              :service/type :octobuilder.service/github}
                             {:db/id 2
                              :service/type :octobuilder.service/jenkins})
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
                    (is (= (bootstrap-contents users projects {} {})
                           minimum-output))))))
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [users {:user {:name "name" :token "token"}}
                      projects []]
                  (testing "one user, no projects"
                    (is (= (bootstrap-contents users projects {} {})
                           one-user-output))))))
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [users {:user {:name "name" :token "token"}}
                      projects [{:owner "me!"
                                 :project "project"
                                 :user :user
                                 :jenkins-job nil}]]
                  (testing "one user, one project"
                    (is (= (bootstrap-contents users projects {} {})
                           one-project-output))))))
    (binding [*state* (ref 0)]
      (stubbing [d/tempid tempid-stub]
                (let [jenkins-users {:jkns {:name "jkns" :password "pwd"}}
                      jenkins {:job {:name "job-name"
                                     :location "url"
                                     :user :jkns}}
                      users {:user {:name "name" :token "token"}}
                      projects [{:owner "me!"
                                 :project "project"
                                 :user :user
                                 :jenkins-job :job}]
                      jenkins-user-part {:db/id 4
                                         :jenkins/password "pwd"
                                         :user/username "jkns"
                                         :user/service 2}
                      jenkins-part {:db/id 5
                                    :jenkins.job/name "job-name"
                                    :jenkins.job/location "url"
                                    :jenkins.job/user 4}
                      expected-output (concat [user-part
                                               jenkins-user-part
                                               jenkins-part
                                               (assoc project-part :db/id (+ 2 (:db/id project-part)))]
                                              minimum-output)]
                  (testing "one user, one project, with jenkins"
                    (is (= (bootstrap-contents users projects jenkins-users jenkins)
                           expected-output))))))))
