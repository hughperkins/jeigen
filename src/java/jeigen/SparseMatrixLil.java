// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

/** Stores a sparse matrix as row,column,value triplets. 
 * SparseMatrixLil is faster if a lot of the matrix values are zero.
 * When the matrix is fully dense, DenseMatrix is faster.
 */
public class SparseMatrixLil {
	//	public static class Entry implements Comparable<Entry> {
	//		public int row;
	//		public int col;
	//		public double value;
	//		public Entry(int row, int col, double value) {
	//			this.row = row;
	//			this.col = col;
	//			this.value = value;
	//		}
	//		@Override
	//		public int compareTo(Entry o) {
	//			if( o.col > col ) {
	//				return 1;
	//			} else if( o.col < col ) {
	//				return -1;
	//			}
	//			if( o.row > row ) {
	//				return 1;
	//			} else if( o.row < row ) {
	//				return -1;
	//			}
	//			return 0;
	//		}
	//		@Override
	//		public String toString() {
	//			return "Entry [row=" + row + ", col=" + col + ", value=" + value + "]";
	//		}
	//		
	//	}
	public int rows;
	public int cols;
	int size = 0;
	int capacity = 1000;
	//	final ArrayList<Entry> entries = new ArrayList<Entry>();
	int[] rowIdx = new int[capacity];
	int[] colIdx = new int[capacity];
	double[] values = new double[capacity];

