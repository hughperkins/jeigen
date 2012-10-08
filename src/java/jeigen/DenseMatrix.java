// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.Random;

/**
 * A dense matrix. This is faster than SparseMatrixLil for fully dense matrices.
 * SparseMatrixLil will be faster if much of the matrix is zeros.
 */
public class DenseMatrix {
	/**
	 * Number of rows
	 */
	public final int rows;
	/**
	 * Number of columns
	 */
	public final int cols;
	/**
	 * underlying array of values, in column-major, dense format
	 */
	double[] values;
	public DenseMatrix(int rows, int cols ) {
		this.rows = rows;
		this.cols = cols;
		this.values = new double[rows * cols];
	}
	/**
	 * Return value at positiono (0,0)
	 */
	public double s() {
		return values[0];
	}
//	public class Row {
//		final int row;
//		public Row(int row ){
//			this.row = row;
//		}
//		public double get(int col) {
//			return values[rows * col + row];
//		}
//		public void set( int col, double value ){
//			values[rows * col + row] = value;
//		}
//	}
//	public class Col {
//		final int col;
//		final int offset;
//		public Col(int col ){
//			this.col = col;
//			this.offset = rows * col;
//		}
//		public double get(int row) {
//			return values[offset + row];
//		}
//		public void set( int row, double value ){
//			values[offset + row] = value;
//		}
//	}
	/**
	 * return copy of column col
	 */
	public DenseMatrix col( int col ) {
		return slice(0, rows, col, col + 1 );
	}
	/**
	 * return copy of row row
	 */
	public DenseMatrix row( int row ) {
		return slice(row, row + 1, 0, cols );
	}
	/**
	 * return copy of columns from startcol to (endcolexclusive-1)
	 */
	public DenseMatrix cols( int startcol, int endcolexclusive ) {
		return slice(0, rows, startcol, endcolexclusive );
	}
	/**
	 * return copy of rows from startrow to (endrowexclusive-1)
	 */
	public DenseMatrix rows( int startrow, int endrowexclusive ) {
		return slice(startrow, endrowexclusive, 0, cols );
	}
	/**
	 * return copy of matrix from startrow to (endrowexclusive-1)
	 * and startcol to (endcolexclusive-1)
	 */
	public DenseMatrix slice(int startrow, int endrowexclusive, int startcol, int endcolexclusive) {
		int resultrows = endrowexclusive - startrow;
		int resultcols = endcolexclusive - startcol;
		if( endrowexclusive > rows ) {
			throw new RuntimeException("endrow must not exceed rows " + endrowexclusive + " vs " + rows );
		}
		if( endcolexclusive > cols ) {
			throw new RuntimeException("endcol must not exceed cols " + endcolexclusive + " vs " + cols );
		}
		if( startrow < 0 ) {
			throw new RuntimeException("startrow must be at least 0, but was  " + startrow );			
		}
		if( startcol < 0 ) {
			throw new RuntimeException("startcol must be at least 0, but was  " + startcol );			
		}
		DenseMatrix result = new DenseMatrix(resultrows,resultcols);
		for( int c = 0; c < resultcols; c++ ) {
			int resultoffset = resultrows * c;
			int sourceoffset = (startcol + c ) * rows;
			for( int r = 0; r < resultrows; r++ ) {
				result.values[resultoffset + r] = values[sourceoffset + startrow + r ];
			}
		}
		return result;
	}
	/**
	 * concatenate two to right of this matrix
	 */
	public DenseMatrix concatRight(DenseMatrix two ){
		if( rows != two.rows ) {
			throw new RuntimeException("row mismatch " + rows + " vs " + two.rows );
		}
		DenseMatrix result = zeros(rows,cols + two.cols );
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				result.set(r,c,get(r,c));
			}
		}
		for( int c = 0; c < two.cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				result.set(r,cols + c,two.get(r,c));
			}
		}
		return result;
	}
	/**
	 * concatenate two underneath this matrix
	 */
	public DenseMatrix concatDown(DenseMatrix two ){
		if( cols != two.cols ) {
			throw new RuntimeException("col mismatch " + cols + " vs " + two.cols );
		}
		DenseMatrix result = zeros(rows + two.rows,cols );
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				result.set(r,c,get(r,c));
			}
		}
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < two.rows; r++ ) {
				result.set(rows + r,c,two.get(r,c));
			}
		}
		return result;
	}
