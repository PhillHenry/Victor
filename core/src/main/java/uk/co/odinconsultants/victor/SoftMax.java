package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class SoftMax {

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
        for (@Parallel int i = 0; i < m.getNumRows(); i++) {
            for (@Parallel int j = 0; j < m.getNumColumns(); j++) {
                m.set(i, j, m.get(i, j) / d.get(j));
            }
        }
    }

    public static void softMaxInPlace(Matrix2DFloat m) {
        var sum = new FloatArray(m.getNumColumns());
        softMaxInPlaceGPU(m, sum);
    }

    static void softMaxInPlaceGPU(Matrix2DFloat m, FloatArray sum) {
        expInPlace(m);
        sum(m, sum);
        divideInPlace(m, sum);
    }

    public static void main(String[] args) {

    }

}
