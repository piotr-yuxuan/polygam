(defproject polygam "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :github/private? false
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License v3"
            :url "https://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.logic "0.8.10"]]
  :main ^:skip-aot polygam.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
