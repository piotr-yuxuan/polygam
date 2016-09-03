(ns polygam.core
  (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
  (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
            [clojure.core.logic.pldb])
  (:require [clojure.core.logic.arithmetic :as ar]
            [clojure.core.logic.fd :as fd])
  (:gen-class))

(declare yap yuk)

(def vertices
  [:a :b :c :d :e :f :g :h :i :j :k :l :m])

(def favour
  (db
   [yap :a]
   [yuk :c] ;; yuk
   [yap :d]
   [yap :e]))

(def kinship
  {:a [:b :c]
   :b [:d :i]
   :c [:e :f]
   :d [:g :h]
   :e [:j]
   :f [:k :l :m]})

(db-rel vertex
 ^:index x)

(db-rel yap
 ^:index x)

(db-rel yuk
 ^:index x)

(db-rel
 ^{:doc "Relationship for parents"}
 child ^:index x
 ^:index y)

(def definitions
  (reduce #(db-fact % vertex %2) (db) vertices))

(def kin
  (reduce #(db-fact % child (first %2) (second %2))
          (db)
          (mapcat (fn [[k vv]] (map #(do [k %]) vv)) kinship)))

(defn available
  []
  (with-dbs [definitions favour kin]
    (run* [q]
      (fresh [a]
        (vertex a)
        (nafc yuk a)
        (l/== q a)))))

(comment "Let's build a relation for a very simple, atomic need: is this child
besides a yapped one without being yapped itself?")

(available)







