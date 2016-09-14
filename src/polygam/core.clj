(ns polygam.core
  (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
  (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
            [clojure.core.logic.pldb])
  (:require [clojure.core.logic.arithmetic :as ar]
            [clojure.core.logic.fd :as fd])
  (:gen-class))

(declare yap yuk)

(def vertices
  [:a :b :c :d :e :f :g :h :i :j
   :l :m :n :o :p :q :r :s :t
   :k :kk
   :u :v :w :x :y :z :a° :b° :p°])

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

(def favour
  (db
   [yap :a]
   [yap :d]
   [yap :m]
   [yap :z]
   [yap :k]

   [yuk :p]
   [yuk :w]))

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

(defn unstrict-order-relationo
  "Abstract the general pattern of a order relation in a logic, relational way."
  [relation x y]
  (conde
   [(l/== x y)]
   [(relation x y)]
   [(fresh [z]
      (relation x z)
      (order-relationo relation z y))]))

(def light-kino
  "A goal where the two inputs x and y share kinship: x is an ancestor of y and
  y a descandant of x."
  (partial order-relationo child))

(defn tope
  "no parents"
  [x]
  (nafc #(fresh [u] (child u %)) x))

(comment
  (with-dbs [definitions kin]
    (run* [q]
      (vertex q)
      (kino q :f))))

(defn leafo
  "no descandants"
  [x]
  (nafc #(fresh [u] (child % u)) x))

(comment
  (with-dbs [definitions kin]
    (sort (run* [q]
            (vertex q)
            (leafo q)))))

(defn boundaryo
  "Extrema of the order relation child"
  [x]
  (conde [(leafo x)]
         [(tope x)]))

(comment
  (with-dbs [definitions kin]
    (run* [q]
      (vertex q)
      (boundaryo q))))

(defn siblingso
  [x s]
  (fresh [z]
    (child z x)
    (child z s)
    (l/!= x s)))

(comment
  (with-dbs [definitions kin]
    (run* [q]
      (siblingso :l q))))

(comment
  (with-dbs [definitions favour kin]
    (run* [q]
      (vertex q)
      (yap q)
      (siblingso :l q))))

(defn empeachedo
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
    (nafc empeachedo q)
    (fresh [p]
           (conde [(tope q)]
             [(kino p q)])
           (nafc empeachedo p))))

(comment
  (with-dbs [definitions favour kin]
    (distinct
     (run* [q]
       (yap-treeo q)))))

(defn yuk-treeo
  [q]
  (vertex q)
  (conde [(yuk q)]
         [(fresh [p]
            (kino p q)
            (yuk p))]))

(comment
  (with-dbs [definitions favour kin]
    (run* [q]
      (yuk-treeo q))))

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

(defn empeached-treeo
  [q]
  (fresh [a]
    (kino a q)
    (empeachedo a)
    (nafc yuk a)))

(comment
  (with-dbs [definitions favour kin]
    (->
     (run* [q]
       (empeached-treeo q))
     sort)))

(defn son-of-empeachedo
  [q]
  (fresh [a b]
    (kino a q)
    (kino a b)
    (empeachedo a)
    (kino b q)
    (nafc yuk a)
    (yap b)))

(comment
  (with-dbs [definitions favour kin]
    (->
     (run* [q]
       (son-of-empeachedo q))
     sort)))

(comment
  (with-dbs [definitions favour kin]
    (->
     (run* [q]
       (fresh [a b]
         (kino a q)
         (kino a b)
         (empeachedo a)
         (kino b q)
         (nafc yuk a)
         (yap b)))
     sort)))

(defn empeached-tree-prunedo
  [q]
  (fresh [a]
    (kino a q)
    (empeachedo a)
    (nafc yuk a)))

(comment
  (with-dbs [definitions favour kin]
    (->
     (run* [q]
       (vertex q)
       (nafc yuk q)
       (conda [(tope q) (yap q)]
              [(yap q)]
              [(nafc empeachedo q)]
              [(son-of-yap-son-of-yuko q)]))
     sort)))

(defn availableo
  [q]
  (all
    (vertex q)
    (nafc yuk q)
    (conda [(yap q)]
           [(son-of-yap-son-of-yuko q)]
           [(empeached-treeo q) (son-of-empeachedo q)]
           [(nafc empeachedo q) (nafc yuk-treeo q)]
           [(empeachedo q) fail]
           [(nafc yuk-treeo q)])))

(comment
  (with-dbs [definitions favour kin]
    (sort
     (time (run* [q]
             (availableo q))))))

(-> (run* [q]
      (availableo q))
    sort
    time)

(comment
  (with-dbs [definitions favour kin]
    (sort
     (run* [q]
       (vertex q)
       (son-of-yap-son-of-yuko q)))))

(comment
  (with-dbs [definitions favour kin]
    (sort
     (time (run* [q]
             (vertex q)
             ;;(availableo q)
             (son-of-yap-son-of-yuko q)
             )))))

(comment
  "Several possible explicit configurations:"
  "C1 If you're yap, you are available"
  "C2 If you're yuk, you're not available"
  "C3 If you are the sibling of a yapped node without being yapped yourself, you
  are not available: you are empeached"
  "C4 If amongst your parents can be found both yap yuk and empeached nodes,
  you're available if and only if: 1) every empeached parent is the parent of a
  yap node and 2) every yuk parent is the parent of a yap node"
  "C5 If amongst your parents can be found yap nodes and can be found neither
  yuk nor empeached nodes, you're available.")

(with-dbs [definitions favour kin]
  (-> (run* [q]
        (vertex q)
        (fresh [z c]
          (child z q)
          (child z c)
          (yap c)
          (l/!= q c)))
      time
      distinct
      sort))

(defn c5o
  [q]
  (all
   (nafc yap q)
   (nafc yuk q)
   (nafc empeachedo q)
   (nafc
    #(fresh [a]
       (vertex a)
       (kino a q)
       (conde [(empeachedo a)]
              [(yuk a)])))))

(with-dbs [definitions favour kin]
  (-> (run* [q]
        (vertex q)
        (c5o q))
      time
      distinct
      sort))

(with-dbs [definitions favour kin]
  (-> (run* [q]
        (vertex q)
        (conde [(c1o q)]
               [(c2o q) fail]
               [(c3o q) fail]
               [(c4o q)]
               [(c5o q)]))
      time
      sort))

(defn c4o
  [q]
  (fresh [a b]
    (kino a q)
    (kino b q)
    (conde [(yuk b)]
           [(fresh [z c]
              (child z b)
              (child z c)
              (yap c)
              (nafc yap b)
              (nafc yuk b)
              (l/!= q c))])
    (fresh [c]
      (yap c)
      (kino c q)
      (kino b c))))

(with-dbs [definitions favour kin]
  (-> (run* [q]
        (vertex q)
        (conde [(yap q)]
               [(yuk q) fail]
               [(fresh [z c]
                  (child z q)
                  (child z c)
                  (l/!= q c)) fail]))
    time
    sort))

(defn c3o
  [q]
  (fresh [z c]
    (child z q)
    (child z c)
    (yap c)
    (nafc yap q)
    (nafc yuk q)
    (l/!= q c)))

(defn c1o
  [q]
  (yap q))

(defn c2o
  [q]
  (yuk q))
