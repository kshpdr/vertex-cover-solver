import sys

with open(sys.argv[1]) as a, open(sys.argv[2]) as b:
    lines_a = a.readlines()
    lines_b = b.readlines()

f = open("compare.csv", "a")

f.write("file;Type;Atime;Btime\n")

results_a = {}
results_b = {}

for i in range(1, len(lines_a)-1):
    line_a = lines_a[i].split(";")
    results_a[line_a[0]] = line_a[1]

for i in range(1, len(lines_b)-1):
    line_b = lines_b[i].split(";")
    results_b[line_b[0]] = line_b[1]

for key in results_a.keys():
    type = key.split("/")[0][2:]
    if key in results_b.keys():
        f.write(f"{key};{type};{results_a[key]};{results_b[key]}\n")
    else:
        f.write(f"{key};{type};{results_a[key]};;\n")

for key in results_b.keys():
    type = key.split("/")[0][2:]
    if key not in results_a.keys():
        f.write(f"{key};{type};;{results_b[key]}\n")

f.write('\n')
f.close()
