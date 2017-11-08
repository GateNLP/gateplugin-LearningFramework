# Testing

{% if site.posts.size > 0 %}
##  Blog Posts ({{ site.posts.size }})

* Total posts: {{ site.posts.size }}
* Total posts: {{ paginator.total_posts }}
* paginator.posts {{ paginator.posts }}
{% for post in paginator.posts %}
[{{ post.title }}]({{post.url}})
{% if post.description %}
{{ post.description }}
{% endif %}
{{ post.date | date_to_string }}
{% endfor %} {% if paginator.total_pages > 1 %}
{% if paginator.previous_page %}
  [<--]({{ paginator.previous_page_path | prepend: site.baseurl | replace: '//', '/' }})
{% endif %}
{% if paginator.next_page %}
  [-->]({{ paginator.next_page_path | prepend: site.baseurl | replace: '//', '/' }})
{% endif %}
{% endif %}
{% else %}
No blog posts yet!
{% endif %}
