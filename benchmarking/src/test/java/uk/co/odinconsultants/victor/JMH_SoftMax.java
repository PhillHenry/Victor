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
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(6)
@Fork(3)
public class JMH_SoftMax {

    @Param({"8"})
    public int arg;
    private final int n = (int)Math.pow(2, arg);
    private final Matrix2DFloat m = new Matrix2DFloat(n, n);
    private TaskGraph t;
    TornadoExecutionPlan executor;
    FloatArray sum = new FloatArray(m.getNumColumns());

    @Setup
    public void setup() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m.set(i, j, (float)Math.random());
            }
        }
        t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, m, sum)
                .task("t0", SoftMax::expInPlace, m)
                .task("t1", SoftMax::sum, m, sum)
                .task("t2", SoftMax::divideInPlace, m, sum)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, m, sum);
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        executor = new TornadoExecutionPlan(immutableTaskGraph);
    }

    @Benchmark
    public float softMaxGPU() {
        executor.execute();
        return m.get(0, 0);
    }

    @Benchmark
    public float softMaxCPU() {
        SoftMax.softMaxInPlace(m, sum);
        return m.get(0, 0);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_SoftMax.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
