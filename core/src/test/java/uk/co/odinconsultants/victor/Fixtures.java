package uk.co.odinconsultants.victor;

public class Fixtures {
    public static float[] vectorAllOf(float x, int size) {
        var vec = new float[size];
        for (int i = 0 ; i < size ; i++) {
            vec[i] = x;
        }
        return vec;
    }
}
