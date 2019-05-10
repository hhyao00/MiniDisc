# MiniDisc
Minimum enclosing ball
optional course project AMS 545/ CSE 555 with Professor Joe Mitchell

![minidisc](https://i.imgur.com/f4Kl8RO.png)

**Compute MiniDisc**: Animates the computation of finding the miniDisc.
**Freehand**: Immediate generation of miniDisc as points are added to the plane (see Disc.java)
**Random (with input)**: Generates random points, but it is in a bounding square because Java’s random generator returns points within a certain rectangular range.
**Animation speed**: Control speed of animation for ‘Compute’ and ‘Random’.

**Shuffle**: The points are considered in the order they are (manually) clicked into the plane. To discard that ordered input, use ‘shuffle’.
**Stop (Pause) / Resume**: Can stop an animation for ‘Compute’ and ‘Random’.
**Clear/Rest**: resets the state of the application.
**A Set P of points; n, (i <= n)**: refers to the number of points in the plane

**Routine**: is the method being called. The outermost loop is MiniDisc( P ) which has at most n iterations. MiniDiscOnePoint( P, q ) is called whenever a point(i) is not enclosed in the current disc. Similarly with MiniDiscTwoPoint( P, q1, q2 ). The number of subroutine calls is what makes the difference of expected O(n) and worst case (n^3). The number next to the routine names is the number of calls to that routine.

**Runtime**:  how the big-O is “measured” here. It has no unit. Animating the computation of the miniDisc required stopping the algorithm, saving and restoring states, in order repaint( ) the visuals. I chose to stop the algorithm to update visuals whenever  a new disc was computed. I thought a good approximate “measure” of runtime would be how many times a new disc is computed, because this would be independent of animation delay and real-time things.

**Other notes about interface**
When not in ‘Dynamic’ mode, the screen is pannable. After adding points to the plane, ‘Compute’ computes the miniDisc.  After computation finishes, points can be further added/appended. ‘Dynamic’ and ‘Random’ mode will erase all points on the plane. ‘Compute’ will not reset the points on the plane. So, transitioning from dynamic->compute and random->compute works.

**Notes about algorithm**
The only “optimization” I did (according to textbook) and keep a permutation on set P for all routines instead of computing the permutation for each routine. 

With randomization, a disc is only computed a certain number of times in the early iterations of the outermost loop via subroutine calls, then for a good majority of the rest of outer loop, there is no need for a new disc to be computed anymore, which allows the algorithm to finish in near linear time.
