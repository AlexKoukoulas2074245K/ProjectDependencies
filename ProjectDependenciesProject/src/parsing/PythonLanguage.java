package parsing;

import javax.swing.JOptionPane;

public final class PythonLanguage implements Language
{
	public static final String PYTHON_SOURCE_FILE_EXTENSION = ".py";
	private static final String PYTHON_IMPORT_STATEMENT = "from&import";
	private static final String PYTHON_LANGUAGE_NAME = "Python";
	
	@Override
	public String getLanguageImportName() { return PYTHON_IMPORT_STATEMENT; }

	@Override
	public String getLanguageName() { return PYTHON_LANGUAGE_NAME; }
	
	@Override
	public String[] getDependenciesFromString(String inputString, String fileName) 
	{
		if(inputString.startsWith("from"))
		{
			String[] splitDepPath = inputString.split("\\s+")[1].split("\\.");
			return new String[]{splitDepPath[splitDepPath.length - 1] + PYTHON_SOURCE_FILE_EXTENSION}; 
		}
		else if(inputString.startsWith("import"))// line starts with import
		{
			String[] dependencies = inputString.split("\\s+")[1].split(","); 
			String[] result = new String[dependencies.length];
			for(int i = 0; i < dependencies.length; ++i)
			{
				String[] compDep = dependencies[i].split("\\.");
				result[i] = compDep[compDep.length - 1] + PYTHON_SOURCE_FILE_EXTENSION;
			}
			
			return result;
		}
		
		JOptionPane.showMessageDialog(
			null,
			fileName + ": posed a problem during parsing.",
			"Error",
			JOptionPane.WARNING_MESSAGE);
		return new String[0];
	}	
}
