/***
*  TCSS 458 Spring 2020
*  Homework 3
*  Alex Larsen
*    
*  This class contains the code for rendering using
*  scan lines. Each draw call eventually turns into
*  a series of calls to TCSS458.drawPixel, using the
*  object passed to the constructor.
 */
public class ScanLineRenderer {
	/***
	 * This sub-class contains a "range" of x values representing
	 * a scan line as well as the z components of each side of
	 * the range.
	 */
	private class Line {
		int left, right;
		double leftZ, rightZ;
		
		public Line() {
			
		}
		
		public Line(int left, int right, double leftZ, double rightZ) {
			this.left = left;
			this.right = right;
			this.leftZ = leftZ;
			this.rightZ = rightZ;
		}
	};
	
	/***
	 * An array of Line references with a length
	 * equal to the number of lines (rows) on the
	 * final image.
	 */
	private Line scanLines[];
	
	int numLines;
	int lineWidth;
	private TCSS458Paint paint;
	private RGB rgb;
	private Transform transform;
	
	public ScanLineRenderer(TCSS458Paint paint) {
		this.paint = paint;
	}
	
	public void setRGB(RGB rgb) {
		this.rgb = rgb;
	}
	
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	/***
	 * Draws a triangle using the scan line algorithm.
	 * 
	 * @param v1 The first vertex of the triangle.
	 * @param v2 The second vertex of the triangle.
	 * @param v3 The third vertex of the triangle.
	 * @param shader The shader used to shade the triangle.
	 */
	public void drawTriangle(Vector v1, Vector v2, Vector v3) {
		v1 = transform.applyModelInteractViewProj(v1);
		v2 = transform.applyModelInteractViewProj(v2);
		v3 = transform.applyModelInteractViewProj(v3);
		
		drawLine(v1, v2);
		drawLine(v2, v3);
		drawLine(v3, v1);
		
		fillScanLines();
		
		reset();
	}
	
	/***
	 * Draws a line between two points "onto" the scanLines
	 * array. Used to create the outline of a primitive before
	 * it gets filled in. The primitive is only actually filled
	 * in once fillScanLines is called. This function is based
	 * around the DDA line drawing algorithm.
	 * 
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 */
    private void drawLine(Vector v1, Vector v2) {
    	//Calculate the x,y pixel coordinates
    	Point2D p1 = paint.worldCoordinatesToScreenCoordinates(v1);
    	Point2D p2 = paint.worldCoordinatesToScreenCoordinates(v2);
    	
    	
    	//Calculate differences in x and y coordinates, as well as the number of steps
    	int differenceX = p2.x - p1.x;
    	int differenceY = p2.y - p1.y;
    	int steps = Math.max(Math.abs(differenceX), Math.abs(differenceY));
    	
    	double deltaX = differenceX / (double)steps;
    	double deltaY = differenceY / (double)steps;
    	double deltaZ = (v2.z - v1.z) / (double)steps;
    	
    	double x = p1.x;
    	double y = p1.y;
    	double z = v1.z;
    	
    	for (int i = 0; i <= steps; i++) {
    		Point2D pixel = new Point2D((int)Math.round(x), (int)Math.round(y));
    		
    		//Allows for a pixel to be disregarded without the increment
    		//statements below not executing
    		boolean doNotDraw = false;
    		
    		if (pixel.y < 0 || pixel.y >= numLines)
    			doNotDraw = true;
    		
    		if (!doNotDraw) {
	    		if (scanLines[pixel.y] == null) {
	    			//Create a 1 pixel wide line with the current x and z value
	    			scanLines[pixel.y] = new Line(pixel.x, pixel.x, z, z);
	    		} else {
		    		Line line = scanLines[pixel.y];
		    		
		    		//If the x coordinate is beyond the left boundry of the existing
		    		//line, then extend the boundry to the left and update the leftZ component.
		    		if (pixel.x < line.left) {
		    			line.left = pixel.x;
		    			line.leftZ = z;
		    		}
		    		
		    		//If the x coordinate is beyond the right boundry of the existing
		    		//line, then extend the boundry to the right and update the rightZ component.
		    		if (pixel.x > line.right) {
		    			line.right = pixel.x;
		    			line.rightZ = z;
		    		}
	    		}
    		}
    		
    		x += deltaX;
    		y += deltaY;
    		z += deltaZ;
    	}
    }
    
    /***
     * This function "actually" draws the primitives. It
     * only draws horizontal lines on a range, however.
     * 
     * @param x1 The x-component of the first vertex.
     * @param z1 The z-component of the first vertex.
     * @param x2 The x-component of the second vertex.
     * @param z2 The z-component of the second vertex.
     * @param y The y-component of both the first and second vertices.
     */
	private void drawScanLine(int x1, double z1, int x2, double z2, int y)
	{
		double deltaZ = (z2 - z1) / (x2 - x1);
		double currentZ = z1;
		for (int x = x1; x <= x2; x++) {
			paint.drawPixel(x, y, currentZ, rgb);
			currentZ += deltaZ;
		}
	}
	
    /***
     * Fills primitives drawn "onto" the scanLines array
     * by calling drawScanLine.
     */
    private void fillScanLines() {
    	for (int y = 0; y < scanLines.length; y++) {
    		if (scanLines[y] != null) { //If a scan line exists on line y, draw it
	    		Line line = scanLines[y];
	    		drawScanLine(line.left, line.leftZ, line.right, line.rightZ, y);
    		}
    	}
    }
    
    /***
     * Resets the scanLines array. Called between each primitive
     * that is drawn.
     */
    private void reset() {
    	for (int i = 0; i < numLines; i++) {
    		scanLines[i] = null; //Indicates nothing has been drawn to this line
    	}
    }
    
    /***
     * Sets the dimensions for drawing. Must
     * be called when the "DIM" command is invoked.
     * 
     * @param width The width of the image.
     * @param height The height of the image.
     */
    public void dim(int width, int height) {
    	scanLines = new Line[height];
    	numLines = height;
    	lineWidth = width;
    	reset();
    }
    
}
