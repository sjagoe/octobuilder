===========
Octobuilder
===========

Octobuilder is a Clojure server that sits between Jenkins and Github
to facilitate automatic building of pull requests.

Development is in very early stages and there is nothing to try yet.


Dependencies
============

You will need [Leiningen][1] 2.0.0-RC1 or above installed.

[1]: https://github.com/technomancy/leiningen


Running
=======

To start a web server for the application, run::

    lein ring server


License
=======

Copyright © 2013 Simon Jagoe

Distributed under the Eclipse Public License, the same as Clojure.
