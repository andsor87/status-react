(ns status-im.ui.screens.offline-messaging-settings.edit-mailserver.events
  (:require [re-frame.core :as re-frame]
            [status-im.utils.handlers :refer [register-handler] :as handlers]
            [status-im.utils.handlers-macro :as handlers-macro]
            [status-im.ui.screens.accounts.utils :as accounts.utils]
            [status-im.utils.ethereum.core :as ethereum]
            [status-im.utils.types :as types]
            [status-im.data-store.mailservers :as data-store.mailservers]
            [clojure.string :as string]))

(defn extract-password [address]
  (rest (re-matches #"enode://(.*?):(.*)@(.*)" address)))

(defn- new-mailserver [{:keys [random-id] :as cofx} mailserver-name address]
  (let [[enode password url :as response] (extract-password address)]
    {:id          (string/replace random-id "-" "")
     :name         mailserver-name
     :address      (if (seq response)
                     (str "enode://" enode "@" url)
                     address)
     :password     password
     :user-defined true}))

(handlers/register-handler-fx
 :save-new-mailserver
 [(re-frame/inject-cofx :random-id)]
 (fn [{{:mailservers/keys [manage] :account/keys [account] :as db} :db :as cofx} _]
   (let [{:keys [name url]} manage
         network (get (:networks (:account/account db)) (:network db))
         chain   (ethereum/network->chain-keyword network)
         mailserver               (new-mailserver cofx (:value name) (:value url))]
     {:db (-> db
              (dissoc :mailservers/manage)
              (assoc-in [:inbox/wnodes chain (:id mailserver)] mailserver))
      :data-store/tx [(data-store.mailservers/save-mailserver-tx (assoc
                                                                  mailserver
                                                                  :chain
                                                                  chain))]
      :dispatch [:navigate-back]})))

(handlers/register-handler-fx
 :mailserver-set-input
 (fn [{db :db} [_ input-key value]]
   {:db (update db :mailservers/manage merge {input-key {:value value
                                                      :error (and (string? value) (empty? value))}})}))

(handlers/register-handler-fx
 :edit-mailserver
 (fn [{db :db} _]
   {:db       (update-in db [:mailservers/manage] assoc
                         :name  {:error true}
                         :url   {:error true})
    :dispatch [:navigate-to :edit-mailserver]}))

