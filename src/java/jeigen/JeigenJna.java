// Copyright Hugh Perkins 2012, 2016 hughperkins -at- gmail
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
        public static final ClassLoader getClassLoader() {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            if (classLoader == null) {
                classLoader = Class.class.getClassLoader();
            }
            return classLoader;
        }
        public static final void addToJnaPath(String newpath ) throws Exception {
            String oldLibraryPath = System.getProperty( "jna.library.path");
            if( oldLibraryPath != null ) {
                System.setProperty( "jna.library.path", oldLibraryPath + File.pathSeparator + newpath );
            } else {
                System.setProperty( "jna.library.path", newpath );
            }
        }
	static void installFile(String sourcePath, String destPath) throws IOException, FileNotFoundException, Exception {
		ClassLoader classLoader = getClassLoader();
                if( !new File(destPath).exists() ) {
                    InputStream inputStream = classLoader.getResourceAsStream(sourcePath);
                    OutputStream outputStream = new FileOutputStream(destPath );
                    FileHelper.copyBetweenStreams( inputStream, outputStream );
                    inputStream.close();
                    outputStream.close();
                }
	}
        static {
            try{
                String userDirectory = System.getProperty("user.home");
                String nativeDirectory = userDirectory + File.separator + ".jeigen" + File.separator + "native2";
		if(OsHelper.isWindows()) {
			nativeDirectory = nativeDirectory + File.separator + "win-" + OsHelper.jvmBits();
		}
                new File( nativeDirectory ).mkdirs();
                ClassLoader classLoader = getClassLoader();
                String nativefilename = "libjeigen-linux-" + OsHelper.jvmBits() + ".so";
                if( OsHelper.isWindows() ) {
                    nativefilename = "jeigen-win-" + OsHelper.jvmBits() + ".dll";
                } else if( OsHelper.isMac() ) {
                    nativefilename = "libjeigen-mac-" + OsHelper.jvmBits() + ".dylib";
                }
		installFile(nativefilename, nativeDirectory + File.separator + nativefilename);
		if(OsHelper.isWindows()) {
			installFile("msvc-redist/win-" + OsHelper.jvmBits() + "/msvcp100.dll", nativeDirectory + File.separator + "msvcp100.dll");
			installFile("msvc-redist/win-" + OsHelper.jvmBits() + "/msvcr100.dll", nativeDirectory + File.separator + "msvcr100.dll");
		}
                addToJnaPath( nativeDirectory );
                Native.register(nativefilename.replace(".dylib","").replace("lib","").replace(".dll","").replace(".so","") );
            } catch(Exception e ) {
                e.printStackTrace();
                System.exit(1);
            }
//            Native.register("jeigen");
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
        
        // Eigenvectors, experimental...
        public static native void jeigen_eig( int rows, double[] in, double[] values_real, double[] values_imag,
            double[] vectors_real, double[] vectors_imag );
        public static native void jeigen_peig( int rows, double[] in, double[] eigenValues, double[] eigenVectors );

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
