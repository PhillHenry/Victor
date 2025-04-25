package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.annotations.Reduce;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class SoftMax {

    public static void sumArray(FloatArray m, FloatArray result, int nRows, int nCols) {
        for (@Parallel int i = 0; i < nCols * nRows; i++) {
            int col = i % nCols;
            result.set(col, result.get(col) + m.get(i));
        }
    }

    public static void expInPlaceArray(FloatArray m, int n) {
        for (@Parallel int i = 0; i < n; i++) {
            m.set(i, TornadoMath.exp(m.get(i)));
        }
    }

    public static void divideInPlaceArray(FloatArray m, FloatArray d, int nRows, int nCols) {
        for (@Parallel int i = 0; i < nRows * nCols; i++) {
            int col = i % nCols;
            m.set(i, m.get(i) / d.get(col));
        }
    }

    public static void sum(Matrix2DFloat m, FloatArray result) {
        for (@Parallel int i = 0; i < m.getNumRows(); i++) {
            for (@Parallel int j = 0; j < m.getNumColumns(); j++) {
                result.set(j, result.get(j) + m.get(i, j));
            }
        }
    }

    public static void expInPlace(Matrix2DFloat m) {
        for (@Parallel int i = 0; i < m.getNumRows(); i++) {
            for (@Parallel int j = 0; j < m.getNumColumns(); j++) {
                m.set(i, j, TornadoMath.exp(m.get(i, j)));
            }
        }
    }

    public static void divideInPlace(Matrix2DFloat m, FloatArray d) {
        for (@Parallel int i = 0; i < m.getNumColumns(); i++) {
            var row = m.row(i);
            for (@Parallel int j = 0; j < row.getLength(); j++) {
                m.set(i, j, row.get(j) / d.get(j));
            }
        }
    }

    public static void softMaxInPlace(Matrix2DFloat m, FloatArray sum) {
        softMaxInPlaceGPU(m, sum);
    }

    static void softMaxInPlaceGPUArray(FloatArray m, FloatArray sum, int nRows, int nCols) {
        expInPlaceArray(m, nRows * nCols);
        sumArray(m, sum, nRows, nCols);
        divideInPlaceArray(m, sum, nRows, nCols);
    }

    static void softMaxInPlaceGPU(Matrix2DFloat m, FloatArray sum) {
        expInPlace(m);
        sum(m, sum);
        divideInPlace(m, sum);
    }

    public static TornadoExecutionPlan taskGraph(FloatArray m, FloatArray sum, int nRows, int nCols) {
        TaskGraph t = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, m, sum)
                .task("t0", SoftMax::expInPlaceArray, m, nRows * nCols)
                .task("t1", SoftMax::sumArray, m, sum, nRows, nCols)
                .task("t2", SoftMax::divideInPlaceArray, m, sum, nRows, nCols)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, m);
        ImmutableTaskGraph immutableTaskGraph = t.snapshot();
        TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph);
        return executor;
    }

    public static void main(String[] args) {
        int n = 1024;
        for (int runs = 0 ; runs < 10 ; runs++) {
            final FloatArray m = new FloatArray(n * n);
            final FloatArray sum = new FloatArray(n);
            for (int i = 0; i < n * n; i++) {
                m.set(i, 1f); //(float)Math.random());
            }
            TornadoExecutionPlan executor = taskGraph(m, sum, n, n);
            executor.execute();
            for (int i = 0; i < n * n; i++) {
                assert(Math.abs(m.get(i) - TornadoMath.exp(1) / n) < 1e-5);
            }
        }
    }

}
