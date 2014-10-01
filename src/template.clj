(ns template
  (:require [noir.session :as session])
  (:use  [hiccup.core :only [html]]
         [hiccup.page :only [include-css doctype]]
         [mongodb :only [get-user-by-username]]))

(defn not-logged-in-menu
  []
  [:ul#menu
   [:li#home
    [:a {:href "/"} "Home"]]
   [:div#logreg
   [:li
    [:a {:href "/login"} "Login"]]
   [:li 
    [:a {:href "/register"} "Register"]]]])

(defn logged-in-menu
  []
  [:ul#menu 
   [:li#home
    [:a {:href "/"} "Home"]]
   [:div#logreg
   [:li 
    [:a {:href "/newrealestate"} "Add realestate"]]
   [:li
    [:a {:href "/logout"} (str "Logout [" (let [user (session/get :logged-user)] 
         (:name (get-user-by-username user))) "]")]]]])

(defn get-template
  [title content]
  (html
    (doctype :xhtml-transitional)
    [:html {:xmlns "http://www.w3.org/1999/xhtml" "xml:lang" "en" :lang "en"} 
      [:head
        (include-css "/css/style.css")
        [:meta {:charset "UTF-8"}]
        [:title title]]
      [:body
       (let [user (session/get :user)] 
         (if-not user (not-logged-in-menu) (logged-in-menu)))
         [:div#container
          [:div#titleheader
           [:a {:href "/"}
	          [:h1#title "ABC Realestate"]]]
          content]
          [:div#footer "ABC Realestate | 2014"]]]))
