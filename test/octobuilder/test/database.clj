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
                      :project/user "him"}
        one-user-output (cons user-part minimum-output)
        one-project-output (concat [user-part project-part] minimum-output)]
    (let [state (ref 0)]
      (binding [*state* state]
        (stubbing [d/tempid tempid-stub]
                  (let [users {}
                        projects []]
                    (testing "no users or projects"
                      (is (= (bootstrap-contents users projects)
                             minimum-output)))))))
    (let [state (ref 0)]
      (binding [*state* state]
        (stubbing [d/tempid tempid-stub]
                  (let [users {"name" "token"}
                        projects []]
                    (testing "one user, no projects"
                      (is (= (bootstrap-contents users projects)
                             one-user-output)))))))
    (let [state (ref 0)]
      (binding [*state* state]
        (stubbing [d/tempid tempid-stub]
                  (let [users {"name" "token"}
                        projects [{:owner "me!"
                                   :project "project"
                                   :user "him"}]]
                    (testing "one user, no projects"
                      (is (= (bootstrap-contents users projects)
                             one-user-output)))))))))
