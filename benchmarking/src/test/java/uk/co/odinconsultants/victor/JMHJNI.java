package uk.co.odinconsultants.victor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHJNI {

    private int DO_NOT_OPTIMIZE = 0;
    private long DO_NOT_OPTIMIZE_Long = 0L;

    static {
        String library = String.format("%s/libtrivia.so", JMHJNI.class.getClassLoader().getResource(".").toString().substring(5));
        System.out.println("Loading " + library);
        System.load(library);
    }

    // Declare native method
    public native int randInt();

    @Benchmark
    public long doKernelCall() {
        DO_NOT_OPTIMIZE_Long = System.currentTimeMillis() ^ DO_NOT_OPTIMIZE_Long;
        return DO_NOT_OPTIMIZE_Long;
    }

    @Benchmark
    public int jniCall() {
        DO_NOT_OPTIMIZE = randInt() ^ DO_NOT_OPTIMIZE;
        return DO_NOT_OPTIMIZE;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_VectorOps.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
