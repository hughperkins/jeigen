// Randomised SVD implementation
// Copyright (c) 2016 Illumina, Inc.
// Auther: Jared O'Connell <joconnell@illumina.com>
//
//This is an implementation of the algorithm described in Halko 2011:
//http://arxiv.org/pdf/0909.4061.pdf

#pragma once

#include "math.h"
#include <stdlib.h>    
#include "Eigen/Dense"
#include "Eigen/Eigenvalues"

template<typename MatrixType>    
class RandomSVD {
    typedef typename MatrixType::Scalar Scalar;
    typedef  Eigen::Matrix<Scalar, Eigen::Dynamic, 1> VectorType;    
    
public:
    //N:nsample L:nsnp e: desired number of PCs
    //mat is an N x L
    RandomSVD(const MatrixType & mat,int e,int q=3)	{
	int r = e;
	if(r>mat.rows())
	{
	    r=mat.rows();
	}
	if(q<1) 
	{
	    q=1;
	}

	MatrixType R;
	rnorm(R,mat.cols(),r);//L x e
	MatrixType Y  = mat * R;//N x e
	orthonormalize(Y);
	MatrixType Ystar;
	for(int i=0;i<q;i++)
	{
	    Ystar=mat.transpose() * Y; // L x e
	    orthonormalize(Ystar);
	    Y=mat * Ystar;
	    orthonormalize(Y);
	}	    
	MatrixType B = Y.transpose() * mat;//e x L
	Eigen::JacobiSVD<MatrixType > svd(B, Eigen::ComputeThinU | Eigen::ComputeThinV);
	_U = Y * svd.matrixU(); //N x e matrix 
	_S = svd.singularValues(); //diagonal e x e matrix
	_V = svd.matrixV(); //L x e matrix.
    }

    const  MatrixType & matrixU() {
	return _U;
    }

    const    VectorType & singularValues() {
	return _S;
    }

    const   MatrixType & matrixV() {
	return _V;
    }
    
private:
    MatrixType _U;
    VectorType _S;
    MatrixType _V;    

    inline void rnorm(MatrixType & X,int nrow, int ncol) {
	X.resize(nrow,ncol);
	float pi = 3.141592653589793238462643383279502884;
	float rmax = (float)RAND_MAX;
	for(int i=0;i<nrow;i++)
	{
	    for(int j=0;j<ncol;j+=2)
	    {
		float v = ((float)std::rand() + 1.) / (rmax+2.);
		float u = ((float)std::rand() + 2.) / (rmax+2.);
		float c = sqrt(-2. * log(v));
		X(i,j) = c * cos(2. * pi * u);
		if(j<ncol-1)
		{
		    X(i,j+1) = c * sin(2. * pi * u);
		}
	    }	
	}
    }

    inline void orthonormalize(MatrixType & mat) {
	MatrixType  thinQ;
	thinQ.setIdentity(mat.rows(), mat.cols());
	mat = mat.householderQr().householderQ()*thinQ;
    }
};


    
