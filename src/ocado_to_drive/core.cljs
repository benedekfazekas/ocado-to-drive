(ns ocado-to-drive.core
  (:require [clojure.string :as str]
            [ocado-to-drive.collections :as colls]
            [cljs-time.coerce :as tc]
            [cljs-time.format :as tf]))

;; TODOs
;; how to request priviliges for everything the script needs
;; somehow the bigger question is re. previous: how to install to arbitary google user
;; set time zone
;; how to externalise config

(def config
  {:ocado-receipts-search "from:(customerservices@ocado.com) subject:(Your Receipt) has:attachment"
   :gdrive-location       "ocado-receipts"
   :gmail-processed-label "ocado2drive/processed"})

(defn- has-attachment? [message]
  (not (empty? (.getAttachments message))))

(def ^:private in-filename-formatter (tf/formatter "yyyyMMdd-HH-mm"))

(defn- process-message [folder message]
  (let [attachment  (first (.getAttachments message))
        msg-date    (tf/unparse in-filename-formatter (tc/from-date (.getDate message)))]
    (.log js/Logger (str "processing attachment on msg at " msg-date " : " (.getName attachment) " saving..."))
    (.setName (.createFile folder attachment) (str "receipt-from-mail-" msg-date ".pdf"))
    (.log js/Logger "saving done.")))

(defn- get-or-create-label [label]
  (or (->> (.getUserLabels js/GmailApp)
           (filter #(= label (.getName %)))
           first)
      (.createLabel js/GmailApp label)))

(defn- folder-on-drive [folder-name]
  (let [folders (colls/iterator-seq (.getFoldersByName js/DriveApp folder-name))]
    (and (= 1 (count folders)) (first folders))))

(defn ^:export ocado-receipts-to-drive []
  (let [label-name      (:gmail-processed-label config)
        processed-label (get-or-create-label label-name)
        search-term     (str (:ocado-receipts-search config) " -label:" label-name)
        folder-name     (:gdrive-location config)
        folder          (folder-on-drive folder-name)]
    (.log js/Logger "starting a run...")
    (if folder
      (doseq [thread (.search js/GmailApp search-term)
              :let   [msg (first (filter has-attachment? (.getMessages thread)))]]
        (process-message folder msg)
        (.log js/Logger (str "thread[" (.getId thread) "] processed, marking the thread with processed label " (.getName processed-label)))
        (.addLabel thread processed-label))
      (.log js/Logger (str "no folder" folder-name " found")))
    (.log js/Logger "finished for now.")))
