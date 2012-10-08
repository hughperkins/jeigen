package jeigen;

import static jeigen.MatrixUtil.*;
import static jeigen.TicToc.*;
import junit.framework.TestCase;

// contains methods to measure perf
// expected usage: in eclipse, put cursor on method name, and press 'f11'
public class TestJeigenPerf extends TestCase {
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
		SparseMatrixLil B;
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
