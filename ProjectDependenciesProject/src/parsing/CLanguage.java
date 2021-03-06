package parsing;

import javax.swing.JOptionPane;

public final class CLanguage implements Language
{
	public static final String C_HEADER_FILE_EXT = ".h";
	private static final String C_INCLUDE_STATEMENT = "#include";
	private static final String C_NAME = "C/C++";
	
	@Override
	public String getLanguageName() { return C_NAME; }
	
	@Override
	public String getLanguageImportName() { return C_INCLUDE_STATEMENT; }
	
	@Override
	public String[] getDependenciesFromString(final String inputString, final String fileName)
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
