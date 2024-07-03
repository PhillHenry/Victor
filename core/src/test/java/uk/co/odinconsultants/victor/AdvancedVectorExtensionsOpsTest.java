package uk.co.odinconsultants.victor;

import org.junit.Test;

public class AdvancedVectorExtensionsOpsTest {
    final AdvancedVectorExtensionsOps toTest = new AdvancedVectorExtensionsOps();

    @Test
    public void testDotProduct() {
        int n = 32;
        float x = 1f;
        var vec = Fixtures.vectorAllOf(x, n);
        assert toTest.vectorFMA(vec, vec) == n * x;
    }
}
