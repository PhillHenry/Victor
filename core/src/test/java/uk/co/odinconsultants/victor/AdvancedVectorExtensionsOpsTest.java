package uk.co.odinconsultants.victor;

import org.junit.Test;

public class AdvancedVectorExtensionsOpsTest {

    final AdvancedVectorExtensionsOps toTest = new AdvancedVectorExtensionsOps();

    public static float[] vectorAllOf(float x, int size) {
        var vec = new float[size];
        for (int i = 0 ; i < size ; i++) {
            vec[i] = x;
        }
        return vec;
    }

    @Test
    public void testDotProduct() {
        int n = 32;
        float x = 1f;
        var vec = vectorAllOf(x, n);
        assert toTest.vectorFMA(vec, vec) == n * x;
    }
}
