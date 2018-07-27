(defproject ocado-to-drive "0.1.0-SNAPSHOT"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :clean-targets [:target-path :compile-path "export"]
  :cljsbuild {:builds
              {:main {:source-paths ["src"]
                      :compiler {:main ocado-to-drive.core
                                 :optimizations :advanced
                                 :output-to "export/OcadoToDrive.gs"
                                 :output-dir "target"
                                 :pretty-print false
                                 :externs ["resources/gas.ext.js"]
                                 :foreign-libs [{:file "src/entry_points.js"
                                                 :provides ["ocado-to-drive.entry-points"]}]}}}})
