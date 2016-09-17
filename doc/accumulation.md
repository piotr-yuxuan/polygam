# _Accumulation_ algorithm

This algorithm makes use of `conde` whose doc states:

> Logical disjunction of the clauses. The first goal in
> a clause is considered the head of that clause. Interleaves the
> execution of the clauses.

The main idea is to list each possible situation for a node and choose either to
accept or reject it.

My current way to see this problem is six-fold:

* 1 If you're yap, you are available
* 2 If you're yuk, you're not available
* 3If you are the sibling of a yapped node without being yapped yourself, you are
not available: you are empeached
* 4 If amongst your parents can be found both yap yuk and empeached nodes, you're
available if and only if: 1) every empeached parent is the parent of a yap node
and 2) every yuk parent is the parent of a yap node
* 5 If amongst your parents can be found yap nodes and can be found neither yuk
nor empeached nodes, you're available.
* 6 If a node descend both from two nodes yap and yuk then its state is
inconsistent and the node should be ignored.

Note that clause 4 could be rewritten: if amongst your parents can be found both
yap yuk and empeached node, you're not available if and only if one can find one
empeached parent node which is not parent of a yap node
