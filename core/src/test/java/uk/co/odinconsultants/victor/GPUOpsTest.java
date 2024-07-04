package uk.co.odinconsultants.victor;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

public class GPUOpsTest {
    private final GPUOps toTest = new GPUOps();

    float x = 1;
    int size = 32;;
    float[] vec = Fixtures.vectorAllOf(x, size);
    final FloatArray vecFloatArray = FloatArray.fromArray(vec);
    final FloatArray vecFloatArrayOther = FloatArray.fromArray(vec);

    @Test
    public void testJavaDotFloatArray() {
        FloatArray result = new FloatArray(1);
        toTest.dotFloatArray(vecFloatArray, vecFloatArray, result, size);
        Assert.assertEquals(x * size, result.get(0), 0f);
    }

    @Test
    public void testJavaVecMultiply() {
        float[] result = new float[1];
        toTest.vecMultiply(vec, vec, result, size);
        Assert.assertEquals(x * size, result[0], 0f);
    }

    @Test
    @Ignore
    public void testTornadoDotProduct() {
        Assert.assertEquals(x * size, toTest.dot(vec, vec), 0f);
    }

    @Test
    @Ignore
    public void testTornadoDotProductFloatrArray() {
        Assert.assertEquals(x * size, toTest.dot(vecFloatArray, vecFloatArrayOther, size), 0f);
    }

    @Test
    public void matrixMult() {

    }

}
