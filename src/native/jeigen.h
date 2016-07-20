// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

#pragma once

// This wrapper presents a 'C' interface using only primitive types and 1d primitive arrays, 
// which is then easy to link with from jna

#if defined _WIN32 || defined _WIN64
#define DllExport   __declspec( dllexport ) 
#else 
#define DllExport 
#endif

extern "C" {

    DllExport void init();
    DllExport void dense_dummy_op1( int rows, int cols, double *one, double *result ); // just used for measuring the overhead of java/jna calls
    DllExport void dense_dummy_op2( int rows, int middle, int cols, double *one, double *two, double *result ); // just used for measuring the overhead of java/jna calls
    DllExport int sparse_dummy_multiply( int rows, int middle, int cols, int oneHandle, int twoHandle, int numResultElements );
    DllExport void dense_multiply( int rows, int middle, int cols, double *first, double *second, double *result );
    DllExport void sparse_dense_multiply( int rows, int middle, int cols, int oneHandle, double *second, double *result );
    DllExport void dense_sparse_multiply( int rows, int middle, int cols, double *first, int twoHandle, double *result );
    DllExport int sparse_multiply( int rows, int middle, int cols, int oneHandle, int twoHandle );

    // use a rows*1 vector for the values real and imaginary parts
    // use a rows*rows matrix for the vectors real and imaginary parts
    DllExport void jeigen_eig( int rows, double* in, double* values_real, double *values_imag, double* vectors_real, double *vectors_imag );
    DllExport void jeigen_peig( int rows, double* in, double* eigenValues, double* eigenVectors );

    DllExport void ldlt_solve( int arow, int acols, int bcols, double *avalues, double *bvalues, double *xvalues );
    DllExport void fullpivhouseholderqr_solve( int arow, int acols, int bcols, double *avalues, double *bvalues, double *xvalues );

    DllExport int allocateSparseMatrix( int numEntries, int numRows, int numCols, int *rows, int *cols, double *values );
    DllExport void getSparseMatrixStats( int handle, int* stats ); // rows, cols, nonzero
    DllExport void getSparseMatrix( int handle, int *rows, int *cols, double *values );
    DllExport void freeSparseMatrix( int handle );

    // does thin svd, returning u,s,v
    DllExport void svd_dense( int numrows, int numcols, double *in, double *u, double *s, double *v );
    //does approximate randomised svd
    DllExport void svd_random( int numrows, int numcols, double *in, double *u, double *s, double *v,int npc,int q);
    
    // from unsupported
    DllExport void jeigen_exp( int n, double *in, double *result );
    DllExport void jeigen_log( int n, double *in, double *result );

}
