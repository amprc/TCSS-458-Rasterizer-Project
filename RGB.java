/***
    TCSS 458 Spring 2020
    Homework 3
    Alex Larsen
    
    Represents an RGB color value.
    Each component has a value that ranges
    from 0 to 255.
*/
public class RGB {
	public int r, g, b;
	
	public RGB() {
		r = 255;
		g = 255;
		b = 255;
	}
	
	public RGB(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
