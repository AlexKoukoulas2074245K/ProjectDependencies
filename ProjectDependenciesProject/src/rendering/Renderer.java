package rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import components.Window;

public final class Renderer 
{	
	/**
	 * The main Graphics object which will
	 * be used for rendering
	 */
	private Graphics2D gfx;
	
	/**
	 * The buffered image (Buffer) where
	 * the content of the rendering will be rendered onto
	 */
	private BufferedImage image;
	
	/**
	 * Window handle necessary for some rendering methods
	 */
	private Window wndHandle;
	
	/**
	 * Flag of whether the renderer has been initialized correctly
	 */
	private boolean isInitialized;
	
	/**
	 * Current font size passed to init
	 */
	
	
	/**
	 * 
	 * @param fontSize fontSize for the renderer to be initialized with
	 * @param wndHandle window handle for the renderer
	 * 
	 * Initializes the Graphics and BufferedImage components of the Renderer
	 * and derives the font with the font size given as well as keeps
	 * a handle for the window.
	 */
	public void init(final float fontSize, final Window wndHandle)
	{
		this.wndHandle = wndHandle;
		
		image = new BufferedImage(wndHandle.getWidth(), wndHandle.getHeight(), BufferedImage.TYPE_INT_RGB);
		gfx = (Graphics2D) image.getGraphics();
		
		gfx.setFont(gfx.getFont().deriveFont(Font.PLAIN, fontSize));
		isInitialized = true;
	}
	
	/**
	 * Prepares the renderer for drawing -- call this method before rendering anything.
	 */
	public void beginFrame()
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		clearScreen();
	}
	
	/**
	 * 
	 * @param gfxCanvas the Graphics object to blit onto
	 * 
	 * Instructs the renderer to end the current frame, blitting
	 * the current Graphics2D object onto the given Graphics object
	 */
	public void endFrame(final Graphics gfxCanvas)
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		Graphics2D 
		g2 = (Graphics2D) gfxCanvas;
			
		g2.drawImage(image,
				0,
				0,
				wndHandle.getWidth(),
				wndHandle.getHeight(),
				null);
	
		g2.dispose();
	}
	
	/**
	 * 
	 * @param baseColor The base Color object to be used for drawing the rectangle 
	 * @param x x-coordinate of the rectangle
	 * @param y y-coordinate of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * 
	 * Fills the area dictated by the rectangle formed
	 * by the parameters passed to this method. The
	 * rectangle will be rendered gradually brighter towards
	 * the lower end.
	 */
	public void drawCustomRect(final Color baseColor,
							   final int x,
							   final int y,
							   final int width,
							   final int height)
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		
		setColor(baseColor);
		int colR, colG, colB;
		colR = baseColor.getRed();
		colB = baseColor.getBlue();
		colG = baseColor.getGreen();
		int goalR = 140 - colR;
		int goalG = 140 - colG;
		int goalB = 140 - colB;
		
		int counter = 0;
		boolean rPassed = goalR <= 0;
		boolean gPassed = goalG <= 0;
		boolean bPassed = goalB <= 0;
		
		for(int yy = y; yy < y + height; ++yy)
		{
			float perc = (float)counter / (float)(height);
			
			int finalR = rPassed ? baseColor.getRed() : colR + (int)(perc * goalR);
			int finalG = gPassed ? baseColor.getGreen() : colG + (int)(perc * goalG);
			int finalB = bPassed ? baseColor.getBlue() : colB + (int)(perc * goalB);
			
			setColor(new Color(finalR, finalG, finalB));		
			drawLine(x, yy, x + width, yy);
			counter++;
		}
	}
	
	/**
	 * @see {@link http://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html#fillRect(int,%20int,%20int,%20int)}
	 */
	public void drawDefaultRect(final int x,
								final int y,
								final int width,
								final int height)
	{
		gfx.fillRect(x, y, width, height);
	}
	
	/**
	 * 
	 * @param str the String to be rendered 
	 * @param fontSize desired font size
 	 * @param x-coordinate of the bottom-left point where the String will be rendered
	 * @param y-coordinate of the bottom-left point where the String will be rendered
	 * 
	 * Renders a string with the constant given size
	 */
	public void drawConstString(final String str, final int fontSize, final int x, final int y)
	{
		Font oldFont = gfx.getFont();
		gfx.setFont(gfx.getFont().deriveFont(Font.PLAIN, fontSize));
		gfx.drawString(str, x, y);
		gfx.setFont(oldFont);
	}
	
	/**
	 * 
	 * @param str the String to be rendered 
 	 * @param x-coordinate of the bottom-left point where the String will be rendered
	 * @param y-coordinate of the bottom-left point where the String will be rendered 
	 * 
	 * Renders a string which can be scaled up and down
	 */
	public void drawVarString(final String str, final int x, final int y)
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");			
		gfx.drawString(str, x, y);
	}
	
	/**
	 * 
	 * @param image to be drawn
	 * @param x x-coordinate of the top-left point where the image will be drawn
	 * @param y y-coordinate of the top-left point where the image will be drawn
	 * @param width the width at which the image should be rendered
	 * @param height the height at which the image should be rendered
	 */
	public void drawImage(final BufferedImage image,
						  final int x,
						  final int y,
						  final int width,
						  final int height)
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		gfx.drawImage(image, x, y, width, height, null);
	}
	
	/**
	 * 
	 * @param x1 x-coordinate of the first point
	 * @param y1 y-coordinate of the first point
	 * @param x2 x-coordinate of the second point
	 * @param y2 y-coordinate of the second point
	 * 
	 * Draws a line dictated by the two points specified
	 * by the parameters
	 */
	public void drawLine(final int x1, final int y1, final int x2, final int y2)
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		gfx.drawLine(x1, y1, x2, y2);
	}

	private void clearScreen()
	{
		if(!isInitialized) throw new RenderingException("Renderer has not been initialized properly");
		gfx.setColor(Color.BLACK);
		gfx.fillRect(0, 0, wndHandle.getWidth(), wndHandle.getHeight());	
	}
	
	public void setColor(final Color color) { gfx.setColor(color); }
}
