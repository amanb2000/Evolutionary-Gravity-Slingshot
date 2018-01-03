# Evolutionary-Gravity-Slingshot
**What is this?**

This is a program written in Processing 3 to visualize and explore how spacecraft can use the gravitational fields of planets to slingshot their way towards their target faster than would otherwise be possible.

**How does it work?**

In order to optimize the initial ship launch angle and speed, evolution is employed. Two potential ship paths are 'competed' by testing to see how close they get to the goal planet. The one that gets closest to the planet survives into the next mutation. A mutated version of the surviving ship path (with slightly different initial speed and angle) is then competed against the ship path from the last generation. This process is repeated over and over until an optimal ship path is reached. 

**Why?**

My physics teacher was talking about how Cassini used the technique and how it was very difficult to calculate so I decided to see how far I could get. Obviously this isn't as accurate as a real one, and apparently this technique is outdated. It's still fun to watch and it's educational both in terms of the physics and computer science. 
