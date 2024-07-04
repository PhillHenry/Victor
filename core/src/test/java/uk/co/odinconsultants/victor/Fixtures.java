package uk.co.odinconsultants.victor;

public class Fixtures {
    public static float[] vectorAllOf(float x, int size) {
        var vec = new float[size];
        for (int i = 0 ; i < size ; i++) {
            vec[i] = x;
        }
        return vec;
    }

    public static float[][] matrixOf(int width, int height, float x) {
        float[][] matrix = new float[width][height];
        for (int i = 0 ; i < width ; i++ ) {
            for (int j = 0 ; j < width ; j++ ) {
                matrix[i][j] = x;
            }
        }
        return matrix;
    }
}
