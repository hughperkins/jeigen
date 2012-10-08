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

    import static jeigen.Shortcuts.*;
    
    DenseMatrix A = rand(3,3);
    DenseMatrix B = rand(3,3);
    DenseMatrix C = A.mmul(B); // mmul is matrix multiplication
    System.out.println(C); // displays C formatted appropriately

How to link to Jeigen
=====================

First, you need to obtain or build the native library. The native library
depends only on Eigen, included in the source code for Jeigen, so it is
very easy to build, just needing CMake, and a c++ compiler.

In Eclipse, add a user library, and add the 'jar/Jeigen.jar' jar to the 
library.  Then expand the library entry for 'jeigen', select 'Native
library location', then click 'Edit', and browse to the directory 
containing the native library.

(If you are not using Eclipse, then add:
   -Djava.library.path=/path/to/jeigen/native/<platform>/folder
... to the java vm arguments)

How to build the native library
===============================

On ubuntu linux:
- sudo apt-get install 'cmake'
- cd into the src/native directory
- mkdir build
- cd build
- cmake ..
- make
There should now be a library 'libjeigen.so' in the current directory.

On Windows:
- install Visual Studio, or your preferred C++ compiler
- install cmake
- load the CMakeLists.txt file from src/native using cmake
- press 'configure' then 'generate'
    - you will need to specify what C++ compiler you wish to use
    - I will assume Visual Studio here
- load the generated project file using Visual Studio and do 'Build'
- change build type to 'Release'
- this should create the jeigen.dll native library, in the
'Release' subdirectory

It is important to build as Release, because Eigen is significantly faster
when built as release, than when built as debug.

Commands to create new matrices
===============================

    import static jeigen.Shortcuts.*;

    DenseMatrix dm1;
    DenseMatrix dm2;
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
                                             // negative definite; fast
    DenseMatrix result = dm1.fullPivHouseholderQRSolve(dm2); // no conditions on 
                                                         // dm1, but slower

Svd
===

    DenseMatrix dm1;
    SvdResult result = dm1.svd();  // uses Jacobi, and returns thin U and V
    // result contains U, S and V matrices

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

License
=======

Jeigen is available under MPL v2 license, http://mozilla.org/MPL/2.0/

