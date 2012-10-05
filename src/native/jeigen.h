// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// License: GNU GPL v3 (or any later version), see LICENSE.txt for details.

#pragma once

// This wrapper presents a 'C' interface using only primitive types and 1d primitive arrays, 
// which is then easy to link with from jna

extern "C" {
   void init();
   void dense_multiply( int rows, int middle, int cols, double *first, double *second, double *result );
   void sparse_dense_multiply( int rows, int middle, int cols, int oneHandle, double *second, double *result );
   void dense_sparse_multiply( int rows, int middle, int cols, double *first, int twoHandle, double *result );
   int sparse_multiply( int rows, int middle, int cols, int oneHandle, int twoHandle );

   void ldlt_solve( int arow, int acols, int bcols, double *avalues, double *bvalues, double *xvalues );
   void fullpivhouseholderqr_solve( int arow, int acols, int bcols, double *avalues, double *bvalues, double *xvalues );

   int allocateSparseMatrix( int numEntries, int numRows, int numCols, int *rows, int *cols, double *values );
   void getSparseMatrixStats( int handle, int* stats ); // rows, cols, nonzero
   void getSparseMatrix( int handle, int *rows, int *cols, double *values );
   void freeSparseMatrix( int handle );

   // does thin svd, returning u,s,v
   void svd_dense( int numrows, int numcols, double *in, double *u, double *s, double *v );
}

