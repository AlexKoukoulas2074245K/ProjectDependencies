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
	public String getDependencyFromString(final String inputString, final String fileName)
	{
		if(inputString.split(" ").length < 1)
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
		return splitOfSlashes[splitOfSlashes.length - 1].substring(1, includedBlock.length() - 1);
	}
}
