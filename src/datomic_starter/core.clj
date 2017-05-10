(ns datomic-starter.core
  (:require [clojure.core.async :refer [<!!]]
            [datomic.client :as client])
  (:gen-class))

(def conn
  (<!! (client/connect
        {:db-name "hello"
         :account-id client/PRO_ACCOUNT
         :secret "mysecret"
         :region "none"
         :endpoint "localhost:8998"
         :service "peer-server"
         :access-key "myaccesskey"})))

(def title {:db/ident :movie/title
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/doc "The title of the movie"})

(def genre {:db/ident :movie/genre
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/doc "The genre of the movie"})

(def year {:db/ident :movie/release-year
           :db/valueType :db.type/long
           :db/cardinality :db.cardinality/one
           :db/doc "The year the movie was released in theaters"})

(def movie-schema [title genre year])

(comment
  (<!! (client/transact conn {:tx-data movie-schema})))

(def first-movies [{:movie/title "The Goonies"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Commando"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Repo Man"
                    :movie/genre "punk dystopia"
                    :movie/release-year 1984}])

(comment
  (<!! (client/transact conn {:tx-data first-movies})))

(def db (client/db conn))

(def all-movies-q '[:find ?e
                    :where [?e :movie/title]])
(comment
  (<!! (client/q conn {:query all-movies-q :args [db]})))

(def all-titles-q '[:find ?movie-title
                    :where [_ :movie/title ?movie-title]])
(comment
  (<!! (client/q conn {:query all-titles-q :args [db]})))

(def titles-from-1985 '[:find ?title
                        :where
                        [?e :movie/title ?title]
                        [?e :movie/release-year 1985]])
(comment
  (<!! (client/q conn {:query titles-from-1985 :args [db]})))

(def all-data-from-1985 '[:find ?title ?year ?genre
                          :where
                          [?e :movie/title ?title]
                          [?e :movie/release-year ?year]
                          [?e :movie/genre ?genre]
                          [?e :movie/release-year 1985]])
(comment
  (<!! (client/q conn {:query all-data-from-1985 :args [db]})))


;; -----------------------------------------------------------------------------
;; Transactions

(def commando-id
  (ffirst (<!! (client/q conn {:query '[:find ?e
                                        :where [?e :movie/title "Commando"]]
                               :args [db]}))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
