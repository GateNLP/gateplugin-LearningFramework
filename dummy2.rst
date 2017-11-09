Title
=====

This is underneath a main title

Subtitle
--------

This is underneath a subtitle


Subsubtitle
+++++++++++

this is under a sub sub title

Paragraphs are separated by empty lines and line up at the left.

Bullet lists are like this:

- some
- and other,
  can be aligned to belong to same item

Enumerated lists:

1: like
2: this

Definition lists:

definition 1
  is this

definition 2
  is that

A literal block starts indented after to colons

::
  This is a literal block
  Here is the second line
  no inline markup is resolved in here

Links can be entered like Gate_

.. _Gate: https://gate.ac.uk

Or like `Gate <https://gate.ac.uk>`_.

Variables:

* page.url: {{ page.url }}
* site.url: {{ site.url }}

Code block: 

.. code-block: java
  :linenos:
  :emphasisze-lines: 2,3

  String a = "this is a string";
  List<Integer> x = new ArrayList<Integer>();

  if (a != null) {
    // do something
  }

BTW, code can also get literally included from a file!

And here is some maths: first inline using backslash-backslash-parenthesis \\( \psi_y(k) \\) and using single dollar  $ \psi,1 $ and using double dollar $$ \psi,2 $$ 


Stand-alone equation:

$$
A = \sum_{i=1}^n f_i
$$



Also a proper equation like this maybe:

\begin{equation}
   |\psi_1\rangle = a|0\rangle + b|1\rangle
\end{equation}

and some stand-alone maths using backslash-backslash-bracket  \\[ \psi_y(k) \\]

Stand-alone withing double-dollar and then inside an equation environment:

$$
\begin{equation}
  f = \sum_{i=0}^n e^{-\frac{1}{i}}
\end{equation}
$$


The End.

