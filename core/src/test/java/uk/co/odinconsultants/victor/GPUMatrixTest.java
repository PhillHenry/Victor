package uk.co.odinconsultants.victor;

import org.junit.Test;
import org.openjdk.jmh.annotations.Setup;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

public class GPUMatrixTest {
    private final MatrixUtils matrixUtils = new MatrixUtils();
    private final GPUOps toTest = new GPUOps();

    float x = 1;
    int size = 256;
    final Matrix2DFloat m = new Matrix2DFloat(size, size);
    final Matrix2DFloat mOther = new Matrix2DFloat(size, size);

    @Setup
    public void setup() {
        matrixUtils.initialize(m, () -> x);
        matrixUtils.initialize(mOther, () -> x);
    }

    @Test
    public void matrixMultiplication() {
//        m.multiply();
    }
}
