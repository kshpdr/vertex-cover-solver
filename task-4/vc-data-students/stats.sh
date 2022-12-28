#!/bin/bash

csv_file=$1
if [ -z "$csv_file" ] ; then
	echo "[*] USAGE: stats.sh <CSV-FILE>"
	exit 1
fi

tests_total=266
tests_solved=$(grep -P "(OK;OK|1;OK;;)" $csv_file | wc -l)
tests_failed=$(grep "Not a VC" $csv_file | wc -l)
tests_timeout=$(grep -Po "^[^;]*;[^;]*;[^;]*;[^;]*;[^;]*;[^;]*;0;" $csv_file | wc -l)
too_large=$(grep ">>INCORRECT" $csv_file)
if [ -z "$too_large" ] ; then
	tests_larger=0
else
	tests_larger=$(echo "$too_large" | wc -l)
fi

tests_run=$(grep -P ".*.dimacs" $csv_file | wc -l)
tests_skipped=$(( $tests_total - $tests_run))


solution_sizes=( $(echo "$too_large" | grep -Po "^[^;]+;[^;]+;[^;]+;[^;]+;(\K[^;]+);" | sed 's/;//') )
size_diffs=( $(echo "$too_large" | grep -Po "(\d+);$" | sed 's/;//') )
files=( $(echo "$too_large" | grep -Po "^[^;]+;" | sed 's/;//') )
times=( $(echo "$too_large" | grep -Po "^[^;]+;(\K[^;]+);" | sed 's/;//') )

len=${#size_diffs[@]}
max=0
avg=0

stats_file=$(echo $csv_file | sed 's/results-/stats-/')
echo "file;time;solver;diff;opt;pct;avg;max" > $stats_file

for ((i=0;i<len;i++))
do
	sol=${solution_sizes[$i]}
	diff=${size_diffs[$i]}
	opt=$(( $sol - $diff ))
	if [ $opt -ne 0 ] ; then
		pct=$(echo $diff $opt | awk '{print $1 / $2 * 100}')
	else
		pct="0"
	fi
	avg=$(echo $avg $pct $len | awk '{print $1 + $2 / $3}')
	max=$(echo $max $pct | awk '{print $1 >= $2 ? $1 : $2}')
	f=${files[$i]}
	tm=${times[$i]}
	echo "$f;$tm;$sol;$diff;$opt;$pct;$avg;$max" >> $stats_file
done

echo "====== STATS ======"
echo "[*] Solved (OK;OK)          : $tests_solved / $tests_total" "("$(echo $tests_solved $tests_total | awk '{print int($1 / $2 * 100 + 0.5)}')"%)"
echo "[*] Timeout (0)             : $tests_timeout / $tests_total" "("$(echo $tests_timeout $tests_total | awk '{print int($1 / $2 * 100 + 0.5)}')"%)"
echo "[*] Invalid (Not a VC)      : $tests_failed / $tests_total" "("$(echo $tests_failed $tests_total | awk '{print int($1 / $2 * 100 + 0.5)}')"%)"
echo "[*] Too-Large (>>INCORRECT) : $tests_larger / $tests_total" "("$(echo $tests_larger $tests_total | awk '{print int($1 / $2 * 100 + 0.5)}')"%)"
echo "[*] Skipped tests           : $tests_skipped / $tests_total" "("$(echo $tests_skipped $tests_total | awk '{print int($1 / $2 * 100 + 0.5)}')"%)"
echo "[*] AVG Too-Large           : $avg %"
echo "[*] MAX Too-Large           : $max %"
echo "====== STATS ======"
echo "=> See details in stats file: $stats_file"