	/**
	 * Creates matrix from valuesstring in format "12 3; 4 5"
	 * Result:
	 *   12 3
	 *   4  5
	 *   Note that all entries should be specified, but 0s will not
	 *   be added to the sparse entries
	 */
	public SparseMatrixLil(String valuesstring ) {
		SparseMatrixLil sm = new DenseMatrix(valuesstring).toSparseLil();
		copy(sm, this);
	}
	/**
	 * copies values from source to target
	 */
	static final void copy(SparseMatrixLil source, SparseMatrixLil target ) {
		int size = source.size;
		target.rows = source.rows;
		target.cols = source.cols;
		target.reserve(size);
		for( int i = 0; i < size; i++ ) {
			target.append(source.rowIdx[i], source.colIdx[i], source.values[i]);
		}
	}
	public int getRowIdx(int i ) {
		return rowIdx[i];
	}
	public int getColIdx(int i ) {
		return colIdx[i];
	}
	public double getValue(int i ) {
		return values[i];
	}
	public int getSize() {
		return size;
	}
	public void sort() {
		sortFast();
	}
	public void sortFast() { // uses more memory
		SparseMatrixLilSorter2.sort(this);		
	}
	public void sortInplace() { // uses less memory
		SparseMatrixLilSorter.sort(this, 0, size);		
	}
	public SparseMatrixLil(int rows, int cols ) {
		this.rows = rows;
		this.cols = cols;
	}
	public SparseMatrixLil col( int col ) {
		return slice(0, rows, col, col + 1 );
	}
	public SparseMatrixLil row( int row ) {
		return slice(row, row + 1, 0, cols );
	}
//	public SparseMatrixLil rows(DenseMatrix indexes ){
//		/**
//		 * returns new sparsematrix containing the rows indexed by
//		 * indexes
//		 * indexes should be a single column
//		 * indexes may contain duplicates
//		 * not terribly efficient right now... 
//		 */
//		if( indexes.cols != 1 ) {
//			throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
//		}
//		SparseMatrixLil result = new SparseMatrixLil(indexes.rows, cols);
//		for( int i = 0; i < indexes.rows; i++ ) {
//			double index = indexes.get(i,0);
//			int entries = values.length;
//			for( int j = 0; j < entries; j++ ) {
//				if( rowIdx[j] == index ) {
//					result.append(i, colIdx[j], values[j]);
//				}
//			}
//		}
//		return result;
//	}
	public SparseMatrixLil rows(DenseMatrix indexes ){
		/**
		 * returns new sparsematrix containing the rows indexed by
		 * indexes
		 * indexes should be a single column
		 * indexes may contain duplicates
		 * not terribly efficient right now... 
		 */
		if( indexes.cols != 1 ) {
			throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
		}
		SparseMatrixCCS translatedCCS = this.t().toCCS();
		SparseMatrixLil result = new SparseMatrixLil(indexes.rows, cols);
		for( int i = 0; i < indexes.rows; i++ ) {
			int index = (int)indexes.get(i,0);
			int entries = translatedCCS.nonZeros(index);
			int coloffset = translatedCCS.outerStarts.get(index);
			for( int j = 0; j < entries; j++ ) {
				result.append(i, 
						translatedCCS.innerIndices.get(coloffset + j), 
						translatedCCS.values.get(coloffset+j));
			}
		}
		return result;
	}
	public SparseMatrixLil cols( int startcol, int endcolexclusive ) {
		return slice(0, rows, startcol, endcolexclusive );
	}
	public SparseMatrixLil rows( int startrow, int endrowexclusive ) {
		return slice(startrow, endrowexclusive, 0, cols );
	}
	public SparseMatrixLil slice( int startrow, int endrowexclusive, int startcol, int endcolexclusive ) {
		// this implementation is far from ideal, but at least it avoids sorting
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
		SparseMatrixLil result = new SparseMatrixLil(resultrows, resultcols);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			if( row >= startrow && row < endrowexclusive
					&& col >= startcol && col < endcolexclusive ) {
				result.append(row - startrow,col - startcol, value );
			}
		}
		return result;
	}
	@Override
	public boolean equals( Object second ) {
		if( second == null ) {
			return false;
		}
		return toDense().equals(second);
	}
	public SparseMatrixLil concatRight(SparseMatrixLil two ){
		if( rows != two.rows ) {
			throw new RuntimeException("row mismatch " + rows + " vs " + two.rows );
		}
		SparseMatrixLil result = spzeros(rows,cols + two.cols );
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			//		for( Entry entry : entries ) {
			result.append(row, col, value );
		}
		count = two.size; for( int i = 0; i < count; i++ ) {
			int row = two.rowIdx[i]; int col = two.colIdx[i]; double value = two.values[i];
			//		for( Entry entry : two.entries ) {
			result.append(row, col + cols, value );
		}
		return result;
	}
	public SparseMatrixLil concatDown(SparseMatrixLil two ){
		if( cols != two.cols ) {
			throw new RuntimeException("col mismatch " + cols + " vs " + two.cols );
		}
		SparseMatrixLil result = spzeros(rows + two.rows,cols );
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			//		for( Entry entry : entries ) {
			result.append(row, col, value );
		}
		count = two.size; for( int i = 0; i < count; i++ ) {
			int row = two.rowIdx[i]; int col = two.colIdx[i]; double value = two.values[i];
			//		for( Entry entry : two.entries ) {
			result.append(row + rows, col, value );
		}
		return result;
	}
	public SparseMatrixLil t() {
		SparseMatrixLil result = new SparseMatrixLil(cols, rows);
		int numElements = size;
		result.reserve(numElements);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(col, row, value );
		}
		return result;
	}
	public void reserve(int capacity) {
		if( capacity > this.capacity ) {
			int[] newrows = new int[capacity];
			int[] newcols = new int[capacity];
			double[] newvalues = new double[capacity];
			for( int i = 0; i < this.size; i++ ) {
				newrows[i] = rowIdx[i];
				newcols[i] = colIdx[i];
				newvalues[i] = values[i];
			}
			this.capacity = capacity;
			this.rowIdx = newrows;
			this.colIdx = newcols;
			this.values = newvalues;
		}
	}
	public void append(int row, int col, double value ) {
		if( size >= capacity - 1 ) {
			reserve( capacity * 3 / 2 );
		}
		rowIdx[size] = row;
		colIdx[size] = col;
		values[size] = value;
		size++;
	}
	public void shrink() { // shrinks capacity down to size
		int[] newrows = new int[size];
		int[] newcols = new int[size];
		double[] newvalues = new double[size];
		for( int i = 0; i < this.size; i++ ) {
			newrows[i] = rowIdx[i];
			newcols[i] = colIdx[i];
			newvalues[i] = values[i];
		}
		this.capacity = size;
		this.rowIdx = newrows;
		this.colIdx = newcols;
		this.values = newvalues;		
	}
	// sparse fill for now...
	public static SparseMatrixLil rand( int rows, int cols ) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		Random random = new Random();
		result.reserve( rows * cols );
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				result.append(r, c, random.nextDouble());
			}
		}
		return result;
	}
	void validateEntries() {
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i];
			//		for( Entry entry : entries ) {
			if( row < 0 || col < 0 || row >= rows || col >= cols ) {
				throw new RuntimeException("entry " + row + " " + col + " outside of matrix dimensions");
			}
		}
	}
	static int allocateSparseMatrix(SparseMatrixLil mat ) {
		//		Collections.sort(mat.entries);
		mat.validateEntries();
		return JeigenJna.Jeigen.allocateSparseMatrix(mat.size, mat.rows, mat.cols, 
				mat.rowIdx, mat.colIdx, mat.values);		
	}
	static SparseMatrixLil getSparseMatrixFromHandle(int handle ) {
		int[] stats = new int[3];
		JeigenJna.Jeigen.getSparseMatrixStats(handle, stats);
		int rows = stats[0];
		int cols = stats[1];
		int numEntries = stats[2];
//		int[] rowarray = new int[numEntries];
//		int[] colarray = new int[numEntries];
//		double[] valuearray = new double[numEntries];
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(numEntries);
		JeigenJna.Jeigen.getSparseMatrix(handle, result.rowIdx, result.colIdx, result.values);
		result.size = numEntries;
//		for( int i = 0; i < numEntries; i++ ) {
//			result.append(rowarray[i], colarray[i], valuearray[i]);
//		}
		return result;
	}
	public SparseMatrixLil mmul( SparseMatrixLil second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		int onehandle = allocateSparseMatrix(this);
		int twohandle = allocateSparseMatrix(second);
		int resulthandle = JeigenJna.Jeigen.sparse_multiply(rows, cols, second.cols, onehandle, twohandle);
		JeigenJna.Jeigen.freeSparseMatrix(onehandle);
		JeigenJna.Jeigen.freeSparseMatrix(twohandle);
		SparseMatrixLil result = getSparseMatrixFromHandle(resulthandle); 
		JeigenJna.Jeigen.freeSparseMatrix(resulthandle);
		return result;
	}
	public SparseMatrixLil dummy_mmul( SparseMatrixLil second, int numFilledResultColumns ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		int onehandle = allocateSparseMatrix(this);
		int twohandle = allocateSparseMatrix(second);
		int resulthandle = JeigenJna.Jeigen.sparse_dummy_op2(rows, cols, second.cols, onehandle, twohandle, numFilledResultColumns);
		JeigenJna.Jeigen.freeSparseMatrix(onehandle);
		JeigenJna.Jeigen.freeSparseMatrix(twohandle);
		SparseMatrixLil result = getSparseMatrixFromHandle(resulthandle); 
		JeigenJna.Jeigen.freeSparseMatrix(resulthandle);
		return result;
	}
	public DenseMatrix mmul( DenseMatrix second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		int onehandle = allocateSparseMatrix(this);
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.sparse_dense_multiply(rows, cols, second.cols,
				onehandle, second.values, result.values );
		JeigenJna.Jeigen.freeSparseMatrix(onehandle);
		return result;
	}
	public DenseMatrix eq( DenseMatrix second ) {
		return this.toDense().eq(second);
	}
	// if second matrix is dense, then simply convert to dense then do the operation
	public DenseMatrix sub( DenseMatrix second ) {
		return toDense().sub(second);
	}
	public DenseMatrix add( DenseMatrix second ) {
		return toDense().sub(second);
	}
	public DenseMatrix div( DenseMatrix second ) {
		return toDense().sub(second);
	}
	public DenseMatrix mul( DenseMatrix second ) {
		return toDense().sub(second);
	}
	public SparseMatrixLil neg() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, - value ); 
		}
		return result;		
	}
	/**
	 * element = 1 / element
	 */
	public SparseMatrixLil recpr() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, 1 / value ); 
		}
		return result;		
	}
	/**
	 * element = Math.round(element)
	 */
	public SparseMatrixLil round() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, Math.round( value ) ); 
		}
		return result;		
	}
	/**
	 * element = Math.floor(element)
	 */
	public SparseMatrixLil floor() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, Math.floor( value ) ); 
		}
		return result;		
	}
	/**
	 * element = Math.ceil(element)
	 */
	public SparseMatrixLil ceil() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, Math.ceil( value ) ); 
		}
		return result;		
	}
	public SparseMatrixLil add(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, value + s ); 
		}
		return result;		
	}
	public SparseMatrixLil sub(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, value - s ); 
		}
		return result;		
	}
	public SparseMatrixLil mul(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, value * s ); 
		}
		return result;		
	}
	public SparseMatrixLil div(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, value / s ); 
		}
		return result;		
	}
	public SparseMatrixLil pow( double power ) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(size);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.append(row,col, Math.pow( value, power ) ); 
		}
		return result;		
	}
	public DenseMatrix toDense() {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			result.set(row, col, value);
		}
		return result;
	}
	public static SparseMatrixLil spzeros( int r, int c ) {
		return new SparseMatrixLil(r, c);
	}
	public static SparseMatrixLil speye( int size ) { // note: need to add fillfactor
		SparseMatrixLil result = new SparseMatrixLil(size, size);
		result.reserve(size );
		for( int i = 0; i < size; i++ ) {
			result.append(i, i, 1);
		}
		return result;
	}
	public static SparseMatrixLil sprand( int rows, int cols ) { // note: need to add fillfactor
		SparseMatrixLil result = new SparseMatrixLil(rows, cols);
		Random random = new Random();
		result.reserve(rows * cols );
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows; r++ ) {
				result.append(r,c, random.nextDouble());
			}
		}
		return result;
	}
	public static SparseMatrixLil spdiag( DenseMatrix v ) {
		if( v.cols != 1 ) {
			throw new RuntimeException("diag needs a matrix with one column exactly");
		}
		int size = v.rows;
		SparseMatrixLil result = new SparseMatrixLil(size,size);
		result.reserve(size);
		for( int i = 0; i < size; i++ ) {
			result.append(i,i,v.get(i,0));
		}
		return result;
	}
	public SparseMatrixCCS toCCS() {
		return Conversion.toCCS(this);
	}
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SparseMatrixLil, " + rows + " * " + cols + ":\n");
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			stringBuilder.append("( " + row + ", " + col + ", " + value + " )\n");
		}		
		return stringBuilder.toString();
	}
	public DenseMatrix shape() {
		return new DenseMatrix(new double[][]{{rows,cols}}); 
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
	public DenseMatrix sumOverRows() {
		DenseMatrix result = new DenseMatrix(1, cols);
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int col = colIdx[i]; double value = values[i];
			result.set(0, col, result.get(0, col) + value);
		}
		return result;
	}
	public DenseMatrix sumOverCols() {
		DenseMatrix result = new DenseMatrix(rows, 1);
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; double value = values[i];
			result.set(row, 0, result.get(row, 0) + value);
		}
		return result;
	}
	public DenseMatrix minOverRows() {
		DenseMatrix result = new DenseMatrix(1, cols);
		for( int i = 0; i < cols; i++ ) {
			result.values[i] = Double.POSITIVE_INFINITY;
		}
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int col = colIdx[i]; double value = values[i];
			result.set(0, col, Math.min( result.get(0, col), value ) );
		}
		return result;
	}
	public DenseMatrix minOverCols() {
		DenseMatrix result = new DenseMatrix(rows, 1);
		for( int i = 0; i < rows; i++ ) {
			result.values[i] = Double.POSITIVE_INFINITY;
		}
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; double value = values[i];
			result.set(row, 0, Math.min( result.get(row, 0), value ) );
		}
		return result;
	}
	public DenseMatrix maxOverRows() {
		DenseMatrix result = new DenseMatrix(1, cols);
		for( int i = 0; i < cols; i++ ) {
			result.values[i] = Double.NEGATIVE_INFINITY;
		}
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int col = colIdx[i]; double value = values[i];
			result.set(0, col, Math.max( result.get(0, col), value ) );
		}
		return result;
	}
	public DenseMatrix maxOverCols() {
		DenseMatrix result = new DenseMatrix(rows, 1);
		for( int i = 0; i < rows; i++ ) {
			result.values[i] = Double.NEGATIVE_INFINITY;
		}
		// cheat and use densematrix for now...
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; double value = values[i];
			result.set(row, 0, Math.max( result.get(row, 0), value ) );
		}
		return result;
	}
	public double s() {
		// assumes out of order
		int count = size; for( int i = 0; i < count; i++ ) {
			int row = rowIdx[i]; int col = colIdx[i]; double value = values[i];
			if( row == 0 && col == 0 ) {
				return value;
			}
		}
		return 0;
	}
}
