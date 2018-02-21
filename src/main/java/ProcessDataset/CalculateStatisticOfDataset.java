package ProcessDataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Tuple;

public class CalculateStatisticOfDataset 
{
	public void printStatisticOfDataset()
	{
		PostprocessFiles post = new PostprocessFiles();
		Map<String, List<Tuple>> mapSentencesNER = new HashMap<>(post.getMapSentencesAndTags());
		Map<String, Integer> mapDocNER = new HashMap<>(post.getMapSentenceIDAndTagNumber());
		//System.out.println("Total number of NERs mapDocNERs "+ sumNERs(mapDocNER));
		printNERs(mapSentencesNER);
	}
	
	private int sumNERs(Map<String, Integer> mapDocNER)
	{
		int sum = 0;
		for (Entry<String, Integer> entry:mapDocNER.entrySet()) {
			sum+=entry.getValue();
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		return sum;
	}
	private void printNERs(Map<String, List<Tuple>> mapSentencesNER)
	{
		int sum = 0;
		for (Entry<String, List<Tuple>> entry:mapSentencesNER.entrySet()) {
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
	}
}
