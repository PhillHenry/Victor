package uk.co.odinconsultants.victor;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class AdvancedVectorExtensionsOps {
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    /**
     * See https://medium.com/@Styp/java-18-vector-api-do-we-get-free-speed-up-c4510eda50d2
     * Fused-Multiply-Add
     */
    public float vectorFMA(float[] a, float[] b){
        var upperBound = SPECIES.loopBound(a.length);
        var sum = FloatVector.zero(SPECIES);
        var i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, a, i);
            var vb = FloatVector.fromArray(SPECIES, b, i);
            sum = va.fma(vb, sum);
        }
        var c = sum.reduceLanes(VectorOperators.ADD);
        for (; i < a.length; i++) { // Cleanup loop
            c += a[i] * b[i];
        }
        return c;
    }
}
