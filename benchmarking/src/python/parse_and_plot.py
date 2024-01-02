import sys 
import re
import math
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
           if "(min, avg, max)" in line:
               print(line)
               vals = re.findall(r"[\d\.]+", line)
               result = Result(vals[0], vals[1], vals[2])
               results_for_mode = results.get(current_type, [])
               results[current_type] = results_for_mode + [result]

    return results


def plot_results(results: dict):
    xs = [32,512,1024,2048,4096,8192,16384,32768,65536,131072,262144,524288,1048576,2097152,4194304,8388608]
    xs_log = [math.log10(x) for x in xs]
    plots = {}
    for mode in results:
        results_for_mode = results[mode]
        ys = []
        for result in results_for_mode:
            y = math.log10(float(result.avg))
            ys = ys + [y]
        plots[mode] = ys
        plt.plot(xs_log, ys)
    print(plots)
    plt.xlabel("Vector size (log10)")
    plt.ylabel("Time per action (log10) / ns")
    plt.title("Vector multiplication times")
    plt.legend(TYPE, loc='lower right')
    plt.show()


if __name__ == "__main__":
    logs = sys.argv[1]
    print(f"Opening file {logs}")
    results = read(logs)
    plot_results(results)
