package components;

interface WindowObservable 
{
	/**
	 * 
	 * @param observer to be registered to this observable
	 */
	public void registerObserver(final WindowObserver observer);
}
