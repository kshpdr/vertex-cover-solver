Instructions to run Code in Linux
----------------

Tested with openjdk version "11.0.16", java version "18.0.2.1" and "19.0.1"

----------------

Code Compilation (for all):  
To compile the code use "find . -name "*.java" -print | xargs javac"

In the folder src do "jar cfm Solver.jar manifest.txt *.class"


----------------

Execute Solver:  
To run the jar file for the Solver run "java -jar Solver.jar" passing a file as stdin. Example: java -jar Solver.jar < ../example-input.txt


----------------

Execute Reduction Rules:  
To run the program to reduce the graph run "java Graph" passing a file as stdin. Example: java Graph < ../example-input.txt


----------------
Execute ILP-Solver:  

Use the following commands to run the ILP-solver passing a file as stdin:
- only ILP: `bash only_ilp.sh < graph.dimacs`
- Reduction and ILP: `bash reduction_and_ilp.sh < graph.dimacs`