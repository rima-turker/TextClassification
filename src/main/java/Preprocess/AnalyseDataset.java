package Preprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Config;
import util.NERTagger;
import util.Tuple;

public class AnalyseDataset 
{
	private static final String FOLDER_ADDRESS_20N = Config.getString("DATA_SET_20NG", "");
	public void readSentenceFromFolderAndFile() {
		
		final File[] listOfFiles = new File(FILES_ADDRESS).listFiles();
		Arrays.sort(listOfFiles);
		for (int i = 0; i < listOfFiles.length; i++) {
			final String file = listOfFiles[i].getName();
			executor.execute(handle(FILES_ADDRESS + File.separator + file));
		}
	}
	
	public List<Tuple> tagSentence(String sentence) 
	{
		List<Tuple> listTagged = new ArrayList<Tuple>();
		String strTagged = NERTagger.getNERTags(sentence);
		final String[] strSplit = strTagged.split("\t");
		
		if (strSplit.length>1) {
			for (int i = 0; i < strSplit.length; i+=2) 
			{
				listTagged.add(new Tuple(strSplit[i], strSplit[i+1]));
			}
		}
		return listTagged;
	}
}
