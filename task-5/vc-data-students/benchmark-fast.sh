#!/bin/bash

useBinaryVerifier=false   # true: Validation done with binary (faster, but might not work on every system); false: Validation done with python script

if [ -z "$1" ]; then
	PROGRAMM_NAME="./my-solver"									  		# insert your program here ...
else
	PROGRAMM_NAME=$1													# ... or give the program as parameter to the script
fi

if [ -z "$2" ]; then
	maxSecPerInstance=60							# insert allowed time (in seconds) for one instance here ...
else
	maxSecPerInstance=$2							# ... or give the program as parameter to the script
fi

if [ -z "$3" ]; then
	maxNotSolved=10									# insert no of instances the program is allowed to fail to solve (if reached, then the script goes to the next folder) here ...
else
	maxNotSolved=$3									# ... or give the program as parameter to the script
fi

today=$(date +%Y-%m-%d-%H-%M-%S)

LOG="log-$today.txt"							# specify the name of the log file
maxSec=432000									# overall allowed time for the whole script

CSV="results-$today.csv"

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

run_ce_solver()
{

	FILES=$(ls $1*.dimacs)

	rm -f time.txt

	overallTime=$(date +%s);
	now=$(date +%s);
	elapsed=`expr $now - $overallTime`;
	notSolved=0
	showedHint=0

	for f in $FILES
	do
		if [ $elapsed -le $maxSec -a $notSolved -le $maxNotSolved ]; then
			echo $f >> $LOG
			
			# start everything in a new process group such that we can kill everything if necessary
			(setsid /usr/bin/time -f "%e" -a -o time.txt timeout --preserve-status -k 10 -s 2 $maxSecPerInstance $PROGRAMM_NAME< $f 1> prog_out.txt 2>&1) & PID=$!


			# kill processes when exiting this script
			trap "{ kill $PID 2>/dev/null; kill -TERM -- -$(pgrep -P $PID)>/dev/null;}" TERM
			trap "{ kill -9 -$PID 2>/dev/null; kill -9 -- -$(pgrep -P $PID)>/dev/null;}" EXIT

			wait $PID

			# just to be sure: if the process still is around brutally kill it
			kill -0 $PID 2>/dev/null || kill -9 -$PID 2>/dev/null;

			# get n
			n=$(head -1 $f | sed 's/#//' | sed 's/ .*//')

			# get m
			m=$(head -1 $f | sed 's/#.* //')
						
			# get k
			k=$(grep -ve "^#" prog_out.txt | wc -l)
			recursiveSteps=$(grep -e "#recursive steps:" prog_out.txt | sed -e 's/.*recursive steps: \([0-9]*\).*/\1/' )
			lastK=$(grep -e "last-k:" prog_out.txt | sed -e 's/.*last-k: \([0-9]*\).*/\1/' )
			cat prog_out.txt >> $LOG
			
			# get time
			time=$(cat time.txt);
			
			if [[ $time == "Command terminated by signal 9"* ]] || [[ $time == "Command exited with non-zero status"* ]]; then
				finished=0;
				(( notSolved += 1 ));
				time="";
			else
				finished=1;
			fi
			
			## Test the solution
			# Get provided solution size (if existing)
			solFile=$(basename $f .dimacs)
			solNumber="";
			if [ -f "$data/$solFile.solution" ]; then
				solNumber=$(cat $data/$solFile.solution);
			fi
			
			verify="\t";
			notAVertexCover=""
			diffToOpt=""
			if [ "$finished" -eq "1" ]; then
			
				# Is user solution a valid vertex cover?
				if [ "$useBinaryVerifier" = true ] ; then
					isVC=$($DIR/verify $f prog_out.txt | sed -e 's/.*2: \([0-9]*\).*/\1/')
					if [ -n "$isVC" ] && [ $isVC -gt 0 ] 2>/dev/null; then
						notAVertexCover="Not a VC: (uncovered edges *2: $isVC)";
					else
						notAVertexCover="OK"
					fi
				else
					msg=$($DIR/verifier.py $f prog_out.txt)
					if [ $? != 0 ]; then
						notAVertexCover="Not a VC: $msg";
					else
						notAVertexCover="OK"
					fi
				fi
				
				# If user solution is a vertex cover check the size and compare it against provided solution size
				if [ "$notAVertexCover" = "OK" ] && [ -n "$solNumber" ] && [ "$solNumber" -eq "$solNumber" ] 2>/dev/null; then
					if [ "$solNumber" -eq "$k" ]; then
						verify="OK\t0";
					elif [ "$solNumber" -gt "$k" ]; then
						verify=">>Your solution ($k) is better than mine($solNumber)!<<\t$(($k-$solNumber))";
					else
						verify=">>INCORRECT Size; Solution too large:<< \t$(($k-$solNumber))";
					fi
# 				else
# 					verify="I don't know the solution size"
				fi
			else 
				# Program timed out: if last-k is provided then parse and return it
				if [ -n "$lastK" ] 2>/dev/null; then
					if [ -n "$solNumber" ]  && [ "$solNumber" -eq "$solNumber" ] 2>/dev/null; then
						diffToOpt=$(($solNumber-$lastK));
					else
						diffToOpt="Last k: $lastK ... We don't now the optimum solution size. :("
					fi
				fi
			fi
			## End of testing the program solution
			
			rm -f prog_out.txt
			
			fileNameLong=$(printf '%-40s' "$(basename $f)");
			
			#logging
			echo -e "$fileNameLong\t"$time"\t"$k"\t"$recursiveSteps"\t"$finished"\t"$notAVertexCover"\t"$verify"\t"$diffToOpt
			echo -e "$(basename $data)/$(basename $f)\t"$time"\t"$n"\t"$m"\t"$k"\t"$recursiveSteps"\t"$finished"\t"$notAVertexCover"\t"$verify"\t"$diffToOpt | sed 's/	/;/g' >> $CSV
			echo "" >> $LOG
			
			rm -f time.txt

			now=$(date +%s);
			elapsed=`expr $now - $overallTime`;
		else
			if [ $showedHint -eq 0 ]; then
				if [ $notSolved -ge $maxNotSolved ]; then
					echo "$notSolved instances not solved. Script aborted."
				else
					echo "maximal time of $maxSec sec elapsed. Script aborted."
				fi
				showedHint=1;
			fi		
		fi
	done
}

echo "file;time;n;m;solsize;recsteps;finished;VC-check;size-check;diff-to-opt;last-k-diff-to-opt" > $CSV
## now loop through data set directories
for data in $(find $DIR -mindepth 1 -maxdepth 1 -type d); do
	FILENAME=$(basename $data)
	echo "run $data instances with $PROGRAMM_NAME"
	echo "(Tab-separated columns: File, Time in seconds, solution size, recursive steps, finished, VC verified, solution size verified, diff-to-opt, last-k-diff-to-opt)"
	run_ce_solver $data/
done

echo ""
