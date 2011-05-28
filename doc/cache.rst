Cache
=====

.. image:: http://www.bdoubliees.com/journalspirou/sfigures6/schtroumpfs/s15.jpg

Hazelcast is integrated for page, action, and object cache. Of course you can
use it for other things (distributed processing etc) in your application.

Cache works with async response.

Upstream

::

                 the action response
                 should be cached and
  request        the cache already exists?
  -------------------------+---------------NO--------------->
                           |
  <---------YES------------+
    respond from cache


Downstream

::

                 the action response
                 should be cached and
                 the cache does not exist?           response
  <---------NO-------------+---------------------------------
                           |
  <---------YES------------+
    store response to cache