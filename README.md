Skir
====

Skir is a play-alike version of a popular board game of global genocide.
To play Skir, click [here](https://skir.herokuapp.com/).

Technical Introduction
======================

The bulk of the code is in Java 8. 
Skir has a message passing architecture using Mike Rettig's [Jetlang](https://github.com/jetlang) library.
Play is through a web UI that is based on a canvas object and some Javascript. 
Communication between the client and server is primarily achieved through web sockets. 
Skir's web components are provided by an embedded [Jetty](http://www.eclipse.org/jetty/) server.

Getting Started
===============

To build and run the program follow these steps:

1. cd bin
2. ./deps.sh
3. ./build.sh
4. ./start.sh
5. Navigate your browswer to http://localhost:8010/skir

If you are using Windows (Cygwin) you need to set some environment variables JAVA\_PATH\_SEPARATOR=";" and CYGWIN\_PREFIX="C:\\cygwin64" (or wherever cygwin is installed for you).

Maven
-----

If you prefer to use Maven rather than the home-brewed scripts do:

1. mvn install
2. java -cp target/classes:target/dependency/\* ojplg.skir.play.Skir
3. Navigate your browswer to http://localhost:8010/skir

Heroku
------

There is a Procfile for [Heroku](https://skir.herokuapp.com/).

Note to self: 

```git push heroku master```

Javascript
==========

The Javascript is pretty lame. I don't get Javascript.
It does not require any libraries however, so at least there's that.
Plenty of opportunity to make the GUI more beautiful and pleasant.

Rules
=====

Skir is a play-alike version of a popular board game of global genocide,
but the rules to Skir might be slightly different from what you are used to.
Here are some of the differences.
- Initial placement of countries and forces is done randomly, not
by picking.
- The value of exchanging cards starts at 2 and increases successively
to 4, 6, 8, and then a maximum of 10.
- Fortification moves can only be to adjacent countries. Still only one
per turn.

Computer Players
================

The game is always between six players. If fewer than 6 web clients are
connected, the remaining player slots will be taken by AI players.

Skir was designed to make writing AIs fairly easy. 
There are a number of different personalities.

Wimpy
-----
Never attempts to attack, just adds armies to owned countries evenly.

Bully
-----
Attacks if and only if an owned country has more armies than its neighbor.

Massy
-----
Puts all armies onto one country and attempts at most one attack per turn.

Grumpy
------
Always attempts to retake previously owned countries. Never attacks otherwise.

Grabby
------
Attempts to control continents.

Pokey
-----
Attempts a plan of slow growth.

Tuney
-----
A generic algorithm that allows its heuristic to be configured.


Writing an AI
=============
To contribute your own AI, you must implement the interface
`ojplg.skir.ai.AutomatedPlayer`. There are only three methods,
and two of them are trivial.

 * There is an optional `initialize` method. You can ignore it to start.
 The default implementation does nothing, and that's fine.
 * The `getPlayer` method must return the `Player` object. This is
 trivial. When you register your AI in the `AiFactory` class, your
 class will be passed a `Player` object. Just return it.
 * All of the work is in the `generateOrder` method. It receives two
 arguments: an `Adjutant` object and a `Game` object. The `Adjutant`
 describes the types of orders that are possible to issue. The `Game`
 object describes everything about the state of the game: what player
 owns what country and the level of forces in each country, etc.

Take a look at the class `ojplg.skir.ai.Wimpy` which is about
the simplest possible implementation that can participate in a game.

Remember to register your AI in `ojplg.skir.ai.AiFactory`.

There is a collection of static methods in `AiUtils` that are
useful for building an intelligent AI.


Modes
=====
In addition to the default web mode, Skir supports some testing and tuning
modes using the --bench and --evolve flags.
