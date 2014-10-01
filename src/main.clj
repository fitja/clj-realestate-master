(ns main
   (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use  [template :only [get-template]]
         [mongodb :only [get-all-realestates get-realestate-by-id get-top-cheapest-realestates db-delete-realestate db-update-realestate]]
         [hiccup.form :only [form-to hidden-field submit-button]]
         [login :only [login-page]]))

(defn logged-delete
  [realestate]
  [:div 
    (form-to [:delete "/"]
      [:div
       (hidden-field :realestate-id (realestate :_id))
       (submit-button {:class "button"} "Delete")])])

(defn logged-edit
  [realestate]
  [:div 
    (form-to [:post "/editrealestate"]
      [:div
       ;(hidden-field :id (realestate :_id))
       (hidden-field :realestate-id (realestate :_id))
       (hidden-field :realestate-name (realestate :realestate-name))
       (hidden-field :realestate-size (realestate :realestate-size))
       (hidden-field :realestate-location (realestate :realestate-location))
       (hidden-field :realestate-price (realestate :realestate-price))
       (hidden-field :realestate-description (realestate :realestate-description))
       (hidden-field :realestate-image (realestate :realestate-image))
       (submit-button {:class "button"} "Edit")])])

(defn show-one-realestate
  [realestate]
  [:div.realestateinfoai
   [:h2 (:realestate-name realestate)]
   [:div.realestateai
   [:ul
	   [:li (str "Price: "(:realestate-price realestate) " EUR")]
	   [:li (str "Location: "(:realestate-location realestate))]
	   [:li (str "Size: " (:realestate-size realestate) " m2")]]]
   [:img.imagedetail {:src (:realestate-image realestate)}]
    (clojure.string/replace (str ""(:realestate-description realestate)) "\n" "</br>")
    [:div 
    (form-to [:post "/realestate"]
      [:div
       ;(hidden-field :realestate-id (realestate :_id))
       (submit-button {:class "button"} "Back")])]
    (let [user (session/get :user)] 
     (if-not user () (logged-delete realestate))) 
    (let [user (session/get :user)] 
     (if-not user () (logged-edit realestate)))
    ]) 

(defn show-one-realestate-short
  [realestate]
  [:div.realestateshort
   [:h2 (:realestate-name realestate)]
   [:div.realestateais
   [:ul
	   [:li (str "Price: "(:realestate-price realestate) " EUR")]
	   [:li (str "Location: "(:realestate-location realestate))]
	   [:li (str "Size: " (:realestate-size realestate) " m2")]]]
   ;[:img.imageshort {:src (:realestate-image realestate)}]
   [:div.realestateaismore
    (form-to [:post "/"]
     [:div
      (hidden-field :realestate-id (realestate :_id))]
      [:div
       (submit-button {:class "button"} "More...")])
    ]
   ])
     
  
(defn show-all-realestates
  [realestate-id]
  [:div.realestateinfo
   (let [realestates (get-realestate-by-id (Integer/valueOf realestate-id))]
   (for [realestate realestates]
		(show-one-realestate realestate)))])  

(defn show-all-realestates-short
  []
  [:div.realestateinfo
   (let [realestates (get-all-realestates)]
   (for [realestate realestates]
		(show-one-realestate-short realestate)))])

(defn show-one-cheap-realestate
  [realestate]
  [:li
    [:h6.name (:realestate-name realestate)]
    ;[:img {:class "realestateimage" :src  (:realestate-image realestate)}]
    (form-to [:post "/"]
      (hidden-field :realestate-id (realestate :_id))
      (submit-button {:class "hitra" :type "image" :src (:realestate-image realestate)} ""))
    [:h6 (str (:realestate-price realestate) " EUR")]])

  
(defn get-cheapest-realestates
  []
  [:ul
   (let [realestates (get-top-cheapest-realestates)]
   (for [realestate realestates]
		(show-one-cheap-realestate realestate)))])

(defn delete-realestate 
  [id]
  (do
    (db-delete-realestate id)
    (session/flash-put! :message-info "Successfully deleted.")
    (response/redirect "/")))

(defn edit-realestate 
  [realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]
  (do
    (session/flash-put! :message-info "Click SAVE when finished with changing.")
    (session/flash-put! :realestate-id realestate-id)
    (session/flash-put! :realestate-name realestate-name)
    (session/flash-put! :realestate-size realestate-size)
    (session/flash-put! :realestate-location realestate-location)
    (session/flash-put! :realestate-price realestate-price)
    (session/flash-put! :realestate-description realestate-description)
    (session/flash-put! :realestate-image realestate-image)
    (response/redirect "/newrealestate")))

(defn update-realestate 
  [realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]
  (do
    (db-update-realestate realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image "2014-09-02" (let [user (session/get :user)] (:_id user))) 
    (session/flash-put! :message-info "Record updated.")   
    (response/redirect "/")))

(defn main-page 
  []
  (get-template "ABC Realestate" 
   [:div.content
    [:div#cheapest
           [:h1 "Hit realestates"]
           (get-cheapest-realestates)]
    [:p.message (session/flash-get :message-info)]
    (show-all-realestates-short)]))

(defn goto-main-page
  []
  (do
    (main-page)
    (response/redirect "/"))) 

(defn goto-login-page
  []
  (do
    (login-page)
    (response/redirect "/login"))) 

(defn show-realestate-detail
  [realestate-id]
  (do
    (session/put! :realestate-id realestate-id)
    (response/redirect "/realestate"))
  ) 

(defn realestate-detail-page 
  [realestate-id]
  (get-template "ABC Realestate" 
   [:div.content
    [:p.message (session/flash-get :message-info)]
    (show-all-realestates realestate-id)]))


