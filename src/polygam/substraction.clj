(ns polygam.substraction
  (:require [clojure.core.logic :only [== >= <= > < = !=] :as l])
  (:use [clojure.core.logic :exclude [== >= <= > < = !=]] [clojure.core.logic.pldb] [polygam.facts] [polygam.common])
  (:require [clojure.core.logic :only [== >= <= > < = !=] :as l]))

(defn tope
  "no parents"
  [x]
  (nafc #(fresh [u] (child u %)) x))

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

(defn yuk-treeo
  [q]
  (vertex q)
  (conde [(yuk q)]
         [(fresh [p]
            (kino p q)
            (yuk p))]))

(defn availableo
  [q]
  (all
    (vertex q)
    (nafc inconsistento q)
    (nafc yuk q)
    (conda [(yap q)]
           [(fresh [a b]
              (kino a q)
              (kino a b)
              (kino b q)
              (yuk a)
              (yap b)
              (l/!= a q)
              (l/!= a b)
              (l/!= b q))]
           [(fresh [a]
              (kino a q)
              (empeachedo a)
              (nafc yuk a)) (fresh [a b]
                              (kino a q)
                              (kino a b)
                              (empeachedo a)
                              (kino b q)
                              (nafc yuk a)
                              (yap b))]
           [(nafc empeachedo q) (nafc yuk-treeo q)
            (single-matcho #(fresh [a]
                              (kino a q)
                              (yap a)))]
           [(empeachedo q) fail]
           [(nafc yuk-treeo q)])))
