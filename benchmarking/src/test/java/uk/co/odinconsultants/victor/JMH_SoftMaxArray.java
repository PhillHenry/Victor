package uk.co.odinconsultants.victor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(6)
@Fork(3)
public class JMH_SoftMaxArray {

    @Param({"8"})
    public int arg;

    private final int n = (int)Math.pow(2, arg);
    private final FloatArray m = new FloatArray(n * n);
    private TaskGraph t;
    TornadoExecutionPlan executor;
    FloatArray sum = new FloatArray(n);

    @Setup
    public void setup() {
        for (int i = 0; i < n * n; i++) {
            m.set(i, (float)Math.random());
        }
        t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, m, sum)
                .task("t0", SoftMax::expInPlaceArray, m, n, n)
                .task("t1", SoftMax::sumArray, m, sum, n, n)
                .task("t2", SoftMax::divideInPlaceArray, m, sum, n, n)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, m);
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        executor = new TornadoExecutionPlan(immutableTaskGraph);
        executor.withWarmUp();
    }

    @Benchmark
    public float softMaxGPU() {
        executor.execute();
        return m.get(0);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_SoftMaxArray.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
