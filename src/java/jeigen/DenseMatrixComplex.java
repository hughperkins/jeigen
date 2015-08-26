package jeigen;

// This was created for use with getting eigenvalues
// but there's no reason why we couldn't generalize it with additional operations
// which similarly just wrap the underlying DenseMatrix operations
public class DenseMatrixComplex {
    DenseMatrix real;
    DenseMatrix imag;
    public DenseMatrixComplex( DenseMatrix real, DenseMatrix imag ) {
        if( real.rows != imag.rows || real.cols != imag.cols ) {
			throw new RuntimeException("matrix size mismatch: " + real.shape() + " vs " + imag.shape() );
        }
        this.real = new DenseMatrix( real );
        this.imag = new DenseMatrix( imag );
    }
    String normalizeBracketsFormatLine( String line ) {
        String normalizedLine = line.replace("  ", " ").replace("( ","(").replace(" )",")").replace(" ,",",").replace(", ",",").trim();
        while( !normalizedLine.equals(line ) ) {
            line = normalizedLine;
            normalizedLine = line.replace("  ", " ").replace("( ","(").replace(" )",")").replace(" ,",",").replace(", ",",").trim();
        }
        return normalizedLine;
    }
    public DenseMatrixComplex( String bracketsFormat ) {
        String[] lines = bracketsFormat.split(";");
        int rows = lines.length;
        String firstline = normalizeBracketsFormatLine( lines[0] );
        int cols = firstline.split(" ").length;
        int row = 0;
        this.real = new DenseMatrix( rows, cols );
        this.imag = new DenseMatrix( rows, cols );
        for( String line : lines ) {
            line = normalizeBracketsFormatLine( line );
            String[]splitLine = line.split(" ");
            for( int col = 0; col < cols; col++ ) {
                String thisValueString = splitLine[col];
                if( thisValueString.indexOf(")") >= 0 ) {
                    String realBit = thisValueString.split("\\(")[1].split(",")[0];
                    String imagBit = thisValueString.split(",")[1].split("\\)")[0];
                    double realValue = Double.parseDouble( realBit );
                    double imagValue = Double.parseDouble( imagBit );
                    real.set( row, col, realValue );
                    imag.set( row, col, imagValue );
                } else {
                    real.set( row, col, Double.parseDouble( thisValueString ) );
                }
            }
            row++;
        }
    }
    public DenseMatrixComplex( int rows, int cols ) {
        real = new DenseMatrix( rows, cols );
        imag = new DenseMatrix( rows, cols );
    }
    public double getReal( int row, int col ) {
        return real.get(row, col );
    }
    public double getImag( int row, int col ) {
        return imag.get(row, col );
    }
	public DenseMatrixComplex sub(DenseMatrixComplex second){
		if( real.cols != second.real.cols || this.real.rows != second.real.rows ) {
			throw new RuntimeException("matrix size mismatch: " + real.shape() + " vs " + second.real.shape() );
		}
        DenseMatrix realResult = this.real.sub( second.real );
        DenseMatrix imagResult = this.imag.sub( second.imag );
		DenseMatrixComplex result = new DenseMatrixComplex(realResult, imagResult);
		return result;
	}
    public DenseMatrix real() {
        return new DenseMatrix( real );
    }
    public DenseMatrix imag() {
        return new DenseMatrix( imag );
    }
    public DenseMatrix abs() {  // abs returns always reals, so can return a normal DenseMatrix?
        DenseMatrix result = new DenseMatrix( this.real.rows, this.real.cols );
        int numElements = this.real.rows * this.real.cols;
		for( int i = 0; i < numElements; i++ ) {
    		result.values[i] = Math.sqrt( real.values[i] * real.values[i] + imag.values[i] * imag.values[i] );
		}
        return result;
    }
    public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DenseMatrixComplex, " + real.rows + " * " + real.cols + ":\n");
		stringBuilder.append("\n");
        int rows = real.rows;
        int cols = real.cols;
		for( int r = 0; r < rows; r++ ) {
			for( int c = 0; c < cols; c++ ) {
				stringBuilder.append( "(" + real.get(r,c) + "," + imag.get(r,c) + ")");
				stringBuilder.append(" ");
			}
			stringBuilder.append("\n");
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
    }
}

