✔ (ns polygam.core
?   (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
?   (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
?             [clojure.core.logic.pldb]
?             [polygam.facts])
?   (:require [clojure.core.logic.arithmetic :as ar]
?             [clojure.core.logic.fd :as fd]
?             [polygam.accumulation :as acc]
?             [polygam.substraction :as sub])
?   (:gen-class))
  
✔ (with-dbs [definitions favour kin]
?   "Use relational goals"
~   (->> (run* [q]
✔         (vertex q)
✔         (acc/availableo q))
✔        doall
?        time
✔        sort))
  
✔ (with-dbs [definitions favour kin]
?   "Use non-relational goals"
~   (->> (run* [q]
✔         (vertex q)
✔          (sub/availableo q))
✔        doall
?        time
✔        sort))
