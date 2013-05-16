(ns octobuilder.test.database
  (:use clojure.test
        conjure.core
        octobuilder.database)
  (:require [datomic.api :as d]))

(def an-organization {:following_url       "https://api.github.com/users/company/following{/other_user}"
                      :gists_url       "https://api.github.com/users/company/gists{/gist_id}"
                      :starred_url       "https://api.github.com/users/company/starred{/owner}{/repo}"
                      :followers_url "https://api.github.com/users/company/followers"
                      :gravatar_id "1234567890abcdef1234567890abcdef"
                      :avatar_url       "https://secure.gravatar.com/avatar/1234567890abcdef1234567890abcdef?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png"
                      :html_url "https://github.com/company"
                      :received_events_url       "https://api.github.com/users/company/received_events"
                      :login "company"
                      :url "https://api.github.com/users/company"
                      :organizations_url "https://api.github.com/users/company/orgs"
                      :type "Organization"
                      :events_url       "https://api.github.com/users/company/events{/privacy}"
                      :repos_url "https://api.github.com/users/company/repos"
                      :id 123456
                      :subscriptions_url       "https://api.github.com/users/company/subscriptions"})

(def a-repo {:archive_url       "https://api.github.com/repos/company/Project/{archive_format}{/ref}"
             :has_issues true
             :notifications_url       "https://api.github.com/repos/company/Project/notifications{?since=all|participating}"
             :forks_count 4
             :git_tags_url       "https://api.github.com/repos/company/Project/git/tags{/sha}"
             :issue_comment_url       "https://api.github.com/repos/company/Project/issues/comments/{number}"
             :contributors_url       "https://api.github.com/repos/company/Project/contributors"
             :compare_url       "https://api.github.com/repos/company/Project/compare/{base}...{head}"
             :fork false
             :labels_url       "https://api.github.com/repos/company/Project/labels{/name}"
             :collaborators_url       "https://api.github.com/repos/company/Project/collaborators{/collaborator}"
             :pushed_at "2013-04-11T00:12:34Z"
             :git_commits_url       "https://api.github.com/repos/company/Project/git/commits{/sha}"
             :trees_url       "https://api.github.com/repos/company/Project/git/trees{/sha}"
             :name "Project"
             :default_branch "master"
             :clone_url "https://github.com/company/Project.git"
             :hooks_url       "https://api.github.com/repos/company/Project/hooks"
             :watchers 10
             :updated_at "2013-05-13T15:20:18Z"
             :assignees_url       "https://api.github.com/repos/company/Project/assignees{/user}"
             :has_wiki true
             :stargazers_url       "https://api.github.com/repos/company/Project/stargazers"
             :html_url "https://github.com/company/Project"
             :teams_url       "https://api.github.com/repos/company/Project/teams"
             :git_refs_url       "https://api.github.com/repos/company/Project/git/refs{/sha}"
             :milestones_url       "https://api.github.com/repos/company/Project/milestones{/number}"
             :owner an-organization
             :language "Python"
             :merges_url       "https://api.github.com/repos/company/Project/merges"
             :size 45877
             :created_at "2012-06-29T16:41:59Z"
             :branches_url       "https://api.github.com/repos/company/Project/branches{/branch}"
             :issues_url       "https://api.github.com/repos/company/Project/issues{/number}"
             :private true
             :homepage nil
             :git_url "git://github.com/company/Project.git"
             :mirror_url nil
             :url "https://api.github.com/repos/company/Project"
             :issue_events_url       "https://api.github.com/repos/company/Project/issues/events{/number}"
             :subscribers_url       "https://api.github.com/repos/company/Project/subscribers"
             :has_downloads true
             :full_name "company/Project"
             :watchers_count 10
             :statuses_url       "https://api.github.com/repos/company/Project/statuses/{sha}"
             :open_issues_count 483
             :master_branch "master"
             :ssh_url "git@github.com:company/Project.git"
             :languages_url       "https://api.github.com/repos/company/Project/languages"
             :commits_url       "https://api.github.com/repos/company/Project/commits{/sha}"
             :forks_url       "https://api.github.com/repos/company/Project/forks"
             :subscription_url       "https://api.github.com/repos/company/Project/subscription"
             :contents_url       "https://api.github.com/repos/company/Project/contents/{+path}"
             :events_url       "https://api.github.com/repos/company/Project/events"
             :tags_url       "https://api.github.com/repos/company/Project/tags"
             :open_issues 483
             :id 1234567
             :forks 4
             :svn_url "https://github.com/company/Project"
             :downloads_url       "https://api.github.com/repos/company/Project/downloads"
             :blobs_url       "https://api.github.com/repos/company/Project/git/blobs{/sha}"
             :description "The FEI Black Adder project"
             :pulls_url       "https://api.github.com/repos/company/Project/pulls{/number}"
             :comments_url       "https://api.github.com/repos/company/Project/comments{/number}"
             :keys_url       "https://api.github.com/repos/company/Project/keys{/key_id}"})

