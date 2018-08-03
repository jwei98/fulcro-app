(ns app.ui.components
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
    [app.api.mutations :as api]
    #?(:cljs [fulcro.client.dom :as dom] :clj [fulcro.client.dom-server :as dom])))

;; A good place to put reusable components
(defsc PlaceholderImage [this {:keys [w h label]}]
  (let [label (or label (str w "x" h))]
    (dom/svg {:width w :height h}
      (dom/rect {:width w :height h :style {:fill        "rgb(200,200,200)"
                                            :strokeWidth 2
                                            :stroke      "black"}})
      (dom/text {:textAnchor "middle" :x (/ w 2) :y (/ h 2)} label))))

(def ui-placeholder (prim/factory PlaceholderImage))

;; Person component
(defsc Person [this {:keys [person/name person/age]} {:keys [onDelete]}]
  {:query [:person/name :person/age]
   :initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete name)} "X"))))
(def ui-person (prim/factory Person {:keyfn :person/name}))

;; Person List component
(defsc PersonList [this {:keys [person-list/label person-list/people]}]
  {:query [:person-list/label {:person-list/people (prim/get-query Person)}]
   :initial-state
   (fn [{:keys [label]}]
     {:person-list/label  label
      :person-list/people (if (= label "Friends")
                            [(prim/get-initial-state Person {:name "Sally" :age 32})
                             (prim/get-initial-state Person {:name "Joe" :age 22})]
                            [(prim/get-initial-state Person {:name "Fred" :age 11})
                             (prim/get-initial-state Person {:name "Bobby" :age 55})])})}
  ;; since we need function to be computed by PersonList, we pass it into Person as a callback/computed thing
  (let [delete-person (fn [name] (prim/transact! this `[(api/delete-person {:list-name ~label :name ~name})]))]
    (dom/div
     (dom/h4 label)
     (dom/ul
      (map (fn [p] (ui-person (prim/computed p {:onDelete delete-person}))) people)))))

(def ui-person-list (prim/factory PersonList))
