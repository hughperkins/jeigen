// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import junit.framework.TestCase;

public class TestDenseAggregator extends TestCase {
	public void test(){
		DenseMatrix A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4");
		DenseMatrix B = A.sumOverRows(new DenseMatrix("2"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("6 3 3; 8 2 4")));
		B = A.sumOverRows(new DenseMatrix("2; 1"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("5 1 3; 1 2 3; 8 2 4")));
		
		A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4; 8 1 3");
		B = A.sumOverRows(new DenseMatrix("2; 1"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("13 1 3; 1 2 3; 8 2 4")));
	}
	public void testMean(){
		DenseMatrix A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4");
		DenseMatrix B = A.meanOverRows(new DenseMatrix("2"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("3 1.5 3; 8 2 4")));
		B = A.meanOverRows(new DenseMatrix("2; 1"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("5 1 3; 1 2 3; 8 2 4")));
		
		A = new DenseMatrix("1 2 3; 5 1 3; 8 2 4; 8 1 3");
		B = A.meanOverRows(new DenseMatrix("2; 1"));
		System.out.println(B);
		assertTrue(B.equals(new DenseMatrix("6.5 1 3; 1 2 3; 8 2 4")));
	}
}
