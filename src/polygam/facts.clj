(ns polygam.facts
  (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
  (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
            [clojure.core.logic.pldb])
  (:require [clojure.core.logic.arithmetic :as ar]
            [clojure.core.logic.fd :as fd])
  (:gen-class))

(db-rel vertex
        ^:index x)

(db-rel yap
        ^:index x)

(db-rel yuk
        ^:index x)

(db-rel ^{:doc "Relationship for parents"}
        child ^:index x
              ^:index y)

(def vertices
  [:a :b :c :d :e :f :g :h :i :j
   :l :m :n :o :p :q :r :s :t
   :k :kk
   :u :v :w :x :y :z :a° :b° :p°])

(def definitions
  (reduce #(db-fact % vertex %2) (db) vertices))

(def kinship
  {:a [:b :i :t]
   :b [:c :d :g]
   :d [:e :f]
   :g [:h]
   :h [:k]
   :k [:kk]
   :i [:j :l]
   :l [:m :p :r]
   :m [:n :o]
   :p [:q]
   :r [:s]
   :t [:u :v :w]
   :w [:x :y]
   :y [:z]
   :z [:a° :b° :p°]})

(def kin
  (reduce #(db-fact % child (first %2) (second %2))
          (db)
          (mapcat (fn [[k vv]] (map #(do [k %]) vv)) kinship)))

(def favour
  (db
   [yap :a]
   [yap :d]
   [yap :m]
   [yap :z]
   [yap :k]

   [yuk :p]
   [yuk :w]))
