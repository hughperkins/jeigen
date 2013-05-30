// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.Random;

import jeigen.statistics.Statistics;

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
	 * Creates matrix from valuesstring in format "12 3; 4 5"
	 * Result:
	 *   12 3
	 *   4  5
	 */
	public DenseMatrix(String valuesstring ) {
		String[] lines = valuesstring.split(";");
		rows = lines.length;
		int row = 0;
		if( rows == 0 ) {
			cols = 0;
			return;
		}
		cols = lines[0].trim().split(" ").length;
		values = new double[rows*cols];
		for( String line : lines ) {
			line = line.trim();
			String[] splitline = line.split(" ");
			if( splitline.length != cols ) {
				throw new RuntimeException("Unequal sized rows in " + valuesstring );
			}
			for( int col = 0; col < cols; col++ ) {
				set(row,col, Double.parseDouble(splitline[col]));
			}
			row++;
		}
	}
	/**
	 * returns new DenseMatrix containing the rows indexed by
	 * indexes
	 * indexes should be a single column
	 * indexes may contain duplicates
	 * not terribly efficient right now... 
	 */
	public DenseMatrix rows(DenseMatrix indexes ){
		if( indexes.cols != 1 ) {
			throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
		}
		int cols = this.cols;
		DenseMatrix result = new DenseMatrix(indexes.rows, cols);
		for( int i = 0; i < indexes.rows; i++ ) {
			int srcrow = (int)indexes.get(i,0);
			for( int c = 0; c < cols; c++ ) {
				result.set(i,c,get(srcrow,c));
			}
		}
		return result;
	}
	public DenseMatrix cols(DenseMatrix indexes ){
		if( indexes.cols != 1 ) {
			throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
		}
		int rows = this.rows;
		DenseMatrix result = new DenseMatrix(rows,indexes.rows);
		for( int i = 0; i < indexes.rows; i++ ) {
			int srccol = (int)indexes.get(i,0);
			for( int r = 0; r < rows; r++ ) {
				result.set(r,i,get(r,srccol));
			}
		}
		return result;
	}
	/**
	 * return copy of rows from startrow to (endrowexclusive-1)
	 */
	public DenseMatrix rows( int startrow, int endrowexclusive ) {
		return slice(startrow, endrowexclusive, 0, cols );
	}
	/**
	 * returns indexes of non-zero rows
	 * we must be a one column matrix
	 */
	public DenseMatrix nonZeroRows() {
		if( cols != 1 ) {
			throw new RuntimeException("cols should be 1 but was " + cols );
		}
		SparseMatrixLil indices = new SparseMatrixLil(0,1);
		int resultrow = 0;
		for( int i = 0; i < rows; i++ ) {
			if( values[i] != 0 ) {
				indices.append(resultrow, 0, i);
				resultrow++;
			}
		}
		indices.rows = resultrow;
		return indices.toDense();
	}
	/**
	 * returns indexes of non-zero cols
	 * we must be a one row matrix
	 */
	public DenseMatrix nonZeroCols() {
		if( rows != 1 ) {
			throw new RuntimeException("rows should be 1 but was " + rows );
		}
		SparseMatrixLil indices = new SparseMatrixLil(0,1);
		int resultrow = 0;
		for( int i = 0; i < cols; i++ ) {
			if( values[i] != 0 ) {
				indices.append( resultrow, 0, i);
				resultrow++;
			}
		}
		indices.rows = resultrow;
		return indices.toDense();
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
	 * returns the sum over rows, or if only one row, returns
	 * sum over columns
	 */
	public DenseMatrix sum() {
		if( rows > 1 ) {
			return sumOverRows();
		}
		return sumOverCols();
	}
	public DenseMatrix varOverRows(){
		return Statistics.varOverRows(this);
	}
	public DenseMatrix varOverCols(){
		return Statistics.varOverCols(this);
	}
	public DenseMatrix meanOverRows(){
		return Statistics.meanOverRows(this);
	}
	public DenseMatrix meanOverCols(){
		return Statistics.meanOverCols(this);
	}
	/**
	 * sum aggregate over rows
	 * result has a single row,
	 * and the same columns as the input
	 * matrix.
	 */
	public DenseMatrix sumOverRows() {
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
	}
	public DenseMatrix sumOverCols() {
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
	public DenseMatrix maxOverRows() {
		if( cols < 1 ) {
			throw new RuntimeException("maxoverrows can't be called on empty matrix");
		}
		DenseMatrix result = new DenseMatrix(1, cols );
		for( int c = 0; c < cols; c++ ) {
			int offset = c * rows;
			double max = get(0,c);
			for( int r = 0; r < rows; r++ ) {
				max = Math.max(max, values[offset + r] );
			}
			result.set(0,c,max);
		}
		return result;
	}
	public DenseMatrix maxOverCols() {
		if( rows < 1 ) {
			throw new RuntimeException("maxOverCols can't be called on empty matrix");
		}
		DenseMatrix result = new DenseMatrix(rows, 1 );
		for( int r = 0; r < rows; r++ ) {
			double max = get(r,0);
			for( int c = 0; c < cols; c++ ) {
				max = Math.max(max, get(r,c) );
			}
			result.set(r,0,max);
		}
		return result;
	}
	public DenseMatrix minOverRows() {
		if( cols < 1 ) {
			throw new RuntimeException("minoverrows can't be called on empty matrix");
		}
		DenseMatrix result = new DenseMatrix(1, cols );
		for( int c = 0; c < cols; c++ ) {
			int offset = c * rows;
			double min = get(0,c);
			for( int r = 0; r < rows; r++ ) {
				min = Math.min(min, values[offset + r] );
			}
			result.set(0,c,min);
		}
		return result;
	}
	public DenseMatrix minOverCols() {
		if( rows < 1 ) {
			throw new RuntimeException("minOverCols can't be called on empty matrix");
		}
		DenseMatrix result = new DenseMatrix(rows, 1 );
		for( int r = 0; r < rows; r++ ) {
			double min = get(r,0);
			for( int c = 0; c < cols; c++ ) {
				min = Math.min(min, get(r,c) );
			}
			result.set(r,0,min);
		}
		return result;
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
	public final void set(int row, int col, double value ) {
		values[rows * col + row] = value;
	}
	/**
	 * sets value of matrix at (offset % rows,offset / rows) to value
	 * less convenient, but faster
	 */
	public final void set(int offset, double value ) {
		values[offset] = value;
	}
	/**
	 * gets value of matrix at (row,col)
	 */
	public final double get(int row, int col ) {
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
	public DenseMatrix recpr(){// note: per element reciprocal, ie 1/element
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
	 * for each element: element = Math.pow(element,power)
	 */
	public DenseMatrix sqrt() {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = Math.sqrt( values[i] );
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
    public DenseMatrix mexp() {
		if( this.cols != this.rows ) {
			throw new RuntimeException("exp matrix size error: must be square matrix");
		}
        DenseMatrix result = new DenseMatrix(this.cols,this.cols);
        JeigenJna.Jeigen.jeigen_exp(rows,values,result.values);
        return result;
    }
    public DenseMatrix mlog() {
		if( this.cols != this.rows ) {
			throw new RuntimeException("log matrix size error: must be square matrix");
		}
        DenseMatrix result = new DenseMatrix(this.cols,this.cols);
        JeigenJna.Jeigen.jeigen_log(rows,values,result.values);
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
	public DenseMatrix sortRows(DenseMatrix keyColumns ) {
		return DenseSorter.sortRows(this, keyColumns);
	}
	public DenseMatrix sortCols(DenseMatrix keyColumns ) {
		return DenseSorter.sortCols(this, keyColumns);
	}
	public DenseMatrix sumOverRows(DenseMatrix keyColumns ) {
		return DenseAggregator.sumOverRows(this, keyColumns);
	}
	public DenseMatrix meanOverRows(DenseMatrix keyColumns ) {
		return DenseAggregator.meanOverRows(this, keyColumns);
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
