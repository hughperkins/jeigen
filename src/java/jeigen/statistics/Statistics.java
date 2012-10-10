package jeigen.statistics;

import jeigen.*;
import static jeigen.Shortcuts.*;

public final class Statistics {
	public static final DenseMatrix meanOverCols(SparseMatrixLil mat ) {
		return mat.sumOverCols().div(mat.cols);
	}
	public static final DenseMatrix meanOverRows(SparseMatrixLil mat ) {
		return mat.sumOverRows().div(mat.rows);
	}
	public static final DenseMatrix varOverCols(SparseMatrixLil mat ) {
		return mat.pow(2).sumOverCols().sub( mat.sumOverCols().pow(2).div(mat.cols) ).div(mat.cols - 1);
	}
	public static final DenseMatrix varOverRows(SparseMatrixLil mat ) {
		return mat.pow(2).sumOverRows().sub( mat.sumOverRows().pow(2).div(mat.rows) ).div(mat.rows - 1);
	}
}
