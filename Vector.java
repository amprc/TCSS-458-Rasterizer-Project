/***
 * TCSS 458 Spring 2020
    Homework 3
    Alex Larsen
    
 * Represents a vector or point in 3-dimensional space.
 * Contains a w component for when needed.
 */
public class Vector {
	public double x, y, z, w;
	
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 1;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
	
	public Vector(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public String toString() {
		return "(" + x + "," + y + "," + z + "," + w + ")";
	}
	
	public Vector mul(Matrix rhs) {
		Vector vector = new Vector();
		vector.x = x * rhs.value[0][0] + y * rhs.value[1][0] + z * rhs.value[2][0] + w * rhs.value[3][0];
		vector.y = x * rhs.value[0][1] + y * rhs.value[1][1] + z * rhs.value[2][1] + w * rhs.value[3][1];
		vector.z = x * rhs.value[0][2] + y * rhs.value[1][2] + z * rhs.value[2][2] + w * rhs.value[3][2];
		vector.w = x * rhs.value[0][3] + y * rhs.value[1][3] + z * rhs.value[2][3] + w * rhs.value[3][3];
		return vector;
	}
	
	public Vector add(Vector rhs) {
		return new Vector(x + rhs.x, y + rhs.y, z + rhs.z);
	}
	
	public Vector sub(Vector rhs) {
		return new Vector(x - rhs.x, y - rhs.y, z - rhs.z);
	}
	
	public Vector mul(Vector rhs) {
		return new Vector(x * rhs.x, y * rhs.y, z * rhs.z);
	}
	
	public Vector div(Vector rhs) {
		return new Vector(x / rhs.x, y / rhs.y, z / rhs.z);
	}
	
	public Vector cross(Vector rhs) {
		double cX = y * rhs.z - z * rhs.y;
		double cY = z * rhs.x - x * rhs.z;
		double cZ = x * rhs.y - y * rhs.x;
		return new Vector(cX, cY, cZ, w * rhs.w);
	}
	
	public double dot(Vector rhs) {
		return x * rhs.x + y * rhs.y + z * rhs.z;
	}
	
	/***
	 * Returns the length of the vector. This
	 * does not take into account the value of
	 * w.
	 * 
	 * @return the length of the vector specified by the x, y, and z components
	 */
	public double length() {
		/**
		 * So far there has been no use for including w in the calculation,
		 * however including it result in a bad calculation since it would
		 * add a 1 to the sum of the squares before taking the square root.
		 */
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	/***
	 * Returns the vector divided by its length.
	 * 
	 * @return the vector divided by its length
	 */
	public Vector normalize() {
		double len = length();
		
		if (Math.abs(len) < 0.0000001)
			return new Vector(0.0, 0.0, 0.0, w);
		
		return new Vector(x / len, y / len, z / len, w);
	}
}
