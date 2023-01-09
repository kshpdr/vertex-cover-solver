Instructions to run Code in Linux
----------------

Tested with openjdk version "11.0.16", java version "18.0.2.1" and "19.0.1"

----------------

Code Compilation (for all):  
To compile the code use "find . -name "*.java" -print | xargs javac"

In the folder src do "jar cfm Solver.jar manifest.txt *.class"

Install python modules via "pip install -r requirements.txt"

----------------
Execute SAT-Solver:  

Use the following commands to run the SAT-solver passing a file as stdin:
- only SAT: `python3 sat.py < graph.dimacs`
- Reduction and SAT: `bash reduction_and_sat.sh < graph.dimacs
