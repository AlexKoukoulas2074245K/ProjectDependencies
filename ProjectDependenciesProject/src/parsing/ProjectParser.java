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

public final class ProjectParser 
{
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
		List<File> headers = accumulateHeaders(workingDirectory);
		
		if(language == null) throw new ParsingException("The Language for the source files provided is not supported");
		
		Map<String, HeaderData> result = new HashMap<String, HeaderData>();
		
		for(File f: headers) result.put(f.getName(), new HeaderData(new ArrayList<String>()));
		
		for(File f: headers)
		{
			String currHeaderName = f.getName();
			try(BufferedReader br = new BufferedReader(new FileReader(f)))
			{
				String line;
				while((line = br.readLine()) != null)
				{
					if(line.startsWith(language.getLanguageImportName()))
					{
						String fName = language.getDependencyFromString(line, currHeaderName);
						if(result.containsKey(fName))
						{
							result.get(currHeaderName).dependencies.add(fName); // add fName as a dependency to f
							++result.get(fName).nHeaderIncluded;   // increment the times fName is included
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
	 * @return a collection of header files
	 */
	private List<File> accumulateHeaders(File dir)
	{
		List<File> result = new ArrayList<File>();
		for(File f : dir.listFiles()) addHeaders(f, result);
		return result;
	}
	
	/**
	 * 
	 * @param f file to be analyzed
	 * @param collection to be filled by header files
	 */
	private void addHeaders(File f, List<File> collection)
	{
		if(f.isDirectory()) for(File internF: f.listFiles()) addHeaders(internF, collection);

		if(f.getName().endsWith(CLanguage.C_HEADER_FILE_EXT)) 
		{
			collection.add(f);
			if(language == null) language = new CLanguage();
		}
		else if(f.getName().endsWith(JavaLanguage.JAVA_SOURCE_FILE_NAME))
		{
			collection.add(f);
			if(language == null) language = new JavaLanguage();
		}
	}
}
