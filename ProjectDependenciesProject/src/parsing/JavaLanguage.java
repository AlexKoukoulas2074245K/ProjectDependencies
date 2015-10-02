package parsing;

public final class JavaLanguage implements Language{

	public static final String JAVA_SOURCE_FILE_NAME = ".java";
	private static final String JAVA_IMPORT_STATEMENT = "import";
	private static final String JAVA_NAME = "Java";
	
	@Override
	public String getLanguageName() { return JAVA_NAME; }
	
	@Override
	public String getLanguageImportName() { return JAVA_IMPORT_STATEMENT; }
	
	@Override
	public String getDependencyFromString(final String inputString, final String fileName) 
	{
		String[] classPath = inputString.split("\\s+")[1].split(";")[0].split("\\.");
		return classPath[classPath.length - 1] + JAVA_SOURCE_FILE_NAME;
	}
}
