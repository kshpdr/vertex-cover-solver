#!/bin/bash

find . -name "*.java" -print | xargs javac
rc=$?
if [ $rc -ne 0 ] ; then
	echo "[ERROR] Comppilation of JAVA-Code failed with rc=$rc"
	exit 1
fi

jar cfm Solver.jar manifest.txt Solver.class Solver\$SolverResult.class Vertex.class Graph.class *.class
rc=$?
if [ $rc -ne 0 ] ; then
	echo "[ERROR] Creating JAR-File failed with rc=$rc"
	exit 1
fi

echo "[*] Program successfully compiled! Execute: 'java -jar Solver.jar'"
exit 0
