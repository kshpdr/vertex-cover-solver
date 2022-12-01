# Benchmark script for testing programs

Last updated: 11.10.2022

## Purpose

This bash script executes your solver on the given set of instances. 
The script enforces the time limit, checks your solutions, and outputs statistics on the console and in a csv. 

## System-requirements

This is a bash script using some basic linux commands (available on many distributions). 
It is tested on the machines we provide.
If you use windows, then WSL (Windows Subsystem for Linux) might be an option.
Interested Mac-users can also make the script run with some minor changes (exchanging some specific commands).
In case of issues with your system, you can ask for help in the office hours.

## Usage

### Quick run

If all files in the zip (the folders with the instances, *benchmark-fast.sh*, *verify*, and *verify.py*) are in the current folder, then the easiest use is as follows:

```
./benchmark-fast.sh "path-to-your-solver/your-vertex-cover program"
```

For example, if you compiled an executable binary *my-vc-solver* which is also in the same folder as the script, then use:

```
./benchmark-fast.sh "./my-vc-solver"
```

Alternatively, java or python programs might be called by:

```
./benchmark-fast.sh "java -jar mySolver.jar"

./benchmark-fast.sh "python3 mySolver.py"
```

### Parameters

The script takes up to three parameters:

1. Your program
2. The time limit per instance in seconds. Default value is 300.
3. The number of instances per category that are allowed to not be solved within the time limit. 
All instance within a folder are of one category.
Once this limit is reached, the script continues with the next category of instances and skips whatever number of instances in the current category are left. This is intended as time saving for testing as instances are sorted by size (and *roughly* by difficulty). Default value is 10.

All three parameters can also be set in the script (see first couple of lines of the script).

If you just want a quick test run, then you might want to call the script as follows (2 second time limit and 3 timeouts allowed per category):

```
./benchmark-fast.sh "./my-vc-solver" 2 3
```

## Output of the script

### Files

The script outputs results of the current test run in the console and in a csv-file. 
Moreover, there is a log file containing the output of your solver on each tested instance (look there if for errors etc.).
The script also produces some temporary files (e.g. *prog_out.txt* and *time.txt*). These files should be removed after the end of the script but can remain if the script is terminated with e.g. ctrl-c.

All files are produced in the currently active folder. Assume your folder structure looks as follows. You have 3 subfolders:

 - *instances* (with the content of the zip-file we provide, including the bash script), 
 - *solver* (a folder containing the source and latest binary of your program), and 
 - *logs* (here you want the results of all test runs).

Then, go to the *logs* and run the script via:

```
../instances/benchmark-fast.sh "../solver/my-vc-solver"
```

The csv and log-files will be saved in your current folder *logs*. These files are marked with the current date and time (in the format Year-Month-Day-Hour-Minutes-Seconds).
These files can also saved in a subfolder. For this adjust the following lines in the script:

```
LOG="log-$today.txt"							# specify the name of the log file

CSV="results-$today.csv"
```

For example, by setting
```
LOG="./only-logs/log-$today.txt"							# specify the name of the log file
```
all log files will be saved in the subfolder *only-logs* (make sure this is a subfolder in your current directory).

### Columns description

The output in the console and the csv-file should only have cosmetic differences (additional spaces in the console for better readability; no spaces in the csv-file for easy analysis with appropriate tools).
In particular, the columns both in the console and the csv have the same meaning (column names taken from csv-file):

1. **file:** Filename of the instance
2. **time:** Time it took your solver to solve the instance. This field is empty if the solver reached the time limit
3. **n:** The number of vertices in the input graph.
3. **m:** The number of edges in the input graph.
4. **solsize**: The size of the solution returned by your program. This is the number of lines not starting with a #, so do not add empty lines in your output!
5. **recsteps**: The number of recursive steps in your program. Your program needs to output ``#recursive steps: xxx`` where ``xxx`` is a nonnegative integer and ``xxx`` is passed by the script to the output. If your program does not out this line, then this field remains empty.
6. **finished**: Contains a ``1`` if your program finished within the time limit. If your program crashes or reaches the time limit, then this column contains a ``0``.
7. **VC-check**: Did your program output a valid vertex cover, that is, are all edges covered? Contains ``OK`` if this is the case. If any other string appears here you have a problem! Note that the vertex cover can be suboptimal, i.e. too large and still pass this test.
8. **size-check**: If your program returns a valid vertex cover (``OK`` in previous column), then here the size of your vertex cover is compared against our (best) solution (this is the number stored in the .solution file). If both are the same size, then the column contains ``OK``. If we do not provide a solution, then this field is empty. Other text in this field should be self-explanatory...
9. **diff-to-opt**: If your program returns a valid vertex cover, then the difference of the sizes of our solution and the one your program returns is given. Thus, if both previous test are passed with ``OK``, then this column contains ``0``. Positive numbers here means your solution is too large; negative numbers mean you found a better solution than we did (please tell us!). This field is empty if we do not provide a solution size.
10. **last-k-diff-to-opt**: Optional output if your program runs in the time limit. If your program outputs ``last-k: xxx`` then the number ``xxx`` is passed in this field.
To output this line we suggest to implement a basic signal handling. (See https://en.wikipedia.org/wiki/Signal_(IPC) for an overview on what signals we mean.) 
More precisely, your solver receives a ``SIGINT`` signal when reaching the timeout. If your program handles this signal, by e.g. outputting a ``last-k: xxx`` and exiting the program then the script can pass on ``xxx``. Please be aware that after a couple of seconds after ``SIGINT`` was send your program will be killed brutally (without it having a chance to do anything, so use these seconds wisely).
