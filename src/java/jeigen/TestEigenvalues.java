// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import static jeigen.TicToc.*;
import static jeigen.Shortcuts.*;
import junit.framework.TestCase;

/**
 * Unit tests
 */
public class TestEigenvalues extends TestCase {
    public void testEigenvaluesEigenExample() {
        // based on example at top-ish of http://eigen.tuxfamily.org/dox/classEigen_1_1EigenSolver.html#a4140972e2b45343d1ef1793c2824159c
		DenseMatrix A = new DenseMatrix(
"  0.68   -0.33   -0.27  -0.717  -0.687  0.0259;" +
" -0.211   0.536  0.0268   0.214  -0.198   0.678;" +
"  0.566  -0.444   0.904  -0.967   -0.74   0.225;" +
"  0.597   0.108   0.832  -0.514  -0.782  -0.408;" +
"  0.823 -0.0452   0.271  -0.726   0.998   0.275;" +
" -0.605   0.258   0.435   0.608  -0.563  0.0486");
        System.out.println( "A:" + A );
        DenseMatrix.EigenResult eigenResult = A.eig();
        System.out.println( "result values: " + eigenResult.values );
        System.out.println( "result vectors: " + eigenResult.vectors );
        DenseMatrixComplex expectedValues = new DenseMatrixComplex(
"(0.049,1.06);" +
" (0.049,-1.06);" +
"     (0.967,0);" +
"     (0.353,0);" +
" (0.618,0.129);" +
"(0.618,-0.129)"
);
        DenseMatrixComplex expectedVectors = new DenseMatrixComplex(
" (-0.292,-0.454)   (-0.292,0.454)      (-0.0607,0)       (-0.733,0)    (0.59,-0.122)     (0.59,0.122);" +
"  (0.134,-0.104)    (0.134,0.104)       (-0.799,0)        (0.136,0)    (0.335,0.368)   (0.335,-0.368);" +
"  (-0.422,-0.18)    (-0.422,0.18)        (0.192,0)       (0.0563,0)  (-0.335,-0.143)   (-0.335,0.143);" +
" (-0.589,0.0274) (-0.589,-0.0274)      (-0.0788,0)       (-0.627,0)   (0.322,-0.156)    (0.322,0.156);" +
"  (-0.248,0.132)  (-0.248,-0.132)        (0.401,0)        (0.218,0)  (-0.335,-0.076)   (-0.335,0.076);" +
"    (0.105,0.18)    (0.105,-0.18)       (-0.392,0)     (-0.00564,0)  (-0.0324,0.103) (-0.0324,-0.103)" 
);
        System.out.println("expected : " + expectedValues );
        System.out.println("expected : " + expectedVectors );
        double absvaluesdiff = expectedValues.sub( eigenResult.values ).abs().sum().sum().s();
        double absvectorsdiff = expectedVectors.sub( eigenResult.vectors ).abs().sum().sum().s();
        System.out.println( "absvaluediff " + absvaluesdiff );
        System.out.println( "absvectorsdiff " + absvectorsdiff );
        assertTrue( absvaluesdiff < 0.1 );
        assertTrue( absvectorsdiff < 10 );
    }
    // from http://lpsa.swarthmore.edu/MtrxVibe/EigMat/MatrixEigen.html
    public void testEigenvaluesLpsa() {
		DenseMatrix A = new DenseMatrix(
"     0   1;" +
"  -2  -3");
        System.out.println( A );
        DenseMatrix.EigenResult eigenResult = A.eig();
        System.out.println( "result values: " + eigenResult.values );
        System.out.println( "result vectors: " + eigenResult.vectors );
        DenseMatrixComplex expectedValues = new DenseMatrixComplex(
"(-1,0);" +
"(-2,0)"
);
        DenseMatrixComplex expectedVectors = new DenseMatrixComplex(
"0.7071  -0.4472;" +
 " -0.7071   0.8944;" 
);
        System.out.println("expected : " + expectedValues );
        System.out.println("expected : " + expectedVectors );
        double absvaluesdiff = expectedValues.sub( eigenResult.values ).abs().sum().sum().s();
        double absvectorsdiff = expectedVectors.sub( eigenResult.vectors ).abs().sum().sum().s();
        System.out.println( "absvaluediff " + absvaluesdiff );
        System.out.println( "absvectorsdiff " + absvectorsdiff );
        assertTrue( absvaluesdiff < 0.01 );
        assertTrue( absvectorsdiff < 0.01 );
    }
    public void testEigenvaluesCircul() { // from http://www.mathworks.com/help/matlab/ref/eig.html
		DenseMatrix A = new DenseMatrix(
"     1     2     3;" +
"     3     1     2;" +
"     2     3     1");
        System.out.println( A );
        DenseMatrix.EigenResult eigenResult = A.eig();
        System.out.println( eigenResult.values );
        System.out.println( eigenResult.vectors );
        // results differ from matlabs results.  Just another equivalent reuslt? or a bug?
    }
    public void testPseudoEigenvaluesComplexCase() {
        // based on example at bottom of http://eigen.tuxfamily.org/dox/classEigen_1_1EigenSolver.html#a4140972e2b45343d1ef1793c2824159c
		DenseMatrix A = new DenseMatrix(new double[][]{
{0.68  , -0.33  , -0.27 , -0.717,  -0.687 , 0.0259},
{ -0.211 ,  0.536 , 0.0268  , 0.214 , -0.198  , 0.678},
{  0.566,  -0.444 ,  0.904 , -0.967 ,  -0.74   ,0.225},
 { 0.597,   0.108  , 0.832 , -0.514 , -0.782  ,-0.408},
{  0.823, -0.0452 ,  0.271 , -0.726 ,  0.998 ,  0.275},
{ -0.605,   0.258 ,  0.435 ,  0.608 , -0.563,  0.0486}});
        DenseMatrix.PseudoEigenResult eigenResult = A.peig();
        System.out.println( eigenResult.values );
        System.out.println( eigenResult.vectors );
        DenseMatrix valuesCorrect = new DenseMatrix(new double[][]{
{ 0.049 ,  1.06  ,    0 ,     0 ,     0,      0},
{ -1.06,  0.049 ,     0 ,     0     , 0    ,  0},
{     0 ,     0,  0.967  ,    0    ,  0     , 0},
 {    0  ,    0    ,  0 , 0.353   ,   0     , 0},
 {    0   ,   0   ,   0  ,    0  ,0.618 , 0.129},
 {    0    ,  0  ,    0   ,   0 ,-0.129  ,0.618}});
        assertTrue( eigenResult.values.sub(valuesCorrect).abs().sum().sum().rows == 1 );
        assertTrue( eigenResult.values.sub(valuesCorrect).abs().sum().sum().cols == 1 );
        assertTrue( eigenResult.values.sub(valuesCorrect).abs().sum().sum().s() <= 0.1 );
        DenseMatrix vectorsCorrect = new DenseMatrix(new double[][]{
{  -0.571   ,-0.888  , -0.066   , -1.13,     17.2  ,  -3.54},
{   0.263   ,-0.204   ,-0.869    , 0.21 ,    9.73 ,    10.7},
 { -0.827  , -0.352,    0.209   ,0.0871  ,  -9.75    ,-4.17},
 {  -1.15 ,  0.0535 , -0.0857  , -0.971   ,  9.36   , -4.53},
 { -0.485   , 0.258  ,  0.436 ,   0.337  ,  -9.74  ,  -2.21},
 {  0.206  ,  0.353   ,-0.426, -0.00873 ,  -0.942 ,    2.98}});
        assertTrue( eigenResult.vectors.sub(vectorsCorrect).abs().sum().sum().rows == 1 );
        assertTrue( eigenResult.vectors.sub(vectorsCorrect).abs().sum().sum().cols == 1 );
        assertTrue( eigenResult.vectors.sub(vectorsCorrect).abs().sum().sum().s() <= 50 );
    }
}

