package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.annotations.Reduce;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

public class GPUOps {

    public static void dotFloatArray(FloatArray A, final FloatArray B, FloatArray result) {
        int size = A.getSize();
        float sum = 0;
        for (int i = 0; i < size; i++) {
            sum += A.get(i) * B.get(i);
        }
        result.set(0, sum);
    }

    public static void multipleAndReduce(FloatArray A, final FloatArray B, @Reduce FloatArray result) {
        int size = A.getSize();
        for (@Parallel int i = 0; i < size; i++) {
            result.set(0, result.get(0) + A.get(i) * B.get(i));
        }
    }

    public static void dotFloatArrayReducingWithAnnotations(FloatArray A, final FloatArray B, @Reduce FloatArray result) {
        result.set(0, 0f);
        for (@Parallel int i = 0; i < A.getSize(); i++) {
            float value = A.get(i) * B.get(i);
            result.set(0, result.get(0) + value);
        }
    }

    public static void vecMultiply(final float[] A, final float[] B, float[] result) {
        int size = A.length;
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


    public float dotReduceOnGPU(final FloatArray A, final FloatArray B) {
        FloatArray result = new FloatArray(1);
        TaskGraph t = dotReduceTaskGraph(A, B, result);
        execute(t);
        return result.get(0);
    }

    public TaskGraph dotReduceTaskGraph(final FloatArray A, final FloatArray B, FloatArray result) {
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B, result)
                .task("t0", GPUOps::dotFloatArrayReducingWithAnnotations, A, B, result)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);
        return t;
    }

    public static void reduceByAdding(FloatArray A, @Reduce FloatArray result) {
        result.set(0, 0.0f);
        for (@Parallel int i = 0; i < A.getSize(); i++) {
            result.set(0, result.get(0) + A.get(i));
        }
    }

    public float reduceOnGPU(final FloatArray A) {
        FloatArray result = new FloatArray(1);
        result.init(0.0f);

        TaskGraph t = new TaskGraph("s0") //
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A)//
                .task("t0", GPUOps::reduceByAdding, A, result) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        execute(t);
        return result.get(0);
    }

    private static void execute(TaskGraph t) {
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
//        executionPlan.withProfiler(ProfilerMode.CONSOLE);
        executionPlan.execute();
    }

    public float dot(final FloatArray A, final FloatArray B, int size) {
        FloatArray result = new FloatArray(size);
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B, result)
                .task("t0", GPUOps::dotFloatArray, A, B, result)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        execute(t);
        return result.get(0);
    }

    public float dotReduceOnCPU(final float[] A, final float[] B) {
        float[] result = new float[A.length];
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B)
                .task("t0", GPUOps::vecMultiply, A, B, result)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);

        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.execute();
        return reduce(result);
    }

    public static void main(String[] args) {
        var smokeTest = new GPUOps();
        smokeTest.dotReduceOnCPU(new float[] {1f, 2f}, new float[] {3f, 4f});
	    System.out.println("Finished smoke test");
    }
}
