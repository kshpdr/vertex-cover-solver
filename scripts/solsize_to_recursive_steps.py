import sys

with open(sys.argv[1]) as a, open(sys.argv[2]) as b:
    lines_a = a.readlines()
    lines_b = b.readlines()

f = open("solsize_to_recursive_steps", "a")

f.write("file;Asolsize;Asteps;Bsolsize;Bsteps\n")

results_a = {}
results_b = {}

for i in range(1, len(lines_a)-1):
    line_a = lines_a[i].split(";")
    results_a[line_a[0]] = line_a

for i in range(1, len(lines_b)-1):
    line_b = lines_b[i].split(";")
    results_b[line_b[0]] = line_b

instances = set(results_a)
instances.update(set(results_b))

for instance in sorted(instances):
    if instance in results_a.keys() and instance in results_b.keys():
        f.write(f"{instance};{results_a[instance][4]};{results_a[instance][5]};{results_b[instance][4]};{results_b[instance][5]}\n")
    elif instance in results_a.keys():
        f.write(f"{instance};{results_a[instance][4]};{results_a[instance][5]};;\n")
    elif instance in results_b.keys():
        f.write(f"{instance};;;{results_b[instance][4]};{results_b[instance][5]}\n")

f.write('\n')
f.close()
