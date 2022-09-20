package Project;

import java.io.IOException;


class Driver {

    public static void main(String[] args) throws IOException {
       
    	//SPECIFY THE NAME OF THE IMAGE IN DIRECTORY HERE
	     ColourMat imageMatrix = new ColourMat("pos4.jpg");
	     
	     //The following images will be written to the project directory
	     imageMatrix.gaussianBlur();
	     imageMatrix.saveImg("1_GaussianBlur.png");
	     Matrix theta = imageMatrix.sobelFilter();
	     imageMatrix.saveImg("2_Gradient.png");
	     
	     imageMatrix.nonMaxSuppresion(theta);
	     imageMatrix.saveImg("3_NonMaxSup.png");
	    
	     imageMatrix.doubleThreshold();
	     imageMatrix.saveImg("4_DoubleThres.png");
	     
	     imageMatrix.hysteresis();
	     imageMatrix.saveImg("5_Hysteresis.png");
	     
	     
    }
}


