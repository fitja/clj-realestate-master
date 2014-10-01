(ns realestate
  (:require [noir.session :as session]
            [noir.validation :as validation]
            [ring.util.response :as response])
  (:use [template :only [get-template]]
        [hiccup.form :only [form-to label hidden-field text-field text-area submit-button]]
        [mongodb :only [insert-new-realestate db-update-realestate]]))

(defn prepare-error-messages
  [realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]
  (do
    (if (> 5 (.length realestate-name)) (session/flash-put! :errrename "*Name must be at least 5 character long.") ())
    (if (> 1 (.length realestate-size)) (session/flash-put! :errresize "*Size must be at least 1 character long.") ())
    (if (not (validation/valid-number? realestate-size)) (session/flash-put! :errresize "*Size must be numeric value.") ())
    (if (> 2 (.length realestate-location)) (session/flash-put! :errreloc "*Location must be at least 2 character long.") ())
    (if (> 1 (.length realestate-price)) (session/flash-put! :errreprice "*Price must be at least 1 character long.") ())
    (if (not (validation/valid-number? realestate-price)) (session/flash-put! :errreprice "*Price must be numeric value.") ())
    (if (> 5 (.length realestate-description)) (session/flash-put! :errredesc "*Description must be at least 5 character long.") ())
    (if (> 2 (.length realestate-image)) (session/flash-put! :errreimg "*Image path must be at least 2 character long.") ())))

(defn check-realestate-data
  [realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]
  (cond
    (> 5 (.length realestate-name)) false
    (> 1 (.length realestate-size)) false
    (> 2 (.length realestate-location)) false
    (> 1 (.length realestate-price)) false
    (> 5 (.length realestate-description)) false
    (> 5 (.length realestate-image)) false
    :else true))

(defn add-new-realestate
  [realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]
  (let [valid-entry (check-realestate-data realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image)]
    (if (= true valid-entry)
      (do 
        (if (empty? (session/flash-get :realestate-id))
          (do
            (insert-new-realestate realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image)
            (response/redirect "/"))
          (do
            (db-update-realestate (session/flash-get :realestate-id) realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image)
            (response/redirect "/"))))        
      (do
        (prepare-error-messages  realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image)
        (session/flash-put! :realestate-error "Error validating data.")
        (session/flash-put! :realestate-name realestate-name)
        (session/flash-put! :realestate-size realestate-size)
        (session/flash-put! :realestate-location realestate-location)
        (session/flash-put! :realestate-price realestate-price)
        (session/flash-put! :realestate-description realestate-description)
        (session/flash-put! :realestate-image realestate-image)
        (response/redirect "/newrealestate")))))
      

   
(defn realestate-page
  []
  (get-template "realestate page"
   [:div.content
    [:p.realestatetitle "Enter  information about new realestate!"]
     [:p.realestateerror (session/flash-get :realestate-error)]
     [:p.message (session/flash-get :message-info)]
    (form-to [:post (if (nil? (session/flash-get :realestate-id)) "/newrealestate" "/updaterealestate")]
             [:div.newrealestateform
              [:div
              (hidden-field :realestate-id (session/flash-get :realestate-id))]
              [:div
              (label {:class "newrealestate"} :realestate-name "Realestate name")
               (text-field :realestate-name (session/flash-get :realestate-name))
               (label {:class "errnewrealestate"} :name (session/flash-get :errrename))]
              [:div
              (label {:class "newrealestate"} :realestate-size "Realestate size")
               (text-field :realestate-size (session/flash-get :realestate-size))
               (label {:class "errnewrealestate"} :name (session/flash-get :errresize))]
              [:div
              (label {:class "newrealestate"} :realestate-location "Realestate location")
               (text-field :realestate-location (session/flash-get :realestate-location))
               (label {:class "errnewrealestate"} :name (session/flash-get :errreloc))]
              [:div
               (label {:class "newrealestate"} :realestate-price "Price")
                (text-field :realestate-price (session/flash-get :realestate-price))
                (label {:class "errnewrealestate"} :name (session/flash-get :errreprice))]
              [:div
               (label {:class "newrealestate"} :realestate-image "Path to realestate image")
                (text-field :realestate-image (session/flash-get :realestate-image))
                (label {:class "errnewrealestate"} :name (session/flash-get :errreimg))]
              [:div
               (label {:class "newrealestate desc"} :realestate-description "Description")
                (text-area {:class "textarea"} :realestate-description (session/flash-get :realestate-description))
                (label {:class "errnewrealestate"} :name (session/flash-get :errredesc))]
               [:div
                (submit-button {:class "button"} "Save")]])]))
                
                                    
 
