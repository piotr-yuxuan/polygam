# polygam

> Choose as many spouses as you want

This description is actually rather accurate but it would be easier to explain
it with trees, colours and arrow.

You are given two trees (aka directed acyclic graphs), A and B. Each vertex of
one tree has a set of available vertices of the other tree according to its
taste. The taste of a vertex is a set of logical rules. Let me remind it another
time: a vertex from a tree is somehow linked to some set of vertices of the
other tree. A vertex and its linked set can't be from the same tree.

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

## Work path

> Divide and conquer

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
