/***
    TCSS 458 Spring 2020
    Homework 3
    Alex Larsen
    
    The main program object, containing the entry point.
*/

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import javax.swing.*;

import java.io.*;
import java.util.*;


public class TCSS458Paint extends JPanel implements KeyListener
{	
    static int width;
    static int height;
    int imageSize;
    int[] pixels; 
    boolean aaEnabled = true; //Anti-aliasing enable/disablee
    double[] zBuffer;
    Shader shader;
    private double viewRotX = 0.0, viewRotY = 0.0;
    Transform transform = new Transform();
    ScanLineRenderer scanLineRenderer = new ScanLineRenderer(this);
    MeshRenderer meshRenderer = new MeshRenderer(this);
    
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_LEFT)
			viewRotX -= 3;
		if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
			viewRotX += 3;
		if (evt.getKeyCode() == KeyEvent.VK_UP)
			viewRotY += 3;
		if (evt.getKeyCode() == KeyEvent.VK_DOWN)
			viewRotY -= 3;
		
		repaint();
	}

	public void keyReleased(KeyEvent evt) {
		
	}

	public void keyTyped(KeyEvent evt) {
		
	}
    
	/**
	 * Converts a vector in world space into 2-dimensional
	 * screen space coordinates. The z and w components are
	 * dropped.
	 * 
	 * @param vec The vector to be converted to screen space coordinates.
	 * @return The screen space coordinates corresponding to the vector's position.
	 */
    Point2D worldCoordinatesToScreenCoordinates(Vector vec) {
    	double pX = Math.round((width - 1) * (vec.x + 1.0) / 2.0);
		double pY = Math.round((height - 1) * (vec.y + 1.0) / 2.0);
		return new Point2D((int)pX, (int)pY);
    }
    
    /***
     * Draws a pixel to the image.
     * 
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     */
    void drawPixel(int x, int y, int r, int g, int b) {
        pixels[(height-y-1)*width*3+x*3] = r;
        pixels[(height-y-1)*width*3+x*3+1] = g;
        pixels[(height-y-1)*width*3+x*3+2] = b;                
    }
    
    RGB getPixel(int x, int y) {
    	return new RGB(pixels[(height-y-1)*width*3+x*3],
    			pixels[(height-y-1)*width*3+x*3+1],
    			pixels[(height-y-1)*width*3+x*3+2]);
    }
    
    /***
     * Draws a pixel, taking into account the z component.
     * Only pixels with a z value equal to or greater than
     * any previously drawn pixels at the given position
     * will be drawn.
     * 
     * @param x The x coordinate (screen space).
     * @param y The y coordinate (screen space).
     * @param z The z coordinate (world space).
     */
    public void drawPixel(int x, int y, double z, RGB rgb) {
    	if (x < 0 || x >= width)
    		return;
    	if (y < 0 || y >= height)
    		return;
    	
    	if (z >= zBuffer[(height - y - 1) * width + x])
    		return;
    	
    	zBuffer[(height - y - 1) * width + x] = z;
    	
    	if (rgb == null)
    		rgb = new RGB(255, 0, 255);
    	
    	drawPixel(x, y, rgb.r, rgb.g, rgb.b);
    }
    
    /***
     * Draws a line using the DDA line algorithm.
     * 
     * @param x1 starting x coordinate
     * @param y1 starting y coordinate
     * @param x2 ending x coordinate
     * @param y2 ending y coordinate
     * @param rgb the color of the line
     */
    void drawLine(Vector v1, Vector v2) {
    	v1 = transform.applyModelInteractViewProj(v1);
    	v2 = transform.applyModelInteractViewProj(v2);
    	
    	Point2D p1 = worldCoordinatesToScreenCoordinates(v1);
    	Point2D p2 = worldCoordinatesToScreenCoordinates(v2);
    	
    	int differenceX = p2.x - p1.x;
    	int differenceY = p2.y - p1.y;
    	double differenceZ = v2.z - v1.z;
    	int steps = Math.max(Math.abs(differenceX), Math.abs(differenceY));
    	
    	double deltaX = differenceX / (double)steps;
    	double deltaY = differenceY  / (double)steps;
    	double deltaZ = differenceZ / steps;
    	
    	double x = p1.x;
    	double y = p1.y;
    	double z = v1.z;
    	for (int i = 0; i <= steps; i++) {
    		drawPixel((int)Math.round(x), (int)Math.round(y), z, shader.getRGB());
    		x += deltaX;
    		y += deltaY;
    		z += deltaZ;
    	}
    }
    
    void clearPixels() {
    	if (pixels == null)
    		return;
    	
    	for (int i = 0; i < pixels.length; i++)
    		pixels[i] = 255;
    }
    
    void clearZBuffer() {
    	if (zBuffer == null)
    		return;
    	
    	for (int i = 0; i < zBuffer.length; i++)
    		zBuffer[i] = Double.POSITIVE_INFINITY;
    }
    
    void createImage() {
    	clearZBuffer();
    	clearPixels();
    	
    	transform = new Transform();
    	transform.setInteractiveRotation(viewRotX, viewRotY);
    	
    	shader = new Shader();
    	shader.setTransform(transform);
    	
    	scanLineRenderer.setTransform(transform);
    	
        Scanner input = getFile();
        while (input.hasNext()) {
            String command = input.next();
            if (command.equals("DIM")){
                width = input.nextInt();
                height = input.nextInt();
                if (aaEnabled) {
                	width *= 2;
                	height *= 2;
                }
                imageSize = width * height;
                pixels = new int[imageSize * 3];
                zBuffer = new double[width * height];
                clearPixels();
                clearZBuffer();
                scanLineRenderer.dim(width, height);
                
            } else if (command.equals("LOAD_IDENTITY_MATRIX")) {
            	transform.loadIdentity();
            	
            } else if (command.equals("SCALE")) {
            	double scaleFactorX = input.nextDouble();
            	double scaleFactorY = input.nextDouble();
            	double scaleFactorZ = input.nextDouble();
            	transform.scale(scaleFactorX, scaleFactorY, scaleFactorZ);
            	
            } else if (command.equals("ROTATEX")) {
            	double angle = input.nextDouble();
            	transform.rotateX(angle);
            	
            } else if (command.equals("ROTATEY")) {
            	double angle = input.nextDouble();
            	transform.rotateY(angle);
            	
            } else if (command.equals("ROTATEZ")) {
            	double angle = input.nextDouble();
            	transform.rotateZ(angle);
            	
            } else if (command.equals("TRANSLATE")) {
            	double x = input.nextDouble();
            	double y = input.nextDouble();
            	double z = input.nextDouble();
            	transform.translate(x, y, z);
            	
            } else if (command.equals("WIREFRAME_CUBE")) {
            	meshRenderer.drawWireFrameCube();
            	
            } else if (command.equals("SOLID_CUBE")) {
            	meshRenderer.drawSolidCube();
            	
            } else if (command.equals("LINE")){
            	 double x1 = input.nextDouble();
                 double y1 = input.nextDouble();
                 double z1 = input.nextDouble();
                 double x2 = input.nextDouble();
                 double y2 = input.nextDouble();
                 double z2 = input.nextDouble();
                 drawLine(
                		 new Vector(x1, y1, z1),
                		 new Vector(x2, y2, z2)
                		 );
                 
            } else if (command.equals("TRI")) {
                Vector v1 = new Vector(input.nextDouble(), input.nextDouble(), input.nextDouble());
                Vector v2 = new Vector(input.nextDouble(), input.nextDouble(), input.nextDouble());
                Vector v3 = new Vector(input.nextDouble(), input.nextDouble(), input.nextDouble());
                scanLineRenderer.setRGB(shader.shade(v1, v2, v3));
                scanLineRenderer.drawTriangle(v1, v2, v3);
                
            } else if (command.equals("RGB")) {
                double r = input.nextDouble();
                double g = input.nextDouble();
                double b = input.nextDouble();
                shader.setRGB(new RGB((int)Math.round(r * 255.0), (int)Math.round(g * 255.0), (int)Math.round(b * 255.0)));
                
            } else if (command.equals("ORTHO")) {
            	double left = input.nextDouble();
            	double right = input.nextDouble();
            	double bottom = input.nextDouble();
            	double top = input.nextDouble();
            	double near = input.nextDouble();
            	double far = input.nextDouble();
            	transform.ortho(left, right, bottom, top, near, far);
            	
            } else if (command.equals("FRUSTUM")) {
            	double left = input.nextDouble();
            	double right = input.nextDouble();
            	double bottom = input.nextDouble();
            	double top = input.nextDouble();
            	double near = input.nextDouble();
            	double far = input.nextDouble();
            	transform.frustum(left, right, bottom, top, near, far);
            	
            } else if (command.equals("LOOKAT")) {
            	double eyeX = input.nextDouble();
            	double eyeY = input.nextDouble();
            	double eyeZ = input.nextDouble();
            	double centerX = input.nextDouble();
            	double centerY = input.nextDouble();
            	double centerZ = input.nextDouble();
            	double upX = input.nextDouble();
            	double upY = input.nextDouble();
            	double upZ = input.nextDouble();
            	transform.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
            	
            } else if (command.equals("LIGHT_DIRECTION")) {
            	double x = input.nextDouble();
            	double y = input.nextDouble();
            	double z = input.nextDouble();
            	shader.setLightDirection(new Vector(x, y, z));
            }
        }
    }


    public void paintComponent(Graphics g) {
        createImage();
        if (aaEnabled) {
        	int[] antiAliasedPixels = new int[(width / 2) * (height / 2) * 3];
        	for (int x = 0; x < width / 2; x++) {
        		for (int y = 0; y < height / 2; y++) {
        			RGB pixels[] = new RGB[4];
        			pixels[0] = getPixel(x * 2 + 0, y * 2 + 0);
        			pixels[1] = getPixel(x * 2 + 1, y * 2 + 0);
        			pixels[2] = getPixel(x * 2 + 0, y * 2 + 1);
        			pixels[3] = getPixel(x * 2 + 1, y * 2 + 1);
        			
        			double avgR = Math.round((pixels[0].r + pixels[1].r + pixels[2].r + pixels[3].r) / 4.0);
        			double avgG = Math.round((pixels[0].g + pixels[1].g + pixels[2].g + pixels[3].g) / 4.0);
        			double avgB = Math.round((pixels[0].b + pixels[1].b + pixels[2].b + pixels[3].b) / 4.0);
        			
        			antiAliasedPixels[((height/2)-y-1)*(width/2)*3+x*3] = (int)avgR;
        			antiAliasedPixels[((height/2)-y-1)*(width/2)*3+x*3+1] = (int)avgG;
        			antiAliasedPixels[((height/2)-y-1)*(width/2)*3+x*3+2] = (int)avgB;
        		}
        	}
        	BufferedImage image = new BufferedImage(width / 2, height / 2, BufferedImage.TYPE_INT_RGB);
        	WritableRaster wr_raster = image.getRaster();
            wr_raster.setPixels(0, 0, width / 2, height / 2, antiAliasedPixels);        
            g.drawImage(image, 0, 0, null);
        }
        else {
        	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        	WritableRaster wr_raster = image.getRaster();
            wr_raster.setPixels(0, 0, width, height, pixels);        
            g.drawImage(image, 0, 0, null);
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("HW3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectFile();
        
        JPanel rootPane = new TCSS458Paint();
        frame.addKeyListener((KeyListener)rootPane);
        getDim(rootPane);
        rootPane.setPreferredSize(new Dimension(width,height));

        frame.getContentPane().add(rootPane);
        frame.pack();      
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );

    }

    static File selectedFile = null;

    static private void selectFile() {
        int approve; //return value from JFileChooser indicates if the user hit cancel

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
    
        approve = chooser.showOpenDialog(null);
        if (approve != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        } else {
            selectedFile = chooser.getSelectedFile();
        }
    }

    static private Scanner getFile() {
        Scanner input = null; 
        try {            
   	       input = new Scanner(selectedFile);
        } catch (Exception e) {
    	       JOptionPane.showMessageDialog(null, 
                "There was an error with the file you chose.", 
                "File Error", JOptionPane.ERROR_MESSAGE);	
    	   }
    	   return input;
    }

    static void getDim(JPanel rootPane) {
        Scanner input = getFile();
        
        String command = input.next();
        if (command.equals("DIM")){
            width = input.nextInt();
            height = input.nextInt();
            rootPane.setPreferredSize(new Dimension(width,height));
        }
    }


}
