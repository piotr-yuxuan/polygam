# polygam

> Choose as many spouses as you want

![polygyny](http://i.huffpost.com/gen/2955428/images/o-POLYGAMY-facebook.jpg)

This above description is actually rather accurate but it would be easier to
explain it with trees, colours and arrow.

You are given two trees (aka directed acyclic connected graphs), A and B. Each
vertex of one tree has a set of available vertices of the other tree according
to its taste. The taste of a vertex is a set of logical rules. Let me remind it
another time: a vertex from a tree is somehow linked to some set of vertices of
the other tree. A vertex and its linked set can't be from the same tree.

My goal in this project is to write such a function f which takes a vertex and
returns all available vertices. It's really a perfect playground for logic
programming. I've been trying to even use the smaller "constraint programming"
which is relational thus more powerfull but sometimes less trivial to write.

This project currently barely stands as a draft. I'm still having painful time
reasoning on this project so don't expect anything to be well-documented,
crystal-clear; don't even expect any simple, beautiful design except the alien,
mesmerising beauty of constraint programming.

I've been finding useful to see this problem from different viewpoint, hence you
can still find vocabulary about genealogy, graph, match-making, logic and
relational algebra.

## Tools

In this section we define the current semantic of goals then we give some
examples of expected behaviours.

### Definition

Consider the following tree, which arrows are from top to bottom.

```
             *
             a
            / \
           /   \
          /     \
         /       \
        /         \
       /           \
      /             \
      *              *
      b              c
     / \            / \
    /   \          /   \
   /     \        /     \
   |      |      /       *
   |      |     /        f
   *      |    *       / | \
   d      |    e      /  |  \
  / \     |    |     /   |   \
 *   *    *    *    *    *    *
 g   h    i    j    k    l    m
```

Each arrow stands for the parent <~> child relation. It's defined in the code by
the relation `child`. We can extend this relation to ascendant <~> descandant:
it's the relational goal `kino`. Logic goal are used to be suffixed with a
superscript `o` (or another vowel), which we render in the code by a standard
letter `o`. The name `kino` comes from English nouns kin and kinship which
denotes such a relationship. Last word about the family, you can access siblings
of a vertex (other children of the same parents different from the given vertex)
by goal `siblingso`.

As we're talking about a tree, it may be useful to later be able to deal
specifically with it. Let's get any vertex which have no parents (aka a root)
with goal `tope` (from top) and any vertex without children with `leafo` (from
leaf). An easy combination of the two latter goals gives `boundaryo` (from
boundary). Although the boundary of a a tree is not really worth a definition,
perhaps it will prove itself useful later.

Vertices can be elicited or rejected. Two relations embodie this is the code.
Their names are pretty straightforward as `yap` convey a positive meaning
(chosen amongst siblings) whilst `yuk` sounds negatively yucky. Ikks!

Even if `yap` and `yuk` can't be used together yet in a coherent fashion, you
can still manipulate them separately with two goals `yap-treeo` and `yuk-treeo`.
The first one can return any vertex which has been elicited (yapped) or any
child of such a vertex. It means that ignored vertices, that's to say non-yapped
siblings of a yapped vertex can not be comprehended by `yap-treeo`. By the way,
forgive my poor ability to name things in proper English but such an ignored
vertex is said to be empeached. You can reach it through the relational goal
`empeachedo`.

`yuk-treeo` is the symetric goal which can accept any rejected (yukked) vertex
of any children of such a vertex.

Some subtleties still have to be defined when you intertwinned yukked and yapped
vertices in a tree.

### Examples

Consider the annotated draw of the same previous graph.

```
             *O
             a
            / \
           /   \
          /     \
         /       \
        /         \
       /           \
      /             \
      *              *X
      b              c
     / \            / \
    /   \          /   \
   /     \        /     \
   |      |      /       *
   |      |     /        f
   *O     |    *O      / | \
   d      |    e      /  |  \
  / \     |    |     /   |   \
 *   *    *    *    *    *    *
 g   h    i    j    k    l    m
```

Vertices a, c and d and e have been added some marks. Nodes a, d and e are
marked with O, meaning they have been elicited (yapped). Node c is rejected,
hence given an X.

Let's describe the expected behaviour of aforedfined goals:

 * `yap-treeo` should allow vertices a, b, c, d, e, g, h, j. Vertex f and its
   children k, l, m are ignored because its sibling e has been elicited
   (yapped). Vertex i is ignored because of the same reason.
 * `empeachedo` should hence return the previous ignores vertices: i, k, l and
   finally m.
 * `yuk-treeo` should match vertices c, e, f, j, k, l, m because c is rejected
   (yukked) and other nodes are children of it.

When looking for general answer, which are available nodes to be chosen?
Rejected (yukked) vertex c is child of elicited vertex a; j is direct child of
elicited (yapped) vertex e and the latter is direct child of rejected (yukked)
vertex c. Because of this setting, available nodes should be a, b, d, e, g, h,
j. Vertex c is removed because explicit rejection. Vertices e and j are
descendants of excluded node c but have a nearer elicited parent so they are
kept available.

## Work path

> Divide and conquer

![graph](http://i.stack.imgur.com/s9wGx.png)

Methinks compulsory to break this problem into smaller pieces that can fit my
brain. Here are the ordained list of foreseen steps toward general resolution.

- Forget the two graphs and the like. Just take one single graph and write
  relations to get ancestors, descendants, siblings and so on.
- Let's introduce two constraints, yap and yuk. Yap means this node has been
  elicited amongst its siblings so the latter are discarded in favour of any
  yapped node. Yuk is the symetric: any yucky yukked node is belittled in favour
  of any other siblings. Let's define and play with these constraints
  separately.
- Once you apply yap on a graph, some nodes get a situation of impeachment to
  deal with. The relation which give all available nodes doesn't have to be
  mind-blowing but it surely will need to be carefully crafted.
- Yap and yuk applied together add another situation of precedence. If your
  parent is yukked but you're yapped, you and your descendants should be
  available but your siblings shouldn't.
- You can add the other graph now. Make careful, small steps not to get lost.
  First of it is a naming rule: graph A is the graph whose vertices are to be
  chosen, graph B is the graph whose vertices have tastes.
- Vertices of B only have their own tastes and nothing else.
- Vertices of B have parents. They inherit their parents' tastes.
- Keep your code in full Clojure but use ClojureScript to build a simple visual
  web interface to this.
- OK, now go and write a memoir about this ^^

## Thanks

* William E. Byrd
* Any other people cited in his dissertation

## License

Copyright © 2016 piotr-yuxuan

Distributed under the GNU General Public License either version 3.0 or (at your
option) any later version.
