// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

/** Sparse matrix in sparse column format.  This isn't used at the moment
 * since Lil format is easier for sending to Eigen, and also for creating new matrices.
 */
public class SparseMatrixCCS {
	public final int rows;
	public final int cols;
	public final ArrayList<Integer> outerStarts = new ArrayList<Integer>();
	public final ArrayList<Integer> innerIndices = new ArrayList<Integer>();
	public final ArrayList<Double> values = new ArrayList<Double>();
	public SparseMatrixCCS(int rows, int cols ) {
		this.rows = rows;
		this.cols = cols;
		outerStarts.ensureCapacity(cols + 1);
		for( int i = 0; i < cols + 1; i++ ) {
			outerStarts.add(0);
		}
	}
	public int nonZeros() {
		return outerStarts.get(cols);
	}
	public int nonZeros( int col  ){
		return outerStarts.get(col + 1) - outerStarts.get(col);		
	}
	public void reserve(int capacity) {
		outerStarts.ensureCapacity(cols);
		innerIndices.ensureCapacity(capacity);
		values.ensureCapacity(capacity);
	}
	public DenseMatrix shape() {
		return new DenseMatrix(new double[][]{{rows,cols}}); 
	}
	public SparseMatrixLil toLil(){
		SparseMatrixLil result = new SparseMatrixLil(rows, cols);
		result.reserve(outerStarts.get(cols));
		for( int col = 0; col < cols; col++ ) {
	          int rowStartIndex = outerStarts.get(col);
	          int rowEndIndex = outerStarts.get(col + 1) - 1;
	          int rowCapacity = rowEndIndex - rowStartIndex + 1;
	          for( int ri = 0; ri < rowCapacity; ri++ ) {
	             int row = innerIndices.get(rowStartIndex + ri);
	             double value = values.get(rowStartIndex + ri);
	             result.append(row, col, value);
	          }
	      }
	      return result;
	}
	public double get(int row, int col ) {
		int numEntries = nonZeros(col);
		if( numEntries == 0 ) {
			return 0;
		}
		int l = outerStarts.get(col);
		int h = outerStarts.get(col+1) - 1;
		while( l < h ) {
			int m =  ( l + h ) / 2;
			int ixm = innerIndices.get(m);
			if(ixm < row ) {
				l = m + 1;
			} else if( ixm > row ) {
				h = m - 1;
			} else {
				l = h = m;
			}
		}
		int ixl = innerIndices.get(l);
		if(ixl == row ) {
			return values.get(l);
		}
		return 0;
	}
}
