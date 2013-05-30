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
public class TestUnsupported extends TestCase {
    public void testExp() {
		DenseMatrix A = new DenseMatrix(new double[][]{{0,-Math.PI/4, 0},
				                                         {Math.PI/4,0,0},
				                                         {0,0,0}});
        DenseMatrix exp = A.mexp();
        DenseMatrix Ccorrect = new DenseMatrix(new double[][]{{0.707107,-0.707107,0},
				                                                {0.707107,0.707107,0},
				                                                {0,0,1}});
		assertTrue(exp.equals(Ccorrect));

    }
    public void testLog() {
		DenseMatrix A = new DenseMatrix(new double[][]{{Math.sqrt(2)/2,-Math.sqrt(2)/2,0},
				                                         {Math.sqrt(2)/2,Math.sqrt(2)/2,0},
				                                         {0,0,1}});
        DenseMatrix log = A.mlog();
        DenseMatrix Ccorrect = new DenseMatrix(new double[][]{{0,-Math.PI/4, 0},
				                                         {Math.PI/4,0,0},
				                                         {0,0,0}});
		assertTrue(log.equals(Ccorrect));

    }
}

