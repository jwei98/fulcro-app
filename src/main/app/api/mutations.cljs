(ns app.api.mutations
  (:require
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.logging :as log]))

;; Place your client mutations here
(defmutation delete-person
  "Mutation: Delete the person with name from the list with list-name"
  [{:keys [list-name name]}] ; (1)
  (action [{:keys [state]}] ; (2)
          (let [path     (if (= "Friends" list-name)
                           [:friends :person-list/people]
                           [:enemies :person-list/people])
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:person/name %) name) old-list))]
            (swap! state assoc-in path new-list))))
