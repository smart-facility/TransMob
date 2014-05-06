#!/opt/local/bin/python

import csv, sys


# First, some error handling on command line arguments:
def print_usage():
    print("Usage: python generate_dwellings.py scenario_id")
    print("where scenario_id = 1, 2, or 3")
    sys.exit(1)

if (len(sys.argv)!=2):
    print_usage();

scenario = 1
try:
    scenario = int(sys.argv[1])
except:
    print_usage()
else:
    if ((scenario < 1) or (scenario > 3)):
        print_usage()

# Define light rail TZs, for scenario 3:
light_rail_TZs = {199,201,207,210,211,237,509,514,515,519,520,521,525,526,527}


header = ''
tables = dict()
for year in range(2006,2037):
    tables[year] = []

def scenario1(row):
    result = list(range(5))
    result[0] = row[0]
    for numbedrooms in range(1,5):
        result[numbedrooms] = row[numbedrooms]*1.005
    return result

def scenario2_initial_year(row):
    result=list(range(5))
    result[0] = row[0]
    result[1] = 1.1*row[1]
    result[2] = 1.1*row[2]
    result[3] = row[3]
    result[4] = row[4]
    return result

def scenario2_other_years(row):
    return scenario1(row)


def scenario3_initial_year(row):
    result=list(range(5))
    result[0] = row[0]
    if (result[0] in light_rail_TZs):
        result[1]=1.1*row[1]
        result[2]=1.1*row[2]
    else:
        result[1]=row[1]
        result[2]=row[2]
    result[3]=row[3]
    result[4]=row[4]
    return result

def scenario3_other_years(row):
    return scenario1(row)



with open('2006.csv') as csvfile:
    reader = csv.reader(csvfile)
    header = next(reader)
    for year in range(2006,2037):
        tables[year].append(header)
    for row in reader:
        tables[2006].append([int(x) for x in row])
    csvfile.close()

print(tables[2006])

if (scenario == 1):
    for year in range(2007,2037):
        for row in tables[year-1][1:]:
            tables[year].append(scenario1(row))

if (scenario == 2):
    for rowidx in range(1,len(tables[2006])):
        tables[2006][rowidx] = scenario2_initial_year(tables[2006][rowidx])
    for year in range(2007,2037):
        for row in tables[year-1][1:]:
            tables[year].append(scenario2_other_years(row))

if (scenario == 3):
    for rowidx in range(1,len(tables[2006])):
        tables[2006][rowidx] = scenario3_initial_year(tables[2006][rowidx])
    for year in range(2007,2037):
        for row in tables[year-1][1:]:
            tables[year].append(scenario3_other_years(row))


for year in range(2006,2037):
    for rowidx in range(1,len(tables[year])):
        tables[year][rowidx] = [str(round(x)) for x in tables[year][rowidx]]

print(tables[2008])

for year in range(2006,2037):
    with open(str(year)+'.csv','w') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerows(tables[year])
    csvfile.close()


