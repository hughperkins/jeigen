Jeigen
======

Jeigen provides a wrapper around the high-performance C++ matrix library "Eigen".

Jeigen provides matrix multiplication, for dense-dense, sparse-dense, and 
sparse-sparse pairs of matrices, using Eigen, and other mathematical operators,
such as add, sub, sum, using native Java.

The matrix classes are :

    DenseMatrix // for dense matrices
    SparseMatrixLil // for sparse matrices

You can import statically Shortcuts.*, in order to have easy access to 
commands such as 'zeros', 'ones', 'eye' and 'diag'.

Example usage, to multiply two matrices:
========================================

    DenseMatrix A = new DenseMatrix("1 2; 3 5; 7 9"); // matrix with 3 rows and 2 columns with values
                                                      // {{1,2},{3,5},{7,9}}
    DenseMatrix B = new DenseMatrix(new double[][]{{4,3},{3,7}}); // matrix with 2 rows and 2 columns
    DenseMatrix C = A.mmul(B); // mmul is matrix multiplication
    System.out.println(C); // displays C formatted appropriately

How to build, linux
===================

Pre-requisites
--------------

- git
- jdk 1.6 or more recent
- ant
- cmake
- g++

Procedure
---------

1. git clone git://github.com/hughperkins/jeigen.git
2. cd jeigen
3. ant

According to whether you use a 64-bit jvm or a 32-bit jvm, the files will be created in 'build/linux-32' or 'build/linux-64'. Jeigen.jar will be created directly in this directory, and libjeigen.so 
will be created in the 'native' subdirectory.

How to build, Windows
=====================

Pre-requisites
--------------

- have installed git
- have a jdk available, at least 1.6
- have installed ant
- have installed cmake, at least version 2.8.11.2
- have installed Visual Studio C++ Express 2012

Procedure
---------

1. git clone git://github.com/hughperkins/jeigen.git
2. cd jeigen
3. set PATH=%PATH%;c:\apache-ant\bin
 * set to appropriate path for your ant installation
4. ant -Dcmake_home="c:\program files (x86)\Cmake" -Dgenerator="Visual Studio 11 Win64"
 * set to appropriate path for your cmake installation
 * if you're using Visual Studio 2010, please change generator name to "Visual Studio 10 Win64"
 * if you're using 32-bit Java JDK, please remove " Win64" from end of generator name

According to whether you use a 64-bit jvm or a 32-bit jvm, the files will be created in 'build\win-32' or 'build\win-64'. Jeigen.jar will be created directly in this directory, and jeigen.dll 
will be created in the 'native\Release' subdirectory.

How to link to Jeigen
=====================

In Eclipse, add a user library, and add the 'Jeigen.jar' jar to the 
library.  Then expand the library entry for 'jeigen', select 'Native
library location', then click 'Edit', and browse to the directory containing libjeigen.so or jeigen.dll.

(If you are not using Eclipse, then add:
   -Djava.library.path=/path/to/jeigen/build/native/directory
... to the java vm arguments)

You will also need the jna-4.0.0.jar file, which you can find in the 'thirdparty' directory.

Commands to create new matrices
===============================

    import static jeigen.Shortcuts.*;

    DenseMatrix dm1;
    DenseMatrix dm2;
    dm1 = new DenseMatrix( "1 2; 3 4" ); // create new matrix
                       // with rows {1,2} and {3,4}
    dm1 = new DenseMatrix( new double[][]{{1,2},{3,4}} ); // create new matrix
                       // with rows {1,2} and {3,4}
    dm1 = zeros(5,3);  // creates a dense matrix with 5 rows, and 3 columns
    dm1 = rand( 5,3); // create a 5*3 dense matrix filled with random numbers
    dm1 = ones(5,3);  // 5*3 matrix filled with '1's
    dm1 = diag(rand(5,1)); // creates a 5*5 diagonal matrix of random numbers
    dm1 = eye(5); // creates a 5*5 identity matrix

    SparseMatrixLil sm1;
    sm1 = spzeros(5,3); // creates an empty 5*3 sparse matrix
    sm1 = spdiag(rand(5,1)); // creates a sparse 5*5 diagonal matrix of random 
                             // numbers
    sm1 = speye(5); // creates a 5*5 identity matrix, sparse

Update matrices
===============

    dm1.set( 3,4,5.0); // sets element at row 3, column 4 to 5.0
    dm1.get( 3,4 ); // gets element at row 3, column 4
    
    sm1.append( 2, 3, 5.0 ); // adds value 5.0 at row 2, column 3

