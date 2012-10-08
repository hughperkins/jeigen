package jeigen;

import java.util.*;

/**
 * Used by SparseMatrixLilSorter
 */
class Leonardo {
	public static long[] generate(int N) {
		long a = 1;
		long b = 1;
		long[] values = new long[N];
		values[0] = a;
		values[1] = b;
		for( int i = 2; i < N; i++ ) {
			long thisvalue = a + b + 1;
//			System.out.println(thisvalue);
			values[i] = thisvalue;
			a = b;
			b = thisvalue;
		}
		return values;
	}
	public static void main(String[] args ) {
		System.out.println(Arrays.toString(generate(80)));
	}
}
