package ProcessDataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import util.Config;
import util.Tuple;

public class PostprocessFiles {
	private final String FILE_SENTENCES_NER = Config.getString("FILE_SENTENCES_NER", "");
	private final String FILE_PERFILE_NER = Config.getString("FILE_PERFILE_NER", "");
	private Map<String, List<Tuple>> mapSentencesAndTags;
	private Map<String, Integer> mapSentenceIDAndTagNumber;
	
	private static final Logger LOG = Logger.getLogger(PreDataset.class);

	public PostprocessFiles() {
		mapSentencesAndTags= new HashMap<String, List<Tuple>>();
		mapSentenceIDAndTagNumber = new HashMap<String, Integer>();
		postProcessNERSentenceses();
		postProcessDocsNER();
	}
	public void postProcessDocsNER() {
		try(BufferedReader br = new BufferedReader(new FileReader(FILE_PERFILE_NER)))
		{
			String line=null;
			int count=0;
			while ((line = br.readLine()) != null) 
			{
				count++;
				mapSentenceIDAndTagNumber.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Integer> getMapSentenceIDAndTagNumber() {
		return mapSentenceIDAndTagNumber;
	}
	public void postProcessNERSentenceses() {
		try(BufferedReader br = new BufferedReader(new FileReader(FILE_SENTENCES_NER)))
		{
			String line=null;
			List<String> lstLines = new ArrayList<>();
			while ((line = br.readLine()) != null) 
			{
				if (line.equals("")) {
					processLine(lstLines);
					lstLines.clear();
				} else {
					lstLines.add(line);
				}
			}
			processLine(lstLines);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void processLine(List<String> lst) {
		List<Tuple> lstNERs = new ArrayList<>();
		String docID = "";
		for (int i = 0; i < lst.size(); i++) {
			if (i==0) {
				docID = lst.get(i).split("\t")[0];
				String sentence = lst.get(i).split("\t")[1];
			}
			else
			{
				lstNERs.add(new Tuple(lst.get(i).split("\t")[0],lst.get(i).split("\t")[1]));
			}
		}
		if (docID.equalsIgnoreCase("George Washington University")) {
			System.out.println("YES");
		}
		addElementMapSentencesAndTags(docID,lstNERs);
	}
	
	public void postPerFileNER() {
		try(BufferedReader br = new BufferedReader(new FileReader(FILE_PERFILE_NER)))
		{
			String line=null;
			StanfordNerRunner ner = new StanfordNerRunner();
			while ((line = br.readLine()) != null) {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void addElementMapSentencesAndTags(String docID, List<Tuple> tp) {
		List<Tuple> lstTemp ;
		if (getMapSentencesAndTags().containsKey(docID)) {
			lstTemp = new ArrayList<>(getMapSentencesAndTags().get(docID));
		}
		else
		{
			lstTemp = new ArrayList<>();
		}
		for (Tuple tuple:tp) {
			lstTemp.add(tuple);
		}
		
		getMapSentencesAndTags().put(docID, lstTemp);
	}
	public Map<String, List<Tuple>> getMapSentencesAndTags() {
		return mapSentencesAndTags;
	}

}
