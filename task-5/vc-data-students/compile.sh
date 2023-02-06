#!/bin/bash

# Switch to script location
cd $(dirname -- "$( readlink -f -- "$0"; )";)
# Navigate to src folder and compile code
cd ../src && javac *.java && jar cfm Solver.jar manifest.txt *.class && echo "[*] Compilation successfull"
