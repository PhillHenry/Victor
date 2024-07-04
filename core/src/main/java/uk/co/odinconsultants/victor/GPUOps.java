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

    public static void dotFloatArray(FloatArray A, final FloatArray B, FloatArray result, final int size) {
        float sum = 0;
        for (@Parallel int i = 0; i < size; i++) {
            sum += A.get(i) * B.get(i);
        }
        result.set(0, sum);
    }

    public static void vecMultiply(final float[] A, final float[] B, float[] result, final int size) {
        float sum = 0.0f;
        for (@Parallel int i = 0; i < size; i++) {
            sum += A[i] * B[i];
        }
        result[0] = sum;
    }

    public float dot(final FloatArray A, final FloatArray B, int size) {
        FloatArray result = new FloatArray(1);
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B, result)
                .task("t0", GPUOps::dotFloatArray, A, B, result, size)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.withProfiler(ProfilerMode.CONSOLE);
        executionPlan.execute();
        return result.get(0);
    }

    public float dot(final float[] A, final float[] B) {
        float[] result = new float[1];
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B)
                .task("t0", GPUOps::vecMultiply, A, B, result, A.length)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.execute();
        return result[0];
    }

    public static void main(String[] args) {
        var smokeTest = new GPUOps();
        smokeTest.dot(new float[] {1f, 2f}, new float[] {3f, 4f});
	    System.out.println("Finished smoke test");
    }
}
