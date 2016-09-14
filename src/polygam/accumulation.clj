(ns polygam.accumulation
  (:require [clojure.core.logic :only    [== >= <= > < = !=] :as l])
  (:use     [clojure.core.logic :exclude [== >= <= > < = !=]]
            [clojure.core.logic.pldb]
            [polygam.facts]
            [polygam.common])
  (:require [clojure.core.logic.arithmetic :as ar]
            [clojure.core.logic.fd :as fd]))

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
  yuk nor empeached nodes, you're available."
  ""
  "C4bis C4 can be rewritten: If amongst your parents can be found both yap yuk and empeached node,
  you're not available if and only if one can find one empeached parent node
  which is not parent of a yap node")

(defn c1o
  [q]
  (yap q))

(defn c2o
  [q]
  (yuk q))

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
   (nafc yap q)
   (nafc yuk q)
   (nafc empeachedo q)
   (nafc
    #(fresh [a]
       (vertex a)
       (kino a q)
       (conde [(empeachedo a)]
              [(yuk a)])))))

(defn availableo
  [q]
  (all
   (vertex q)
   (nafc c2o q)
   (nafc c3o q)
   (or* [(c1o q)
         (c4o q)
         (c5o q)])))
