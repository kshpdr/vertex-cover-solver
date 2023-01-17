Instructions to run Solver.java in Linux
----------------

Tested with openjdk version "11.0.16", java version "18.0.2.1" and "19.0.1"

To compile the solver use "find . -name "*.java" -print | xargs javac"

In the folder src do "jar cfm Solver.jar manifest.txt *.class"

To run the jar file for the Solver run "java -jar Solver.jar" passing a file as stdin. Example: java -jar Solver.jar < ../example-input.txt

To run the program to reduced the graph run "java Graph" passing a file as stdin. Example: java Graph < ../example-input.txt

