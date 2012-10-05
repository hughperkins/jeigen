// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// License: GNU GPL v3 (or any later version), see LICENSE.txt for details.

package jeigen;

import java.util.*;

// a dense matrix;  this is faster than SparseMatrixLil, for fully dense matrices
// SparseMatrixLil will be faster if much of the matrix is zeros
public class DenseMatrix {
	final int rows;
	final int cols;
	double[] values;
	public DenseMatrix(int rows, int cols ) {
		this.rows = rows;
		this.cols = cols;
		this.values = new double[rows * cols];
	}
	public double s() {
		return values[0];
	}
	public static DenseMatrix rand(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		Random random = new Random();
		int i = 0;
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows ; r++ ) {
				result.values[i] = random.nextDouble();
				i++;
			}
		}
		return result;		
	}
	public static DenseMatrix zeros(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		return result;
	}
	public static DenseMatrix ones(int rows, int cols ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = 1;
		}
		return result;
	}
	public static DenseMatrix eye(int size ) {
		DenseMatrix result = new DenseMatrix(size,size);
		for( int i = 0; i < size; i++ ) {
			result.values[size * i + i] = 1;
		}
		return result;
	}
	public static DenseMatrix diag( DenseMatrix v ) {
		if( v.cols != 1 ) {
			throw new RuntimeException("diag needs a matrix with one column exactly");
		}
		int size = v.rows;
		DenseMatrix result = new DenseMatrix(size,size);
		for( int i = 0; i < size; i++ ) {
			result.set(i,i,v.get(i, 0));
		}
		return result;
	}
	public DenseMatrix sum() {
		return sum(0);
	}
	public DenseMatrix sum( int axis ) {
		if( axis == 0 ) {
			DenseMatrix result = new DenseMatrix(1, cols );
			for( int c = 0; c < cols; c++ ) {
				int offset = c * rows;
				double sum = 0;
				for( int r = 0; r < rows; r++ ) {
					sum += values[offset + r];
				}
				result.set(0,c,sum);
			}
			return result;
		} else {
			DenseMatrix result = new DenseMatrix(rows, 1 );
			for( int r = 0; r < rows; r++ ) {
				double sum = 0;
				for( int c = 0; c < cols; c++ ) {
					sum += get(r,c);
				}
				result.set(r,0,sum);
			}			
			return result;
		}
	}
	public DenseMatrix t() { // this could be optimized a lot, by not actually transposing...
	    DenseMatrix result = new DenseMatrix(cols,rows );
	    for( int r = 0; r < rows; r++ ) {
		    for( int c = 0; c < cols; c++ ) {
		    	result.set(c, r, get(r,c));
		    }
	    }
	    return result;
	}
	public DenseMatrix(double[][] values ) {
		this.rows = values.length;
		this.cols = values[0].length;
		this.values = new double[rows * cols];
		int i = 0;
		for( int c = 0; c < cols; c++ ) {
			for( int r = 0; r < rows ; r++ ) {
				this.values[i] = values[r][c];
				i++;
			}
		}
	}
	public void set(int row, int col, double value ) {
		values[rows * col + row] = value;
	}
	public double get(int row, int col ) {
		return values[rows * col + row];
	}
	public DenseMatrix neg(){
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = - values[i];
		}
		return result;		
	}
	public DenseMatrix inv(){// note: per element inverse, ie 1/element
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = 1 / values[i];
		}
		return result;		
	}
	public DenseMatrix abs(){
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = Math.abs(values[i]);
		}
		return result;		
	}
	public DenseMatrix mul( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] * scalar;
		}
		return result;		
	}
	public DenseMatrix div( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] / scalar;
		}
		return result;		
	}
	public DenseMatrix add( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] + scalar;
		}
		return result;		
	}
	public DenseMatrix sub( double scalar ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] - scalar;
		}
		return result;		
	}
	public DenseMatrix mul(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] * second.values[i];
		}
		return result;		
	}
	public DenseMatrix div(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] / second.values[i];
		}
		return result;		
	}
	public DenseMatrix add(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] + second.values[i];
		}
		return result;		
	}
	public DenseMatrix sub(DenseMatrix second){
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int capacity = rows * cols;
		for( int i = 0; i < capacity; i++ ) {
			result.values[i] = values[i] - second.values[i];
		}
		return result;		
	}
	@Override
	public boolean equals( Object osecond ) {
		if( osecond == null ) {
			return false;
		}
		DenseMatrix second = (DenseMatrix)osecond;
		if( this.cols != second.cols || this.rows != second.rows ) {
			return false;
		}
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( Math.abs( values[i] - second.values[i] ) > 0.000001 ) {
				return false;
			}
		}
		return true;
	}
	public DenseMatrix eq( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] == s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix ne( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] != s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix le( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] <= s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix ge( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] >= s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix lt( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] < s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix gt( double s ) {
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] > s ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix eq( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] == second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix ne( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] != second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix le( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] <= second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix ge( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] >= second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix gt( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] > second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix lt( DenseMatrix second ) {
		if( this.cols != second.cols || this.rows != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape() );
		}
		DenseMatrix result = new DenseMatrix(rows,cols);
		int numElements = rows * cols;
		for( int i = 0; i < numElements; i++ ) {
			if( values[i] < second.values[i] ) {
				result.values[i] = 1;
			}
		}
		return result;
	}
	public DenseMatrix mmul( DenseMatrix second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.dense_multiply(this.rows, this.cols, second.cols, this.values, second.values, result.values );
		return result;
	}
	public DenseMatrix mmul( SparseMatrixLil second ) {
		if( this.cols != second.rows ) {
			throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
		}
		int twohandle = SparseMatrixLil.allocateSparseMatrix(second);
		DenseMatrix result = new DenseMatrix(this.rows, second.cols);
		JeigenJna.Jeigen.dense_sparse_multiply(rows, cols, second.cols,
				values, twohandle, result.values );
		JeigenJna.Jeigen.freeSparseMatrix(twohandle);
		return result;
	}
	public DenseMatrix shape() {
		return new DenseMatrix(new double[][]{{rows,cols}}); 
	}
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DenseMatrix, " + rows + " * " + cols + ":\n");
		stringBuilder.append("\n");
		for( int r = 0; r < rows; r++ ) {
			for( int c = 0; c < cols; c++ ) {
				stringBuilder.append(get(r,c));
				stringBuilder.append(" ");
			}
			stringBuilder.append("\n");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
	public DenseMatrix ldltSolve(DenseMatrix b ) {
		if( this.cols != b.rows ) {
			throw new RuntimeException("ldltsolve matrix size mismatch " + shape() + " vs " + b.shape());
		}
		DenseMatrix result = new DenseMatrix(this.cols, b.cols);
		JeigenJna.Jeigen.ldlt_solve(rows, cols, b.cols,
				values, b.values, result.values );
		return result;		
	}
	public DenseMatrix fullPivHouseholderQRSolve(DenseMatrix b ) {
		if( this.cols != b.rows ) {
			throw new RuntimeException("ldltsolve matrix size mismatch " + shape() + " vs " + b.shape());
		}
		DenseMatrix result = new DenseMatrix(this.cols, b.cols);
		JeigenJna.Jeigen.fullpivhouseholderqr_solve(rows, cols, b.cols,
				values, b.values, result.values );
		return result;		
	}
}
