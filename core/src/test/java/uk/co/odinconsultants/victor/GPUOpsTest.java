package uk.co.odinconsultants.victor;

import org.junit.Test;

public class GPUOpsTest {
    private final GPUOps toTest = new GPUOps();

    @Test
    public void testDotProduct() {
        float[] vec = Fixtures.vectorAllOf(1, 32);
        toTest.dot(vec, vec);
    }

}
