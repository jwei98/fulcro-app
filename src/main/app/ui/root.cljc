(ns app.ui.root
  (:require
    [fulcro.client.mutations :as m]
    [fulcro.client.data-fetch :as df]
    #?(:cljs [fulcro.client.dom :as dom] :clj [fulcro.client.dom-server :as dom])
    [app.ui.components :as ui.components]
    [app.api.mutations :as api]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.i18n :as i18n :refer [tr trf]]))

;; The main UI of your application

(defsc Root [this {:keys [ui/react-key friends enemies]}]
  {:query         [:ui/react-key
                   {:friends (prim/get-query ui.components/PersonList)}
                   {:enemies (prim/get-query ui.components/PersonList)}]
   :initial-state (fn [params] {:friends (prim/get-initial-state ui.components/PersonList {:id :friends :label "Friends"})
                                :enemies (prim/get-initial-state ui.components/PersonList {:id :enemies :label "Enemies"})})}
    (dom/div
     (ui.components/ui-person-list friends)
     (ui.components/ui-person-list enemies)))
