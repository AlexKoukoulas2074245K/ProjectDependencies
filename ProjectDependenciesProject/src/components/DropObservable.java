package components;

interface DropObservable 
{
	/**
	 * 
	 * @param observer to be registered to this observable
	 */
	public void registerObserver(final DropObserver observer);
}
