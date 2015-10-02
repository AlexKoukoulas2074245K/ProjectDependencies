package components;

interface DropObserver
{
	/**
	 * 
	 * @param path of the element dropped
	 * 
	 * Notification of a file drop in the window
	 */
	public void onDropNotification(final String path);
}
