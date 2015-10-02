package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public final class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	public static final int LEFT_MB = 0x01;
	public static final int RIGHT_MB = 0x02;
	
	private int currentState;
	private int previousState;
	private int mouseWheelRot;
	private int[] mousePosition;
	
	public InputHandler()
	{
		currentState  = 0;
		previousState = 0;
		mouseWheelRot = 0;
		mousePosition = new int[2];
	}
	
	public void endFrame()
	{
		previousState = currentState;
		mouseWheelRot = 0;
	}
	
	public boolean isButtonDown(final int mButton) 
	{
		return (currentState & mButton) != 0; 
	}
	
	public boolean isButtonClicked(final int mButton)
	{
		return ((currentState & mButton) != 0) && ((previousState & mButton) == 0);
	}
	
	public int getMouseWheelRotation() { return mouseWheelRot; }
	
	public int[] getMousePosition() { return mousePosition; }
	
	@Override
	public void mousePressed(final MouseEvent e)
	{
		
		switch(e.getButton())
		{
			case MouseEvent.BUTTON1:
			{
				currentState |= LEFT_MB;
			}break;
			
			case MouseEvent.BUTTON3:
			{
				currentState |= RIGHT_MB;
			}break;
		}
		
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}

	@Override
	public void mouseReleased(final MouseEvent e) 
	{
		switch(e.getButton())
		{
			case MouseEvent.BUTTON1:
			{
				currentState ^= LEFT_MB;
			}break;
			
			case MouseEvent.BUTTON3:
			{
				currentState ^= RIGHT_MB;
			}break;
		}
		
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}
		
	@Override
	public void mouseMoved(final MouseEvent e) 
	{
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}
	
	@Override
	public void mouseDragged(final MouseEvent e) 
	{
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();	
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		mouseWheelRot = e.getWheelRotation();
	}
	
	@Override
	public void keyPressed(final KeyEvent e) 
	{
	}

	@Override
	public void keyReleased(final KeyEvent e) 
	{
	}

	
	@Override
	public void mouseClicked(final MouseEvent e) {}
	
	@Override
	public void mouseEntered(final MouseEvent e) {}
	
	@Override
	public void mouseExited(final MouseEvent e) {}

	@Override
	public void keyTyped(final KeyEvent e) {}
}
