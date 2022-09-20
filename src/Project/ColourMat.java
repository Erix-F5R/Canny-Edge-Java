package Project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class ColourMat {
	
	int[] pixels;
	Matrix mat;
	int width;
	int height;
	BufferedImage image;
	double weak = 50.0;
	double strong = 255.0;
	
	public ColourMat(String img) throws IOException {
        
		//Reads in a file
		InputStream res =
                Files.newInputStream(Paths.get(img));
		
        image = ImageIO.read(res);
        
        width = image.getWidth();
        height = image.getHeight();
        
	    // read image buffer
        //image is read as a 1D array
	     pixels = image.getRGB(0, 0, width, height, pixels, 0, width);
	     double[] tempPixels = new double[pixels.length];
	     
	     //Binary operator to take the hexadecimal color to an RGB value 
	     for(int i = 0; i< pixels.length; i++) {
	    	
	    	 tempPixels[i] = pixels[i] &255;
	     }
	     
	     mat = new Matrix(tempPixels, height, width );

	}
	
	public void setPixel(int x, int y, int rgb) {

	    mat.setVal(x, y, rgb);
	     
	}
		
	
	//Save image converts the 0-255 back to hex
	//Then it saves the image to the project directory
	public void saveImg(String fileName) throws IOException {
		
		//
		for(int i = 0; i < height;i++)
			for(int j = 0; j< width; j++) {
				
				int rgb = (int) mat.getVal(j, i);
				int col = (255 << 24) | (rgb << 16) | (rgb << 8) | rgb;
				pixels[i*width + j] = col;
				
			}
		
		 image.setRGB(0, 0, width, height, pixels, 0, width);
	     File outputfile = new File(fileName);
	     ImageIO.write(image, "png", outputfile);
	
	
	
	}
	
	//Step 1.
	//This blurs the image 
	public void gaussianBlur() {
		
		
		double[] gaussianArray = {
				
				 0.002589, 0.0107788, 0.0241466, 0.0107788, 0.002589,
				 
				 0.0107788, 0.0448755, 0.10053, 0.0448755, 0.0107788,
				 
				 0.0241466, 0.10053, 0.225206, 0.10053, 0.0241466,
					
				 0.0107788, 0.0448755, 0.10053, 0.0448755, 0.0107788,
				
				 0.002589, 0.0107788, 0.0241466, 0.0107788, 0.002589
			};
		Matrix gaussianKernal = new Matrix(gaussianArray,5,5);
		
		mat = mat.convolution(gaussianKernal);
		
		
	}
	
	//Step 2.
	//This build two matrix, a gradient intensity, and theta a direction matrix
	public Matrix sobelFilter() {
		double [] Gx = {-1, 0, 1, -2, 0, 2,-1, 0, 1};
		double [] Gy = {1, 2, 1, 0, 0, 0, -1, -2, -1};
		
		Matrix Sx = new Matrix(Gx, 3, 3);
		Matrix Sy = new Matrix(Gy, 3, 3);
		
		Sx = mat.convolution(Sx);
		Sy = mat.convolution(Sy);
		
		Matrix theta = new Matrix(height, width);
		
		for(int i = 0; i < height;i++)
			for(int j = 0; j< width; j++) {
				
				mat.setVal(j, i,Math.hypot(Sx.getVal(j, i), Sy.getVal(j, i))  );
				
				//theta processing
				double syTemp = Sy.getVal(j, i);
				double sxTemp = Sx.getVal(j, i);
				double rawTheta;
				
				//Avoid divide by zero				
				if(sxTemp == 0.0) {
					if(syTemp == 0.0)
						rawTheta = 0.0;
					else {
					 rawTheta = 90.0;
					}
				}
				else {
					rawTheta = Math.toDegrees( Math.atan2(syTemp,sxTemp)); 
				}
				
				//no angles < 0
				if(rawTheta < 0.0)
					rawTheta += 180;
				
				theta.setVal(j, i, rawTheta);
			}
	
		return theta;
	}
	
	//Step 3.
	//Looks along the angle and compares gradient values
	public void nonMaxSuppresion(Matrix theta) {
		
		Matrix nms = new Matrix(height, width);
		
		for(int i = 1; i < height-1;i++)
			for(int j = 1; j< width-1; j++) {
				
				double adjA;
				double adjB;
				double currentVal = mat.getVal(j, i);
				
				//0 horizontal
				if(theta.getVal(j, i) <= 22.5) {
					adjA = mat.getVal(j+1, i);
					adjB = mat.getVal(j-1, i);
					
				}
				//45 diagonal 3oclock
				else if(theta.getVal(j, i) <= 67.5){
					adjA = mat.getVal(j+1, i+1);
					adjB = mat.getVal(j-1, i-1);
					
				}
				//90 vertical
				else if(theta.getVal(j, i) <= 112.5){
					adjA = mat.getVal(j, i+1);
					adjB = mat.getVal(j, i-1);
					
				}
				//135 diagonal 9oclock
				else if(theta.getVal(j, i) <= 157.5){
					adjA = mat.getVal(j+1, i-1);
					adjB = mat.getVal(j-1, i+1);
					
				}
				//180 horizontal
				else {
					adjA = mat.getVal(j+1, i);
					adjB = mat.getVal(j-1, i);
				}
				
				if(currentVal >= adjA && currentVal >= adjB)
					nms.setVal(j, i, mat.getVal(j, i));
				else				
					nms.setVal(j, i, 0.0);
				
				
			}
		
		mat = nms;
	}
	
	//Step 4.
	public void doubleThreshold() {
		
		//The correct values for thresholding varies
		double lowThreshold = 255 * 0.1;
		double highThreshold = 255 * 0.5;
		
		for(int i = 0; i < height;i++)
			for(int j = 0; j< width; j++) {
				
				double val = mat.getVal(j, i);
				
				if(val < lowThreshold)
					val = 0.0;
				else if(val >= lowThreshold && val < highThreshold)
					val =weak;
				else if(val > highThreshold)
					val = strong;
				
				mat.setVal(j, i, val);
				
			}
	
	}
	
	//Step 5.
	//Revisit weak gradient
	public void hysteresis() {
		
		for(int i = 1; i < height-1;i++)
			for(int j = 1; j< width-1; j++) {
				
				if(mat.getVal(j, i) == weak) {
					//If the weak gradient touches a strong gradient then it is marked strong
					if( mat.getVal(j+1, i+1) == strong || mat.getVal(j+1, i) == strong || mat.getVal(j-1, i) == strong ||
							mat.getVal(j, i+1) == strong || mat.getVal(j, i-1) == strong || mat.getVal(j-1, i-1) == strong ||
							mat.getVal(j+1, i-1) == strong || mat.getVal(j-1, i+1) == strong )
						mat.setVal(j, i, strong);
					else
						mat.setVal(j, i, 0.0);
					
				}
				
				
			}
		
	}
	
}
