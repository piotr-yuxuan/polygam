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

(defn order-relationo
  "Abstract the general pattern of a order relation in a logic, relational way."
  [relation x y]
  (conde
   [(relation x y)]
   [(fresh [z]
      (relation x z)
      (order-relationo relation z y))]))

(def kino
  "A goal where the two inputs x and y share kinship: x is an ancestor of y and
  y a descandant of x."
  (partial order-relationo child))

(defn tope
  "no parents"
  [x]
  (nafc #(fresh [u] (child u %)) x))

(with-dbs [definitions kin]
  (run* [q]
    (vertex q)
    (kino q :f)))

(defn leafo
  "no descandants"
  [x]
  (nafc #(fresh [u] (child % u)) x))

(with-dbs [definitions kin]
  (run* [q]
    (vertex q)
    (leafo q)))

(defn boundaryo
  "Extrema of the order relation child"
  [x]
  (conde [(leafo x)]
         [(tope x)]))

(with-dbs [definitions kin]
  (run* [q]
    (vertex q)
    (boundaryo q)))

(defn siblingso
  [x s]
  (fresh [z]
    (child z x)
    (child z s)
    (l/!= x s)))

(with-dbs [definitions kin]
  (run* [q]
    (siblingso :l q)))

(def favour
  (db
   [yap :a]
   [yuk :c] ;; yuk
   [yap :d]
   [yap :e]
   [yap :m]
   ))

(with-dbs [definitions favour kin]
  (run* [q]
    (vertex q)
    (yap q)
    (siblingso :l q)))

(defn empeachedo
  [q]
  (all
   (fresh [z]
     (vertex z)
     (yap z)
     (siblingso q z))
   (nafc yap q)))

(with-dbs [definitions favour kin]
  (run* [q]
    (vertex q)
    (empeachedo q)))
