(ns login
  (:require [noir.session :as session]
            [ring.util.response :as response])
  (:use  [template :only [get-template]]
         [hiccup.form :only [form-to label text-field password-field submit-button]]
         [mongodb :only [get-user-by-username]]))

(defn login-page
  []
  (get-template "ABC Realestate" 
    [:div.content
     [:p.logintitle "Please enter your username and password: "]
      [:p.loginerror (session/flash-get :login-error)]
      (form-to [:post "/login"]
              [:div.loginform
               [:div 
               (label {:class "loginlabel"} :username "Username: ")
               (text-field :username)]
               [:div
               (label {:class "loginlabel"} :password "Password: ")
               (password-field :password)]
               [:div
                (submit-button {:class "button"} "Log in")]])]))

(defn- check-user
  [user password]
    (cond
      (nil? user) "Username does not exist."
      (not= password (:password user)) "Password is incorrect."
      :else true))

(defn login
  [username password]
  (let [lu (clojure.string/lower-case username)
        user (get-user-by-username lu)
        error-msg (check-user user password)]
    (if-not (string? error-msg) 
      (do 
        (session/put! :user user)
        (session/put! :logged-user lu)
        (response/redirect "/"))
      (do
        (session/flash-put! :login-error error-msg)
        (response/redirect "/login")))))

(defn logout []
  (session/remove! :user))