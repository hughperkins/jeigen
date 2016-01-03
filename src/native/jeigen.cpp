// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

#include <stdexcept>
#include <iostream>
using namespace std;

#include "jeigen.h"

#include "Eigen/Dense"
#include "Eigen/Sparse"
#include "Eigen/Core"
#include "Eigen/Eigenvalues"
#include "unsupported/Eigen/MatrixFunctions"
using namespace Eigen;

/* use Map intead of this method
void valuesToMatrix( int rows, int cols, double *values, MatrixXd *pM ) {
   int i = 0;
   for( int c = 0; c < cols; c++ ) {
      for ( int r = 0; r < rows; r++ ) { 
         (*pM)(r,c) = values[i];
         i++;
      }
   }
}*/

void matrixToValues( int rows, int cols, const MatrixXd *pM, double *values ) {
   int i = 0;
   for( int c = 0; c < cols; c++ ) {
      for ( int r = 0; r < rows; r++ ) { 
         values[i] = (*pM)(r,c);
         i++;
      }
   }
}

const int RESULTS_SIZE = 1000;
void *data[RESULTS_SIZE];

int storeData_(void *m) {
   for( int i = 0; i < RESULTS_SIZE; i++ ) {
      if( data[i] == 0 ) {
         data[i] = m;
         return i;
      }
   }
   return 0;
}
SparseMatrix<double> *getSparseMatrix_(int handle ) {
   return (SparseMatrix<double> *)(data[handle]);
}