(def a-head {:label "company:branch_name"
             :ref "branch_name"
             :sha "0000000000000000000000000000000000000000"
             :user      an-organization
             :repo      a-repo})

(def a-real-user {:following_url      "https://api.github.com/users/sjagoe/following{/other_user}"
                  :gists_url "https://api.github.com/users/sjagoe/gists{/gist_id}"
                  :starred_url      "https://api.github.com/users/sjagoe/starred{/owner}{/repo}"
                  :followers_url "https://api.github.com/users/sjagoe/followers"
                  :gravatar_id "00000000000000000000000000000000"
                  :avatar_url      "https://secure.gravatar.com/avatar/00000000000000000000000000000000?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
                  :html_url "https://github.com/sjagoe"
                  :received_events_url      "https://api.github.com/users/sjagoe/received_events"
                  :login "sjagoe"
                  :url "https://api.github.com/users/sjagoe"
                  :organizations_url "https://api.github.com/users/sjagoe/orgs"
                  :type "User"
                  :events_url      "https://api.github.com/users/sjagoe/events{/privacy}"
                  :repos_url "https://api.github.com/users/sjagoe/repos"
                  :id 12345678
                  :subscriptions_url      "https://api.github.com/users/sjagoe/subscriptions"})

(def a-pull-request {:review_comments_url     "https://github.com/company/Project/pull/1234/comments"
                     :review_comment_url     "/repos/company/Project/pulls/comments/{number}"
                     :state "open"
                     :patch_url     "https://github.com/company/Project/pull/1234.patch"
                     :merge_commit_sha nil
                     :head a-head
                     :updated_at "2013-05-13T15:20:17Z"
                     :issue_url     "https://github.com/company/Project/issues/1234"
                     :diff_url     "https://github.com/company/Project/pull/1234.diff"
                     :closed_at     nil
                     :html_url "https://github.com/company/Project/pull/1234"
                     :title "Some Title"
                     :created_at "2013-05-13T15:20:17Z"
                     :url     "https://api.github.com/repos/company/Project/pulls/1234"
                     :base     a-repo
                     :_links     {:self      {:href       "https://api.github.com/repos/company/Project/pulls/1234"}
                                  :html      {:href "https://github.com/company/Project/pull/1234"}
                                  :issue      {:href       "https://api.github.com/repos/company/Project/issues/1234"}
                                  :comments      {:href       "https:// api.github.com/repos/company/Project/issues/1234/comments"}
                                  :review_comments      {:href       "https://api.github.com/repos/company/Project/pulls/1234/comments"}}
                     :user     a-real-user
                     :commits_url     "https://github.com/company/Project/pull/1234/commits"
                     :number 1234
                     :body     "Some PR Body"
                     :assignee nil
                     :id 123456789
                     :merged_at nil
                     :milestone nil
                     :comments_url     "https://api.github.com/repos/company/Project/issues/1234/comments"})


