(ns ocado-to-drive.core
  (:require [clojure.string :as str]))

;; TODOs
;; how to request priviliges for everything the script needs
;; somehow the bigger question is re. previous: how to install to arbitary google user
;; set time zone
;; figure out iter-seq for cljs

(defn ^:export hello-world []
  (.log js/Logger "Hello, ocado2drive here!"))

(defn ^:export list-gmail-tags []
  (.log
   js/Logger
   (str
    "Hello "
    (.. js/Session (getActiveUser) (getEmail))
    ", your gmail labels: "
    (str/join ", " (map #(.getName %) (.getUserLabels js/GmailApp))))))

(defn ^:export list-gdrive-contents []
  (let [folders (.getFoldersByName js/DriveApp "story")]
    (if-let [folder (and (.hasNext folders) (.next folders))]
      (let [files (.getFiles folder)]
        (.log js/Logger (str "files in: " (.getName folder)))
        (while (.hasNext files)
          (.log js/Logger (str "  file: " (.getName (.next files))))))
      "folder not found")))
