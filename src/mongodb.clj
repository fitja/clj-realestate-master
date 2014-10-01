(ns mongodb
  (:require [noir.session :as session]
            [clj-time.format :as time-format]
            [clj-time.core :as time])
  (:use [somnium.congomongo]))

(def conn 
  (make-connection "abc_realestate"))

(set-connection! conn)

(defn- generate-id [collection]
  (:seq (fetch-and-modify :sequences {:_id collection} {:$inc {:seq 1}}
                          :return-new? true :upsert? true)))

(defn- insert-entity [collection values]
  (insert! collection (assoc values :_id (generate-id collection))))

(def parser-formatter (time-format/formatter "yyyy-MM-dd HH:mm:ss"))

(defn insert-user
  [name email username password]
  (insert-entity :users 
                  {:name name
                   :email email
                   :username username
                   :password password}))

(defn get-user-by-username [username] 
  (fetch-one :users :where {:username username}))

(defn get-all-users []
  (fetch :users))

(defn get-user-by-email [email]
  (fetch-one :users :where {:email email}))

(defn insert-user-realestate
  [realestate-name 
   realestate-size 
   realestate-location 
   realestate-price 
   realestate-description 
   realestate-image 
   date-added user-id]
  (insert-entity :user-realestates 
                  {:realestate-name realestate-name
                   :realestate-size realestate-size
                   :realestate-location realestate-location
                   :realestate-price (read-string realestate-price)
                   :realestate-description realestate-description
                   :realestate-image realestate-image
                   :date-added (time-format/unparse parser-formatter (time/now))
                   :user-id user-id}))

(defn db-delete-realestate [id]
  (destroy! :user-realestates {:_id id}))

(defn db-update-realestate
  [realestate-id 
   realestate-name 
   realestate-size 
   realestate-location 
   realestate-price 
   realestate-description 
   realestate-image 
   date-added 
   user-id]
  (do
    ( update! :user-realestates 
         {:_id realestate-id}
         {:$set 
          {:realestate-name realestate-name 
           :realestate-size realestate-size 
           :realestate-location realestate-location 
           :realestate-price (read-string realestate-price) 
           :realestate-description realestate-description 
           :realestate-image realestate-image 
           :date-added date-added 
           :user-id user-id}}))) 

(defn- next-seq [coll]
  (:seq (fetch-and-modify :sequences {:_id coll} {:$inc {:seq 1}}
                          :return-new? true :upsert? true)))

(defn get-all-realestates []
  (fetch :user-realestates :sort {:realestate-price -1}))

(defn get-latest-realestates []
  (fetch :user-realestates :sort {:date-added -1} :limit 3))

(defn get-top-cheapest-realestates []
  (fetch :user-realestates :sort {:realestate-price -1} :limit 3))

(defn get-realestate-by-id [id]
  (fetch :user-realestates :where {:_id id}))

(defn insert-new-realestate
  [realestate-name 
   realestate-size 
   realestate-location 
   realestate-price 
   realestate-description 
   realestate-image]
  (let [user (session/get :user)]
    (insert-entity :user-realestates
                   {:realestate-name realestate-name
                   :realestate-size realestate-size
                   :realestate-location realestate-location
                   :realestate-price (read-string realestate-price)
                   :realestate-description realestate-description
                   :realestate-image realestate-image
                   :date-added (time-format/unparse parser-formatter (time/now))
                   :user-id (:_id user)})))


