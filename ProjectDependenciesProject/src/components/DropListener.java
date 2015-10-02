package components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class DropListener implements DropObservable, DropTargetListener
{
	private List<DropObserver> observers;
	private File mostRecentf;
	
	public DropListener()
	{
		observers = new LinkedList<DropObserver>();
	}
	
	@Override
	public void registerObserver(DropObserver observer) { observers.add(observer); }

	@Override
	public void drop(DropTargetDropEvent event) 
	{
		// Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    @SuppressWarnings("unchecked")
					List <File> files = (List<File>) transferable.getTransferData(flavor);
                    mostRecentf = files.get(0);
                }

            } catch (Exception e) {

                // Print out the error stack
                e.printStackTrace();

            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);
        
        for(DropObserver observer : observers) observer.onDropNotification(mostRecentf.getAbsolutePath());
	}

	
	@Override
	public void dragEnter(DropTargetDragEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
}
