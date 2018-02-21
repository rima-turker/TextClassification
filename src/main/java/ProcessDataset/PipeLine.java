package ProcessDataset;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import Embeddings.ConvertToNumber;
import Embeddings.EvaluateEmbeddings;
import Embeddings.GenerateDataSetForNetworkEmbeddings;
import Test.ReadTestData;

public class PipeLine {
	
	public static void main(String[] args) {
		
		ReadTestData.analiseDBLPData();
//		EvaluateEmbeddings evaluate = new EvaluateEmbeddings();
//		EvaluateEmbeddings.initializeMaps();
//		evaluate.findTheMostSimilarCategories();
		
//		GenerateDataSetForNetworkEmbeddings generate = new GenerateDataSetForNetworkEmbeddings();
//		generate.generateDatasetForLineTitlesAndEntities();
//		ConvertToNumber.main(null);
//		PreDataset ana = new PreDataset();
//		ana.readAndTagSentenceFromFolderAndFile_parallel();
//		CalculateStatisticOfDataset cal = new CalculateStatisticOfDataset();
//		cal.printStatisticOfDataset();
		
		
		
	}
	private static void test() {
		String s = "Ƒ";
		Map<String,String> map = new HashedMap<>();
		map.put(s, "1");
		map.put("F", "3");
		
		System.err.println(map.get(s));
		System.err.println(map.get("F"));
		
	}
	private static void initilaze() {
		
	}

}