extern "C" {
DllExport void init() {
   for( int i = 0; i < RESULTS_SIZE; i++ ) {
      data[i] = 0;
   }
}
DllExport int allocateSparseMatrix( int numEntries, int numRows, int numCols, int *rows, int *cols, double *values ) {
   SparseMatrix<double> *pmat = new SparseMatrix<double>(numRows, numCols);
   pmat->reserve(numEntries);
   typedef Eigen::Triplet<double> T;
   std::vector<T> tripletList;
   for( int i = 0; i < numEntries; i++ ) {
      tripletList.push_back(T(rows[i],cols[i],values[i]));
   }
   pmat->setFromTriplets(tripletList.begin(), tripletList.end() );
   return storeData_(pmat);
}
DllExport void getSparseMatrixStats( int handle, int* stats ) { // rows, cols, nonzero
   stats[0] = getSparseMatrix_(handle)->rows();
   stats[1] = getSparseMatrix_(handle)->cols();
   stats[2] = getSparseMatrix_(handle)->nonZeros();
}
DllExport int getSparseMatrixNumEntries( int handle ) {
   return getSparseMatrix_(handle)->nonZeros();
}
DllExport void getSparseMatrix( int handle, int *rows, int *cols, double *values ) {
   SparseMatrix<double> *pmat = getSparseMatrix_(handle);
   int numEntries = pmat->nonZeros();
   int i = 0;
   for (int k=0; k<pmat->outerSize(); ++k) {
      for (SparseMatrix<double>::InnerIterator it(*pmat,k); it; ++it) {
        rows[i] = it.row();
        cols[i] = it.col();
        values[i] = it.value();
        i++;
      }
  }
}
DllExport void freeSparseMatrix( int handle ) {
   if( handle < 0 || handle >= RESULTS_SIZE ) {
      throw std::runtime_error("handle out of range");
   }
   delete (SparseMatrix<double> *)(data[handle] );
   data[handle] = 0;
}
// dummy operation to measure end to end latency
DllExport void dense_dummy_op1( int rows, int cols, double *afirst, double *aresult ) {
   Map<MatrixXd> first(afirst,rows,cols);
   Map<MatrixXd> result(aresult,rows,cols);
}
// dummy operation to measure end to end latency
DllExport void dense_dummy_op2( int rows, int middle, int cols, double *afirst, double *asecond, double *aresult ) {
   Map<MatrixXd>first(afirst,rows,middle);
   Map<MatrixXd>second(asecond,middle,cols);
   Map<MatrixXd>result(aresult,rows,cols);
}
DllExport void dense_multiply( int rows, int middle, int cols, double *afirst, double *asecond, double *aresult ) {
   Map<MatrixXd>first(afirst,rows,middle);
   Map<MatrixXd>second(asecond,middle,cols);
   Map<MatrixXd>result(aresult,rows,cols);
   result = first * second;
}
DllExport int sparse_multiply( int rows, int middle, int cols,
   int onehandle, int twohandle ) {
   SparseMatrix<double> *presult = new SparseMatrix<double>(rows,cols);
   *presult = (*getSparseMatrix_(onehandle)) * (*getSparseMatrix_(twohandle));
   return storeData_(presult);
}
DllExport int sparse_dummy_op2( int rows, int middle, int cols,
   int onehandle, int twohandle, int numResultColumns ) {
   SparseMatrix<double> *presult = new SparseMatrix<double>(rows,cols);
   presult->reserve(numResultColumns*rows);
   typedef Eigen::Triplet<double> T;
   std::vector<T> tripletList;
   for( int c = 0; c < numResultColumns; c++ ) {
      for( int r = 0; r < rows; r++ ) {
         tripletList.push_back(T(r,c,1));
      }
   }
   presult->setFromTriplets(tripletList.begin(), tripletList.end() );
   return storeData_(presult);
}
DllExport void sparse_dense_multiply( int rows, int middle, int cols, int onehandle, double *asecond, double *aresult ) {
   Map<MatrixXd> second(asecond,middle,cols);
   Map<MatrixXd> result(aresult,rows,cols);
   result = (*getSparseMatrix_(onehandle)) * second;
}
DllExport void dense_sparse_multiply( int rows, int middle, int cols, double *afirst, int twohandle, double *aresult ) {
   Map<MatrixXd> first(afirst, rows,middle);
   Map<MatrixXd> result(aresult,rows,cols);
   result =  first * (*getSparseMatrix_(twohandle));
}
DllExport void ldlt_solve( int arows, int acols, int bcols, double *avalues, double *bvalues, double *xvalues ) {
   Map<MatrixXd> A(avalues,arows, acols);
   Map<MatrixXd> b(bvalues, arows, bcols);
   Map<MatrixXd> result(xvalues, acols, bcols);
   result = A.ldlt().solve(b);
}
DllExport void fullpivhouseholderqr_solve( int arows, int acols, int bcols, double *avalues, double *bvalues, double *xvalues ) {
   Map<MatrixXd> A(avalues, arows, acols);
   Map<MatrixXd> b(bvalues, arows, bcols);
   Map<MatrixXd> result(xvalues, acols, bcols);
   result = A.fullPivHouseholderQr().solve(b);
}
DllExport void svd_dense( int n, int p, double *in, double *u, double *s, double *v ) {
   int m = min( n,p);
   Map<MatrixXd> In(in, n, p );
   JacobiSVD<MatrixXd,HouseholderQRPreconditioner> svd(In, ComputeThinU | ComputeThinV);
   matrixToValues( n, m, &(svd.matrixU()), u );
   for( int i = 0; i < m; i++ ) {
      s[i] = svd.singularValues()(i);
   }
   matrixToValues( p, m, &(svd.matrixV()), v );
}
DllExport void jeigen_eig( int n, double* in, double* values_real, double *values_imag, double* vectors_real, double *vectors_imag ) {
   Map<MatrixXd> In( in, n, n );
   EigenSolver<MatrixXd> eigenSolve( In );
   VectorXcd EigenValues = eigenSolve.eigenvalues();
   MatrixXcd EigenVectors = eigenSolve.eigenvectors();
   int i = 0;
   for ( int r = 0; r < n; r++ ) { 
      values_real[i] = EigenValues(r).real();
      values_imag[i] = EigenValues(r).imag();
      i++;
   }
   i = 0;
   for( int c = 0; c < n; c++ ) {
      for ( int r = 0; r < n; r++ ) { 
         vectors_real[i] = EigenVectors(r,c).real();
         vectors_imag[i] = EigenVectors(r,c).imag();
         i++;
      }
   }
}
DllExport void jeigen_peig( int n, double* in, double* eigenValues, double* eigenVectors ) {
   Map<MatrixXd> In( in, n, n );
   EigenSolver<MatrixXd> eigenSolve( In );
   MatrixXd EigenValues = eigenSolve.pseudoEigenvalueMatrix();
   MatrixXd EigenVectors = eigenSolve.pseudoEigenvectors();
   matrixToValues( n, n, &(EigenValues), eigenValues );
   matrixToValues( n, n, &(EigenVectors), eigenVectors );
}
DllExport void jeigen_exp( int n, double *in, double *result ) {
   Map<MatrixXd> In(in, n, n );
   Map<MatrixXd> Result(result,n,n);      
   Result = In.exp();
}
DllExport void jeigen_log(int n, double *in, double *result ) {
   Map<MatrixXd> In(in, n, n );
   Map<MatrixXd> Result(result,n,n);      
   Result = In.log();
}

} // extern "C"

