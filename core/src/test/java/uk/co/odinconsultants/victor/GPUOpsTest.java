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
        toTest.dotFloatArray(vecFloatArray, vecFloatArray, result, size);
        Assert.assertEquals(x * size, reduce(result.toHeapArray()), 0f);
    }

    @Test
    public void testJavaVecMultiply() {
        float[] result = new float[size];
        toTest.vecMultiply(vec, vec, result, size);
        Assert.assertEquals(x * size, reduce(result), 0f);
    }

    @Test
    public void testTornadoDotProduct() {
        Assert.assertEquals(x * size, toTest.dot(vec, vec), 0f);
    }

    @Test
    public void testTornadoDotProductFloatArray() {
        logger.info("Hello world");
        System.out.println("This is the console");
        Assert.assertEquals(x * size, toTest.dot(vecFloatArray, vecFloatArrayOther, size), 0f);
    }

    @Test
    public void testTornadoDotProductFloatArrayReduceOnGPU() {
        logger.info("Hello world");
        System.out.println("This is the console");
        Assert.assertEquals(x * size, toTest.dotReduceOnGPU(vecFloatArray, vecFloatArrayOther, size), 0f);
    }

    @Test
    public void matrixMult() {

    }

}
