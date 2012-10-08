// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

/**
 * You can import this statically for convenience, like this:
 *    "import static jeigen.Shortcuts.*;"
*/
public final class Shortcuts {
	/**
	 * returns dense rows*cols matrix of uniform random numbers from 0 to 1
	 */
	public static DenseMatrix rand(int rows, int cols ) {
		return DenseMatrix.rand(rows,cols);
	}
	/**
	 * returns dense rows*cols matrix of zeros
	 */
	public static DenseMatrix zeros(int rows, int cols ) {
		return DenseMatrix.zeros(rows,cols);
	}
	/**
	 * returns dense rows*cols matrix of ones
	 */
	public static DenseMatrix ones(int rows, int cols ) {
		return DenseMatrix.ones(rows,cols);
	}
	/**
	 * returns identity matrix of size 'size', in dense format
	 */
	public static DenseMatrix eye(int size ) {
		return DenseMatrix.eye(size);
	}
	/**
	 * converts v into diagonal matrix. 'v' should have a single column
	 */
	public static DenseMatrix diag( DenseMatrix v ) {
		return DenseMatrix.diag(v);
	}
	/**
	 * for each element: element = abs(element)
	 */
	public static DenseMatrix abs( DenseMatrix a ) {
		return a.abs();
	}
	/**
	 * returns sparse matrix with v along the diagonal. v should have a single column
	 */
	public static SparseMatrixLil spdiag( DenseMatrix v ) {
		return SparseMatrixLil.spdiag(v);
	}
	/**
	 * returns a sparse matrix with r rows and c columns, with no entries
	 */
	public static SparseMatrixLil spzeros( int r, int c ) {
		return SparseMatrixLil.spzeros(r, c);
	}
	/**
	 * returns an identity matrix in sparse format, of size 'size'
	 */
	public static SparseMatrixLil speye(int size ) {
		return SparseMatrixLil.speye(size);
	}
	/**
	 * returns a fully dense matrix of random values in sparse format
	 * with r rows and c columns
	 */
	public static SparseMatrixLil sprand( int r, int c ) {
		return SparseMatrixLil.sprand(r, c);
	}
}
