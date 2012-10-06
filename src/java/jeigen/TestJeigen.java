// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

// unit tests

package jeigen;

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
}
