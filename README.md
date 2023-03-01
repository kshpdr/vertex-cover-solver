#Vertex Cover Solver
This project is a collection of algorithms and heuristics for solving the vertex cover problem. The project consists of five iterations, each containing a task-X folder with handouts, READMEs, and presentations describing the features implemented in that iteration.

##Features
###Iteration 1
The first iteration of the project implemented a basic local search algorithm for solving the vertex cover problem. The algorithm starts with an arbitrary solution and iteratively improves it by making small changes to the solution until no further improvements can be made.

###Iteration 2
The second iteration focused on improving the lower bounds of the vertex cover problem. Two lower bounds were implemented: the clique bound and the linear programming (LP) bound using bipartite graph. In addition, the branching strategy was modified to extract the vertex with the highest degree first.

###Iteration 3
The third iteration introduced multiple reduction rules to reduce the size of the problem instance. These rules included the zero-, one-, and two-degree rules, the high-degree and buzz rule, the domination rule, the unconfined rule, and the LP relaxation.

###Iteration 4
The fourth iteration implemented multiple heuristics for solving the vertex cover problem. These included the Clever Steady Strategy algorithm and Savage’s Vertex Cover Algorithm, among others. These heuristics were designed to quickly find good solutions to the problem, although they may not always produce an optimal solution.

###Iteration 5
The fifth and final iteration introduced a new branch-and-reduce algorithm with packing constraints. This algorithm added a new way to prune the search tree by using packing constraints to eliminate parts of the search space that cannot contain a feasible solution. In addition, new reduction rules were implemented to further reduce the size of the problem instance. Finally, a Maximum Clique Solver was integrated to solve some specific instances of the vertex cover problem.

##How to Run
Each task-X folder contains a README file with instructions on how to run the implementation for that iteration. Please refer to the appropriate README file for instructions on how to run the algorithm or heuristic for that iteration.

##Credits
This project was developed by Denis Koshelev, Julio Cesar Perez Duran, and Julian Fechner as part of the course 'Algorithm Engineering' at the Technical University of Berlin. Big thanks to André Nichterlein and Niclas Boehmer, who guided us through this course and made it really joyful. If you have any questions or suggestions, please feel free to contact us.
