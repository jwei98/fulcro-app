(defproject app "0.1.0-SNAPSHOT"
  :description "My Cool Project"
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.7.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [fulcrologic/fulcro "2.6.0-RC5"]

                 ; Only required if you use server
                 [http-kit "2.2.0"]
                 [ring/ring-core "1.6.3" :exclusions [commons-codec]]
                 [bk/ring-gzip "0.2.1"]
                 [bidi "2.1.3"]

                 ; only required if you want to use this for tests
                 [fulcrologic/fulcro-spec "2.1.0-1" :scope "test" :exclusions [fulcrologic/fulcro]]]

  :uberjar-name "app.jar"

  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :clean-targets ^{:protect false} ["target" "resources/public/js" "resources/private"]

  ; Notes  on production build:
  ; To limit possible dev config interference with production builds
  ; Use `lein with-profile production cljsbuild once production`
  :cljsbuild {:builds [{:id           "production"
                        :source-paths ["src/main"]
                        :jar          true
                        :compiler     {:asset-path    "js/prod"
                                       :main          app.client-main
                                       :optimizations :advanced
                                       :source-map    "resources/public/js/app.js.map"
                                       :output-dir    "resources/public/js/prod"
                                       :output-to     "resources/public/js/app.js"}}]}

  :profiles {:uberjar    {:main           app.server-main
                          :aot            :all
                          :jar-exclusions [#"public/js/prod" #"com/google.*js$"]
                          :prep-tasks     ["clean" ["clean"]
                                           "compile" ["with-profile" "production" "cljsbuild" "once" "production"]]}
             :production {}
             :dev        {:source-paths ["src/dev" "src/main" "src/test" "src/cards"]

                          :jvm-opts     ["-XX:-OmitStackTraceInFastThrow" "-client" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"
                                         "-Xmx1g" "-XX:+UseConcMarkSweepGC" "-XX:+CMSClassUnloadingEnabled" "-Xverify:none"]

                          :doo          {:build "automated-tests"
                                         :paths {:karma "node_modules/karma/bin/karma"}}

                          :figwheel     {:css-dirs ["resources/public/css"]}

                          :test-refresh {:report       fulcro-spec.reporters.terminal/fulcro-report
                                         :with-repl    true
                                         :changes-only true}

                          :cljsbuild    {:builds
                                         [{:id           "dev"
                                           :figwheel     {:on-jsload "cljs.user/mount"}
                                           :source-paths ["src/dev" "src/main"]
                                           :compiler     {:asset-path           "js/dev"
                                                          :main                 cljs.user
                                                          :optimizations        :none
                                                          :output-dir           "resources/public/js/dev"
                                                          :output-to            "resources/public/js/app.js"
                                                          :preloads             [devtools.preload fulcro.inspect.preload]
                                                          :source-map-timestamp true}}
                                          {:id           "i18n" ;for gettext string extraction
                                           :source-paths ["src/main"]
                                           :compiler     {:asset-path    "i18n"
                                                          :main          app.client-main
                                                          :optimizations :whitespace
                                                          :output-dir    "target/i18n"
                                                          :output-to     "target/i18n.js"}}
                                          {:id           "test"
                                           :source-paths ["src/test" "src/main"]
                                           :figwheel     {:on-jsload "app.client-test-main/client-tests"}
                                           :compiler     {:asset-path    "js/test"
                                                          :main          app.client-test-main
                                                          :optimizations :none
                                                          :output-dir    "resources/public/js/test"
                                                          :output-to     "resources/public/js/test/test.js"
                                                          :preloads      [devtools.preload]}}
                                          {:id           "automated-tests"
                                           :source-paths ["src/test" "src/main"]
                                           :compiler     {:asset-path    "js/ci"
                                                          :main          app.CI-runner
                                                          :optimizations :none
                                                          :output-dir    "resources/private/js/ci"
                                                          :output-to     "resources/private/js/unit-tests.js"}}
                                          {:id           "cards"
                                           :figwheel     {:devcards true}
                                           :source-paths ["src/main" "src/cards"]
                                           :compiler     {:asset-path           "js/cards"
                                                          :main                 app.cards
                                                          :optimizations        :none
                                                          :output-dir           "resources/public/js/cards"
                                                          :output-to            "resources/public/js/cards.js"
                                                          :preloads             [devtools.preload]
                                                          :source-map-timestamp true}}]}

                          :plugins      [[lein-cljsbuild "1.1.7"]
                                         [lein-doo "0.1.10"]
                                         [com.jakemccrary/lein-test-refresh "0.21.1"]]

                          :dependencies [[binaryage/devtools "0.9.10"]
                                         [fulcrologic/fulcro-inspect "2.2.1" :exclusions [fulcrologic/fulcro-css]]
                                         [org.clojure/tools.namespace "0.3.0-alpha4"]
                                         [org.clojure/tools.nrepl "0.2.13"]
                                         [com.cemerick/piggieback "0.2.2"]
                                         [lein-doo "0.1.10" :scope "test"]
                                         [figwheel-sidecar "0.5.16" :exclusions [org.clojure/tools.reader]]
                                         [devcards "0.2.5" :exclusions [cljsjs/react cljsjs/react-dom]]]
                          :repl-options {:init-ns          user
                                         :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
