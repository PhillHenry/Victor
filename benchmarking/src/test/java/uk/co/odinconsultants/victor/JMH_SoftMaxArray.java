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

import static uk.co.odinconsultants.victor.SoftMax.softMaxInPlaceGPUArray;
import static uk.co.odinconsultants.victor.SoftMax.taskGraph;

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
    TornadoExecutionPlan executor;
    FloatArray sum = new FloatArray(n);

    @Setup
    public void setup() {
        for (int i = 0; i < n * n; i++) {
            m.set(i, (float)Math.random());
        }
        executor = taskGraph(m, sum, n, n);
        executor.withWarmUp();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2, time = 30, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    public float softMaxGPU() {
        executor.execute();
        return m.get(0);
    }

//    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2, time = 30, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    public float softMaxCPU() {
        softMaxInPlaceGPUArray(m, sum, n, n);
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
