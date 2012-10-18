// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import static jeigen.Shortcuts.*;
import static jeigen.TicToc.*;
import junit.framework.TestCase;

/**Contains methods to measure perf.
 * Expected usage: in eclipse, put cursor on method name, and press 'f11'.
 * You might need to add java heap space, eg -Xmx1400m.
 */
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
	public void testSortBigMatrixFast() { // you will need to add option -Xmx1400m to run this
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
	public void testSortBigMatrixInplace() { // you will need to add option -Xmx1400m to run this
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
    public void testJaggedPerf() {
		final int N = 200;
		double[][] jagged = new double[N][N];
		double[][] jagged2 = new double[N][N];
		for( int j = 0; j < N; j++ ) {
			double[] r1 = jagged[j];
			for( int i = 0; i < N; i++ ) {
				r1[i] = 123;
			}
		}
		for( int j = 0; j < N; j++ ) {
			double[] r1 = jagged2[j];
			for( int i = 0; i < N; i++ ) {
				r1[i] = 123;
			}
		}
		tic();
		double[] values1 = new double[N*N];
		double[] values2 = new double[N*N];
		int i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged[c];
			for( int r = 0; r < N;r ++ ) {
				values1[i] = col[c];
				i++;
			}
		}
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged2[c];
			for( int r = 0; r < N;r ++ ) {
				values2[i] = col[c];
				i++;
			}
		}
		double[] result = new double[N*N];
		JeigenJna.Jeigen.dense_multiply(N, N, N, values1, values2, result);
		values1 = null;
		values2 = null;
		double[][] resultjagged = new double[N][N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = resultjagged[c];
			for( int r = 0; r < N;r ++ ) {
				col[r] = result[i];
				i++;
			}
		}
		toc();
		resultjagged = null;
		result = null;
		System.gc();
		System.gc();
		tic();
		values1 = new double[N*N];
		values2 = new double[N*N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged[c];
			for( int r = 0; r < N;r ++ ) {
				values1[i] = col[c];
				i++;
			}
		}
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged2[c];
			for( int r = 0; r < N;r ++ ) {
				values2[i] = col[c];
				i++;
			}
		}
		result = new double[N*N];
		JeigenJna.Jeigen.dense_multiply(N, N, N, values1, values2, result);
		values1 = null;
		values2 = null;
		resultjagged = new double[N][N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = resultjagged[c];
			for( int r = 0; r < N;r ++ ) {
				col[r] = result[i];
				i++;
			}
		}
		toc();
    }
    public void testJaggedPerfDummy() {
		final int N = 2000;
		double[][] jagged = new double[N][N];
		double[][] jagged2 = new double[N][N];
		for( int j = 0; j < N; j++ ) {
			double[] r1 = jagged[j];
			for( int i = 0; i < N; i++ ) {
				r1[i] = 123;
			}
		}
		for( int j = 0; j < N; j++ ) {
			double[] r1 = jagged2[j];
			for( int i = 0; i < N; i++ ) {
				r1[i] = 123;
			}
		}
		tic();
		double[] values1 = new double[N*N];
		double[] values2 = new double[N*N];
		int i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged[c];
			for( int r = 0; r < N;r ++ ) {
				values1[i] = col[c];
				i++;
			}
		}
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged2[c];
			for( int r = 0; r < N;r ++ ) {
				values2[i] = col[c];
				i++;
			}
		}
		double[] result = new double[N*N];
		JeigenJna.Jeigen.dense_dummy_op2(N, N, N, values1, values2, result);
		values1 = null;
		values2 = null;
		double[][] resultjagged = new double[N][N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = resultjagged[c];
			for( int r = 0; r < N;r ++ ) {
				col[r] = result[i];
				i++;
			}
		}
		toc();
		resultjagged = null;
		result = null;
		System.gc();
		System.gc();
		tic();
		values1 = new double[N*N];
		values2 = new double[N*N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged[c];
			for( int r = 0; r < N;r ++ ) {
				values1[i] = col[c];
				i++;
			}
		}
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = jagged2[c];
			for( int r = 0; r < N;r ++ ) {
				values2[i] = col[c];
				i++;
			}
		}
		result = new double[N*N];
		JeigenJna.Jeigen.dense_dummy_op2(N, N, N, values1, values2, result);
		values1 = null;
		values2 = null;
		resultjagged = new double[N][N];
		i = 0;
		for( int c = 0; c < N; c++ ) {
			double[] col = resultjagged[c];
			for( int r = 0; r < N;r ++ ) {
				col[r] = result[i];
				i++;
			}
		}
		toc();
    }
    public void testVectorPerf() {
		final int N = 200;
		double[] v1 = new double[N*N];
		double[] v2 = new double[N*N];
		int size = N * N;
		for( int i = 0; i < size; i++ ) {
			v1[i] = 123;
			v2[i] = 123;
		}
		tic();
		double[] result = new double[N*N];
		JeigenJna.Jeigen.dense_multiply(N, N, N, v1, v2, result);
		toc();
		result = new double[N*N];
		JeigenJna.Jeigen.dense_multiply(N, N, N, v1, v2, result);
		toc();
    }
    public void testVectorPerfDummy() {
		final int N = 2000;
		double[] v1 = new double[N*N];
		double[] v2 = new double[N*N];
		int size = N * N;
		for( int i = 0; i < size; i++ ) {
			v1[i] = 123;
			v2[i] = 123;
		}
		tic();
		double[] result = new double[N*N];
		JeigenJna.Jeigen.dense_dummy_op2(N, N, N, v1, v2, result);
		toc();
		result = new double[N*N];
		JeigenJna.Jeigen.dense_dummy_op2(N, N, N, v1, v2, result);
		toc();
    }
    public void testDenseSortPerf(){
    	DenseMatrix A = rand(2000,2000);
    	tic();
    	DenseMatrix B = A.sortRows( new DenseMatrix("3 5 1 2 4 8 9 10").t());
    	toc();
    	System.out.println(B.slice(0, 100, 0, 10));
    }
}

