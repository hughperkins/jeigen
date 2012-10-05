// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// License: GNU GPL v3 (or any later version), see LICENSE.txt for details.

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
		SparseMatrixLil A = SparseMatrixLil.rand(3, 3);
		System.out.println(A);
		SparseMatrixLil B = SparseMatrixLil.rand(3, 3);
		System.out.println(B);
		SparseMatrixLil C = A.mmul(B);
		System.out.println(C);
		DenseMatrix CDense = A.toDense().mmul(B.toDense());
		System.out.println(CDense);
		assertTrue(C.equals(CDense));
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
		A = rand(500,1000);
		Timer timer = new Timer();
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
}
