package parsing;

public final class JSLanguage implements Language
{
	public  static final String JS_SOURCE_FILE_EXTENSION = ".js";
	private static final String JS_LANGUAGE_NAME = "JavaScript";
	private static final String JS_IMPORT_STATEMENT = "import";
			
	@Override
	public String getLanguageImportName() { return JS_IMPORT_STATEMENT; }

	@Override
	public String getLanguageName() { return JS_LANGUAGE_NAME; }
	
	@Override
	public String[] getDependenciesFromString(String inputString, String fileName) 
	{
		String[] splitComps = inputString.split("\\s+");
		String moduleName = splitComps[splitComps.length - 1].split(";")[0];
		String[] splitModName = moduleName.split("/");
		String lastName = splitModName[splitModName.length - 1];
		if(lastName.startsWith("\"") || lastName.startsWith("'")) lastName = lastName.substring(1);
		if(lastName.endsWith("\"") || lastName.endsWith("'")) lastName = lastName.substring(0, lastName.length() - 1);
		return new String[]{lastName + JS_SOURCE_FILE_EXTENSION};
	}
}
