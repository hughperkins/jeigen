// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

import java.util.*;

public class TicToc {
	static long startTime;

	public static final void tic() {
		startTime = System.nanoTime();		
	}
	public static final void toc() {
		long elapsednano = System.nanoTime() - startTime;
		int milliseconds = (int)(elapsednano / 1000 / 1000);
		//double seconds = milliseconds / 1000.0;
		System.out.println("Elapsed time: " + milliseconds + " ms" );
		startTime = System.nanoTime();
	}
	public static final void toc(String message) {
		long elapsednano = System.nanoTime() - startTime;
		int milliseconds = (int)(elapsednano / 1000 / 1000);
		//double seconds = milliseconds / 1000.0;
		System.out.println(message + ": " + milliseconds + " ms" );
		startTime = System.nanoTime();
	}
}
