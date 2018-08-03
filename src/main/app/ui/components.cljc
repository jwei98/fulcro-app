(ns app.ui.components
  (:require
    [fulcro.client.primitives :as prim :refer [defsc]]
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
(defsc Person [this {:keys [person/name person/age]}]
  {:initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
  (dom/li
   (dom/h5 (str name " (age: " age ")"))))
(def ui-person (prim/factory Person {:keyfn :person/name}))

;; Person List component
(defsc PersonList [this {:keys [person-list/label person-list/people]}]
  {:initial-state
   (fn [{:keys [label]}]
     {:person-list/label  label
      :person-list/people (if (= label "Friends")
                            [(prim/get-initial-state Person {:name "Sally" :age 32})
                             (prim/get-initial-state Person {:name "Joe" :age 22})]
                            [(prim/get-initial-state Person {:name "Fred" :age 11})
                             (prim/get-initial-state Person {:name "Bobby" :age 55})])})}
  (dom/div
   (dom/h4 label)
   (dom/ul
    (map ui-person people))))

(def ui-person-list (prim/factory PersonList))
