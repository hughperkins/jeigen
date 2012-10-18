// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import junit.framework.TestCase;

public class TestDenseSorter extends TestCase {
	public void testSort() {
		DenseMatrix A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4");
		DenseMatrix B = A.sortRows(new DenseMatrix("2; 1"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("5 1 3; 1 2 3; 8 2 4")));
	}
	public void testSortByCols() {
		DenseMatrix A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4").t();
		DenseMatrix B = A.sortCols(new DenseMatrix("2; 1"));
		System.out.println(B.t());
		assertTrue(B.equals(new DenseMatrix("5 1 3; 1 2 3; 8 2 4").t()));
	}
}
