// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

// stores a sparse matrix as row,column,value triplets
// this is the most convenient for sending through jna to eigen
// it's also the fastest for appending new values to a new matrix
// Note that DenseMatrix is faster than SparseMatrixLil, if the matrix is fully dense
// SparseMatrixLil is only faster if a lot of the matrix values are zero
public class SparseMatrixLil {
	public static class Entry implements Comparable<Entry> {
		public int row;
		public int col;
		public double value;
		public Entry(int row, int col, double value) {
			this.row = row;
			this.col = col;
			this.value = value;
		}
		@Override
		public int compareTo(Entry o) {
			if( o.col > col ) {
				return 1;
			} else if( o.col < col ) {
				return -1;
			}
			if( o.row > row ) {
				return 1;
			} else if( o.row < row ) {
				return -1;
			}
			return 0;
		}
		@Override
		public String toString() {
			return "Entry [row=" + row + ", col=" + col + ", value=" + value + "]";
		}
		
	}
	public int rows;
	public int cols;
	final ArrayList<Entry> entries = new ArrayList<Entry>();

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
		for( Entry entry : entries ) {
			if( entry.row >= startrow && entry.row < endrowexclusive
					&& entry.col >= startcol && entry.col < endcolexclusive ) {
				result.append(entry.row - startrow,entry.col - startcol, entry.value );
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
		for( Entry entry : entries ) {
			result.append(entry.row, entry.col, entry.value );
		}
		for( Entry entry : two.entries ) {
			result.append(entry.row, entry.col + cols, entry.value );
		}
		return result;
	}
	public SparseMatrixLil concatDown(SparseMatrixLil two ){
		if( cols != two.cols ) {
			throw new RuntimeException("col mismatch " + cols + " vs " + two.cols );
		}
		SparseMatrixLil result = spzeros(rows + two.rows,cols );
		for( Entry entry : entries ) {
			result.append(entry.row, entry.col, entry.value );
		}
		for( Entry entry : two.entries ) {
			result.append(entry.row + rows, entry.col, entry.value );
		}
		return result;
	}
	public SparseMatrixLil t() {
		SparseMatrixLil result = new SparseMatrixLil(cols, rows);
		int numElements = entries.size();
		result.reserve(numElements);
		for( int i = 0; i < numElements; i++ ) {
			Entry entry = entries.get(i);
			result.append(entry.col, entry.row, entry.value );
		}
		return result;
	}
	public void reserve(int capacity) {
		entries.ensureCapacity(capacity);
	}
	public void append(int row, int col, double value ) {
		entries.add( new Entry(row,col,value));
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
		for( Entry entry : entries ) {
			if( entry.row < 0 || entry.col < 0 || entry.row >= rows || entry.col >= cols ) {
				throw new RuntimeException("entry " + entry + " outside of matrix dimensions");
			}
		}
	}
	static int allocateSparseMatrix(SparseMatrixLil mat ) {
//		Collections.sort(mat.entries);
		mat.validateEntries();
		int numEntries = mat.entries.size();
		int[] rowarray = new int[numEntries];
		int[] colarray = new int[numEntries];
		double[] valuearray = new double[numEntries];
		for( int i = 0; i < numEntries; i++ ) {
			Entry entry = mat.entries.get(i);
			rowarray[i] = entry.row;
			colarray[i] = entry.col;
			valuearray[i] = entry.value;
		}
		return JeigenJna.Jeigen.allocateSparseMatrix(mat.entries.size(), mat.rows, mat.cols, rowarray, colarray, valuearray);		
	}
	static SparseMatrixLil getSparseMatrixFromHandle(int handle ) {
		int[] stats = new int[3];
		JeigenJna.Jeigen.getSparseMatrixStats(handle, stats);
		int rows = stats[0];
		int cols = stats[1];
		int numEntries = stats[2];
		int[] rowarray = new int[numEntries];
		int[] colarray = new int[numEntries];
		double[] valuearray = new double[numEntries];
		JeigenJna.Jeigen.getSparseMatrix(handle, rowarray, colarray, valuearray);
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(numEntries);
		for( int i = 0; i < numEntries; i++ ) {
			result.append(rowarray[i], colarray[i], valuearray[i]);
		}
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
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, - entry.value ); 
		}
		return result;		
	}
	public SparseMatrixLil inv() {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, 1 / entry.value ); 
		}
		return result;		
	}
	public SparseMatrixLil add(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, entry.value + s ); 
		}
		return result;		
	}
	public SparseMatrixLil sub(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, entry.value - s ); 
		}
		return result;		
	}
	public SparseMatrixLil mul(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, entry.value * s ); 
		}
		return result;		
	}
	public SparseMatrixLil div(double s) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, entry.value / s ); 
		}
		return result;		
	}
	public SparseMatrixLil pow( double power ) {
		SparseMatrixLil result = new SparseMatrixLil(rows,cols);
		result.reserve(entries.size());
		for( Entry entry : entries ) {
			result.append(entry.row,entry.col, Math.pow( entry.value, power ) ); 
		}
		return result;		
	}
	public DenseMatrix toDense() {
		DenseMatrix result = new DenseMatrix(rows,cols);
		for( Entry entry : entries ) {
			result.set(entry.row, entry.col, entry.value);
		}
		return result;
	}
	public static SparseMatrixLil spzeros( int r, int c ) {
		return new SparseMatrixLil(r, c);
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
		SparseMatrixCCS result = new SparseMatrixCCS(rows, cols);
		result.reserve(entries.size());
		Collections.sort(entries);
		int i = 0;
		int lastCol = -1;
		for( Entry entry : entries) {
			int thiscol = lastCol + 1;
			while( thiscol <= entry.col ) {
				result.outerStarts.set(thiscol, i);
				thiscol += 1;
			}
			lastCol = entry.col;
			result.innerIndices.set(i, entry.row );
			result.values.set(i, entry.value );
			i += 1;
		}
		int thiscol = lastCol + 1;
		while( thiscol < cols ) {
			result.outerStarts.set(thiscol, i);
			thiscol += 1;
		}
		lastCol = cols - 1;
		result.outerStarts.set(lastCol+1, i );
		return result;
	}
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SparseMatrixLil, " + rows + " * " + cols + ":\n");
		for( Entry entry : entries) {
			stringBuilder.append("( " + entry.row + ", " + entry.col + ", " + entry.value + " )\n");
		}		
		return stringBuilder.toString();
	}
	public DenseMatrix shape() {
		return new DenseMatrix(new double[][]{{rows,cols}}); 
	}
	public SparseMatrixLil sum(int axis ) {
		if( axis == 0 ) {
			DenseMatrix result = new DenseMatrix(1, cols);
			// cheat and use densematrix for now...
			for( Entry entry : entries ) {
				result.set(0, entry.col, result.get(0, entry.col) + entry.value);
			}
			return result.toSparseLil();
		} else if( axis == 1 ) {
			DenseMatrix result = new DenseMatrix(rows, 1);
			// cheat and use densematrix for now...
			for( Entry entry : entries ) {
				result.set(entry.row, 0, result.get(entry.row, 0) + entry.value);
			}
			return result.toSparseLil();
		} else {
			throw new RuntimeException("invalid axis " + axis );
		}
	}
	public double s() {
		// assumes out of order
		for( Entry entry : entries ) {
			if( entry.row == 0 && entry.col == 0 ) {
				return entry.value;
			}
		}
		return 0;
	}
}
