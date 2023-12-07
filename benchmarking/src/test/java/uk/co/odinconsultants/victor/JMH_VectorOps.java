package uk.co.odinconsultants.victor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMH_VectorOps {

    private final int n = 32;

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
    public float measureRight() {
        return VectorOps.vectorFMA(x, y);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_VectorOps.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
