Instructions to run Solver.java in Linux
----------------

Tested with openjdk version "11.0.16", java version "18.0.2.1" and "19.0.1"

To compile the solver use "find . -name "*.java" -print | xargs javac"

In the folder src do "jar cfm Solver.jar manifest.txt Solver.class Solver\$SolverResult.class Vertex.class Graph.class"

Finally run the jar file with "java -jar Solver.jar" passing a file as stdin. Example: java -jar < ../example-input.txt

