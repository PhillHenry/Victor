package uk.co.odinconsultants.victor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * See https://medium.com/@Styp/java-18-vector-api-do-we-get-free-speed-up-c4510eda50d2
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMH_VectorOps {

    private final AdvancedVectorExtensionsOps aveOps = new AdvancedVectorExtensionsOps();
    private final GPUOps gpuOps = new GPUOps();

    private final int n = 8388608;

    public static float[] createRandomVector(int n) {
        var random = new Random();
        var vec = new float[n];
        for (int i = 0 ; i < n ; i++) {
            vec[i] = random.nextFloat();
        }
        return vec;
    }

    float[] x = createRandomVector(n);
    float[] y = createRandomVector(n);

    @Benchmark
    public void usingGPU() {
        gpuOps.dotReduceOnCPU(x, y);
    }
    @Benchmark
    public float usingJEP426() {
        return aveOps.vectorFMA(x, y);
    }
    @Benchmark
    public float usingPlainOldJava() {
        int c = 0;
        for (int i = 0; i < x.length; i++) { // Cleanup loop
            c += x[i] * y[i];
        }
        return c;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_VectorOps.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
