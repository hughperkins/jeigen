// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

#include <stdexcept>
using namespace std;

#include "jeigen.h"

#include "Eigen/Dense"
#include "Eigen/Sparse"
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
}
SparseMatrix<double> *getSparseMatrix_(int handle ) {
   return (SparseMatrix<double> *)(data[handle]);
}

extern "C" {
void init() {
   for( int i = 0; i < RESULTS_SIZE; i++ ) {
      data[i] = 0;
   }
}
int allocateSparseMatrix( int numEntries, int numRows, int numCols, int *rows, int *cols, double *values ) {
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
void getSparseMatrixStats( int handle, int* stats ) { // rows, cols, nonzero
   stats[0] = getSparseMatrix_(handle)->rows();
   stats[1] = getSparseMatrix_(handle)->cols();
   stats[2] = getSparseMatrix_(handle)->nonZeros();
}
int getSparseMatrixNumEntries( int handle ) {
   return getSparseMatrix_(handle)->nonZeros();
}
void getSparseMatrix( int handle, int *rows, int *cols, double *values ) {
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
void freeSparseMatrix( int handle ) {
   if( handle < 0 || handle >= RESULTS_SIZE ) {
      throw std::runtime_error("handle out of range");
   }
   delete (SparseMatrix<double> *)(data[handle] );
   data[handle] = 0;
}
// dummy operation to measure end to end latency
void dense_dummy_op1( int rows, int cols, double *afirst, double *aresult ) {
   Map<MatrixXd> first(afirst,rows,cols);
   Map<MatrixXd> result(aresult,rows,cols);
   //valuesToMatrix( rows, cols, afirst, &first );
  // MatrixXd result = first; // aliasing doesnt matter since we will copy this anyway
//   matrixToValues( rows, cols, &result, aresult );
}
// dummy operation to measure end to end latency
void dense_dummy_op2( int rows, int middle, int cols, double *afirst, double *asecond, double *aresult ) {
//   MatrixXd first(rows,middle);
 //  valuesToMatrix( rows, middle, afirst, &first );
  // MatrixXd second(middle,cols);
   //valuesToMatrix( middle, cols, asecond, &second );
   Map<MatrixXd>first(afirst,rows,middle);
   Map<MatrixXd>second(asecond,middle,cols);
   Map<MatrixXd>result(aresult,rows,cols);
   //MatrixXd result(rows,cols);
   //matrixToValues( rows, cols, &result, aresult );
}
void dense_multiply( int rows, int middle, int cols, double *afirst, double *asecond, double *aresult ) {
   //MatrixXd first(rows,middle);
   //valuesToMatrix( rows, middle, afirst, &first );
   //MatrixXd second(middle,cols);
   //valuesToMatrix( middle, cols, asecond, &second );
   Map<MatrixXd>first(afirst,rows,middle);
   Map<MatrixXd>second(asecond,middle,cols);
   Map<MatrixXd>result(aresult,rows,cols);
   result = first * second;
//   matrixToValues( rows, cols, &result, aresult );
}
int sparse_multiply( int rows, int middle, int cols,
   int onehandle, int twohandle ) {
   SparseMatrix<double> *presult = new SparseMatrix<double>(rows,cols);
   *presult = (*getSparseMatrix_(onehandle)) * (*getSparseMatrix_(twohandle));
   return storeData_(presult);
}
int sparse_dummy_op2( int rows, int middle, int cols,
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
//   *presult = (*getSparseMatrix_(onehandle)) * (*getSparseMatrix_(twohandle));
   return storeData_(presult);
}
void sparse_dense_multiply( int rows, int middle, int cols, int onehandle, double *asecond, double *aresult ) {
   Map<MatrixXd> second(asecond,middle,cols);
   //valuesToMatrix( middle, cols, asecond, &second );
   Map<MatrixXd> result(aresult,rows,cols);
   result = (*getSparseMatrix_(onehandle)) * second;
//   matrixToValues( rows, cols, &result, aresult );
}
void dense_sparse_multiply( int rows, int middle, int cols, double *afirst, int twohandle, double *aresult ) {
   Map<MatrixXd> first(afirst, rows,middle);
//   valuesToMatrix( rows, middle, afirst, &first );
   Map<MatrixXd> result(aresult,rows,cols);
   result =  first * (*getSparseMatrix_(twohandle));
   //matrixToValues( rows, cols, &result, aresult );
}
void ldlt_solve( int arows, int acols, int bcols, double *avalues, double *bvalues, double *xvalues ) {
   Map<MatrixXd> A(avalues,arows, acols);
   //valuesToMatrix( arows, acols, avalues, &A );
   Map<MatrixXd> b(bvalues, acols, bcols);
   //valuesToMatrix( acols, bcols, bvalues, &b );
   Map<MatrixXd> result(xvalues,acols,bcols);
   result = A.ldlt().solve(b);
   //matrixToValues( acols, bcols, &result, xvalues );   
}
void fullpivhouseholderqr_solve( int arows, int acols, int bcols, double *avalues, double *bvalues, double *xvalues ) {
   Map<MatrixXd> A(avalues, arows, acols);
   //valuesToMatrix( arows, acols, avalues, &A );
   Map<MatrixXd> b(bvalues, acols, bcols);
   //valuesToMatrix( acols, bcols, bvalues, &b );
   Map<MatrixXd> result(xvalues,acols,bcols);
   result = A.fullPivHouseholderQr().solve(b);
   //matrixToValues( acols, bcols, &result, xvalues );   
}
void svd_dense( int n, int p, double *in, double *u, double *s, double *v ) {
   int m = min( n,p);
   Map<MatrixXd> In(in, n, p );
   //valuesToMatrix(n,p, in, &In );
   JacobiSVD<MatrixXd,HouseholderQRPreconditioner> svd(In, ComputeThinU | ComputeThinV);
   matrixToValues( n, m, &(svd.matrixU()), u );
   for( int i = 0; i < m; i++ ) {
      s[i] = svd.singularValues()(i);
   }
   matrixToValues( p, m, &(svd.matrixV()), v );
}
}

