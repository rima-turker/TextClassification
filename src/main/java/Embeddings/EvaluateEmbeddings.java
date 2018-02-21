package Embeddings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.lucene.search.Collector;

import util.Config;
import util.FileUtil;
import util.MapUtil;
import util.Model;
import util.RequestEmbeddingsServer;

public class EvaluateEmbeddings {
	private static Map<String, String> mapInfo = new HashMap<>();
	private static Map<String, String> mapInfo_numEntity = new HashMap<>();
	final static String FILE_MAPPING = Config.getString("FILE_MAPPING", "");
	final static String FILE_ENTITIES = Config.getString("FILE_ENTITIES", "");
	final static String FILE_CATEGORIES = Config.getString("FILE_CATEGORIES", "");
	final int numberOfElements = 10;

	public void findTheMostSimilarCategories()
	{

		try {
			final List<String> linesCategories= new ArrayList<>(Files.readAllLines(Paths.get(FILE_CATEGORIES), StandardCharsets.UTF_8));
			final List<String> linesEntities = new ArrayList<>(Files.readAllLines(Paths.get(FILE_ENTITIES), StandardCharsets.UTF_8));
			final Model[] model = Model.values();
			int top=1;
			// System.out.println(mapInfo.get(word1)+" "+mapInfo.get(word2));
			for (int i = 0; i < model.length; i++) {
				String modelName = model[i].toString();
				List<String> tempToWrite = new ArrayList<>();
				for (String entity : linesEntities) {
					Map<String, Double> mapEntCat = new HashMap<>();  
					for (String cat : linesCategories) {

						String category="Category:"+cat;
						if (modelName.equals("WORD2VEC")) {
							mapEntCat.put(category, RequestEmbeddingsServer.getSimilarity(entity, category, modelName));
						}
						else
						{
							mapEntCat.put(category, RequestEmbeddingsServer.getSimilarity(mapInfo.get(entity),
									mapInfo.get(category),modelName));
						}
					}
					Map<String, Double> linkedMap = new LinkedHashMap<>(MapUtil.sortByValueDescending(mapEntCat));
					for(Entry <String, Double> entry: linkedMap.entrySet()){
						if (!entry.getValue().isNaN()) {
							tempToWrite.add(entity+"\t"+entry.getKey()+"\t"+entry.getValue());	
						}
						
					}
					//final List<String> collect = linkedMap.entrySet().stream().map(p->p.toString()).collect(Collectors.toList());

				}
				FileUtil.writeDataToFile(tempToWrite, new File(modelName));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ListOfExamples() {
		final Model[] model = Model.values();
		String word1 = "Albert_Einstein";
		String word2 = "Category:Physics";
		// System.out.println(mapInfo.get(word1)+" "+mapInfo.get(word2));
		for (int i = 0; i < model.length; i++) {
			List<String> lst;
			if (model[i].toString().equals("WORD2VEC")) {
				lst = new ArrayList<>(
						RequestEmbeddingsServer.getMostSimilarWords(word1, model[i].toString(), numberOfElements));
				for (String entitiy : lst) {
					if (entitiy.contains("Category:")) {

						// System.out.println(entitiy);
					}
				}
			} else {
				System.out.println();
				lst = new ArrayList<>(RequestEmbeddingsServer.getMostSimilarWords(mapInfo.get(word1),
						model[i].toString(), numberOfElements));
				for (String entitiy : lst) {
					String category = mapInfo_numEntity.get(entitiy);
					if (category.contains("Category:")) {
						System.out.println(category);
					}
				}
			}
			System.out.println();
		}
	}

	public static void initializeMaps() {
		try {
			List<String> linesInfo = Files.readAllLines(Paths.get(FILE_MAPPING), StandardCharsets.UTF_8);
			for (int i = 0; i < linesInfo.size(); i++) {
				String[] split = linesInfo.get(i).split("\t");
				mapInfo.put(split[0], split[1]);
				mapInfo_numEntity.put(split[1], split[0]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
