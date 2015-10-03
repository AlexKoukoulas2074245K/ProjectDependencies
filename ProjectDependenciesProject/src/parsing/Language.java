package parsing;

public interface Language 
{
	public String[] getDependenciesFromString(final String inputString, final String fileName);
	public String getLanguageImportName();
	public String getLanguageName();
}
