package uk.co.odinconsultants.victor;

import org.junit.Test;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;

public class SoftMaxTest {

    public static final float TOLERANCE = 1e-5f;
    public static final float EXP1 = TornadoMath.exp(1);

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
        SoftMax.softMaxInPlace(m);
        forEachCell(m, (i, j) ->
            assertEquals(EXP1 / 2, m.get(i, j), TOLERANCE)
        );
    }

    private static Matrix2DFloat allOnes() {
        var m = new Matrix2DFloat(2, 2);
        forEachCell(m, (i, j) ->
            m.set(i, j, 1.0f)
        );
        return m;
    }

    private static void forEachCell(Matrix2DFloat m, BiConsumer<Integer, Integer> fn) {
        for (int i = 0; i < m.getNumRows(); i++) {
            for (int j = 0; j < m.getNumColumns(); j++) {
                fn.accept(i, j);
            }
        }
    }

}
