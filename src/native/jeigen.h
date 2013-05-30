// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

#pragma once

// This wrapper presents a 'C' interface using only primitive types and 1d primitive arrays, 
// which is then easy to link with from jna

extern "C" {

   void init();
   void dense_dummy_op1( int rows, int cols, double *one, double *result ); // just used for measuring the overhead of java/jna calls
   void dense_dummy_op2( int rows, int middle, int cols, double *one, double *two, double *result ); // just used for measuring the overhead of java/jna calls
   int sparse_dummy_multiply( int rows, int middle, int cols, int oneHandle, int twoHandle, int numResultElements );
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

   // from unsupported
   void jeigen_exp( int n, double *in, double *result );
   void jeigen_log( int n, double *in, double *result );

}

