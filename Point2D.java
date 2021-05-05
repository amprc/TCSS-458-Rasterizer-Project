/***
 * TCSS 458 Spring 2020
 *  Homework 3
 *  Alex Larsen
 *   
 * Represents an integer location in 2-dimensional space.
 * This class is used to represent pixel coordinates.
 */
public class Point2D {
	public int x, y;
	
	public Point2D() {
		x = 0;
		y = 0;
	}
	
	public Point2D(Point2D point) {
		x = point.x;
		y = point.y;
	}
	
	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Point2D point) {
		x = point.x;
		y = point.y;
	}
	
	public boolean equals(Point2D other) {
		return x == other.x && y == other.y;
	}
	
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
