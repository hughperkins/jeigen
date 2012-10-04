// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// License: GNU GPL v3 (or any later version), see LICENSE.txt for details.

// You can import this statically for convenience, like this:
//    import static jeigen.MatrixUtil.*;

package jeigen;

public final class MatrixUtil {
	public static DenseMatrix rand(int rows, int cols ) {
		return DenseMatrix.rand(rows,cols);
	}
	public static DenseMatrix zeros(int rows, int cols ) {
		return DenseMatrix.zeros(rows,cols);
	}
	public static DenseMatrix ones(int rows, int cols ) {
		return DenseMatrix.ones(rows,cols);
	}
	public static DenseMatrix eye(int size ) {
		return DenseMatrix.eye(size);
	}
	public static DenseMatrix diag( DenseMatrix v ) {
		return DenseMatrix.diag(v);
	}
	public static DenseMatrix abs( DenseMatrix a ) {
		return a.abs();
	}
	public static SparseMatrixLil spdiag( DenseMatrix v ) {
		return SparseMatrixLil.spdiag(v);
	}
	public static SparseMatrixLil spzeros( int r, int c ) {
		return SparseMatrixLil.spzeros(r, c);
	}
}
