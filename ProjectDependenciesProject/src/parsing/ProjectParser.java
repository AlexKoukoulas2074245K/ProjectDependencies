package parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public final class ProjectParser 
{
	private enum AvailLang
	{
		C(new CLanguage()),
		CS(new CSLanguage()),
		JAVA(new JavaLanguage()),
		PYTHON(new PythonLanguage()),
		JS(new JSLanguage()),
		OC(new OCLanguage());
		
		public Language lang;
		AvailLang(Language lang) { this.lang = lang; }
	}
	
	private Language language;
	private File workingDirectory;

	/**
	 * 
	 * @param map Map to be sorted
	 * @return the map sorted by value
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream <Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue()))
          .forEach(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }
	
	/**
	 * 
	 * @param dirPath Directory to search into
	 * @return A HashMap containing data about the headers
	 * discovered as well as their dependencies, during parsing
	 */
	public Map<String, HeaderData> parseDirectory(final String dirPath)
	{
		workingDirectory = new File(dirPath);
			
		language = null;
		Map<AvailLang, Integer> languageFileCounts = new HashMap<AvailLang, Integer>();
		List<File> sourceFiles = accumulateHeaders(workingDirectory, languageFileCounts);
		
		int maxNFiles = 0;
		AvailLang maxLang = null;
		
		for(Entry<AvailLang, Integer> entry : languageFileCounts.entrySet())
		{
			if(entry.getValue() > maxNFiles)
			{
				maxNFiles = entry.getValue();
				maxLang = entry.getKey();
			}
		}

		Map<String, HeaderData> result = new HashMap<String, HeaderData>();
		
		if(maxLang == null)
		{			
			JOptionPane.showMessageDialog(
					null,
					"The language of your project is not supported",
					"Error",
					JOptionPane.WARNING_MESSAGE);
			return result;	
		}
		
		language = maxLang.lang;
		if(maxLang == AvailLang.C || maxLang == AvailLang.OC) // In C/C++ and OBJC headers start with .h so we need to distinguish on source files
		{
			language = findBetweenCAndOC(workingDirectory);
		}
		
		for(File f: sourceFiles) result.put(f.getName(), new HeaderData(new ArrayList<String>()));
		
		for(File f: sourceFiles)
		{
			String currHeaderName = f.getName();
			try(BufferedReader br = new BufferedReader(new FileReader(f)))
			{
				String line;
				while((line = br.readLine()) != null)
				{
					String langImportName = language.getLanguageImportName();
					String[] langDiffImports = langImportName.split("&"); // some languages can import in multiple ways(e.g. Python: import, from)
					for(String importEntry : langDiffImports)
					{		
						if(line.startsWith(importEntry))
						{
							String[] fNames = language.getDependenciesFromString(line, currHeaderName);

							for(String fName : fNames)
							{
								if(result.containsKey(fName))
								{
									result.get(currHeaderName).dependencies.add(fName); // add fName as a dependency to f
									++result.get(fName).nHeaderIncluded;   // increment the times fName is included
								}							
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public Language getLanguage() { return language; } 
	
	/**
	 * 
	 * @param dir to be searched
	 * @param langMap to fill in based on the method's findings
	 * @return a collection of header files
	 */
	private List<File> accumulateHeaders(final File dir, Map<AvailLang, Integer> langMap)
	{
		List<File> result = new ArrayList<File>();
		
		AvailLang[] allVals = AvailLang.values();
		for(AvailLang lang : allVals) langMap.put(lang, 1);
		
		for(File f : dir.listFiles()) addHeaders(f, result, langMap);
		
		return result;
	}
	
	private Language findBetweenCAndOC(File dir)
	{
		List<File> cLangFiles = new ArrayList<File>();
		List<File> ocLangFiles = new ArrayList<File>();
	
		addSourceFiles(dir, cLangFiles, ocLangFiles);
		return cLangFiles.size() >= ocLangFiles.size() ? AvailLang.C.lang : AvailLang.OC.lang;
	}
	
	private void addSourceFiles(final File f, final List<File> a, final List<File> b)
	{
		if(f.isDirectory()) for(File internF: f.listFiles()) addSourceFiles(internF, a, b);

		if(f.getName().endsWith(".c")) a.add(f);
		else if(f.getName().endsWith(".m")) b.add(f);
	}
	
	/**
	 * 
	 * @param f file to be analyzed
	 * @param collection to be filled by header files
	 * @param langMap to fill in based on the method's findings
	 */
	private void addHeaders(final File f, final List<File> collection, Map<AvailLang, Integer> langMap)
	{
		if(f.isDirectory()) for(File internF: f.listFiles()) addHeaders(internF, collection, langMap);

		if(f.getName().endsWith(CLanguage.C_HEADER_FILE_EXT)) 
		{
			collection.add(f);
			langMap.put(AvailLang.C, langMap.get(AvailLang.C) + 1);
			langMap.put(AvailLang.OC, langMap.get(AvailLang.OC) + 1);
		}
		else if(f.getName().endsWith(JavaLanguage.JAVA_SOURCE_FILE_NAME))
		{
			collection.add(f);
			langMap.put(AvailLang.JAVA, langMap.get(AvailLang.JAVA) + 1);
		}
		else if(f.getName().endsWith(CSLanguage.CS_SOURCE_FILE_EXTENSION))
		{
			collection.add(f);			
			langMap.put(AvailLang.CS, langMap.get(AvailLang.CS) + 1);
		}
		else if(f.getName().endsWith(PythonLanguage.PYTHON_SOURCE_FILE_EXTENSION))
		{
			collection.add(f);
			langMap.put(AvailLang.PYTHON, langMap.get(AvailLang.PYTHON) + 1);
		}
		else if(f.getName().endsWith(JSLanguage.JS_SOURCE_FILE_EXTENSION))
		{
			collection.add(f);
			langMap.put(AvailLang.JS, langMap.get(AvailLang.JS) + 1);
		}
	}
}
