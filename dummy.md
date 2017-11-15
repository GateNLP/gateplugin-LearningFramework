# Dummy page for testing

Variables:
* page.url: {{ page.url }}
* site.url: {{ site.url }}


```java
String a = "this is a string";
List<Integer> x = new ArrayList<Integer>();

if (a != null) {
  // do something
}
```

Some pretty picture:
![Gate Logo](https://gate.ac.uk/plugins/gau-0.1/images/logo-gate.png)

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

## More variables, for blogs?

* site.posts.size {{ site.posts.size }}
* paginator.posts {{ paginator.posts }}
* paginator.total_pages {{ paginator.total_pages  }}
* sitebase {{ sitebase }}

{% for post in site.posts %}
* Title: {{ post.title }}
* post.url: {{ post.url }}
* post.description: {{ post.description }}
* post.date: {{ post.date | date_to_string }}
* URL: [{{ post.title }}]({{ site.sitebase }}/{{ post.url }})
{% if post.description %}
{{ post.description }}
{% endif %}
{{ post.date | date_to_string }}
{% endfor %} 


The End.

