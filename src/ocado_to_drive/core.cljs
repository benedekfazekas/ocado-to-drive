(ns ocado-to-drive.core
  (:require [clojure.string :as str]
            [ocado-to-drive.collections :as colls]))

;; TODOs
;; how to request priviliges for everything the script needs
;; somehow the bigger question is re. previous: how to install to arbitary google user
;; set time zone

(defn ^:export hello-world []
  (.log js/Logger "Hello, ocado2drive here!"))

(defn ^:export list-gmail-tags []
  (->> (.getUserLabels js/GmailApp)
       (map #(.getName %))
       (str/join ", ")
       (str
        "Hello "
        (.. js/Session (getActiveUser) (getEmail))
        ", your gmail labels: ")
       (.log js/Logger)))

(defn ^:export list-gdrive-contents []
  (let [folders (colls/iterator-seq (.getFoldersByName js/DriveApp "story"))]
    (if-let [folder (and (= 1 (count folders)) (first folders))]
      (do
        (.log js/Logger (str "files in: " (.getName folder)))
        (->> (.getFiles folder)
             colls/iterator-seq
             (map #(.getName %))
             (str/join ", ")
             (.log js/Logger)))
      (.log js/Logger (str "folder not found or too many folders" (str/join ", " (map #(.getName %) folders)))))))
