// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import com.sun.jna.Native;
import java.io.*;

/**
 *  the jna link from java to the native Eigen library
 */
class JeigenJna {
	public static class Jeigen {
		public static final void addToJnaPath(String newpath ) throws Exception {
			String oldLibraryPath = System.getProperty( "jna.library.path");
//			System.out.println("adding " + newpath);
            if( oldLibraryPath != null ) {
				System.setProperty( "jna.library.path", oldLibraryPath + File.pathSeparator + newpath );
			} else {
				System.setProperty( "jna.library.path", newpath );
			}
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
        public static native void jeigen_exp( int n, double[] in, double[] result );
        public static native void jeigen_log( int n, double[] in, double[] result );
		
		public static native void ldlt_solve( int arow, int acols, int bcols, double []avalues, double []bvalues, double []xvalues );
		public static native void fullpivhouseholderqr_solve( int arow, int acols, int bcols, double []avalues, double []bvalues, double []xvalues );

		public static native int allocateSparseMatrix( int numEntries, int numRows, int numCols, int []rows, int []cols, double []values );
		public static native void getSparseMatrixStats( int handle, int[] stats ); // rows, cols, nonzeros
		public static native void getSparseMatrix( int handle, int []rows, int []cols, double []values );
		public static native void freeSparseMatrix( int handle );

		// dummy ops to measure latency
		public static native void dense_dummy_op1( int rows, int cols, double []first, double []result );
		public static native void dense_dummy_op2( int rows, int middle, int cols, double []first, double []second, double []result );
		public static native int sparse_dummy_op2( int rows, int middle, int cols,
			       int oneHandle,
			       int twoHandle, int numResultColumns );
	}
}
