// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

// unit tests

package jeigen;

import static jeigen.TicToc.*;
import static jeigen.MatrixUtil.*;
import jeigen.DenseMatrix.SvdResult;
import junit.framework.TestCase;

public class TestJeigen extends TestCase {
	public void testOne() {
		DenseMatrix A = ones(3, 3);
		System.out.println(A);
		DenseMatrix B = ones(3, 3);
		System.out.println(B);
		DenseMatrix C = A.mmul(B);
		System.out.println(C);
	}
	public void testOneSparse() {
		SparseMatrixLil A = SparseMatrixLil.rand(3, 3).t();
		System.out.println(A);
		SparseMatrixLil B = SparseMatrixLil.rand(3, 3);
		System.out.println(B);
		SparseMatrixLil C = A.mmul(B);
		System.out.println(C);
		DenseMatrix CDense = A.toDense().mmul(B.toDense());
		System.out.println(CDense);
		assertTrue(C.equals(CDense));
	}
	public void testCreateSparse() {
		DenseMatrix A = rand(5,5);
		SparseMatrixLil B = A.toSparseLil();
		assertTrue(B.equals(B.mmul(eye(5))));
		assertTrue(B.equals(B.t().mmul(eye(5)).t()));
		B = spzeros(2,2);
		B.append(1, 0, 5);
		B.append(0,1,7);
		B.append(0,0,11);
		System.out.println(B.toDense());
		System.out.println(B.mmul(eye(2)));
		assertTrue( B.equals(B.mmul(eye(2))));
	}
	public void testTwo() {
		int K = 100;
		int N = 100000;
		DenseMatrix A = rand(N, K);
		DenseMatrix B = rand(K, N);
		Timer timer = new Timer();
		DenseMatrix C = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		DenseMatrix C1 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		DenseMatrix C2 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
	}
	public void testDenseSparseMmul() {
		int K = 60;
		int N = 1000;
		DenseMatrix A = DenseMatrix.rand(K, N);
		SparseMatrixLil B = SparseMatrixLil.rand(N, K);
		Timer timer = new Timer();
		DenseMatrix C = A.mmul(B);		
		timer.printTimeCheckMilliseconds();
		DenseMatrix C2 = A.mmul(B.toDense());
		assertTrue(C.equals(C2));
	}
	public void testSparseDenseMmul() {
		int K = 60;
		int N = 1000;
		SparseMatrixLil A = SparseMatrixLil.rand(K, N);
		DenseMatrix B = DenseMatrix.rand(N, K);
		Timer timer = new Timer();
		DenseMatrix C = A.mmul(B);		
		timer.printTimeCheckMilliseconds();
		DenseMatrix C2 = A.toDense().mmul(B);
		assertTrue(C.equals(C2));
	}
	public void testThreeSparse() {
		int K = 60;
		int N = 1000;
		SparseMatrixLil A = SparseMatrixLil.rand(N, K);
		SparseMatrixLil B = SparseMatrixLil.rand(K, N);
//		System.out.println(A.toDense());
		Timer timer = new Timer();
		SparseMatrixLil C = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		SparseMatrixLil C1 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		SparseMatrixLil C2 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		DenseMatrix CDense = B.toDense().mmul(A.toDense());
		timer.printTimeCheckMilliseconds("for dense");
		assertTrue(C.equals(CDense));

		K = 400;
		N = 1000;
		A = SparseMatrixLil.rand(N, K);
		B = SparseMatrixLil.rand(K, N);
//		System.out.println(A.toDense());
		timer = new Timer();
		C = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		C1 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		C2 = B.mmul(A);
		timer.printTimeCheckMilliseconds();
		CDense = B.toDense().mmul(A.toDense());
		timer.printTimeCheckMilliseconds("for dense");
		assertTrue(C.equals(CDense));
	}
	public void testldltsolve() {
		int K = 1000;
		DenseMatrix A_ = rand(K, K);
		DenseMatrix A = A_.t().mmul(A_);
		DenseMatrix B = rand(K, K);
		Timer timer = new Timer();
		DenseMatrix X = A.ldltSolve(B);
		timer.printTimeCheckMilliseconds();
		assertTrue( A.mmul(X).equals(B) );
	}
	public void testfullpivhouseholderqrsolve() {
		int K = 1000;
		DenseMatrix A = rand(K, K);
		DenseMatrix B = rand(K, K);
		Timer timer = new Timer();
		DenseMatrix X = A.fullPivHouseholderQRSolve(B);
		timer.printTimeCheckMilliseconds();
		assertTrue( A.mmul(X).equals(B) );
	}
	public void testSvd() {
		DenseMatrix A = rand(5,8);
		SvdResult result = A.svd();
		assertEquals(A, result.U.mmul(diag(result.S)).mmul(result.V.t()));
		Timer timer = new Timer();
		A = rand(500,1000);
		timer.printTimeCheckMilliseconds();
		result = A.svd();
		timer.printTimeCheckMilliseconds();
		assertEquals(A, result.U.mmul(diag(result.S)).mmul(result.V.t()));
	}
	public void testElements() {
		DenseMatrix A = rand(5,8);
		DenseMatrix B = rand(5,8);
		assertTrue(zeros(5,8).equals(A.sub(A)));
		assertTrue(A.equals(A.add(A).div(2)));
		assertTrue(ones(5,8).equals(A.div(A)));
		assertTrue(A.equals(A.mul(A).div(A)));
		assertTrue(A.neg().abs().equals(A));
		assertTrue(A.neg().add(A).equals(zeros(5,8)));
	}
	public void testSlice(){
		DenseMatrix A = rand(5,8);
		DenseMatrix B = rand(5,12);
		DenseMatrix C = rand(4,8);
		DenseMatrix AB = A.concatRight(B);
		assertTrue(A.equals(AB.cols(0,8)));
		assertTrue(B.equals(AB.cols(8,20)));
		DenseMatrix AC = A.concatDown(C);
		assertTrue(A.equals(AC.rows(0,5)));
		assertTrue(C.equals(AC.rows(5,9)));
	}
	public void testSliceSparse(){
		SparseMatrixLil A = sprand(5,8);
		SparseMatrixLil B = sprand(5,12);
		SparseMatrixLil C = sprand(4,8);
		SparseMatrixLil AB = A.concatRight(B);
		assertTrue(A.equals(AB.cols(0,8)));
		assertTrue(B.equals(AB.cols(8,20)));
		SparseMatrixLil AC = A.concatDown(C);
		assertTrue(A.equals(AC.rows(0,5)));
		assertTrue(C.equals(AC.rows(5,9)));
		
		A = spzeros(1,5);
		A.append(0, 2, 3);
		assertEquals(A.row(0).cols, 5);
	}
	public void testToSparse(){
		DenseMatrix A = rand(5,8);
		assertTrue(A.equals(A.toSparseLil().toDense()));
	}
	public void testSum() {
		DenseMatrix A = new DenseMatrix(new double[][]{{1,9},{7,3}});
		assertTrue( new DenseMatrix(new double[][]{{8,12}}).equals(A.sum(0)) );
		assertTrue( new DenseMatrix(new double[][]{{10},{10}}).equals(A.sum(1)) );
		SparseMatrixLil B = A.toSparseLil();
		assertTrue( new DenseMatrix(new double[][]{{8,12}}).toSparseLil().equals(B.sum(0)) );
		assertTrue( new DenseMatrix(new double[][]{{10},{10}}).toSparseLil().equals(B.sum(1)) );
		assertTrue(B.sum(0).equals(A.sum(0)));
		assertTrue(B.sum(1).equals(A.sum(1)));
		assertTrue(B.sum(0).sum(1).equals(A.sum(0).sum(1)));
		SparseMatrixLil C = spzeros(4,5);
		C.append(1,2,5);
		C.append(3,1,3);
		C.append(3,2,7);
		C.append(1,4,11);
		System.out.println(C.sum(0).toDense() );
		assertTrue(new DenseMatrix(new double[][]{{0,3,12,0,11}}).equals(C.sum(0)));
		System.out.println(C.sum(1).toDense() );
		assertTrue(new DenseMatrix(new double[][]{{0},{16},{0},{10}}).equals(C.sum(1)));
		assertEquals(26.0, C.sum(1).sum(0).s());
		assertEquals(26.0, C.sum(0).sum(1).s());
	}	
	public void testTranspose() {
		DenseMatrix A = rand(5,8);
		DenseMatrix At = A.t();
		assertEquals(5,At.cols);
		assertEquals(8,At.rows);
		assertTrue(A.equals(At.t()));
		SparseMatrixLil B = A.toSparseLil();
		SparseMatrixLil Bt = B.t();
		assertEquals(5,Bt.cols);
		assertEquals(8,Bt.rows);
		assertTrue(At.equals(Bt));		
		assertTrue(A.equals(Bt.t()));		
	}
	public void testSort() {
		SparseMatrixLil B = spzeros(2,2);
		B.append(1, 0, 5);
		B.append(0,1,7);
		B.append(0,0,11);
		System.out.println(B.toDense());
		System.out.println(B);
		B.sort();
		System.out.println(B.toDense());
		System.out.println(B);
		assertEquals(11.0, B.values[0]);
		assertEquals(5.0, B.values[1]);
		assertEquals(7.0, B.values[2]);
		
		SparseMatrixLil A = sprand(5,8);
		B = A.add(0); // make copy
		System.out.println(B);
		B.sort();
		for( int i = 1; i < B.size; i++ ) {
			assertTrue( B.colIdx[i] >= B.colIdx[i-1] );
			if( B.colIdx[i] == B.colIdx[i-1]) {
				assertTrue( B.rowIdx[i] >= B.rowIdx[i-1] );
			}
		}
		assertTrue( A.equals(B));
		System.out.println(B);
		SparseMatrixLil Bt = B.t();
		System.out.println(Bt);
		Bt.sort();
		for( int i = 1; i < Bt.size; i++ ) {
			assertTrue( Bt.colIdx[i] >= Bt.colIdx[i-1] );
			if( Bt.colIdx[i] == Bt.colIdx[i-1]) {
				assertTrue( Bt.rowIdx[i] >= Bt.rowIdx[i-1] );
			}
		}
		System.out.println(Bt);
		assertTrue( A.toDense().t().equals(Bt));
	}
	public void xtestSortViaNative() {
		SparseMatrixLil B = spzeros(2,2);
		
		int R = 1000;
		int C = 8000;
		SparseMatrixLil A = sprand(R,C);
		System.out.println("created A");
		B = A.add(0); // make copy
		System.out.println("created B");
//		System.out.println(B);
//		B.sort();
		B = B.mmul(speye(C));
		for( int i = 0; i < B.size; i++ ) {
			if( i > 0 ) {
			assertTrue( B.colIdx[i] >= B.colIdx[i-1] );
			if( B.colIdx[i] == B.colIdx[i-1]) {
				assertTrue( B.rowIdx[i] >= B.rowIdx[i-1] );
			}
			}
			assertEquals(A.rowIdx[i], B.rowIdx[i]);
			assertEquals(A.colIdx[i], B.colIdx[i]);
			assertEquals(A.values[i], B.values[i]);
		}
		System.out.println("sorted B");
//		assertTrue( A.equals(B));
//		System.out.println("asserted equals");
//		System.out.println(B);
		B = null;
		SparseMatrixLil Bt = A.t(); // t() is also a copy
		System.out.println("transposed A");
//		System.out.println(Bt);
//		Bt.sort();
		Bt = Bt.mmul(speye(R));
		System.out.println("sorted Bt");
		for( int i = 0; i < Bt.size; i++ ) {
			if( i > 0 ) {
			assertTrue( Bt.colIdx[i] >= Bt.colIdx[i-1] );
			if( Bt.colIdx[i] == Bt.colIdx[i-1]) {
				assertTrue( Bt.rowIdx[i] >= Bt.rowIdx[i-1] );
			}
			}
		}
//		System.out.println(Bt);
		assertTrue( A.toDense().t().equals(Bt));
		System.out.println("checed equal to At");		
	}
	public void testSortBigMatrixFast() {
		SparseMatrixLil B = spzeros(2,2);
		
		SparseMatrixLil A = sprand(1000,10000);
		System.out.println("created A");
		B = A.add(0); // make copy
		System.out.println("created B");
//		System.out.println(B);
		B.sortFast();
		for( int i = 0; i < B.size; i++ ) {
			if( i > 0 ) {
			assertTrue( B.colIdx[i] >= B.colIdx[i-1] );
			if( B.colIdx[i] == B.colIdx[i-1]) {
				assertTrue( B.rowIdx[i] >= B.rowIdx[i-1] );
			}
			}
			assertEquals(A.rowIdx[i], B.rowIdx[i]);
			assertEquals(A.colIdx[i], B.colIdx[i]);
			assertEquals(A.values[i], B.values[i]);
		}
		System.out.println("sorted B");
//		assertTrue( A.equals(B));
//		System.out.println("asserted equals");
//		System.out.println(B);
		B = null;
		SparseMatrixLil Bt = A.t(); // t() is also a copy
		System.out.println("transposed A");
//		System.out.println(Bt);
		Timer timer = new Timer();
		Bt.sortFast();
		timer.printTimeCheckMilliseconds();
		System.out.println("sorted Bt");
		for( int i = 0; i < Bt.size; i++ ) {
			if( i > 0 ) {
			assertTrue( Bt.colIdx[i] >= Bt.colIdx[i-1] );
			if( Bt.colIdx[i] == Bt.colIdx[i-1]) {
				assertTrue( Bt.rowIdx[i] >= Bt.rowIdx[i-1] );
			}
			}
		}
//		System.out.println(Bt);
		assertTrue( A.toDense().t().equals(Bt));
		System.out.println("checed equal to At");
	}
	public void testSortBigMatrixInplace() {
		SparseMatrixLil B = spzeros(2,2);
		
		SparseMatrixLil A = sprand(1000,12000);
		System.out.println("size: " + A.getSize() );
		System.out.println("created A");
		B = A.add(0); // make copy
		System.out.println("created B");
//		System.out.println(B);
		B.sortInplace();
		for( int i = 0; i < B.size; i++ ) {
			if( i > 0 ) {
			assertTrue( B.colIdx[i] >= B.colIdx[i-1] );
			if( B.colIdx[i] == B.colIdx[i-1]) {
				assertTrue( B.rowIdx[i] >= B.rowIdx[i-1] );
			}
			}
			assertEquals(A.rowIdx[i], B.rowIdx[i]);
			assertEquals(A.colIdx[i], B.colIdx[i]);
			assertEquals(A.values[i], B.values[i]);
		}
		System.out.println("sorted B");
//		assertTrue( A.equals(B));
//		System.out.println("asserted equals");
//		System.out.println(B);
		B = null;
		SparseMatrixLil Bt = A.t(); // t() is also a copy
		System.out.println("transposed A");
//		System.out.println(Bt);
		Timer timer = new Timer();
		Bt.sortInplace();
		timer.printTimeCheckMilliseconds();
		System.out.println("sorted Bt");
		for( int i = 0; i < Bt.size; i++ ) {
			if( i > 0 ) {
			assertTrue( Bt.colIdx[i] >= Bt.colIdx[i-1] );
			if( Bt.colIdx[i] == Bt.colIdx[i-1]) {
				assertTrue( Bt.rowIdx[i] >= Bt.rowIdx[i-1] );
			}
			}
		}
//		System.out.println(Bt);
		assertTrue( A.toDense().t().equals(Bt));
		System.out.println("checed equal to At");
	}
	public void testToCCS(){
		SparseMatrixLil A = sprand(10,10);
		SparseMatrixLil B = A.toCCS().toLil();
		assertTrue(A.equals(B));
		A = sprand(1000,1000);
		B = A.toCCS().toLil();
		assertTrue(A.equals(B));
		A = A.t();
		B = A.toCCS().toLil();
		assertTrue(A.equals(B));
	}
	public void testCCsGetNonZeros(){
		SparseMatrixLil A = spzeros(3,3);
		A.append(1, 0, 5);
		A.append(0,1,7);
		A.append(0,0,11);
		SparseMatrixCCS B = A.toCCS();
		assertEquals(3,B.nonZeros());
		assertEquals(2,B.nonZeros(0));
		assertEquals(1,B.nonZeros(1));
		assertEquals(0,B.nonZeros(2));
	}
	public void testMultiply() {
		DenseMatrix a = rand(2782,128);
		DenseMatrix b = rand(4000,128);
		tic();
		DenseMatrix c = a.mmul(b.t());
		toc();
		c = a.mmul(b.t());
		toc();
		c = a.mmul(b.t());
		toc();
	}
	public void testMultiplySimple(){
		DenseMatrix b = rand(4000,128);
		DenseMatrix a, c;
		tic();
		c = b.mmul(eye(128)); toc();		
		c = b.mmul(eye(128)); toc();		
		c = b.mmul(eye(128)); toc();		
		a = rand(2782,128);
		c = a.mmul(eye(128)); toc();		
		c = a.mmul(eye(128)); toc();		
		c = a.mmul(eye(128)); toc();	
		b.t(); toc();
		b.t(); toc();
		b.t(); toc();
	}
	public void testLatencyDense() {
		DenseMatrix a = rand(2782,128);
		DenseMatrix b = rand(4000,128);
		tic();
		DenseMatrix c;
		c = a.dummy_mmul(b.t()); toc();
		c = a.dummy_mmul(b.t()); toc();
		c = a.dummy_mmul(b.t()); toc();
	}
	public void testLatency2() {
		DenseMatrix a,b;
		a = rand(100,100);
		b = rand(100,100);
		tic(); a.mmul(b); toc();		
		tic(); a.mmul(b); toc();		
		tic(); a.dummy_mmul(b); toc();		
		tic(); a.dummy_mmul(b); toc();		

		a = rand(2000,2000);
		b = rand(2000,2000);
		tic(); a.mmul(b); toc();		
		tic(); a.mmul(b); toc();		
		tic(); a.dummy_mmul(b); toc();		
		tic(); a.dummy_mmul(b); toc();		
	}
	public void testSparseLatency() {
		SparseMatrixLil a,b;
		a = sprand(100,100);
		b = sprand(100,100);
		tic(); a.mmul(b); toc();		
		tic(); a.mmul(b); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();
		
		a = sprand(500,500);
		b = sprand(500,500);
		tic(); a.mmul(b); toc();		
		tic(); a.mmul(b); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();		
		
		a = sprand(1000,1000);
		b = sprand(1000,1000);
		tic(); a.mmul(b); toc();		
		tic(); a.mmul(b); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();		
		tic(); a.dummy_mmul(b,b.cols); toc();		
	}
}