//	public DenseMatrix getCol(int col) {
//		DenseMatrix result = new DenseMatrix(rows,1);
//		int offset = rows * col;
//		for( int i = 0; i < rows; i++ ) {
//			result.values[i] = values[offset + i ];
//		}
//		return result;
//	}
	/**
	 * return rows*cols matrix of uniform random values from 0 to 1
	 */
	public static DenseMatrix rand(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		Random random = new Random();
		int i = 0;
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows ; r++ ) {
				result.values[i] = random.nextDouble();
				i++;
			}
		}
		return result;		
	}
	/**
	 * return rows*cols dense matrix of zeros
	 */
	public static DenseMatrix zeros(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		return result;
	}
	/**
	 * return rows*cols dense matrix of ones
	 */
	public static DenseMatrix ones(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = 1;
		}
		return result;
	}
	/**
	 * return identity matrix of size 'size', as dense matrix
	 */
	public static DenseMatrix eye(int size ) {
		DenseMatrix result = new DenseMatrix(size,size);
		for( int i = 0; i < size; i++ ) {
			result.values[size * i + i] = 1;
		}
		return result;
	}
	/**
	 * returns matrix with v along the diagonal
	 * v should have a single column
	 */
	public static DenseMatrix diag( DenseMatrix v ) {
		if( v.cols != 1 ) {
			throw new RuntimeException("diag needs a matrix with one column exactly");
		}
		int size = v.rows;
		DenseMatrix result = new DenseMatrix(size,size);
		for( int i = 0; i < size; i++ ) {
			result.set(i,i,v.get(i, 0));
		}
		return result;
	}
	/**
	 * returns the sums of each column
	 */
	public DenseMatrix sum() {
		return sum(0);
	}
	/**
	 * returns the sums:
	 * when axis = 0, sums of columns
	 * when axis = 1, sums or rows
	 */
	public DenseMatrix sum( int axis ) {
		if( axis == 0 ) {
			DenseMatrix result = new DenseMatrix(1, cols );
			for( int c = 0; c < cols; c++ ) {
				int offset = c * rows;
				double sum = 0;
				for( int r = 0; r < rows; r++ ) {
					sum += values[offset + r];
				}
				result.set(0,c,sum);
			}
			return result;
		} else {
			DenseMatrix result = new DenseMatrix(rows, 1 );
			for( int r = 0; r < rows; r++ ) {
				double sum = 0;
				for( int c = 0; c < cols; c++ ) {
					sum += get(r,c);
				}
				result.set(r,0,sum);
			}			
			return result;
		}
	}
	/**
	 * returns transpose
	 */
	public DenseMatrix t() { // this could be optimized a lot, by not actually transposing...
	    DenseMatrix result = new DenseMatrix(cols,rows );
	    for( int r = 0; r < rows; r++ ) {
		    for( int c = 0; c < cols; c++ ) {
		    	result.set(c, r, get(r,c));
		    }
	    }
	    return result;
	}
	/**
	 * constructs new dense matrix from values
	 */
	public DenseMatrix(double[][] values ) {
		this.rows = values.length;
		this.cols = values[0].length;
		this.values = new double[rows * cols];
		int i = 0;
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows ; r++ ) {
				this.values[i] = values[r][c];
				i++;
			}
		}
	}
	/**
	 * sets value of matrix at (row,col) to value
	 */
	public void set(int row, int col, double value ) {
		values[rows * col + row] = value;
	}
	/**
	 * gets value of matrix at (row,col)
	 */
	public double get(int row, int col ) {
		return values[rows * col + row];
	}
	/**
	 * for each element: element = - element
	 */
	public DenseMatrix neg(){
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = - values[i];
		}
		return result;		
	}
	/**
	 * for each element: element = 1 / element
	 */
	public DenseMatrix inv(){// note: per element inverse, ie 1/element
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = 1 / values[i];
		}
		return result;		
	}
	/**
	 * for each element: element = abs( element )
	 */
	public DenseMatrix abs(){
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = Math.abs(values[i]);
		}
		return result;		
	}
	/**
	 * for each element: element = element * scalar
	 */
	public DenseMatrix mul( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] * scalar;
		}
		return result;		
	}
	/**
	 * for each element: element = Math.pow(element,power)
	 */
	public DenseMatrix pow( double power ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = Math.pow( values[i], power );
		}
		return result;		
	}
	/**
	 * for each element: element = element / scalar
	 */
	public DenseMatrix div( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] / scalar;
		}
		return result;		
	}
	/**
	 * for each element: element = element + scalar
	 */
	public DenseMatrix add( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] + scalar;
		}
		return result;		
	}
	/**
	 * for each element: element = element - scalar
	 */
	public DenseMatrix sub( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] - scalar;
		}
		return result;		
	}
	/**
	 * for each element: element[result] = element[this] * element[second]
	 */
	public DenseMatrix mul(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] * second.values[i];
		}
		return result;		
	}
	/**
	 * for each element: element[result] = element[this] / element[second]
	 */
	public DenseMatrix div(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] / second.values[i];
		}
		return result;		
	}
	/**
	 * for each element: element[result] = element[this] + element[second]
	 */
	public DenseMatrix add(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] + second.values[i];
		}
		return result;		
	}
	/**
	 * for each element: element[result] = element[this] - element[second]
	 */
	public DenseMatrix sub(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] - second.values[i];
		}
		return result;		
	}
	/**
	 * checks whether the sizes and values of this and osecond are the same
	 */
	@Override
	public boolean equals( Object osecond ) {
		if( osecond == null ) {
			return false;
		}
		DenseMatrix second = null;
		if( osecond instanceof SparseMatrixLil ) {
			second = ((SparseMatrixLil)osecond).toDense();
		} else {
			second = (DenseMatrix)osecond;
		}
		if( this.cols != second.cols || this.rows != second.rows ) {
			return false;
		}
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( Math.abs( values[i] - second.values[i] ) > 0.000001 ) {
				return false;
			}
		}
		return true;
	}
	/**
	 * for each element: element[result] = element[this] == s ? 1 : 0
	 */
	public DenseMatrix eq( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] == s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] != s ? 1 : 0
	 */
	public DenseMatrix ne( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] != s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] <= s ? 1 : 0
	 */
	public DenseMatrix le( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] <= s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] >= s ? 1 : 0
	 */
	public DenseMatrix ge( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] >= s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] < s ? 1 : 0
	 */
	public DenseMatrix lt( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] < s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] > s ? 1 : 0
	 */
	public DenseMatrix gt( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] > s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] == element[second] ? 1 : 0
	 */
	public DenseMatrix eq( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] == second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] != element[second] ? 1 : 0
	 */
	public DenseMatrix ne( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] != second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] <= element[second] ? 1 : 0
	 */
	public DenseMatrix le( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] <= second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] >= element[second] ? 1 : 0
	 */
	public DenseMatrix ge( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] >= second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] > element[second] ? 1 : 0
	 */
	public DenseMatrix gt( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] > second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * for each element: element[result] = element[this] < element[second] ? 1 : 0
	 */
	public DenseMatrix lt( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] < second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	/**
	 * Tests latency of multiplication: does everything except call the Eigen multiplication routine
	 */
	public DenseMatrix dummy_mmul( DenseMatrix second ) { // just to test latency
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.dense_dummy_op2(this.rows, this.cols, second.cols, this.values, second.values, result.values );
		return result;
	}
	/**
	 * matrix multiplication of this by second
	 */
	public DenseMatrix mmul( DenseMatrix second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.dense_multiply(this.rows, this.cols, second.cols, this.values, second.values, result.values );
		return result;
	}
	/**
	 * matrix multiplication of this by second
	 */
	public DenseMatrix mmul( SparseMatrixLil second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		int twohandle = SparseMatrixLil.allocateSparseMatrix(second);
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.dense_sparse_multiply(rows, cols, second.cols,
				values, twohandle, result.values );
		JeigenJna.Jeigen.freeSparseMatrix(twohandle);
		return result;
	}
	/**
	 * returns matrix with number of rows and columns of this
	 */
	public DenseMatrix shape() {
		return new DenseMatrix(new double[][]{{rows,cols}}); 
	}
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DenseMatrix, " + rows + " * " + cols + ":\n");
		stringBuilder.append("\n");
		for( int r = 0; r < rows; r++ ) {
			for( int c = 0; c < cols; c++ ) {
				stringBuilder.append(get(r,c));
				stringBuilder.append(" ");
			}
			stringBuilder.append("\n");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
	/**
	 * Solves this * result = b, and returns result
	 * ldlt is fast, needs this to be positive or negative definite
	 */
	public DenseMatrix ldltSolve(DenseMatrix b ) {
		if( this.cols != b.rows ) {
			throw new RuntimeException("ldltsolve matrix size mismatch " + shape() + " vs " + b.shape());
		}
		DenseMatrix result = new DenseMatrix(this.cols, b.cols);
		JeigenJna.Jeigen.ldlt_solve(rows, cols, b.cols,
				values, b.values, result.values );
		return result;		
	}
	/**
	 * Solves this * result = b, and returns result
	 * Relatively slow, but accurate, and no conditions on this
	 */
	public DenseMatrix fullPivHouseholderQRSolve(DenseMatrix b ) {
		if( this.cols != b.rows ) {
			throw new RuntimeException("ldltsolve matrix size mismatch " + shape() + " vs " + b.shape());
		}
		DenseMatrix result = new DenseMatrix(this.cols, b.cols);
		JeigenJna.Jeigen.fullpivhouseholderqr_solve(rows, cols, b.cols,
				values, b.values, result.values );
		return result;		
	}
	/**
	 * Stores result of singular value decomposition
	 */
	public static class SvdResult {
		/**
		 * U matrix
		 */
		public final DenseMatrix U;
		/**
		 * S matrix (singular values)
		 */
		public final DenseMatrix S;
		/**
		 * V matrix
		 */
		public final DenseMatrix V;
		public SvdResult(DenseMatrix u, DenseMatrix s, DenseMatrix v) {
			U = u;
			S = s;
			V = v;
		}
	}
	/**
	 * Calculates singular value decomposition on this
	 * returns SvdResult containing U,S,V
	 * uses Jacobi, which is accurate, and good for small matrices
	 */
	public SvdResult svd() { // returns the thin U and V (Note:  I have no objection to extending this to make
		                     // the thinness of U and V optional)
		int n = rows;
		int p = cols;
		int m = Math.min(n,p);
		DenseMatrix U = zeros(n,m);
		DenseMatrix S = zeros(m,1);
		DenseMatrix V = zeros(p,m);
		JeigenJna.Jeigen.svd_dense(rows, cols, values, U.values, S.values, V.values);
		return new SvdResult(U, S, V);
	}
	/**
	 * converts this matrix to sparse lil format
	 */
	SparseMatrixLil toSparseLil(){
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		int notZero = 0;
		int count = rows * cols;
		for( int i = 0; i < count; i++ ) {
			if( values[i] != 0 ) {
				notZero++;
			}
		}
		result.reserve(notZero);
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				double value = values[rows * c + r]; 
				if( value != 0 ) {
					result.append(r,c,value);
				}
			}
		}
		return result;
	}
}
