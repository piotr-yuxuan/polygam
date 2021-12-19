(ns polygam.accumulation
  (:require [clojure.core.logic :only [== >= <= > < = !=] :as l])
  (:use [clojure.core.logic :exclude [== >= <= > < = !=]] [clojure.core.logic.pldb] [polygam.facts] [polygam.common])
  (:require [clojure.core.logic :only [== >= <= > < = !=] :as l]))

(def c1o yap)

(def c2o yuk)

(defn c3o
  [q]
  (fresh [z c]
    (child z q)
    (child z c)
    (yap c)
    (nafc yap q)
    (nafc yuk q)
    (l/!= q c)))

(defn c4o
  [q]
  (fresh [a b]
    (kino a q)
    (kino b q)
    (yap a)
    (conde [(yuk b)]
           [(empeachedo b)])
    (kino b a)))

(defn c5o
  [q]
  (all
   (vertex q)
   (nafc yap q)
   (nafc yuk q)
   (nafc empeachedo q)
   (nafc #(fresh [a]
           (vertex a)
           (kino a q)
           (conde [(empeachedo a)]
                  [(yuk a)])))
   (single-matcho #(fresh [a] (kino a q) (yap a)))))

(def c6o inconsistento)

(defn availableo
  [q]
  (all
   (vertex q)
   (conde [(c1o q)]
          [(c2o q) fail]
          [(c3o q) fail]
          [(c4o q)]
          [(c5o q)])
   (nafc c6o q)))
