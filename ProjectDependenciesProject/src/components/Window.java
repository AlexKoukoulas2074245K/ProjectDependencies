package components;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

public class Window extends ComponentAdapter implements WindowObservable
{
	private static final Dimension SCREEN_DIMS = Toolkit.getDefaultToolkit().getScreenSize();
	// Window metric variables
	private int width;
	private int height;
	private float xOffset;
	private float yOffset;
	private float aspect;
	private boolean resized;
	private Rectangle mouseRect;
	
	// Observers
	private List<WindowObserver> observers;
	
	public Window()
	{
		observers = new LinkedList<WindowObserver>();
		init();
	}
	
	@Override
    public void componentResized(ComponentEvent e) 
    {
        width = e.getComponent().getWidth();
        height = e.getComponent().getHeight();
        aspect = (float)width / (float)height;
        resized = true;
        for(WindowObserver observer : observers) observer.onResizeNotification();
    }
	
	public void init()
	{
		width = (int)(SCREEN_DIMS.getWidth() / 1.5);
		height = (int)(SCREEN_DIMS.getHeight() / 1.5);
		aspect = (float) width / (float) height;
	}
	
	public void update()
	{
	}
	
	@Override
	public void registerObserver(final WindowObserver observer) { observers.add(observer); }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public float getAspect() { return aspect; }
	public boolean isResized() { return resized; }
	public Dimension getDimensions() { return new Dimension(width, height); }
	public float[] getRenderOffset() { return new float[]{xOffset, yOffset}; }
	public Rectangle getMouseRect() { return mouseRect; }
}
