(use 'clojure.repl)
(use 'clojure.pprint)

(require '[tentacles.pulls :as pulls])
(require '[octobuilder.database :as database] :reload)
(require '[octobuilder.secrets :as secrets] :reload)
(require '[octobuilder.github :as github] :reload)

(database/bootstrap database/schema
                    secrets/github-users
                    secrets/projects
                    secrets/jenkins-users
                    secrets/jenkins-jobs)
