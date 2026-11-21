(def MIN_JAVA_VERSION "13")

(defproject pg-native "0.1.0-SNAPSHOT"

  :description
  "FIXME: write description"

  :url
  "http://example.com/FIXME"

  :license
  {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url "https://www.eclipse.org/legal/epl-2.0/"}

  #_#_
  :managed-dependencies
  [[org.clojure/clojure "1.11.1"]
   [com.github.seancorfield/honeysql "2.4.1078"]
   [org.postgresql/postgresql "42.2.18"]
   [criterium "0.4.6"]]

  :dependencies
  [[org.clojure/clojure "1.11.1"]]

  :pom-addition
  [:properties
   ["maven.compiler.source" ~MIN_JAVA_VERSION]
   ["maven.compiler.target" ~MIN_JAVA_VERSION]]

  :target-path "target/%s"

  :profiles
  {:test
   {:jvm-opts ["--enable-native-access=ALL-UNNAMED"
               "-Dorg.eclipse.jetty.LEVEL=OFF"
               "-Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog"]
    :dependencies
    [[com.github.seancorfield/honeysql "2.4.1078"]
     [org.postgresql/postgresql "42.2.18"]
     [criterium "0.4.6"]]}

   :uberjar
   {:aot :all
    :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
