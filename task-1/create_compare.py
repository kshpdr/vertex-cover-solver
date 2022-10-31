import sys

with open(sys.argv[1]) as a, open(sys.argv[2]) as b:
    lines_a = a.readlines()
    lines_b = b.readlines()

f = open("compare.csv", "a")

f.write("file;Atime;Btime\n")
for i in range(1, len(lines_a)-1):
    line_a = lines_a[i].split(";")
    line_b = lines_b[i].split(";")
    f.write(line_a[0] + ';' + line_a[1] + ';' + line_b[1] + '\n')

f.write('\n')
f.close()
