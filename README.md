<div id="top"></div>

## Technology used
<p style="display: inline">
<img src="https://img.shields.io/badge/-Java-007396.svg?logo=java&style=popout">

<img src="https://img.shields.io/badge/-Clojure-5881D8.svg?logo=clojure&style=social">

</p>


## About

This is an educational interpreter written in Java.

I value things that are easy to understand and change.

The following Japanese e-book is written about this interpreter.

『[LISPインタープリタを作成しよう](https://zenn.dev/clazz/books/92f00040722df7)』([Zenn Books](https://zenn.dev/books))


## A mix of Common Lisp and Clojure, small lisp interpreter

Basically, programs are written in the style of Common Lisp.

```
(defun loop-sum (lst)
   (let ((sum 0))
    (dolist (x lst)
       (setq sum (+ sum x))
    )
   sum ))

```

Since this interpreter is made in Java, it uses Clojure's ns macro.
It's not a Common Lisp package.

```
(ns starter.test
   [ :refer-iris ]
   [ :require  clojure.math  :as m  ]
)

(defun foo (x y)
   (m/sin  (+ x y)))

```

You can also use clojure's java call function.

```
(defconstant *sb* (new StringBuilder "Hello"))
(.append *sb*  " world")
(.toString *sb*)
```

The reader uses and modifies the Clojure source.
You can use { } and [ ] just like Clojure .

```
(defn json-response (str array)     
    { :name  str , :data : array } )

(json-response "sample" '[ 1 2 3 4 5] )  

```


## License

Eclipse Public License 1.0










