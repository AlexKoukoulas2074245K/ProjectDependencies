package parsing;

public interface Language 
{
	public String getDependencyFromString(final String inputString, final String fileName);
	public String getLanguageImportName();
	public String getLanguageName();
}
