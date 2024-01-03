package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

public class GPUOps {

    public static final int WARMING_UP_ITERATIONS = 15;

    public static void vecMultiply(final float[] A, final float[] B, float[] result, final int size) {
        float sum = 0.0f;
        for (@Parallel int i = 0; i < size; i++) {
            sum += A[i] * B[i];
        }
        result[0] = sum;
    }

    public void dot(final float[] A, final float[] B) {
        float[] result = new float[1];
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, A, B)
                .task("t0", GPUOps::vecMultiply, A, B, result, A.length)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.execute();
    }

    public static void main(String[] args) {
        var smokeTest = new GPUOps();
        smokeTest.dot(new float[] {1f, 2f}, new float[] {3f, 4f});
	    System.out.println("Finished smoke test");
    }
}
