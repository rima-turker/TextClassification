package Embeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacpp.FlyCapture2.SystemInfo;
import org.bytedeco.javacpp.opencv_core.Scalar;

import util.Config;
import util.FileUtil;
import util.Tuple;

public class ConvertToNumber {

	private final static Map<String, Long> map = new HashMap<>();

	private final static String FILLE_WIKI_MAPPING_RESULT = Config.getString("FILLE_WIKI_MAPPING_RESULT", "");
	private final static String FILLE_WIKI_MAPPING = Config.getString("FILLE_WIKI_MAPPING", "");//"mappingInfo";//
	private final static String FILE_WIKI_TITLEANDCATEGORIES = Config.getString("FILE_WIKI_TITLEANDCATEGORIES", "");
	private final static String FILE_WIKI_TITLEANDCATEGORIES_CLEAN = Config.getString("FILE_WIKI_TITLEANDCATEGORIES_CLEAN", "");
	private final static String FILE_WIKI_VECTORS_SECOND = Config.getString("FILE_WIKI_VECTORS_SECOND", "");//"vec_2nd_wo_norm.txt_sample";// 
	
	public static void convertVectorNumbersToNames() {
		
		Map<String, String> mapInfo = new HashMap<>();
		List<String> lstResult = new ArrayList<>();
		
		String entity_mapValue = "";
		String entity_Original = "";
		String sCurrentLine = "";
		String newLine= "";
		try (final BufferedReader br = new BufferedReader(new FileReader(FILE_WIKI_VECTORS_SECOND))) {
			List<String> linesInfo = Files.readAllLines(Paths.get(FILLE_WIKI_MAPPING), StandardCharsets.UTF_8);
			

			System.out.println("Reading complete "+FILE_WIKI_VECTORS_SECOND);
			System.out.println("Reading complete "+FILE_WIKI_TITLEANDCATEGORIES);
			
			for (int i = 0; i < linesInfo.size(); i++) {
				String[] split = linesInfo.get(i).split("\t");
				mapInfo.put(split[1],split[0]);
			}
			
			sCurrentLine = br.readLine();//ignore the first line
			while ((sCurrentLine = br.readLine()) != null) {
				String[] split = sCurrentLine.split(" ", 2);
				entity_mapValue = split[0];
				entity_Original = mapInfo.get(entity_mapValue);
				String theRest = split[1];
				
				if (entity_mapValue==null||entity_Original==null) {
					System.out.println("-----------------------------------------");
					System.out.println("entity_mapValue "+entity_mapValue+" entity_Original "+entity_Original);
					System.out.println("-----------------------------------------");
				}
				
				//StringBuilder strBuilder = 
				newLine=entity_Original+" "+theRest;
//				if(StringUtils.countMatches(newLine, entity_Original)>1)
//				{
//					System.out.println("-----------------------------------------");
//					System.out.println(sCurrentLine);
//					System.out.println();
//					System.err.println(entity_Original);
//					System.out.println();
//					System.err.println(entity_mapValue);
//					System.err.println(newLine);
//					System.out.println();
//					
//					//System.exit(1);
//				}
				lstResult.add(newLine); 
				//System.out.println(newLine);
			}
		}
		catch (Exception e) {
			System.out.println("I am in the catch blok : "+e.getMessage());
			System.out.println(sCurrentLine);
			
			
		}
//		System.out.println("Writing To a file.");
//		System.out.println("*****************************************************");
//		System.out.println(sCurrentLine);
//		System.out.println();
//		System.err.println(entity_mapValue);
//		System.out.println();
//
//		System.out.println("*****************************************************");
//		System.out.println("Size of the list "+lstResult.size());
//		
//		System.err.println(mapInfo.get(entity_mapValue));
//		System.out.println();
//		System.out.println(entity_mapValue+"value should be replaced with "+mapInfo.get(entity_mapValue));
//		System.out.println(sCurrentLine.replaceFirst(entity_mapValue,entity_Original));
//		
//		System.err.println(newLine);
//		System.err.println(entity_mapValue);

		
		FileUtil.writeDataToFile(lstResult, "vec_2nd_wo_norm_originalName.txt", false);
		
	}
	
