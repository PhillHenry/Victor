package uk.co.odinconsultants.victor;

import uk.ac.manchester.tornado.api.types.matrix.Matrix2DFloat;

import java.util.function.Supplier;

public class MatrixUtils {

    public Matrix2DFloat createAndInitializeMatrix(int rows, int columns, Supplier<Float> intializer) {
        Matrix2DFloat m = new Matrix2DFloat(rows, columns);
        initialize(m, intializer);
        return m;
    }

    public void initialize(Matrix2DFloat m, Supplier<Float> fn) {
        for (int i = 0 ; i < m.getNumRows() ; i++) {
            for (int j = 0; j < m.getNumColumns() ; j++) {
                m.set(i, j, fn.get());
            }
        }
    }

}
