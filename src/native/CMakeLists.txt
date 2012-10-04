cmake_minimum_required(VERSION 2.8)

set(CMAKE_BUILD_TYPE "Release" CACHE STRING "Choose the type of build, options are: Debug Release RelWithDebInfo MinSizeRel." FORCE)

set(EIGEN_DIRECTORY /data/install/eigen CACHE STRING "Eigen directory.  The directory specified should have a subdirectory 'Eigen'.")
include_directories(${EIGEN_DIRECTORY})

add_library( jeigen SHARED jeigen.cpp )

