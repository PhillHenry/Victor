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
public class JMH_SoftMax {

    @Param({"10", "11", "12"})
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
                .task("t0", SoftMax::softMaxInPlaceGPU, m, sum)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, sum);
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        executor = new TornadoExecutionPlan(immutableTaskGraph);
    }

    @Benchmark
    public Matrix2DFloat gpuSoftMax() {
        executor.execute();
        return m;
    }

    @Benchmark
    public Matrix2DFloat softMaxCPU() {
        SoftMax.softMaxInPlace(m);
        return m;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_SoftMax.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
