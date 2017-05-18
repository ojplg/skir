Skir
====

Skir is a play-alike version of a popular board game of global genocide.

Technical Introduction
======================

The bulk of the code is in Java. Play is through a web UI that is based
on a canvas object and some Javascript. Communication between the client
and server is primarily achieved through web sockets.

Getting Started
===============

To build and run the program follow these steps

1. cd bin
2. ./deps.sh
3. ./build.sh
4. ./start.sh
5. Navigate your browswer to http://localhost:8080/

If you are using Windows (Cygwin) you need to set an environment variable JAVA\_PATH\_SEPARATOR=";"

Some Comments
=============

- The BASH scripts for building and running the program are pretty ridiculous.
I guess I should make a pom.xml or something.
- The Javascript is pretty lame. I don't get Javascript.
It does not require any libraries however, so at least there's that.
- There are plenty of things to work on. See the TODO file for the most pressing.

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

The game is always between six players. The computer neither cheats nor
implements a particularly good strategy. It should be relatively easy
to create new AIs.
