# _Substraction_ algorithm

![polygyny](doc/graph-sample-extended.png)

In this section, the relation `availableo` is constructed by step by
substraction. Each step takes builds on the result of the previous one. This
construction produces a non-relational goal. For the peace of the mind, another
way is possible (e.g. without `conda`): see
[_addition_ algorithm](./addition.md).

The logic goal `availablero` binds the value of a logic variable to all nodes
which can are available for a given gutt and only these nodes. How to get such a
relation?

## Step -1: Anything

```Clojure
(run* [q])
```

`=> (_0)` because there is no rule of constraint on the variable.

## Step 0: all vertices

We use the facts defined in `definitions` as the definition of what a vertex is.

```Clojure
(with-dbs [definitions]
  (run* [q]
    (vertex q)))
```

`=> (:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p :q)` all nodes are here.
I've added an implicit call to `sort` to make the result more human friendly.

## Step 1: only keep vertices which are not explicitly rejected

Let's bind the logic variable to any node which has been explicitly rejected:

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (yuk q))))
```

`=> (:b :h)` it seems to work well, let's go further and use _negation as a
failure_ to retrieve all the values for which it's impossible to match the goal
`yuk`.

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (nafc yuk q))))
```

outputs something like `=> (_0)` because the logic variable can be anything,
provided that *anything* doesn't match the goal `yuk`. You need to narrow the
possible values of the logic variable to all vertices:

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (vertex q)
    (nafc yuk q))))
```

`=> (:a :c :d :e :f :g :i :j :k :l :m :n :o :p :q)` from which `:b` and `:h`
have been removed.

## Step 2: avoid inconsistent parents

```Clojure
(with-dbs [definitions favour kin]
  (run* [q]
    (fresh [a b]
      (kino a q)
      (kino b q)
      (nafc kino a b)
      (nafc kino b a)
      (yap a)
      (yuk b))))
```

## Step 3: go through sub-steps

A soft-cut strategy is applied: ask each possible value the question of the
sub-step: if it's a match then conditions apply and further questions are
ignored; if it's a miss, go to next step and repeat the process. If no sub-steps
are valid it fails. If one step is valid then further steps are ignored.

See `condo` in the Reasoned Schemer of `conda` in `core.logic`.

The final form of this step can be found in the code. Only simple sub-steps are
shown here.

### Step 3.0: has this vertex been explicitly chosen?

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (yap q))))
```

`=> (:a :c :j :k :n)`

If yes, it succeeds without any conditions. Here, it means the logic variable
the goal `availableo` is dealing with will be able to take the value which
succeeds here.

### Step 3.1: is this vertex free from impeachment?

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (vertex q)
    (nafc impeachedo q)))
```

If yes, it will succeed if and only if it's not a descandant of an explicitly
rejected node. In equivalent terms, it will succeed if and only if it's
impossible to find an explicitly rejected vertex whose this node descend from.

### Step 3.2: is this vertex impeached?

```Clojure
(with-dbs [definitions favour]
  (run* [q]
    (vertex q)
    (impeachedo q)))
```

If yes, it's a fail.

### Step 3.3: is this vertex son of both a rejected / impeached node and an elicited node?

```Clojure
(with-dbs [definitions favour kin]
  (run* [q]
    (fresh [a b]
      (kino a q)
      (kino a b)
      (kino b q)
      (yuk a)
      (yap b)
      (l/!= a q)
      (l/!= a b)
      (l/!= b q))))
```

If yes, it succeeds.

### Step 3.4: is it impossible to find an explicitly rejected vertex whose this node descend from?

```Clojure
(with-dbs [definitions favour kin]
  (run* [q]
    (vertex q)
    (nafc yuk-treeo q)))
```

If yes, it succeeds.