	public static void CheckMapping()
	{
		try  {
			
			System.out.println("FILLE_WIKI_MAPPING_RESULT "+ FILLE_WIKI_MAPPING_RESULT );
			System.out.println( "FILLE_WIKI_MAPPING"+FILLE_WIKI_MAPPING );
			System.out.println( "FILE_WIKI_TITLEANDCATEGORIES " +FILE_WIKI_TITLEANDCATEGORIES );
			
			List<String> linesResults = Files.readAllLines(Paths.get(FILLE_WIKI_MAPPING_RESULT), StandardCharsets.UTF_8);
			List<String> linesInfo = Files.readAllLines(Paths.get(FILLE_WIKI_MAPPING), StandardCharsets.UTF_8);
			List<String> linesOriginal = Files.readAllLines(Paths.get(FILE_WIKI_TITLEANDCATEGORIES), StandardCharsets.UTF_8);
	
			System.out.println("linesResults "+FILLE_WIKI_MAPPING_RESULT+" "+linesResults.size());
			System.out.println("linesInfo "+FILLE_WIKI_MAPPING+" "+linesInfo.size());
			System.out.println("linesOriginal "+FILE_WIKI_TITLEANDCATEGORIES+" "+linesOriginal.size());
			
			List<Tuple> lstInfo = new ArrayList<>();
			List<Tuple> lstResul = new ArrayList<>();
			List<Tuple> lstMain = new ArrayList<>();
			
			
			Map<String, String> mapInfo = new HashMap<>();
			
			for (int i = 0; i < linesResults.size(); i++) {
				String[] split = linesResults.get(i).split("\t");
				lstResul.add(new Tuple(split[0], split[1]));
			}
			
			for (int i = 0; i < linesInfo.size(); i++) {
				String[] split = linesInfo.get(i).split("\t");
				lstInfo.add(new Tuple(split[0], split[1]));
				mapInfo.put(split[1],split[0]);
			}
			
			for (int i = 0; i < linesOriginal.size(); i++) {
				String[] split = linesOriginal.get(i).split(" ");
				lstMain.add(new Tuple(split[0], split[1]));
			}
			List<Tuple> lstconverted = new ArrayList<>();

			for(Tuple tp : lstResul)
			{
				String key = tp.getA();
				String value = tp.getB();
				lstconverted.add(new Tuple(mapInfo.get(key), mapInfo.get(value)));
			}
			System.out.println("Started to check");

			int count = lstMain.size()-1;
			for (int i = count; i > 0; i--) {
				
				//assertFalse(!lstconverted.contains(lstMain.get(i)));
				
				if (lstconverted.contains(lstMain.get(i))) {
					lstconverted.remove(lstMain.get(i));
					lstMain.remove(lstMain.get(i));
				}
				else
				{
					System.out.println("HATA"+ lstMain.get(i) );
					System.out.println();
				}
				
			}
			System.out.println(lstconverted.size());
			System.out.println(lstMain.size());
		}
		catch (Exception e) {

			System.out.println( FILLE_WIKI_MAPPING_RESULT );
			System.out.println( FILLE_WIKI_MAPPING );
			System.out.println( FILE_WIKI_TITLEANDCATEGORIES );
			System.out.println("Exception "+e.getMessage());
			
		}
	}

	public static void main(String[] args) {
		System.out.println("In the main function");
		List<String> lstMappingResults = new ArrayList<>();
		List<String> lstCorrespondingMapping = new ArrayList<>();
		Set<String> hsetLeft = new HashSet<>();
		Set<String> hsetRight = new HashSet<>();

		try (final BufferedReader br = new BufferedReader(new FileReader(Config.getString("FILE_WIKI_TITLEANDCATEGORIES_CLEAN", "")))) {
			System.out.println("reading a file "+Config.getString("FILE_WIKI_TITLEANDCATEGORIES_CLEAN", ""));
			String sCurrentLine;

			long counter = 1;
			while ((sCurrentLine = br.readLine()) != null) {
				final String[] split = sCurrentLine.split("\t");
				String a = split[0];
				String b = split[1];
				String c = split[2];
				
//				hsetLeft.add(a);
//				hsetRight.add(b);

				String newA;
				String newB;

				final Long valueA = map.get(a);

				if (valueA == null) {
					map.put(a, counter);
					newA = String.valueOf(counter);
					lstCorrespondingMapping.add(a + "\t" + newA);
					counter++;
				} else {
					newA = String.valueOf(valueA);
				}

				final Long valueB = map.get(b);
				if (valueB == null) {
					map.put(b, counter);
					newB = String.valueOf(counter);
					lstCorrespondingMapping.add(b + "\t" + newB);
					counter++;
				} else {
					newB = String.valueOf(valueB);
				}
				lstMappingResults.add(newA + "\t" + newB + "\t" + c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		System.out.println("Left side original file "+hsetLeft.size());
//		System.out.println("Right side original file "+hsetRight.size());
		System.out.println("Writing to a file");
		FileUtil.writeDataToFile(lstMappingResults,"data_titleCategories_mapping_clean_LINE",false);
		FileUtil.writeDataToFile(lstCorrespondingMapping,"mappingInfo_clean_LINE",false);
	}

}
