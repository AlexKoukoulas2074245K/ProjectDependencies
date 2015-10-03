package parsing;

import javax.swing.JOptionPane;

public final class OCLanguage implements Language 
{
	
	public static final String OBJC_HEADER_FILE_EXTENSION = ".h";
	public static final String OBJC_SOURCE_FILE_EXTENSION = ".m";
	
	private static final String OBJC_IMPORT_STATEMENT = "#import";
	private static final String OBJC_LANGUAGE_NAME = "Objective-C";


	@Override
	public String getLanguageImportName() { return OBJC_IMPORT_STATEMENT; }

	@Override
	public String getLanguageName() { return OBJC_LANGUAGE_NAME; }
	
	@Override
	public String[] getDependenciesFromString(String inputString, String fileName) 
	{
		if(inputString.split("\\s+").length < 1)
		{	
			JOptionPane.showMessageDialog(
				null,
				fileName + ": posed a problem during parsing.",
				"Error",
				JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		String includedBlock = inputString.split(" ")[1];
		if(includedBlock.length() < 3) 
		{
			JOptionPane.showMessageDialog(
				null,
				fileName + ": posed a problem during parsing.",
				"Error",
				JOptionPane.WARNING_MESSAGE);
				return null;
		}
		
		String[] splitOfSlashes = includedBlock.split("/");
		String lastName = splitOfSlashes[splitOfSlashes.length - 1];
		if(splitOfSlashes.length > 1) return new String[]{lastName.substring(0, lastName.length() - 1)};
		return new String[]{lastName.substring(1, lastName.length() - 1)};
	}
}
