/***
 * TCSS 458 Spring 2020
*  Homework 3
*  Alex Larsen
*   
* Represents a 4x4 matrix.
*/
public class Matrix {
	public double value[][];
	
	public Matrix() {
		value = new double[4][];
		
		for (int i = 0; i < 4; i++) {
			value[i] = new double[4];
			for (int j = 0; j < 4; j++)
				value[i][j] = 0.0;
		}
		
		for (int i = 0; i < 4; i++) {
			value[i][i] = 1.0;
		}
	}
	
	public Matrix mul(Matrix rhs) {
		Matrix matrix = new Matrix();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				matrix.value[j][i] = value[0][i] * rhs.value[j][0] + value[1][i] * rhs.value[j][1] + value[2][i] * rhs.value[j][2] + value[3][i] * rhs.value[j][3];
			}
		}
		
		return matrix;
	}
	
	public String toString() {
		String str = "";
		for (int i = 0; i < 4; i++) {
			str += value[0][i] + ", " + value[1][i] + ", " + value[2][i] + ", " + value[3][i];
			if (i < 3)
				str += "\n";
		}
		return str;
	}
	
	/***
     * Creates a scale matrix with a scalar factor along the x, y, and z axis.
     * 
     * @param x the scale factor along the x axis
     * @param y the scale factor along the y axis
     * @param z the scale factor along the z axis
     * @return a matrix representing the scale transformation
     */
	public static Matrix scale(double x, double y, double z) {
		Matrix matrix = new Matrix();
		matrix.value[0][0] = x;
		matrix.value[1][1] = y;
		matrix.value[2][2] = z;
		return matrix;
	}
	
	/***
	 * Creates a rotation matrix that rotations on the x axis by a given angle.
	 * 
	 * @param angle the angle in which to rotate
	 * @return a matrix representing the rotation transformation
	 */
	public static Matrix rotateX(double angle) {
		angle = Math.toRadians(angle);
		Matrix matrix = new Matrix();
		
		matrix.value[0][0] = 1.0;
		matrix.value[1][1] = Math.cos(angle);
		matrix.value[2][1] = -Math.sin(angle);
		matrix.value[1][2] = Math.sin(angle);
		matrix.value[2][2] = Math.cos(angle);
		
		return matrix;
	}
	
	/***
	 * Creates a rotation matrix that rotations on the y axis by a given angle.
	 * 
	 * @param angle the angle in which to rotate
	 * @return a matrix representing the rotation transformation
	 */
	public static Matrix rotateY(double angle) {
		angle = Math.toRadians(angle);
		Matrix matrix = new Matrix();
		matrix.value[0][0] = Math.cos(angle);
		matrix.value[2][0] = Math.sin(angle);
		matrix.value[0][2] = -Math.sin(angle);
		matrix.value[2][2] = Math.cos(angle);
		return matrix;
	}

	/***
	 * Creates a rotation matrix that rotations on the z axis by a given angle.
	 * 
	 * @param angle the angle in which to rotate
	 * @return a matrix representing the rotation transformation
	 */
	public static Matrix rotateZ(double angle) {
		angle = Math.toRadians(angle);
		Matrix matrix = new Matrix();
		matrix.value[0][0] = Math.cos(angle);
		matrix.value[1][0] = -Math.sin(angle);
		matrix.value[0][1] = Math.sin(angle);
		matrix.value[1][1] = Math.cos(angle);
		return matrix;
	}
	
	/***
	 * Creates a translation matrix.
	 * 
	 * @param x the amount to translate along the x axis
	 * @param y the amount to translate along the y axis
	 * @param z the amount to translate along the z axis
	 * @return a matrix representing the translation transformation
	 */
	public static Matrix translate(double x, double y, double z) {
		Matrix matrix = new Matrix();
		matrix.value[3][0] = x;
		matrix.value[3][1] = y;
		matrix.value[3][2] = z;
		return matrix;
	}
	
	public static Matrix ortho(double left, double right, double bottom, double top, double near, double far) {
		Matrix matrix = new Matrix();
		matrix.value[0][0] = 2.0 / (right - left);
		matrix.value[1][1] = 2.0 / (top - bottom);
		matrix.value[2][2] = -2.0 / (far - near);
		matrix.value[3][0] = -(right + left) / (right - left);
		matrix.value[3][1] = -(top + bottom) / (top - bottom);
		matrix.value[3][2] = -(far + near) / (far - near);
		return matrix;
	}
	
	public static Matrix frustum(double left, double right, double bottom, double top, double near, double far) {
		Matrix matrix = new Matrix();
		matrix.value[0][0] = 2.0 * near / (right - left);
		matrix.value[1][1] = 2.0 * near / (top - bottom);
		matrix.value[2][2] = -(far + near) / (far - near);
		matrix.value[3][0] = -near * (right + left) / (right - left);
		matrix.value[3][1] = -near * (top + bottom) / (top - bottom);
		matrix.value[3][2] = 2.0 * far * near / (near - far);
		matrix.value[2][3] = -1.0;
		matrix.value[3][3] = 0.0;
		return matrix;
	}
	
	
	public static Matrix lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ) {
		Vector eye = new Vector(eyeX, eyeY, eyeZ);
		Vector center = new Vector(centerX, centerY, centerZ);
		Vector up = new Vector(upX, upY, upZ);
		
		Vector n = eye.sub(center).normalize();
		Vector u = up.cross(n).normalize();
		Vector v = n.cross(u).normalize();
		
		double tx = -u.dot(eye);
		double ty = -v.dot(eye);
		double tz = -n.dot(eye);
		
		Matrix matrix = new Matrix();
		
		matrix.value[0][0] = u.x; matrix.value[1][0] = u.y; matrix.value[2][0] = u.z; matrix.value[3][0] = tx;
		matrix.value[0][1] = v.x; matrix.value[1][1] = v.y; matrix.value[2][1] = v.z; matrix.value[3][1] = ty;
		matrix.value[0][2] = n.x; matrix.value[1][2] = n.y; matrix.value[2][2] = n.z; matrix.value[3][2] = tz;
		
		return matrix;
	}
}