Matrix Operators
================

    dm1.mmul(dm1);  // matrix multiply, dense by dense
    dm1.mmul(sm1);  // matrix multiply, dense by sparse
    sm1.mmul(sm1); // matrix multiply, sparse by sparse
    sm1.mmul(dm1); // matrix multiply, sparse by dense

    dm1 = dm1.t(); // matrix transpose, dense
    sm1 = sm1.t(); // matrix transpose, sparse

Per-element operators:
======================

    dm1 = dm1.neg();  // element = - element
    dm1 = dm1.recpr();   // element = 1 / element 
    dm1 = dm1.ceil();   // element = Math.ceil( element )
    dm1 = dm1.floor();   // element = Math.floor( element )
    dm1 = dm1.round();   // element = Math.round( element )

    dm1 = dm1.add( dm2 );    // by-element addition
    dm1 = dm1.add( 3 );    // by-element addition, of 3
    dm1 = dm1.sub( 3 );    // by-element subtraction, of 3
    dm1 = dm1.mul( 3 );    // by-element multiplication, by 3
    dm1 = dm1.div( 3 );    // by-element division, by 3
    dm1 = dm1.sub( dm2 );    // by-element subtraction
    dm1 = dm1.mul( dm2 );    // by-element multiplication
    dm1 = dm1.div( dm2 );    // by-element division

    dm1 = dm1.le(dm2); // element1 <= element2
    dm1 = dm1.ge(dm2); // element1 >= element2
    dm1 = dm1.eq(dm2); // element1 == element2
    dm1 = dm1.ne(dm2); // element1 != element2
    dm1 = dm1.lt(dm2); // element1 &lt; element2
    dm1 = dm1.gt(dm2); // element1 &gt; element2

Aggregation operators
=====================

These work for both sparse and dense matrices.

    dm1 = dm1.sumOverRows(); // sum over all rows
    dm1 = dm1.sumOverCols(); // sum over all columns
    dm1 = dm1.sum(); // sum over rows, unless 1 row, in which case
                     // sum over columns
    dm1 = dm1.minOverRows();
    dm1 = dm1.maxOverRows();
    dm1 = dm1.minOverCols();
    dm1 = dm1.maxOverCols();

Scalar operators
================

Work for both dense and sparse.

    double value = dm1.s();  // returns dm1.get(0,0);

Slicing
=======

Slices are by-value.  They work for both dense and sparse matrices.

    dm1 = dm1.slice(startrow, endrowexclusive, startcol, endcolexclusive);
    dm1 = dm1.row(row);
    dm1 = dm1.col(col);
    dm1 = dm1.rows(startrow, endrowexclusive);
    dm1 = dm1.cols(startcol, endcolexclusive);

    dm1 = dm1.concatRight(dm2); // concatenate [ dm1 dm2 ]
    dm1 = dm1.concatDown(dm2); // concatenate [ dm1; dm2 ]

Operators in Shortcuts:
========================

    import static jeigen.Shortcuts.*;

    DenseMatrix dm1;
    dm1 = abs(dm1);  // element = abs(element)

Solvers
=======

    DenseMatrix dm1;
    DenseMatrix dm2;

    // to solve dm1.mmul(result) = dm2:
    DenseMatrix result = dm1.ldltSolve(dm2); // using ldlt, dm1 must be positive or
                                             // negative semi-definite; fast
    DenseMatrix result = dm1.fullPivHouseholderQRSolve(dm2); // no conditions on 
                                                         // dm1, but slower

Eigenvalues
===========

    // Added on Dec 2014, new, so let me know if any issues.
    // Since it might return complex results, created DenseMatrixComplex 
    // to handle this
    DenseMatrix dm1; // should be square
    EigenResult eig = dm1.eig();
    DenseMatrixComplex values = eig.values; // single column, with each value
                                            // per row
                                            // might be complex
    DenseMatrixComplex vectors = eig.vectors; // one column per vector, maybe
                                              // complex
    // if you want to avoid complexes, you can use the pseudo-eigenvector
    // decomposition
    // but the eigenvalues are now returned as a square matrix, which
    // might not be diagonal
    PseudoEigenResult peig = dm1.peig();
    DenseMatrix values = peig.values;
    DenseMatrix vectors = peig.vectors;

DenseMatrixComplex
==================

    // Created for use with eigenvalue decomposition, above.
    // DenseMatrixComplex basically contains two DenseMatrices, one for the 
    // real part, and one for the imaginary part
    DenseMatrixComplex dmc1 = new DenseMatrixComplex( numrows, numcols );
    // create from a string of complex pairs, each row separated by ';':
    DenseMatrixComplex dmc1 = new DenseMatrixComplex( "(1,-1) (2,0); (-1,3) (4,-1)" );
    DenseMatrix realPart = dmc.real();
    DenseMatrix imagPart = dmc.imag();
    double aRealValue = dmc1.getReal(2,3); // get real value at row 2 col 3
    double anImaginaryValue = dmc1.getImag(2,3); // get imaginary value
    dmc3 = dmc1.sub(dmc2) // can subtract
    dmc3 = dmc1.abs() // per-element abs, ie sqrt(real^2+imag^2), for each 
                      // element

