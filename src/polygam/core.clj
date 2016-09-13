(ns polygam.core
  (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
  (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
            [clojure.core.logic.pldb])
  (:require [clojure.core.logic.arithmetic :as ar]
            [clojure.core.logic.fd :as fd])
  (:gen-class))

(declare yap yuk)

(comment
  "Other settings"
  "This a compilation of very vicious situations."
  "A propoer way to deal with them would be to restrict the graph we are working
  on: instead of a tree (directed acyclic graph) we could choose a directed
  acyclic graph which has at a root (a node all other veritces are reachable
  from) and whose underlying undirected graph is still acyclic."
  "Perhaps I could generalise little by little"
  ""
  "Anyway, for now the goal `availableo` in this file has been demonstrated
  correct for one graph. How could I prove it more formally?"
  (def vertices
    [:a :b :c :d :e :f :g :h :i :j
     :k :l :m :n :o :p :q :r :s :t
     :u :v :w :x :y :z :a° :b° :c°
     :d° :e° :f° :g° :h° :i° :j° :k°
     :l° :m° :n :o°])

  (def kinship
    {:a [:b :c :d]
     :c [:k :l]
     :l [:m :n :o]
     :m [:r :s]
     :n [:q]
     :o [:p]
     :b [:e :f :g]
     :g [:j]
     :f [:h :i]
     :d [:t :u :v]
     :v [:w :x]
     :x [:y]
     :y [:z :a° :b°]
     :l° [:b° :m° :n° :o°]
     :j° [:l°]
     :h° [:j° :k°]
     :e° [:h° :i°]
     :c° [:d° :e° :f°]
     :d° [:g° :v]})

  (def favour
    (db
     [yap :a]
     [yap :m]
     [yap :f]
     [yap :h°]
     [yap :o°]
     [yap :y]

     [yuk :n]
     [yuk :v]
     [yuk :l°])))

(comment
  (def vertices
    [:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p :q])

  (def kinship
    {:a [:b :m :c]
     :b [:d :e :f]
     :c [:h]
     :e [:r]
     :h [:i :j :k :l]
     :m [:n :o]
     :n [:g :q]
     :q [:p]})

  (def favour
    (db
     [yap :a]
     [yap :c]
     [yap :j]
     [yap :k]
     [yap :n]

     [yuk :b]
     [yuk :h])))

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

(with-dbs [definitions favour kin]
  (run* [q]
    (vertex q)
    (yap q)
    (siblingso :l q)))

(defn impeachedo
  [q]
  (all
   (fresh [z]
     (vertex z)
     (yap z)
     (siblingso q z))
   (nafc yap q)))

(defn yap-treeo
  "Nodes which are yapped or sons of yapped node. No parent are empeached. Top
  node is also included."
  [q]
  (all
    (vertex q)
    (nafc impeachedo q)
    (fresh [p]
           (conde [(tope q)]
             [(kino p q)])
           (nafc impeachedo p))))

(with-dbs [definitions favour kin]
  (distinct
   (run* [q]
         (yap-treeo q))))

(defn yuk-treeo
  [q]
  (vertex q)
  (conde [(yuk q)]
         [(fresh [p]
            (kino p q)
            (yuk p))]))

(with-dbs [definitions favour kin]
  (run* [q]
    (yuk-treeo q)))

(defn son-of-yap-son-of-yuko [q]
  (fresh [a b]
    (kino a q)
    (kino a b)
    (kino b q)
    (yuk a)
    (yap b)
    (l/!= a q)
    (l/!= a b)
    (l/!= b q)))

(with-dbs [definitions favour kin]
  (->
   (run* [q]
     (vertex q)
     (nafc yuk q)
     (conda [(tope q) (yap q)]
            [(yap q)]
            [(nafc impeachedo q)]
            [(son-of-yap-son-of-yuko q)]))
   sort))

(defn availableo
  [q]
  (all
    (vertex q)
    (nafc yuk q)
    (conda [(tope q) (yap q)]
           [(yap q)]
           [(nafc impeachedo q) (nafc yuk-treeo q)]
           [(impeachedo q) fail]
           [(son-of-yap-son-of-yuko q)]
           [(nafc yuk-treeo q)])))

(with-dbs [definitions favour kin]
  (sort
   (time (run* [q]
           (availableo q)))))

(with-dbs [definitions favour kin]
  (sort
   (run* [q]
     (vertex q)
     (nafc yuk-treeo q))))

(with-dbs [definitions favour kin]
  (sort
   (time (run* [q]
           (vertex q)
           (availableo q)))))
