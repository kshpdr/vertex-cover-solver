#!/bin/bash

#echo "$PWD"

# Script to run a Python and C script and parse the output to a separate file
# Input file
#input_file=$1
# Running the Python script and saving the output to a file
#python_output=$(python3 ../../task-5/scripts/get_complement_graph.py)
python_output=$(python3 ./get_complement_graph.py)

read -r first_line < "./complement/output.txt"
#read -r first_line < "../../task-5/scripts/complement/output.txt"
number=$(echo $first_line | cut -d' ' -f3)

# Running the C script and saving the output to a file
#c_output=$(../../task-5/scripts/MoMC "../../task-5/scripts/complement/output.txt" )
c_output=$(./MoMC "./complement/output.txt" )
#echo "$c_output" > "../../task-5/scripts/c_output/output.txt"
echo "$c_output" > "./c_output/output.txt"

#line=$(grep "^M" "../../task-5/scripts/c_output/output.txt")
line=$(grep "^M" "./c_output/output.txt")

echo $line
out=""
for i in $(seq 1 $number); do
  if ! echo $line | grep -qw $i; then
    out="${out}${i}\n"
  fi
done
echo $out
echo $out
