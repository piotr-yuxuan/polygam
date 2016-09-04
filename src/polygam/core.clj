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

(defn children
  [father]
  (with-dbs [definitions favour kin]
    (run* [q]
      (project [father]
               (child father q)))))

(defn side-favouro
  "fa (favour) can be yap or yuk. For example for yap, it returns v if eligible,
  nothing if a sibling is yapped and it's not"
  [node favour-fn output]
  (fresh [father sibling]
    (child father node)
    (child father sibling)
    (conda [(favour-fn sibling) (favour-fn node)]
           ;;[(nafc favour-fn node) (log "f")]
           )
    (l/== output node)))

(defn side-favour
  [node favour-fn]
  (with-dbs [definitions favour kin]
    (run* [q]
      (side-favouro node favour-fn q))))

(side-favour :l yap)

(defn kino
  [x y]
  "A goal where x and y share kinship: x is an ancestor of y and y a descandant
  of x. Beware edge effect: x given is not returned. This shows a pattern for an
  order relation."
  (conde
   [(child x y)]
   [(fresh [z]
      (child x z)
      (kino z y))]))

(defn tope
  "no parents"
  [x]
  (nafc #(fresh [u] (child u %)) x))

(with-dbs [definitions favour kin]
  (run* [q]
    (vertex q)
    (kino q :f)))

(defn niko
  "x, y father. Similar to childo but transitive relation. Symetric of kino, for
  pedagogic purpose."
  [x y]
  (conde
   [(child y x)]
   [(fresh [z]
      (child z x)
      (niko z y))]))
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
    (niko q :f)
    (kino :f q)))
