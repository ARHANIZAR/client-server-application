# client/server application in JAVA
This source code is a simple client / server application based on sockets communication in Java.

The server allows for different operations based on the number of the random draw.
It can accommodate four types of queries:

    1- GETINT: Send a random number of type int, between 0 and a terminal sent by the client,
    2- SHUFFLE: Mix the elements of a collection of Double, and send the result,
    3- SETSEED: Update the "seed" that is used to initialize the random generator,
    4- ALEAFILE: Generate and send a binary file of a certain size, containing bytes randomly drawn.
