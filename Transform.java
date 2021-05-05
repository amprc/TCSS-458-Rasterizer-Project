/***
    TCSS 458 Spring 2020
    Homework 3
    Alex Larsen
    
    A Transform instance represents the state of all transform matrices used
    to draw objects. The actual transformation matrix code is in the Matrix class.
    This class just organizes all the matrices together.
*/
public class Transform {
	 /**
	  * Represents the model matrix transformations (scale, translate, and rotate)
	  */
	private Matrix model;
	
	/**
	 * Represents the view matrix transformations (lookAt)
	 */
	private Matrix view;
	
	/**
	 * Represents the projection matrix transformations (ortho and frustum)
	 */
	private Matrix proj;
	
	/**
	 * Represents the "interact" transformations aka the keyboard input
	 */
	private Matrix interact;
	
	/**
	 * Cached matrix that is equal to interact * model
	 */
	private Matrix modelInteract;
	
	/**
	 * Cached matrix that is equal to proj * view * interact * model
	 */
	private Matrix modelInteractViewProj;
	
	public Transform() {
		model = new Matrix();
		view = new Matrix();
		proj = new Matrix();
		interact = new Matrix();
		
		modelInteract = new Matrix();
		modelInteractViewProj = new Matrix();
	}
	
	public Matrix getModelMatrix() {
		return model;
	}
	
	public Matrix getViewMatrix() {
		return view;
	}
	
	public Matrix getProjMatrix() {
		return proj;
	}
	
	public Matrix getInteractMatrix() {
		return interact;
	}
	
	/**
	 * Updates the modelInteract matrix's value
	 */
	private void updateModelInteract() {
		modelInteract = interact.mul(model);
		updateModelInteractViewProj();
	}
	
	/**
	 * Updates the modelInteractViewPorj matrix's value
	 */
	private void updateModelInteractViewProj() {
		modelInteractViewProj = proj.mul(view.mul(interact.mul(model)));
	}
	
	/**
	 * Sets the interact matrix to an arbitrary rotation on the x and y axis.
	 * 
	 * @param viewRotX The rotation on the x-axis in degrees
	 * @param viewRotY The rotation on the y-axis in degrees
	 */
	public void setInteractiveRotation(double viewRotX, double viewRotY) {
		interact = new Matrix();
		interact = Matrix.rotateX(viewRotX).mul(Matrix.rotateY(viewRotY));
		updateModelInteract();
	}
	
	public void scale(double x, double y, double z) {
		model = Matrix.scale(x, y, z).mul(model);
		updateModelInteract();
	}
	
	public void rotateX(double angle) {
		model = Matrix.rotateX(angle).mul(model);
		updateModelInteract();
	}
	
	public void rotateY(double angle) {
		model = Matrix.rotateY(angle).mul(model);
		updateModelInteract();
	}
	
	public void rotateZ(double angle) {
		model = Matrix.rotateZ(angle).mul(model);
		updateModelInteract();
	}
	
	public void translate(double x, double y, double z) {
		model = Matrix.translate(x, y, z).mul(model);
		updateModelInteract();
	}
	
	/**
	 * Loads the identity matrix in the model matrix, which
	 * effectively resets all scales, rotations, and translations
	 * previously performed. This does not change the other matrices.
	 */
	public void loadIdentity() {
		model = new Matrix();
		updateModelInteract();
	}
	
	public void ortho(double left, double right, double bottom, double top, double near, double far) {
		proj = Matrix.ortho(left, right, bottom, top, near, far);
		updateModelInteractViewProj();
	}
	
	public void frustum(double left, double right, double bottom, double top, double near, double far) {
		proj = Matrix.frustum(left, right, bottom, top, near, far);
		updateModelInteractViewProj();
	}
	
	public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ) {
		view = Matrix.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		updateModelInteractViewProj();
	}
	
	/**
	 * Applies the modelInteract matrix to the vector.
	 * 
	 * @param vec the vector to be transformed
	 * @return the transformed vector
	 */
	public Vector applyModelInteract(Vector vec) {
		return vec.mul(modelInteract);
	}
	
	/**
	 * Applies the modelInteractViewProj matrix to the vector.
	 * Additionally, it will divide by w since the projection
	 * matrix will be applied.
	 * 
	 * @param vec the vector to be transformed
	 * @return the transformed vector
	 */
	public Vector applyModelInteractViewProj(Vector vec) {
		Vector v = vec.mul(modelInteractViewProj);
		
		v.x /= v.w;
		v.y /= v.w;
		v.z /= v.w;
		v.w = 1.0;
		
		v.y *= -1.0;
		
		return v;
	}
}
