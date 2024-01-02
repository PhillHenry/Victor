import sys 
import re
import matplotlib.pyplot as plt


TYPE = ["usingPlainOldJava", "usingGPU", "usingJEP426"]

class Result:
    def __init__(self, minimum, avg, maximum):
        self.minimum = minimum
        self.avg = avg 
        self.maximum = maximum

    def __str__(self):
        return f"{self.minimum}"


def read(logs):
    results = {}
    with open(logs, "r") as f:
        for line in f.readlines():
           line = line.rstrip()
           for mode in TYPE:
               if mode in line:
                   current_type = mode
                   print(line)
               if "stdev" in line:
                   vals = re.findall(r"[\d\.]+", line)
                   result = Result(vals[0], vals[1], vals[2])
                   results_for_mode = results.get(mode, [])
                   results[mode] = results_for_mode + [result]

    return results


def plot


if __name__ == "__main__":
    logs = sys.argv[1]
    print(f"Opening file {logs}")
    results = read(logs)
    print(results)

