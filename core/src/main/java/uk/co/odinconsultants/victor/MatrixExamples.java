package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.enums.ProfilerMode;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

import java.util.Random;

public class MatrixExamples {

    private static final MatrixUtils matrixUtils = new MatrixUtils();

    private static void matrixMultiplication(Matrix2DFloat A, Matrix2DFloat B, Matrix2DFloat C) {
        for (@Parallel int i = 0; i < A.getNumRows(); i++) {
            for (@Parallel int j = 0; j < A.getNumColumns(); j++) {
                float sum = 0.0f;
                for (int k = 0; k < A.getNumColumns(); k++) {
                    sum += A.get(i, k) * B.get(k, j);
                }
                C.set(i, j, sum);
            }
        }
    }

    public TaskGraph dotReduceTaskGraph(final Matrix2DFloat A, final Matrix2DFloat B, Matrix2DFloat result) {
        return new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, A, B, result)
                .task("t0", MatrixExamples::matrixMultiplication, A, B, result)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, result);
    }

    private static void execute(TaskGraph t) {
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);
        executionPlan.withProfiler(ProfilerMode.CONSOLE);
        executionPlan.execute();
    }

    private void matrixMultExample() {
        var rand = new Random();
        int size = 8000; // since this is not a multiple of 32, ncu-ui will warn
        final Matrix2DFloat m = matrixUtils.createAndInitializeMatrix(size, size, rand::nextFloat);
        Matrix2DFloat output = new Matrix2DFloat(size, size);
        TaskGraph t = dotReduceTaskGraph(m,m, output);
        execute(t);
    }

    public static void main(String[] args) {
        MatrixExamples app = new MatrixExamples();
        app.matrixMultExample();
    }

}
