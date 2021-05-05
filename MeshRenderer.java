/***
 * TCSS 458 Spring 2020
 * Homework 3
 * Alex Larsen
 *   
 * This class contains the code for drawing the meshes (e.g. cubes).
  */
public class MeshRenderer {
	private TCSS458Paint paint;
	
	public MeshRenderer(TCSS458Paint paint) {
		this.paint = paint;
	}
	
	/***
	 * Draws a wire frame cube.
	 */
	 void drawWireFrameCube() {
	    	Vector v1 = new Vector(-0.5, -0.5, 0.5);
	    	Vector v2 = new Vector(0.5, -0.5, 0.5);
	    	Vector v3 = new Vector(0.5, 0.5, 0.5);
	    	Vector v4 = new Vector(-0.5, 0.5, 0.5);
	    	
	    	Vector v5 = new Vector(-0.5, -0.5, -0.5);
	    	Vector v6 = new Vector(0.5, -0.5, -0.5);
	    	Vector v7 = new Vector(0.5, 0.5, -0.5);
	    	Vector v8 = new Vector(-0.5, 0.5, -0.5);
	    	
	    	paint.drawLine(v1, v2);
	    	paint.drawLine(v2, v3);
	    	paint.drawLine(v3, v4);
	    	paint.drawLine(v4, v1);
	    	
	    	paint.drawLine(v5, v6);
	    	paint.drawLine(v6, v7);
	    	paint.drawLine(v7, v8);
	    	paint.drawLine(v8, v5);
	    	
	    	paint.drawLine(v1, v5);
	    	paint.drawLine(v2, v6);
	    	paint.drawLine(v3, v7);
	    	paint.drawLine(v4, v8);
	    }
	 
	 private void drawShadedTriangle(Vector v1, Vector v2, Vector v3) {
		paint.scanLineRenderer.setRGB(paint.shader.shade(v1, v2, v3));
	 	paint.scanLineRenderer.drawTriangle(v1, v2, v3);
	 }
	    
	 /***
	  * Draws a solid cube.
	  */
	   void drawSolidCube() {
	    	Vector v1 = new Vector(-0.5, -0.5, 0.5);
	    	Vector v2 = new Vector(0.5, -0.5, 0.5);
	    	Vector v3 = new Vector(0.5, 0.5, 0.5);
	    	Vector v4 = new Vector(-0.5, 0.5, 0.5);
	    	
	    	Vector v5 = new Vector(-0.5, -0.5, -0.5);
	    	Vector v6 = new Vector(0.5, -0.5, -0.5);
	    	Vector v7 = new Vector(0.5, 0.5, -0.5);
	    	Vector v8 = new Vector(-0.5, 0.5, -0.5);
	    	
	    	//front
	    	drawShadedTriangle(v3, v4, v1);
	    	drawShadedTriangle(v3, v1, v2);
	    	
	    	//back
	    	drawShadedTriangle(v8, v7, v6);
	    	drawShadedTriangle(v8, v6, v5);
	    	
	    	//left
	    	drawShadedTriangle(v4, v8, v5);
	    	drawShadedTriangle(v4, v5, v1);
	    	
	    	//right
	    	drawShadedTriangle(v7, v3, v2);
	    	drawShadedTriangle(v7, v2, v6);
	    	
	    	//top
	    	drawShadedTriangle(v3, v8, v4);
	    	drawShadedTriangle(v7, v8, v3);
	    	
	    	//bottom
	    	drawShadedTriangle(v5, v6, v1);
	    	drawShadedTriangle(v6, v2, v1);
	   }
}
