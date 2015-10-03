package parsing;

public final class CSLanguage implements Language 
{
	
	public static final String CS_SOURCE_FILE_EXTENSION = ".cs";
	private static final String CS_USING_STATEMENT = "using";
	private static final String CS_LANGUAGE_NAME = "C#";

	@Override
	public String getLanguageImportName() { return CS_USING_STATEMENT; }

	@Override
	public String getLanguageName() { return CS_LANGUAGE_NAME; }
	
	@Override
	public String[] getDependenciesFromString(String inputString, String fileName) 
	{
		String[] classPath = inputString.split("\\s+")[1].split(";")[0].split("\\.");
		return new String[]{classPath[classPath.length - 1] + CS_SOURCE_FILE_EXTENSION};
	}
}
