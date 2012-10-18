// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

public class DenseSorter {
	public static class Key implements Comparable<Key> {
		public double[] keys;
		public int originalindex;
		public Key(double[] keys, int originalindex) {
			this.keys = keys;
			this.originalindex = originalindex;
		}
		@Override
		public int compareTo(Key second) {
			int numKeys = keys.length;
			for( int i = 0; i < numKeys; i++ ) {
				double keyone = keys[i];
				double keytwo = second.keys[i];
				if( keyone < keytwo ) {
					return -1;
				} else if( keyone > keytwo ) {
					return 1;
				}
			}
			return 0;
		}
		
	}
	public static final DenseMatrix sortRows(DenseMatrix mat, DenseMatrix sortKeys ) {
		int rows = mat.rows;
		ArrayList<Key> keys = new ArrayList<DenseSorter.Key>(rows);
		if( sortKeys.cols != 1 ) {
			throw new RuntimeException("sortKeys should have one column, not " + sortKeys.cols );
		}
		int numKeys = sortKeys.rows;
		int[] keyColumns = new int[numKeys];
		for( int i = 0; i < numKeys; i++ ) {
			keyColumns[i] = (int)sortKeys.values[i];
		}
		for( int r = 0; r < rows; r++ ) {
			double[] thiskeys = new double[numKeys];
			for( int i = 0; i < numKeys; i++ ) {
				thiskeys[i] = mat.get(r, keyColumns[i]);
			}
			Key key = new Key(thiskeys,r);
			keys.add(key);
		}
		Collections.sort(keys);
		int cols = mat.cols;
		DenseMatrix result = new DenseMatrix(rows, mat.cols);
		for( int r = 0; r < rows; r++ ) {
			int destrow = r;
			Key key = keys.get(destrow);
			int sourcerow = key.originalindex;
			for( int c = 0; c < cols; c++ ) {
				result.set(destrow,c,mat.get(sourcerow,c));
			}
		}
		return result;
	}
	public static final DenseMatrix sortCols(DenseMatrix mat, DenseMatrix sortKeys ) {
		int rows = mat.rows;
		int cols = mat.cols;
		ArrayList<Key> keys = new ArrayList<DenseSorter.Key>(rows);
		if( sortKeys.cols != 1 ) {
			throw new RuntimeException("sortKeys should have one column, not " + sortKeys.cols );
		}
		int numKeys = sortKeys.rows;
		int[] keyRows = new int[numKeys];
		for( int i = 0; i < numKeys; i++ ) {
			keyRows[i] = (int)sortKeys.values[i];
		}
		for( int c = 0; c < cols; c++ ) {
			double[] thiskeys = new double[numKeys];
			for( int i = 0; i < numKeys; i++ ) {
				thiskeys[i] = mat.get( keyRows[i], c );
			}
			Key key = new Key(thiskeys,c);
			keys.add(key);
		}
		Collections.sort(keys);
		DenseMatrix result = new DenseMatrix(rows, mat.cols);
		for( int c = 0; c < cols; c++ ) {
			int destcol = c;
			Key key = keys.get(destcol);
			int sourcecol = key.originalindex;
			for( int r = 0; r < rows; r++ ) {
				result.set(r,destcol,mat.get(r,sourcecol));
			}
		}
		return result;
	}
}
