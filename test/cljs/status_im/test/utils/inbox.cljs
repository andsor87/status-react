(ns status-im.test.utils.inbox
  (:require [cljs.test :refer-macros [deftest is testing]]
            [status-im.utils.inbox :as inbox]))

(deftest address->mailserver
  (testing "with password"
    (let [address "enode://some-id:the-password@206.189.56.154:30504"]
      (is (= {:address "enode://some-id@206.189.56.154:30504"
              :password "the-password"
              :user-defined true}
             (inbox/address->mailserver address)))))
  (testing "without password"
    (let [address "enode://some-id@206.189.56.154:30504"]
      (is (= {:address "enode://some-id@206.189.56.154:30504"
              :user-defined true}
             (inbox/address->mailserver address))))))
