(defproject com.github.piotr-yuxuan/polygam "0.1.0"
  :description "Experiment with core.logic"
  :url "https://github.com/piotr-yuxuan/slava"
  :license {:name "European Union Public License 1.2 or later"
            :url "https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12"
            :distribution :repo}
  :scm {:name "git"
        :url "https://github.com/piotr-yuxuan/polygam"}
  :pom-addition [:developers [:developer
                              [:name "胡雨軒 Петр"]
                              [:url "https://github.com/piotr-yuxuan"]]]
  :dependencies [[org.clojure/core.logic "1.0.1"]]
  :main ^:skip-aot polygam.core
  :profiles {:github {:github/topics ["clojure" "core-logic" "logic-programming"]
                      :github/private? false}
             :provided {:dependencies [[org.clojure/clojure "1.12.0-alpha2"]]}
             :dev {:global-vars {*warn-on-reflection* true}}
             :jar {:jvm-opts ["-Dclojure.compiler.disable-locals-clearing=false"
                              "-Dclojure.compiler.direct-linking=true"]}}
  :deploy-repositories [["clojars" {:sign-releases false
                                    :url "https://clojars.org/repo"
                                    :username :env/WALTER_CLOJARS_USERNAME
                                    :password :env/WALTER_CLOJARS_PASSWORD}]
                        ["github" {:sign-releases false
                                   :url "https://maven.pkg.github.com/piotr-yuxuan/polygam"
                                   :username :env/GITHUB_ACTOR
                                   :password :env/WALTER_GITHUB_PASSWORD}]])
