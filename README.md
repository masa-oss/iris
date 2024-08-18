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

## Simple Swing GUI
You can use the GUI to see what is registered in the Namespace.
<img width="903" alt="image" src="https://github.com/user-attachments/assets/ffc9701a-b593-42cc-a8ec-dbc7a6c5136c">

## CUI
CUI is also available.



<img width="398" alt="image" src="https://github.com/user-attachments/assets/c307d756-a76a-4f91-b3a9-3fcce208ea07">

## How to build

Download dependent files.
```
cd download
chmod +x *.sh
./downlib.sh

or mvn dependency:copy-dependencies
```

Open the project in NetBeans or build it with ant with the command below .

```
cd ant.run
ant jar
cd ..

Mac:
chmod +x run.sh
./run.sh

Windows:
.¥iris_run.bat
```


## License

Eclipse Public License 1.0










