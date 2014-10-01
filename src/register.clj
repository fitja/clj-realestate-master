(ns register
   (:require [noir.session :as session]
             [noir.validation :as validation]
             [ring.util.response :as response])
  (:use [template :only [get-template]]
        [hiccup.form :only [form-to label text-field password-field submit-button]]
        [mongodb :only [insert-user get-user-by-email get-user-by-username]]))

(defn register-page
  []
  (get-template "ABC Realestate"
  [:div.content
   [:p.newusertitle "Fill all fields for registration:" ]
    [:p.newusererror (session/flash-get :register-error)]
    (form-to [:post "/register"]
	    [:div.registerform
	     [:div
	      (label {:class "newuser"} :name "Name: ")
	      (text-field :name (session/flash-get :name))
        (label {:class "errnewuser"} :name (session/flash-get :errname))]
	     [:div
	      (label {:class "newuser"} :email "Email: ")
	      (text-field :email (session/flash-get :email))
        (label {:class "errnewuser"} :name (session/flash-get :errmail))]
	     [:div
	      (label {:class "newuser"} :username "Username: ")
	      (text-field :username (session/flash-get :username))
        (label {:class "errnewuser"} :name (session/flash-get :erruser))]
	     [:div
	      (label {:class "newuser"} :password "Password: ")
	      (password-field :password)
        (label {:class "errnewuser"} :name (session/flash-get :errpass))]
	     [:div
	      (label {:class "newuser"} :password2 "Confirm password: ")
	      (password-field :password2)
        (label {:class "errnewuser"} :name (session/flash-get :errpass2))]
	     [:div
	      (submit-button {:class "button"} "Register")]])]))

(defn- check-user-data
  [name email username password password2]
  (cond
	  (> 3 (.length name)) false
	  (< 30 (.length name)) false
	  (not (nil? (get-user-by-email email))) false
    (> 5 (.length email)) false
    (not (validation/is-email? email)) false
	  (not (nil? (get-user-by-username username))) false
	  (> 5 (.length username)) false
	  (< 15 (.length username)) false
	  (> 6 (.length password)) false
	  (not= password password2) false
	  :else true))

(defn prepare-error-messages
  [name email username password password2]
  (do
    (if (> 3 (.length name)) (session/flash-put! :errname "*Name must be at least 3 character long.") ())
    (if (< 30 (.length name)) (session/flash-put! :errname "*Name must be max 35 characters long.") ())
    (if (not (nil? (get-user-by-email email))) (session/flash-put! :errmail "*Email address already exists.") ())
    (if (> 5 (.length email)) (session/flash-put! :errmail "*Email must be at least 3 character long.") ())
    (if (not (validation/is-email? email)) (session/flash-put! :errmail "*Email is not in correct format."))
    (if (not (nil? (get-user-by-username username))) (session/flash-put! :erruser "*Username already exists.") ())
    (if (> 5 (.length username)) (session/flash-put! :erruser "*Username must be at least 5 characters long.") ())
    (if (< 15 (.length username)) (session/flash-put! :errpass "*Username must be max 15 characters long.") ()) 
    (if (> 6 (.length password)) (session/flash-put! :errpass "*Password must be at least 6 characters long.") ())
    (if (not= password password2) (session/flash-put! :errpass2 "*Password confirmation does not match.") ())))

(defn register 
  [name email username password password2]
  (let [username-lower (clojure.string/lower-case username)
        valid-entry (check-user-data name email username-lower password password2)]
    (if (= true valid-entry)
      (do
        (insert-user name email username-lower password)
        (response/redirect "/"))
      (do
        (prepare-error-messages name email username password password2)
        (session/flash-put! :register-error "Error validating data.")
        (session/flash-put! :name name)
        (session/flash-put! :email email)
        (session/flash-put! :username username-lower)
        (response/redirect "/register")))))


