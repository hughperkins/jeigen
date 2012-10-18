// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

final class Conversion {
	public static final SparseMatrixCCS toCCS( SparseMatrixLil in ) {
		SparseMatrixCCS result = new SparseMatrixCCS(in.rows, in.cols);
		in.shrink();
		result.reserve(in.size);
		in.sort();
		int lastCol = -1;
		int size = in.size;
//		int j = 0;
		for( int i = 0; i < size; i++ ) {
			int row = in.rowIdx[i];
			int col = in.colIdx[i];
			double value = in.values[i];
			int thiscol = lastCol + 1;
			while( thiscol <= col ) {
				result.outerStarts.set(thiscol, i);
				thiscol += 1;
			}
			lastCol = col;
			result.innerIndices.add(i, row );
			result.values.add(i, value );
//			j++;
		}
		int thiscol = lastCol + 1;
		while( thiscol < in.cols ) {
			result.outerStarts.set(thiscol, size);
			thiscol += 1;
		}
		lastCol = in.cols - 1;
		result.outerStarts.set(lastCol+1, size );
		return result;

	}
}
