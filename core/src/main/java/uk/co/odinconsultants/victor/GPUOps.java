package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.enums.ProfilerMode;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

public class GPUOps {

    public static final int WARMING_UP_ITERATIONS = 15;

    /**
     * This is broken as it only computes the first value.
     * The reason seems to be that the @Parallel must work over the
     */
    public static void dotFloatArrayBroken(FloatArray A, final FloatArray B, FloatArray result, final int size) {
        float sum = 0;
        for (@Parallel int i = 0; i < size; i++) {
            sum += A.get(i) * B.get(i);
        }
        result.set(0, sum);
    }

    public static void dotFloatArray(FloatArray A, final FloatArray B, FloatArray result, final int size) {
        for (@Parallel int i = 0; i < size; i++) {
            result.set(i, A.get(i) * B.get(i));
        }
    }

    public static void vecMultiply(final float[] A, final float[] B, float[] result, final int size) {
        for (@Parallel int i = 0; i < size; i++) {
            result[i] = A[i] * B[i];
        }
    }


    static float reduce(float[] xs) {
        float sum = 0f;
        for (int i = 0 ; i < xs.length ; i++) {
            sum += xs[i];
        }
        return sum;
    }


    public float dot(final FloatArray A, final FloatArray B, int size) {
        FloatArray result = new FloatArray(size);
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B, result)
                .task("t0", GPUOps::dotFloatArray, A, B, result, size)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.withProfiler(ProfilerMode.CONSOLE);
        executionPlan.execute();
        return reduce(result.toHeapArray());
    }

    public float dot(final float[] A, final float[] B) {
        float[] result = new float[A.length];
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B)
                .task("t0", GPUOps::vecMultiply, A, B, result, A.length)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.execute();
        return reduce(result);
    }

    public static void main(String[] args) {
        var smokeTest = new GPUOps();
        smokeTest.dot(new float[] {1f, 2f}, new float[] {3f, 4f});
	    System.out.println("Finished smoke test");
    }
}
