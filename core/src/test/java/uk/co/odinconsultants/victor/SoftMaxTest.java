package uk.co.odinconsultants.victor;

import org.junit.Test;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertEquals;

public class SoftMaxTest {

    public static final float TOLERANCE = 1e-5f;
    public static final float EXP1 = TornadoMath.exp(1);
    public static final int MATRIX_SIDE = 2;

    @Test
    public void testExpInPlaceArray() {
        var m = allOnesArray();
        SoftMax.expInPlaceArray(m, MATRIX_SIDE * MATRIX_SIDE);
        forEachCellArray(m, (x) -> assertEquals(EXP1, m.get(x), TOLERANCE));
    }

    @Test
    public void testSumArray() {
        var m = allOnesArray();
        var sum = new FloatArray(2);
        SoftMax.sumArray(m, sum, MATRIX_SIDE, MATRIX_SIDE);
        for (int i = 0; i < MATRIX_SIDE; i++) {
            assertEquals(2f, sum.get(i), TOLERANCE);
        }
    }
    @Test
    public void testHappyPathArray() {
        var m = allOnesArray();
        var sum = new FloatArray(MATRIX_SIDE);
        SoftMax.softMaxInPlaceGPUArray(m, sum, MATRIX_SIDE, MATRIX_SIDE);
        forEachCellArray(m, (i) ->
            assertEquals(1f / 2, m.get(i), TOLERANCE)
        );
    }

    @Test
    public void testExpInPlace() {
        var m = allOnes();
        SoftMax.expInPlace(m);
        forEachCell(m, (x, y) -> assertEquals(EXP1, m.get(x, y), TOLERANCE));
    }

    @Test
    public void testSum() {
        var m = allOnes();
        var sum = new FloatArray(m.getNumColumns());
        SoftMax.sum(m, sum);
        for (int i = 0; i < m.getNumColumns(); i++) {
            assertEquals(2f, sum.get(i), TOLERANCE);
        }
    }
    @Test
    public void testHappyPath() {
        var m = allOnes();
        var sum = new FloatArray(m.getNumColumns());
        SoftMax.softMaxInPlace(m, sum);
        forEachCell(m, (i, j) ->
            assertEquals(1f / 2, m.get(i, j), TOLERANCE)
        );
    }

    private static FloatArray allOnesArray() {
        int n = MATRIX_SIDE * MATRIX_SIDE;
        FloatArray arr = new FloatArray(n);
        for (int i = 0; i < n; i++) {
            arr.set(i, 1f);
        }
        return arr;
    }

    private static Matrix2DFloat allOnes() {
        var m = new Matrix2DFloat(2, 2);
        forEachCell(m, (i, j) ->
            m.set(i, j, 1.0f)
        );
        return m;
    }

    private static void forEachCellArray(FloatArray m, IntConsumer fn) {
        for (int i = 0; i < m.getSize(); i++) {
            fn.accept(i);
        }
    }

    private static void forEachCell(Matrix2DFloat m, BiConsumer<Integer, Integer> fn) {
        for (int i = 0; i < m.getNumRows(); i++) {
            for (int j = 0; j < m.getNumColumns(); j++) {
                fn.accept(i, j);
            }
        }
    }

}
