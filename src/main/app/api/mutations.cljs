(ns app.api.mutations
  (:require
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.logging :as log]))

;; Place your client mutations here
(defmutation delete-person
  "Removes person of name from list"
  [{:keys [list-id person-id]}]
  (action [{:keys [state]}]
    (let [path (if (= :friends list-id)
                 [:friends :person-list/people]
                 [:enemies :person-list/people])
          old-list (get-in @state path)
          new-list (vec (remove #(= (:person/name %) name) old-list))]
      (swap! state assoc-in path new-list))))
