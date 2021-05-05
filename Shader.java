/***
    TCSS 458 Spring 2020
    Homework 3
    Alex Larsen
    
    Contains the shading code for polygons.
*/
public class Shader {
	/**
	 * The direction of the light source.
	 */
	private Vector lightDirection;
	
	/**
	 * The RGB color value use to draw before any shading is applied.
	 */
	private RGB rgb;
	
	/**
	 * Enables shading for triangles. Lines are never effected
	 * so this does nothing to how lines are drawn.
	 */
	private boolean enableShading;
	
	/**
	 * The current transform object used by the program.
	 * This is used to determine where the object is located
	 * before any view or projection matrix is applied.
	 */
	private Transform transform;
	
	public Shader() {
		enableShading = true;
		rgb = new RGB();
	}
	
	public void setShadingEnabled(boolean enabled) {
		enableShading = enabled;
	}
	
	public void setRGB(RGB rgb) {
		this.rgb = rgb;
	}
	
	public RGB getRGB() {
		return rgb;
	}
	
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	public void setLightDirection(Vector lightDirection) {
		this.lightDirection = lightDirection.normalize();
	}
	
	private Vector calculateNormal(Vector v1, Vector v2, Vector v3) {
		Vector u = v2.sub(v1);
		Vector v = v3.sub(v1);
		return (u.cross(v)).normalize();
	}
	
	private double calculateShadingFactor(Vector normal) {
		double factor = normal.dot(lightDirection);
		
		if (factor < 0.0)
			factor = 0.0;
		
		return factor;
	}
	
	/**
	 * Shades a triangle.
	 * 
	 * @param v1 the first vertex of the triangle
	 * @param v2 the second vertex of the triangle
	 * @param v3 the first vertex of the triangle
	 * @return the shaded color of the triangle
	 */
	public RGB shade(Vector v1, Vector v2, Vector v3) {
		if (!enableShading || lightDirection == null) {
			return rgb;
		}
		
		/*
		 * Apply the transforms to move the vertices into their final
		 * positions in space, prior to any view or projection matrix
		 * being applied.
		 */
		v1 = transform.applyModelInteract(v1);
		v2 = transform.applyModelInteract(v2);
		v3 = transform.applyModelInteract(v3);
		
		Vector normal = calculateNormal(v1, v2, v3);
		double shadingFactor = calculateShadingFactor(normal);
		
		double r = (0.5 * rgb.r) + (0.5 * shadingFactor * rgb.r);
		double g = (0.5 * rgb.g) + (0.5 * shadingFactor * rgb.g);
		double b = (0.5 * rgb.b) + (0.5 * shadingFactor * rgb.b);
		
		return new RGB(Math.min((int)Math.round(r), 255), Math.min((int)Math.round(g), 255), Math.min((int)Math.round(b), 255));
	}
}
