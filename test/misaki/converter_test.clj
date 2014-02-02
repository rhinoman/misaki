(ns misaki.converter-test
  (:require
    [misaki.config    :refer [*config*]]
    [misaki.converter :refer :all]
    [midje.sweet      :refer :all]
    [conjure.core     :refer :all]))

(def text-conv
  {:-type (fn [] (list :txt))})
(def pict-conv
  {:-type (fn [] (list :jpg :gif :png))})
(def star-conv
  {:-type (fn [] (list :*))})

(facts "type-matched? should work fine."
  (fact "type equals"
    (type-matched? :txt text-conv) => true
    (type-matched? :jpg pict-conv) => true
    (type-matched? :gif pict-conv) => true
    (type-matched? :png pict-conv) => true
    (type-matched? :jpg text-conv) => false
    (type-matched? :txt pict-conv) => false)

  (fact "stared types"
    (type-matched? :txt star-conv) => true
    (type-matched? :jpg star-conv) => true))

(fact "skip? should work fine."
  (skip? {})              => false
  (skip? {:status :foo})  => false
  (skip? {:status :skip}) => true)

(def test-converters
  (list {:-type #(list :txt)
         :-main #(assoc % :x "x")}
        {:-type #(list :clj)
         :-main  #(assoc % :y "y")}))

(def star-converters
  (list {:-type #(list :*)
         :-main #(assoc % :z "z")}
        {:-type #(list :dummy)
         :-main #(assoc % :unexpected "value")}))

(fact "apply-converters should work fine."
  (stubbing [get-converters test-converters]
    (apply-converters {:type :txt}) => {:type :txt :x "x"}
    (apply-converters {:type :clj}) => {:type :clj :y "y"})
  (stubbing [get-converters star-converters]
    (apply-converters {:a 1})         => {:a 1 :z "z"}
    (apply-converters {:type :dummy}) => {:type :dummy :z "z"})) ; dummy should not be worked
