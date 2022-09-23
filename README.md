# Canny Edge Detection Algorithm in Java

<img src='Animation.gif' style='width:100%' />

This Java application implements a 5-stage Canny edge detection algorithm. It includes custom librarys to store the matrices and preform matrix operations, such as mutliplication and convolutions.

The Canny detector is optimised for the following outcome:
1. Accurately detect all the edges that are present in the image, as well as minimise false edges produced by noise
2. The edge should be placed in the center of the true edge
3. Each edge is only present once in the final result

---
## Stages
---

### 1. Gaussian filter

The Gaussian filter reduces the noise in the image as much as possible without obscuring the edges. A 5x5 kernel convolutes over every pixel averaging them out with the surrounding pixels.



### 2. Gradient Intensity & Direction

A sobel operator(3x3) searches for clusters that show either a horizontal or vertical gradient. The higher(whiter) the retured pixel the stronger the gradient.

### 3. Non-maximum Suppression

Two pixels at a time are compared to find which is the strongest line. This stage thins the edge down to 1px.

### 4. Double Thresholding

Strong pixels are kept, weak pixels are discarded. Pixels that may represent an edge are passed to step 5.

### 5. Hystersis

If the ambigous pixel touches a strong pixel, it is kept. Otherwise it is assumed to be noise.
