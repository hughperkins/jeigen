// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

public final class DenseAggregator {
	public static final DenseMatrix sumOverRows( DenseMatrix mat, DenseMatrix keyColumns ) {
		if( keyColumns.cols != 1 ) {
			throw new RuntimeException("keyColumns should have exactly 1 column");
		}
		if( mat.rows < 1 ) {
			return new DenseMatrix(1, mat.cols);
		}
		DenseMatrix sortedmat = DenseSorter.sortRows(mat, keyColumns);
//		System.out.println(sortedmat);
		int numKeys = keyColumns.rows;
		int[] keyColumnsi = new int[numKeys];
		for( int i = 0; i < numKeys; i++ ) {
			keyColumnsi[i] = (int)keyColumns.values[i];
		}
		int rows = mat.rows;
		int cols = mat.cols;
		int numResultRows = 1;
		for( int r = 1; r < rows; r++ ) {
			boolean keysSame = true;
			for( int k = 0; keysSame && k < numKeys; k++ ) {
				int index = keyColumnsi[k];
				if(sortedmat.get(r,index)!=sortedmat.get(r-1, index)) {
					keysSame = false;
				}
			}
			if(!keysSame){
				numResultRows++;
			}
		}
		DenseMatrix result = new DenseMatrix(numResultRows, cols );
		int resultrow = 0;
		for( int i = 0; i < cols; i++ ) {
			result.set(0,i,sortedmat.get(0, i));
		}
		for( int r = 1; r < rows; r++ ) {
			boolean keysSame = true;
			for( int k = 0; keysSame && k < numKeys; k++ ) {
				int index = keyColumnsi[k];
				if(sortedmat.get(r,index)!=sortedmat.get(r-1, index)) {
					keysSame = false;
				}
			}
			if(!keysSame){
				resultrow++;
			}
			for( int c = 0; c < cols; c++ ) {
				result.set(resultrow, c, result.get(resultrow, c) + sortedmat.get(r, c));
			}
			for( int k = 0; k < numKeys; k++ ) {
				int index = keyColumnsi[k];
				result.set(resultrow, index, sortedmat.get(r, index));
			}
		}
		return result;
	}
	public static final DenseMatrix meanOverRows( DenseMatrix mat, DenseMatrix keyColumns ) {
		if( keyColumns.cols != 1 ) {
			throw new RuntimeException("keyColumns should have exactly 1 column");
		}
		if( mat.rows < 1 ) {
			return new DenseMatrix(1, mat.cols);
		}
		DenseMatrix sortedmat = DenseSorter.sortRows(mat, keyColumns);
//		System.out.println(sortedmat);
		int numKeys = keyColumns.rows;
		int[] keyColumnsi = new int[numKeys];
		for( int i = 0; i < numKeys; i++ ) {
			keyColumnsi[i] = (int)keyColumns.values[i];
		}
		int rows = mat.rows;
		int cols = mat.cols;
		int numResultRows = 1;
		for( int r = 1; r < rows; r++ ) {
			boolean keysSame = true;
			for( int k = 0; keysSame && k < numKeys; k++ ) {
				int index = keyColumnsi[k];
				if(sortedmat.get(r,index)!=sortedmat.get(r-1, index)) {
					keysSame = false;
				}
			}
			if(!keysSame){
				numResultRows++;
			}
		}
		DenseMatrix result = new DenseMatrix(numResultRows, cols );
		int resultrow = 0;
		for( int i = 0; i < cols; i++ ) {
			result.set(0,i,sortedmat.get(0, i));
		}
		int numRowsThisKey = 1;
		for( int r = 1; r < rows; r++ ) {
			boolean keysSame = true;
			for( int k = 0; keysSame && k < numKeys; k++ ) {
				int index = keyColumnsi[k];
				if(sortedmat.get(r,index)!=sortedmat.get(r-1, index)) {
					keysSame = false;
				}
			}
			if(!keysSame){
				for( int c = 0; c < cols; c++ ) {
					result.set(resultrow, c, result.get(resultrow, c)  / (double)numRowsThisKey);
				}				
				for( int k = 0; k < numKeys; k++ ) {
					int index = keyColumnsi[k];
					result.set(resultrow, index, sortedmat.get(r-1, index));
				}
				resultrow++;
				numRowsThisKey = 0;
			}
			for( int c = 0; c < cols; c++ ) {
				result.set(resultrow, c, result.get(resultrow, c) + sortedmat.get(r, c));
			}
			numRowsThisKey++;
		}
		for( int c = 0; c < cols; c++ ) {
			result.set(resultrow, c, result.get(resultrow, c)  / (double)numRowsThisKey);
		}				
		for( int k = 0; k < numKeys; k++ ) {
			int index = keyColumnsi[k];
			result.set(resultrow, index, sortedmat.get(rows - 1, index));
		}
		return result;
	}
}
