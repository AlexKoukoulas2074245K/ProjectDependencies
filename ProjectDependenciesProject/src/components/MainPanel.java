package components;

import input.InputHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import parsing.HeaderData;
import parsing.ProjectParser;
import rendering.Renderer;

public class MainPanel extends JPanel implements Runnable, WindowObserver, DropObserver
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// panel objects
	private Thread thread;
	private Renderer renderer;
	private InputHandler inputHandler;
	private ProjectParser parser;
	private Window wnd;
	private DropListener dropListener;
	
	// images
	private BufferedImage backgroundImage;
	
	// timing variables
	private int FPS = 60;
	private long targetTime = 1000/FPS;
	
	// arguments passed to panel 
	private Map<String, HeaderData> headerData;
	private Map<String, Rectangle> rects;
	private Map<String, float[]> rectPos0;
	private Map<String, float[]> rectDeltas;
	
	private float rectWidth;
	private float rectHeight;
	private float fontSize;
	private int maxDeps;
	private int zoomLevel;
	
	private boolean swiping;
	private int[] rectMouseDist;
	private int mousePressX;
	private int mousePressY;
	private String headerMoving;
	private String headerHighlighted;
	
	private volatile File currentProject;
	private volatile boolean wndResized = false;
	private volatile boolean newProject = false;
	
	public MainPanel() {
		super();
		
		parser = new ProjectParser();
		
		headerData = new HashMap<String, HeaderData>();
		rectDeltas = new HashMap<String, float[]>();
		rectPos0   = new HashMap<String, float[]>();
		
		zoomLevel = 40;
		
		dropListener = new DropListener();
		dropListener.registerObserver(this);
		
		wnd = new Window();
		wnd.init();
		wnd.registerObserver(this);
		
		setPreferredSize(wnd.getDimensions());
		setFocusable(true);
		requestFocus();
		
		try { backgroundImage = ImageIO.read(getClass().getResourceAsStream("/compsci.png")); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void addNotify() 
	{
		super.addNotify();
		if (thread == null){
			thread = new Thread(this);
			inputHandler = new InputHandler();
			
			addComponentListener(wnd);
			addMouseListener(inputHandler);
			addMouseMotionListener(inputHandler);
			addMouseWheelListener(inputHandler);
			addKeyListener(inputHandler);
			
			thread.start();
		}
	}
	
	private void init()
	{
		if(wnd.getWidth() <= 0 || wnd.getHeight() <= 0) return;
		setPreferredSize(wnd.getDimensions());
		setFocusable(true);
		requestFocus();
		
		JLabel dropArea = new JLabel();
		dropArea.setLocation(0, 0);
		dropArea.setBounds(0, 0, wnd.getWidth(), wnd.getHeight());
		new DropTarget(dropArea, dropListener);
		this.getRootPane().add(BorderLayout.CENTER, dropArea);
		
		int nRects = headerData.size();
		int nRectsPerDimension = (int)Math.ceil(Math.sqrt(nRects));
		
		int x0Offset = 80;
		int y0Offset = (int)(50 * wnd.getAspect());
		
		rectWidth  = (float) (wnd.getWidth() - 2 * x0Offset) / (float)nRectsPerDimension * ((float)zoomLevel / 40f);
		rectHeight = (float) rectWidth / (wnd.getAspect() * 2.2f);
		
		rects = new HashMap<String, Rectangle>();
		
		int xOffset  = (int)(rectWidth * 1.64f);
		int yOffset  = (int)(rectHeight * 1.7f);
		int x = x0Offset;
		int y = y0Offset;
		
		maxDeps = 0;
		
		for(Entry<String, HeaderData> entry : headerData.entrySet())
		{
			if(rectDeltas.containsKey(entry.getKey()))
			{				
				rects.put(entry.getKey(), new Rectangle((int)((float)wnd.getWidth() * rectDeltas.get(entry.getKey())[0]),
														(int)((float)wnd.getHeight() * rectDeltas.get(entry.getKey())[1]),
														(int)rectWidth,
														(int)rectHeight));
			}	
			else
				rects.put(entry.getKey(), new Rectangle(x, y, (int)rectWidth, (int)rectHeight));
			
			x += xOffset;
			
			Rectangle wndRect = new Rectangle(0, 0, wnd.getWidth() - x0Offset, wnd.getHeight());
			Rectangle nextHeaderRect = new Rectangle(x, y, (int)rectWidth, (int)rectHeight);
			if(!wndRect.contains(nextHeaderRect))
			{
				y += yOffset;
				x = x0Offset;
			}
			
			if(entry.getValue().dependencies.size() > maxDeps) maxDeps = entry.getValue().dependencies.size();
		}
		
		int maxLen = 0;
		for(Entry<String, HeaderData> entry : headerData.entrySet()) 
			if(entry.getKey().length() > maxLen) maxLen = entry.getKey().length();
		
		fontSize = ((float)rectWidth * 1.87f)/ (float)maxLen;
		
		swiping = false;
		
		renderer = new Renderer();
		renderer.init(fontSize, wnd);
	}
	
	public void run()
	{
		init();
		
		long start;
		long elapsed;
		long wait;
		
		// game loop
		while (true)
		{
			start = System.nanoTime();
			
			/**
			 * @invariant regardless of asynchronous notifications
			 * the window will always be updated during the main loop
			 */
			wnd.update();
			
			if(wndResized)
			{
				wndResized = false;
				init();
			}
			else if(newProject)
			{
				headerData = parser.parseDirectory(currentProject.getAbsolutePath());
				zoomLevel = 40;
				init();
				newProject = false;
				if(parser.getLanguage() == null) currentProject = null;
			}
			
			updateComponents();
			
			renderer.beginFrame();
			draw();
			renderer.endFrame(getGraphics());
			inputHandler.endFrame();
			
			elapsed = System.nanoTime() - start;
			
			wait = targetTime - elapsed / 1000000;
			
			if (wait < 0) wait = 5;
			
			try
			{
				Thread.sleep(wait);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onResizeNotification() 
	{
		wndResized = true;
	}

	@Override
	public void onDropNotification(String path) 
	{
		File testDir = new File(path);
		if(!testDir.isDirectory()) 
		{
			JOptionPane.showMessageDialog(
					this,
					"Bad file input: File dropped is not a directory",
					"Error",
					JOptionPane.ERROR_MESSAGE);	
		}
		else
		{
			currentProject = testDir;
			newProject = true;
		}
	}
	
	private void updateComponents()
	{
		headerHighlighted = getHeaderRectHovered();
		if(inputHandler.isButtonClicked(InputHandler.LEFT_MB))
		{
			if(headerHighlighted != null)
			{
				Rectangle rect = rects.get(headerHighlighted);
				headerMoving = headerHighlighted;
				rectMouseDist = new int[]{rect.x - inputHandler.getMousePosition()[0],
										  rect.y - inputHandler.getMousePosition()[1]};
			}
			else
			{
				for(Entry<String, Rectangle> entry : rects.entrySet())
				{
					rectPos0.put(entry.getKey(),
								new float[]{entry.getValue().x, entry.getValue().y});
				}
				
				mousePressX = inputHandler.getMousePosition()[0];
				mousePressY = inputHandler.getMousePosition()[1];
				swiping = true;
			}
		}
		else if(inputHandler.isButtonDown(InputHandler.LEFT_MB))
		{
			if(headerMoving != null)
			{
				Rectangle newRect = new Rectangle(inputHandler.getMousePosition()[0] + rectMouseDist[0],
												  inputHandler.getMousePosition()[1] + rectMouseDist[1],
												  (int)rectWidth,
												  (int)rectHeight);
				
				rects.put(headerMoving, newRect);
				rectDeltas.put(headerMoving, new float[]{(float)newRect.x / (float)wnd.getWidth(),
													     (float)newRect.y / (float)wnd.getHeight()});
			}
			else if(swiping)
			{
				float[] deltas = new float[]{inputHandler.getMousePosition()[0] - mousePressX,
											 inputHandler.getMousePosition()[1] - mousePressY};
				
				for(Entry<String, Rectangle> entry : rects.entrySet())
				{
					Rectangle newRect = new Rectangle((int)(rectPos0.get(entry.getKey())[0] + deltas[0]),
													  (int)(rectPos0.get(entry.getKey())[1] + deltas[1]),
													  (int)rectWidth,
													  (int)rectHeight);
					entry.setValue(newRect);
					rectDeltas.put(entry.getKey(), new float[]{(float)newRect.x / (float)wnd.getWidth(),
															   (float)newRect.y / (float)wnd.getHeight()});
				}
			}
		}
		else if(!inputHandler.isButtonDown(InputHandler.LEFT_MB))
		{
			headerMoving = null;
			rectMouseDist = null;
			swiping = false;
			rectDeltas.clear();
		}
		
		if(inputHandler.getMouseWheelRotation() != 0)
		{
			zoomLevel -= inputHandler.getMouseWheelRotation();
			init();
		}
	}
	
	private void draw()
	{
		renderer.drawImage(backgroundImage, 0, 0, (int)wnd.getWidth(), (int)wnd.getHeight());
		if(currentProject == null) 
		{
			drawPrompt();
			return;
		}
		drawDepBoxes();
		drawDepLines();
		drawCutoffBox();
		drawLanguageName();
		drawHeaderInfo();
	}	
	
	private void drawDepLines()
	{
		for(Entry<String, HeaderData> entry : headerData.entrySet())
		{			
			Rectangle rect = rects.get(entry.getKey());
			for(String dep : entry.getValue().dependencies)
			{
				Rectangle otherRect = rects.get(dep);
				if(otherRect == null) continue;
				
				renderer.setColor(Color.red);
				renderer.drawLine((int)rect.getCenterX(),
								  (int)rect.getCenterY(),
								  (int)otherRect.getCenterX(),
								  (int)otherRect.getCenterY());
			}
		}
	}
	
	private void drawDepBoxes()
	{
		for(Entry<String, HeaderData> entry : headerData.entrySet())
		{
			Rectangle rect = rects.get(entry.getKey());
			
			Color boxColor;
			int nDeps = entry.getValue().dependencies.size();
			
			if(nDeps > (int)(maxDeps * 0.75)) boxColor = Color.RED;
			else if(nDeps > (int)(maxDeps * 0.5)) boxColor = Color.ORANGE;
			else if(nDeps > (int)(maxDeps * 0.25)) boxColor = Color.YELLOW;
			else boxColor = Color.GREEN;
			
			if(headerHighlighted != null && headerHighlighted.equals(entry.getKey()))
			{
				int newRed = boxColor.getRed() + 160; if(newRed > 255) newRed = 255;
				int newGreen = boxColor.getGreen() + 160; if(newGreen > 255) newGreen = 255;
				int newBlue = boxColor.getBlue() + 160; if(newBlue > 255) newBlue = 255;
				boxColor = new Color(newRed, newGreen, newBlue);
			}
			renderer.drawCustomRect(boxColor, rect.x, rect.y, rect.width, rect.height);
			
			float strWidth = (entry.getKey().length() * fontSize) / 4f;
			int xOffset = (int)(rect.getCenterX() - strWidth);
			int yOffset = (int)(rect.getCenterY()) + (int)(fontSize / 2);
			
			renderer.setColor(Color.black);
			renderer.drawVarString(entry.getKey(), xOffset, yOffset);
		}
	}
	
	private void drawCutoffBox()
	{
		renderer.setColor(Color.BLACK);
		renderer.drawDefaultRect(0, 0, wnd.getWidth(), (int)(50 * wnd.getAspect()));
		renderer.drawDefaultRect(0, 0, 80, wnd.getHeight());
		renderer.drawDefaultRect(wnd.getWidth() - 80, 0, 80, wnd.getHeight());
	}
	
	private void drawHeaderInfo()
	{
		if(headerHighlighted != null)
		{
			String rndstr = "Header: " + headerHighlighted;
			String rndstr2 = "no. dependencies: " + headerData.get(headerHighlighted).dependencies.size();
			String rndstr3 = "no. headers depended on it: " + headerData.get(headerHighlighted).nHeaderIncluded;
			
			renderer.setColor(Color.YELLOW);
			renderer.drawConstString(rndstr, 20, 10, 20);
			renderer.drawConstString(rndstr2, 20, 10, 50);
			renderer.drawConstString(rndstr3, 20, 10, 80);
		}
	}
	
	private void drawLanguageName()
	{
		if(parser.getLanguage() == null) return;
		String rndstr = "Project Language: " + parser.getLanguage().getLanguageName();
		renderer.setColor(Color.BLUE);
		renderer.drawConstString(rndstr,
								30,
						   		(int)(wnd.getWidth() / 2f - (30 * rndstr.length() / 2f) / 2f),
						   		(int)(wnd.getHeight() / 30f + 30));
	}
	
	private void drawPrompt()
	{
		String rndstr = "Please drag and drop your project folder";
		String rndstr2 = "anywhere inside this window";
		
		renderer.setColor(Color.CYAN);
		renderer.drawConstString(rndstr,
				30,
				(int)(wnd.getWidth() / 2f - (30 * rndstr.length() / 2f) / 2f),
				(int)(wnd.getHeight() / 30f + 30));
		renderer.drawConstString(rndstr2,
				30,
				(int)(wnd.getWidth() / 2f - (30 * rndstr2.length() / 2f) / 2f),
				(int)(wnd.getHeight() / 30f + 70));
	}
	
	private String getHeaderRectHovered()
	{
		Point mouseLoc = new Point((int)inputHandler.getMousePosition()[0], (int)inputHandler.getMousePosition()[1]);
		for(Entry<String, Rectangle> entry : rects.entrySet())
		{
			if(entry.getValue().contains(mouseLoc)) return entry.getKey();
		}
		
		return null; 
	}
}