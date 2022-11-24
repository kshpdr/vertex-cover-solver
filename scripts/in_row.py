import sys

with open(sys.argv[1]) as a:
    lines_a = a.readlines()

f = open("row.csv", "a")

f.write("file;A-sorted\n")

results_a = {}

for i in range(1, len(lines_a)-1):
    line_a = lines_a[i].split(";")
    results_a[line_a[0]] = line_a

time_a = 0

for key in results_a.keys():
    if results_a[key][1] != '':
        time_a += float(results_a[key][1])
        f.write(f"{key};{round(time_a, 2)}\n")

f.write('\n')
f.close()
