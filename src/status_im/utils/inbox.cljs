(ns status-im.utils.inbox)

(defn- extract-url-components [address]
  (rest (re-matches #"enode://(.*?):(.*)@(.*)" address)))

(defn- address->mailserver [address]
  (let [[enode password url :as response] (extract-url-components address)]
    (cond-> {:address      (if (seq response)
                             (str "enode://" enode "@" url)
                             address)
             :user-defined true}
      password (assoc :password password))))
