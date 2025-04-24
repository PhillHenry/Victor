package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.annotations.Reduce;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class SoftMax {

    public static void sumArray(FloatArray m, @Reduce FloatArray result) {
        for (@Parallel int row = 0; row < m.getElementSize() / result.getSize(); row++) {
            for (@Parallel int col = 0; col < result.getSize(); col++) {
                result.set(col, result.get(col) + m.get(row * result.getSize() + col));
            }
        }
    }

    public static void expInPlaceArray(FloatArray m, int nColumns) {
        for (@Parallel int row = 0; row < m.getElementSize() / nColumns; row++) {
            for (@Parallel int col = 0; col < nColumns; col++) {
                m.set(row * nColumns + col, TornadoMath.exp(m.get(row * nColumns + col)));
            }
        }
    }

    public static void divideInPlaceArray(FloatArray m, @Reduce FloatArray d) {
        for (@Parallel int row = 0; row < m.getElementSize() / d.getSize(); row++) {
            for (@Parallel int col = 0; col < d.getSize(); col++) {
                m.set(row * d.getSize() + col, m.get(row * d.getSize() + col) / d.get(col));
            }
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

    static void softMaxInPlaceGPUArray(FloatArray m, FloatArray sum) {
        expInPlaceArray(m, sum.getSize());
        sumArray(m, sum);
        divideInPlaceArray(m, sum);
    }

    static void softMaxInPlaceGPU(Matrix2DFloat m, FloatArray sum) {
        expInPlace(m);
        sum(m, sum);
        divideInPlace(m, sum);
    }

    public static void main(String[] args) {

    }

}
