package jeigen;

import jeigen.*;

class TestSimple {
    // if this runs, means picked up the native library ok, and the non-native library too
    public static void main( String[] args ) {
       DenseMatrix dm1 = new DenseMatrix("1 4; 3 8");
       DenseMatrix dm2 = new DenseMatrix("1 0; 0 0.5");
//       dm1.set(1, 0, 1);
//       dm1.set(1, 1, 1);
//       dm1.set(1, 2, 2);
//       dm1.set(2, 2, 1);
       DenseMatrix dm3 = dm1.mmul(dm2);
       System.out.println( dm3 );
       System.out.println("If you got this far, saw a 2x2 matrix with numbers 1,2,3,4, and no error message, then everything is working ok");
    }
}

