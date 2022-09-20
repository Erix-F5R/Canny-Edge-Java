package Project;

//Matrix is a utility class to enhance the 2D array with some methods
//Such as multiply, convolution, subMatrix
public class Matrix {
	
	double [][] mat2D;
	int height;
	int width;

	//Our image comes in a 1D array, so this constructor reads and places in back into a 2D matrix
	public Matrix(double[] arr1D, int h,int w) {
		
		height =h;
		width = w;
		mat2D = new double[h][w];
		
		for(int i = 0; i < h;i++)
			for(int j = 0; j< w; j++)
				mat2D[i][j] = arr1D[i*w + j];
		
		
	}
	
	//More general matrix for the kernels and angle data
	public Matrix(int h, int w) {
		
		height =h;
		width = w;
		mat2D = new double[h][w];
	}
	
	//toString to facilitate development
	public String toString() {
		String temp = "";
		
		for(int i = 0; i < height;i++) {
			for(int j = 0; j< width; j++)
				temp += mat2D[i][j]+ " ";
			temp += "\n";
		}
	
		
		return temp;
	}
	
	
	public double getVal(int x, int y) {
		
		return mat2D[y][x];
		
	}
	
	public void setVal(int x, int y, double val) {
		
		mat2D[y][x] = val;
		
	}
	
	
	//The convolution stays away from the edges to avoid Array out of bounds errors
	public Matrix convolution(Matrix kernal) {
		
		Matrix tempMat = new Matrix(height,width);
		
		for(int i = (kernal.height)/2; i < height-(kernal.height/2);i++) 
			for(int j = (kernal.height)/2; j< width-(kernal.height/2); j++) {
				int newVal = 0;
				newVal = subMatrix(kernal.height,j,i).multiply(kernal);
				tempMat.setVal(j, i, newVal);
			}
				
		return tempMat;
	}
	
	//Assumes that they are the same size
	//Multiplies corresponding elements then sums
	public int multiply(Matrix otherMatrix) {
		int temp = 0;
		
		for(int i = 0; i < height;i++) 
			for(int j = 0; j< width; j++)
				temp += mat2D[i][j] * otherMatrix.mat2D[i][j];
		
		return temp;
	}
	
	//Produces a square submatrix at location (x,y)
	public Matrix subMatrix(int size, int x, int y) {
		
		Matrix sub = new Matrix(size,size); 
		
		for(int i = 0; i < size;i++) 
			for(int j = 0; j < size; j++)
				sub.setVal(i, j, mat2D[y-(size/2)+j][x-(size/2)+i]);
		
		return sub;
	}
}
