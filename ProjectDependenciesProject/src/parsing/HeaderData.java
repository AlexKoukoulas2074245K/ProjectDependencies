package parsing;

import java.util.ArrayList;

public final class HeaderData 
{
	public ArrayList<String> dependencies;
	public int nHeaderIncluded;
	
	public HeaderData(final ArrayList<String> dependencies)
	{
		this.dependencies = dependencies;
		nHeaderIncluded = 0;
	}
}
