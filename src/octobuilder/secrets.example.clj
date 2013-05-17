(ns octobuilder.secrets)


(def client-id "00000000000000000000")


(def client-secret "0000000000000000000000000000000000000000")


(def jenkins-users {:jenkins-user {:name "jenkins-user"
                                   :password "jenkins-password"}})
(def jenkins-jobs {:jenkins-job {:name "Job-Name"
                                 :location "https://jenkins.host.invalid/job/Job-Name/"
                                 :user :jenkins-user}})


(def github-users {:github-user {:name "github-user-name"
                                 :token "github-token"}})
(def projects [{:owner "owner"
                :project "Project"
                :user :github-user
                :jenkins-job :jenkins-job}])
