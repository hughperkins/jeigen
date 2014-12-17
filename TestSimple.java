import jeigen.*;

class TestSimple {
    // if this runs, means picked up the native library ok, and the non-native library too
    public static void main( String[] args ) {
       DenseMatrix dm1 = new DenseMatrix(3,3);
       System.out.println( dm1 );
       dm1 = dm1.mmul(dm1);
    }
}

