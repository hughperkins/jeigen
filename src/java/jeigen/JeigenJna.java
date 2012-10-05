// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// License: GNU GPL v3 (or any later version), see LICENSE.txt for details.

// the jna link from java to the native Eigen library

package jeigen;

import com.sun.jna.Native;

public class JeigenJna {
	public static class Jeigen {
		public static final void addToJnaPath(String newpath ) throws Exception {
			String oldLibraryPath = System.getProperty( "jna.library.path");
//			System.out.println("adding " + newpath);
			System.setProperty( "jna.library.path", oldLibraryPath + ":" + newpath );
		}
		static {
			try{
				addToJnaPath(System.getProperty("java.library.path"));
			} catch(Exception e ) {
				e.printStackTrace();
				System.exit(1);
			}
			Native.register("jeigen");
		}
		
		public static native void init();
		public static native void dense_multiply( int rows, int middle, int cols, double []first, double []second, double []result );
		public static native int sparse_multiply( int rows, int middle, int cols,
	       int oneHandle,
	       int twoHandle );
		public static native void sparse_dense_multiply( int rows, int middle, int cols, int onehandle, double []asecond, double []aresult );
		public static native void dense_sparse_multiply( int rows, int middle, int cols, double []afirst, int twohandle, double []aresult );
		
		public static native void svd_dense( int numrows, int numcols, double []in, double []u, double []s, double []v );
		
		public static native void ldlt_solve( int arow, int acols, int bcols, double []avalues, double []bvalues, double []xvalues );
		public static native void fullpivhouseholderqr_solve( int arow, int acols, int bcols, double []avalues, double []bvalues, double []xvalues );

		public static native int allocateSparseMatrix( int numEntries, int numRows, int numCols, int []rows, int []cols, double []values );
		public static native void getSparseMatrixStats( int handle, int[] stats ); // rows, cols, nonzeros
		public static native void getSparseMatrix( int handle, int []rows, int []cols, double []values );
		public static native void freeSparseMatrix( int handle );
	}
}
