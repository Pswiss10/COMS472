This is Proj2 for COMS 472/572 where we needed to create two AI agents (two separate algorithms) to play checkers against a human player. These Algorithms were Alpha-Beta Pruning and Monte-Carlo tree search.

notes for running algorithms:

Alpha Beta:
You can change the depth variable within the alphaBetaSearch method to be larger but the algorithm will run for a long time. Max depth I was able to do and somewhat play was 12

MCTS:
The calculations for UCT are done within MCTree class. In that class there is a variable called CVal that you can change to change the C value for the UCT calculation.

Also, in MonteCarlo, you can change the number of iterations to be as large as you want. I was able to run with 2000 pretty easily but higher than that will make the agents time between moves longer.
