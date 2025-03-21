package uk.co.odinconsultants.victor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

import static uk.co.odinconsultants.victor.GPUOps.reduce;

public class GPUOpsTest {
    private final Logger logger = LogManager.getLogger();

    private final GPUOps toTest = new GPUOps();

    float x = 1;
    int size = 33;;
    float[] vec = Fixtures.vectorAllOf(x, size);
    final FloatArray vecFloatArray = FloatArray.fromArray(vec);
    final FloatArray vecFloatArrayOther = FloatArray.fromArray(vec);

    @Test
    public void testJavaDotFloatArray() {
        FloatArray result = new FloatArray(size);
        toTest.dotFloatArray(vecFloatArray, vecFloatArray, result);
        Assert.assertEquals(x * size, reduce(result.toHeapArray()), 0f);
    }

    @Test
    public void testJavaVecMultiply() {
        float[] result = new float[size];
        toTest.vecMultiply(vec, vec, result);
        Assert.assertEquals(x * size, reduce(result), 0f);
    }

    @Test
    public void testTornadoDotProduct() {
        Assert.assertEquals(x * size, toTest.dotReduceOnCPU(vec, vec), 0f);
    }

    @Test
    public void testTornadoDotProductFloatArray() {
        Assert.assertEquals(x * size, toTest.dot(vecFloatArray, vecFloatArrayOther, size), 0f);
    }

    @Test
    public void testTornadoDotProductFloatArrayReduceOnGPU() throws InterruptedException {
        Assert.assertEquals(x * size, toTest.dotReduceOnGPU(vecFloatArray, vecFloatArrayOther), 0f);
    }

    @Test
    public void testMultiplyReduceCPU() {
        FloatArray result = new FloatArray(size);
        toTest.multipleAndReduce(vecFloatArray, vecFloatArrayOther, result);
        Assert.assertEquals(squareAndReduceVec(), result.get(0), 0f);
    }

    private float squareAndReduceVec() {
        float total = 0f;
        for (int i = 0; i < vec.length; i++) {
            total += vec[i] * vec[i];
        }
        return total;
    }

    @Test
    public void testReduceGPU() {
        Assert.assertEquals(squareAndReduceVec(), toTest.reduceOnGPU(vecFloatArray), 0f);
    }

    @Test
    public void matrixMult() {

    }

}
