import sys

with open(sys.argv[1]) as a, open(sys.argv[2]) as b:
    lines_a = a.readlines()
    lines_b = b.readlines()

f = open("plots/after-lp-bound/lp-row.csv", "a")

f.write("file;Atime;Btime\n")

results_a = {}
results_b = {}

for i in range(1, len(lines_a)-1):
    line_a = lines_a[i].split(";")
    results_a[line_a[0]] = line_a[1]

for i in range(1, len(lines_b)-1):
    line_b = lines_b[i].split(";")
    results_b[line_b[0]] = line_b[1]

instances = set(results_a)
instances.update(set(results_b))

time_a = 0
time_b = 0

for instance in sorted(instances):
    if instance in results_a.keys() and instance in results_b.keys():
        time_a += float(results_a[instance])
        time_b += float(results_b[instance])
        f.write(f"{instance};{round(time_a, 2)};{round(time_b, 2)}\n")
    elif instance in results_a.keys():
        time_a += float(results_a[instance])
        f.write(f"{instance};{round(time_a, 2)};\n")
    elif instance in results_b.keys():
        print(instance)
        time_b += float(results_b[instance])
        f.write(f"{instance};;{round(time_b, 2)}\n")

f.write('\n')
f.close()
