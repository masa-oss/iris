(ns starter.test2
    [ :require  starter.test2.model  :as m ]
)

(defun foo (x y)
   (+ x y))


(defun bar (a &rest x)
   (cons  a x))