Svd
===

    DenseMatrix dm1;
    SvdResult result = dm1.svd();  // uses Jacobi, and returns thin U and V
    // result contains U, S and V matrices

Unsupported
===========

(This is called 'unsupported', because these functions are from the 'unsupported'
Eigen modules)

    DenseMatrix dm1;
    DenseMatrix result1 = dm1.mexp(); // matrix exponential
    DenseMatrix result2 = dm1.mlog(); // matrix logarithm

Overhead of using java/jna?
===========================

Dense
-----

You can use the 'dummy_mmul' method of DenseMatrix to measure the overhead. 
It makes a call, with two matrices, right through to the native layer, doing
everything that would be done for a real multiplication, but not actually
calling the Eigen multiplication method:

    DenseMatrix a = rand(2000,2000);
    DenseMatrix b = rand(2000,2000);
    tic(); a.mmul(b); toc();	
    tic(); a.mmul(b); toc();	
    tic(); a.dummy_mmul(b); toc();	
    tic(); a.dummy_mmul(b); toc();	

Example results:

    Elapsed time: 13786 ms
    Elapsed time: 13588 ms
    Elapsed time: 408 ms
    Elapsed time: 407 ms

So, for 2000 by 2000 matrices, the overhead of using java/jna, instead of 
programming directly in C++, is about 408/13600*100 = 3%.

For N*N matrices, the empirical percent overhead is about:

    N = 10: 27%
    N = 100: 14%
    N = 1000: 4%

Sparse
------

For sparse matrices, a corresponding test method is:
cut
    SparseMatrixLil a,b;	
    a = sprand(1000,1000);
    b = sprand(1000,1000);
    tic(); a.mmul(b); toc();	
    tic(); a.mmul(b); toc();	
    tic(); a.dummy_mmul(b,b.cols); toc();	
    tic(); a.dummy_mmul(b,b.cols); toc();	

Approximate empirical overheads for multiplication of two sparse
N*N matrices are:

    N = 10: 77%
    N = 100: 35%
    N = 1000: 9%

Wrapping additional functions
=============================

If you want to add additional functions, here's the procedure:

1. Find the appropriate function in Eigen, and find out how to call it.
1. Add a method to jeigen.cpp and jeigen.h, that wraps this function
   - matrices arrives as arrays of double (double *)
   - you also need one or two integer parameters to describe the size of the array
   - result matrices also arrive as a parameter of type double *
1. In the jeigen.cpp method, use 'Map<MatrixXd> AnEigenMatrix(doublearray, rows, cols );' 
to convert the matrix represented by the double array 'doublearray' into an Eigen
matrix, here called 'AnEigenMatrix'.
   - do this for each of the incoming matrices, and for any results matrices
1. Then call the Eigen method, and ... that's it for the C++ part.  You don't need to do anything
with the result matrix, since it's already mapped to the function results parameter
1. Make sure this compiles ok
1. Add a method to JeigenJna.java, with the exact same name and parameter names as
the method you just added to jeigen.cpp/.h
   - any double arrays ('double *') from jeigen.cpp/.h need to be written as 'double[]' here, since
this bit is java
1. Add a method to DenseMatrix.cpp, with an appropriate, concise name (see names above for 
examples, and/or check with me), which:
   - creates any result DenseMatrices, using 'new DenseMatrix(desiredrows,desiredcols)';
   - calls the JeigenJna method you created just now, using '.values' on each matrix, to obtain
     the underlying array of doubles
   - note that after passing the result matrix as a parameter, you don't need to do any additional
     work to get the result of the call into this matrix
1. And that's it...  Check it compiles, ideally write a test case in TestJeigen.java, or 
TestUnsupported.java

Note that some methods might be better implemented in native Java, because of the 
time required to copy data across to the C++ side.  Basically, if a method 
is asymptotically O(n^3), where n is the size of the matrix, then implementing
it in C++/Eigen will be 
faster.  If it's O(n^2), then implementing it in native Java might be better.  For example:
- applying the same operation to all values of a matrix is implemented in native Java
- multiplying two matrices is implemented using wrapped C++/Eigen

Third-party libraries used
==========================

- The build process uses cmake-for-ant, https://github.com/hughperkins/cmake-for-ant .
- Unit tests use junit 4.
- And of course Eigen :-)  http://eigen.tuxfamily.org

License
=======

Jeigen is available under MPL v2 license, http://mozilla.org/MPL/2.0/

