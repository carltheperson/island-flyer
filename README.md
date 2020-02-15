# Island Flyer

This simple game is still very early in development, but one day will become an island explorer game. The game is made with OpenGl using LWJGL.

![](screenshot.png)

## Generation
The islands are procedurally generated, so the world you can explore is infinite.
The islands are generated using perlin noise as a base, and shaped using what I call “island points“ and “island squares”. Island points can only exist inside of an island square, and has a higher chance of existing closer to the center of the square. A given points height is defined by how close it is to an island point.

## How to run
There are executables for every operating system [here](./executables). I have only tested the game on Windows, so please submit an issue, if it does not work for you.