(deftest test-pull-request-to-tx
  (let [make-db-id #(keyword (str "id-" %))
        organization-id (make-db-id (:id an-organization))
        user-id (make-db-id (:id a-real-user))
        repo-id (make-db-id (:id a-repo))
        head-id (make-db-id (identify-head a-head))
        pull-request-id (make-db-id (:id a-pull-request))]
    (let [expected-organization-tx {:db/id organization-id
                                    :github.general/id (:id an-organization)
                                    :github.user/type :github.usertype/organization
                                    :github.user/login (:login an-organization)}
          expected-user-tx {:db/id user-id
                            :github.general/id (:id a-real-user)
                            :github.user/type :github.usertype/user
                            :github.user/login (:login a-real-user)}
          expected-repo-tx {:db/id repo-id
                            :github.general/id (:id a-repo)
                            :github.repository/name (:name a-repo)
                            :github.repository/full-name (:full_name a-repo)
                            :github.repository/owner organization-id}
          expected-head-tx {:db/id head-id
                            :github.head/label (:label a-head)
                            :github.head/ref (:ref a-head)
                            :github.head/sha (:sha a-head)
                            :github.head/user organization-id
                            :github.head/repository repo-id}
          expected-pr-tx {:db/id pull-request-id
                          :github.general/id (:id a-pull-request)
                          :github.pullrequest/state :github.pullrequest/open
                          :github.pullrequest/html-url (:html_url a-pull-request)
                          :github.pullrequest/number (:number a-pull-request)
                          :github.pullrequest/user user-id
                          :github.pullrequest/base repo-id
                          :github.pullrequest/head head-id}]
      (stubbing [get-existing-entity-id make-db-id]
                (testing "user-to-tx"
                  (let [result (user-to-tx {} an-organization organization-id)]
                    (is (= result
                           {:id-cache {}
                            :tx {organization-id expected-organization-tx}}))
                    (verify-call-times-for get-existing-entity-id 0)))
                (testing "repo-to-tx"
                  (let [result (repo-to-tx {} a-repo repo-id)]
                    (is (= result
                           {:id-cache {(:id an-organization) organization-id}
                            :tx {organization-id expected-organization-tx
                                 repo-id expected-repo-tx}}))
                    (verify-call-times-for get-existing-entity-id 1)))
                (testing "head-to-tx"
                  (let [result (head-to-tx {} a-head head-id)]
                    (testing ":id-cache"
                      (is (= (:id-cache result)
                             {(:id an-organization) organization-id
                              (:id a-repo) repo-id})))
                    (testing ":tx"
                      (is (= (:tx result)
                             {organization-id expected-organization-tx
                              repo-id expected-repo-tx
                              head-id expected-head-tx})))))
                (testing "pull-request-to-tx"
                  (let [result (pull-request-to-tx a-pull-request)]
                    (testing ":id-cache"
                      (is (= (:id-cache result)
                             {(:id an-organization) organization-id
                              (:id a-real-user) user-id
                              (:id a-repo) repo-id
                              (identify-head a-head) head-id
                              (:id a-pull-request) pull-request-id})))
                    (testing ":tx"
                      (is (= (:tx result)
                             {organization-id expected-organization-tx
                              user-id expected-user-tx
                              repo-id expected-repo-tx
                              head-id expected-head-tx
                              pull-request-id expected-pr-tx})))))))))


(deftest test-merge-txs
  (testing "merge-txs"
    (let [first-input {:id-cache {:a 1} :tx {:b 2}}
          second-input {:id-cache {:c 3} :tx {:d 4}}
          third-input {:id-cache {:e 5} :tx {:f 6}}]
      (testing "one input"
        (is (= (merge-txs [first-input])
               first-input)))
      (testing "two inputs"
        (is (= (merge-txs [first-input second-input])
               {:id-cache {:a 1 :c 3}
                :tx {:b 2 :d 4}})))
      (testing "three inputs"
        (is (= (merge-txs [first-input second-input third-input])
               {:id-cache {:a 1 :c 3 :e 5}
                :tx {:b 2 :d 4 :f 6}}))))))
