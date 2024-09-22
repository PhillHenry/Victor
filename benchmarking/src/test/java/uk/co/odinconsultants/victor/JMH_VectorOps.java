package uk.co.odinconsultants.victor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static uk.ac.manchester.tornado.api.types.arrays.FloatArray.fromArray;

/**
 * See https://medium.com/@Styp/java-18-vector-api-do-we-get-free-speed-up-c4510eda50d2
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMH_VectorOps {

    private final AdvancedVectorExtensionsOps aveOps = new AdvancedVectorExtensionsOps();
    private final GPUOps gpuOps = new GPUOps();

    private final int n = 8192;

    public static float[] createRandomVector(int n) {
        var random = new Random();
        var vec = new float[n];
        for (int i = 0 ; i < n ; i++) {
            vec[i] = random.nextFloat();
        }
        return vec;
    }

    private final float[] x = createRandomVector(n);
    private final float[] y = createRandomVector(n);
    private final FloatArray X = fromArray(x);
    private final FloatArray Y = fromArray(y);
    private final FloatArray result = new FloatArray(1);

    @Benchmark
    public float usingGPU() {
        gpuOps.dotFloatArrayReducingWithAnnotations(X, Y, result);
        return result.get(0);
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
