// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

// copies the matrix into an arraylist of Entry objects, then uses Collections sorter
// advantages: fast (since just sorts pointers), simple
// disadvantages: uses more memory
class SparseMatrixLilSorter2 {
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
				return -1;
			} else if( o.col < col ) {
				return 1;
			}
			if( o.row > row ) {
				return -1;
			} else if( o.row < row ) {
				return 1;
			}
			return 0;
		}
		@Override
		public String toString() {
			return "Entry [row=" + row + ", col=" + col + ", value=" + value + "]";
		}

	}

	public static void sort(SparseMatrixLil mat ) {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		mat.shrink();
		int size = mat.size;
		entries.ensureCapacity(mat.size);
		for( int i = 0; i < size; i++ ) {
			entries.add(new Entry(mat.rowIdx[i],mat.colIdx[i],mat.values[i]));
		}
		Collections.sort(entries);
		for( int i = 0; i < size; i++ ) {
			Entry entry = entries.get(i);
			mat.rowIdx[i] = entry.row;
			mat.colIdx[i] = entry.col;
			mat.values[i] = entry.value;
		}
	}
}
