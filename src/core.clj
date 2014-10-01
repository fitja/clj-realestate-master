(ns core
  (:require [compojure.route :as route]
            [noir.session :as session]
				    [ring.util.response :as response]
            [clj-time.format :as time-format]
            [clj-time.core :as time])
  (:use [compojure.core :only [defroutes GET POST DELETE PUT]]
        [ring.adapter.jetty :only [run-jetty]]
        [main :only [main-page goto-main-page delete-realestate edit-realestate update-realestate show-realestate-detail realestate-detail-page]]
        [login :only [login-page login logout]]
        [register :only [register-page register]]
        [mongodb :only [insert-user insert-user-realestate get-user-by-username get-all-users get-latest-realestates db-delete-realestate parser-formatter get-realestate-by-id]]
        [realestate :only [realestate-page add-new-realestate]]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        [ring.middleware.params :only [wrap-params]]
        [clojure.java.browse :only [browse-url]]))

(defroutes handler
  (GET "/" [] (main-page))
  (POST "/" [realestate-id] (let [realestate (get-realestate-by-id realestate-id)]  (show-realestate-detail realestate-id)))
  (GET "/login" [] (let [user (session/get :user)] (if-not user (login-page) (main-page))))
  (POST "/login" [username password] (login username password))
  (GET "/logout" [] (do (logout) (response/redirect "/")))
  (GET "/register" [] (let [user (session/get :user)] (if-not user (register-page) (main-page))))
  (POST "/register" [name email username password password2] (register name email username password password2)) 
  (GET "/newrealestate" [] (let [user (session/get :user)] (if user (realestate-page) (login-page))))
  (POST "/newrealestate" [realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image] (add-new-realestate realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image))
  (GET "/editrealestate" [] (let [user (session/get :user)] (if user (realestate-page) (login-page))))
  (POST "/editrealestate" [realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]  (edit-realestate realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image))
  (GET "/updaterealestate" [] (let [user (session/get :user)] (if user (realestate-page) (login-page))))
  (POST "/updaterealestate" [realestate-id realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image]  (update-realestate (Integer/valueOf realestate-id) realestate-name realestate-size realestate-location realestate-price realestate-description realestate-image))
  (DELETE "/" [realestate-id] (delete-realestate (Integer/valueOf realestate-id)))
  (GET "/realestate" [] (let [realestate (get-realestate-by-id (Integer/valueOf (session/get :realestate-id)))] (if-not realestate (goto-main-page) (realestate-detail-page (session/get :realestate-id)))))
  (POST "/realestate" [] (goto-main-page))
  (route/resources "/")
  (route/not-found "Sorry, page not found!"))

 (def app
  (-> #'handler
    (wrap-reload)
    (wrap-params)
    (session/wrap-noir-flash)
    (session/wrap-noir-session)
    (wrap-stacktrace)))
 
 (def port-number 8080)

 (defn start-jetty-server []
   (run-jetty #'app {:port port-number :join? false})
   (browse-url (str "http://localhost:" port-number)))
 
 (defn insert-test-user [] 
   (if (empty? (get-all-users))
     (do
  (insert-user "Admin" "admin@admin.com" "admin" "adminadmin"))))
 
 (defn insert-test-data [] 
   (if (empty? ( get-latest-realestates))
    (do 
	   (let [user (get-user-by-username "admin")]
	     (do
	       (insert-user-realestate "Studio Apple III" "50" "Wenceslas Square" "60000.00" "STUDIO BALCONY APPLE I. is 50 m2 / 538 ft2 total with living bedroom with BALCONY with double bed, SHARED living roomwith fully equipped kitchen, SHARED bathroom and SHARED toilet with other two rooms and our staff. Quiet and cheap accommodation in the middle of town. The Apartment is suitable for short or extended stays and is on the 6th floor with an elevator(lift). Oriented - courtyard.
	1 x living bedroom (15 m2 / 161 ft2, double bed)
	1 x SHARED living room with kitchen 25 sqm / 322 ft2, dining table, large fridge
	1 x SHARED bathroom (6 m2 / 65 ft2, bathtub, washing machine)
	1 x SHARED toilet (3 m2 / 32 ft2, washing machine)
	1 x balcony" "images/img1.jpg" (time-format/unparse parser-formatter (time/now)) (:_id user))
	       (insert-user-realestate "Studio Apple II" "50" "Wenceslas Square" "63000.00"  "STUDIO APPLE II. is 50 m2 / 538 ft2 total with living bedroom with double bed, SHARED living room with fully equipped kitchen, SHARED bathroom and SHARED toilet with other two rooms and our staff. Quiet and cheap accommodation in the middle of town. The Apartment is suitable for short or extended stays and is on the 6th floor with an elevator(lift). Oriented - street.
	1 x living bedroom (15 m2 / 1 ft2, double bed)
	1 x SHARED living room with kitchen 25 sqm / 322 ft2, dining table, large fridge
	1 x SHARED bathroom (6 m2 / 65 ft2, bathtub, washing machine)
	1 x SHARED toilet (3 m2 / 32 ft2, washing machine)
	1 x balcony" "images/img2.jpg" (time-format/unparse parser-formatter (time/now)) (:_id user))
	       (insert-user-realestate "Studio Dusni 2B" "35" "Cambridge gate" "92000.00" "STUDIO APARTMENT Dusni 2B is 35 m2 / 376 ft2 total with living bedroom with kitchen with double bed & double sofa bed, bathroom and a romantic balcony. Apartment blends homely comforts such as fully equipped kitchen, entertainment centre, bedding and work area (free WIFI), with useful elements. As for attention and services the apartment is on level of a 3 - 4 star hotel. The apartment is suitable for short or  extended stays. The apartment is on the 2nd floor with an elevator(lift) oriented to the courtyard.
	1 x living bedroom with kitchen corner(25 m2 / 269 ft2, 1 x double bed, double sofa bed)
	1 x bathroom (shower and toilet)
	1 x balcony (4 m2 / 43 ft2)" "images/img3.jpg" (time-format/unparse parser-formatter (time/now)) (:_id user))
	       (insert-user-realestate "Studio Terrace 806" "45" "Holloway" "101000.00" "STUDIO TERRACE APARTMENT 806 is 45 m2 / 484 ft2 total with living bedroom upstairs with double bed & double sofa bed, kitchen, bathroom and a terrace with view of Prague. Apartment blends homely comforts such as fully equipped kitchen, entertainment centre, bedding and work area (free WiFi), with useful elements. As for attention and services the apartment is on level of a 3 - 4 star hotel. The apartment is suitable for short or extended stays and is on the 7th floor with an elevator(lift) oriented to the courtyard.
	1 x living bedroom (20 m2 / 215 ft2, 1 x double bed, 1 x double sofa bed)
	1 x kitchen (10 m2 / 107 ft2)
	1 x bathroom (shower and toilet)
	1 x terrace 10 m2 / 107 ft2" "images/img4.jpg" (time-format/unparse parser-formatter (time/now)) (:_id user))
	       )))))
;(read-string 
 (defn -main [& args]
   (do
     (start-jetty-server)
      (let [user (get-user-by-username "admin")]
       (if-not user 
         (do 
           (insert-test-user) 
           (insert-test-data))))))
 