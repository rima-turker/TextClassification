package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils.Null;

import Model.DBLP;
import util.Config;
import util.FileUtil;

public class ReadTestData {
	private final static String FILE_DBLP = Config.getString("FILE_DBLP", "");
			//"/home/rtue/workspace/TextClassification/Data/JSON" ;//
	
	public static void analiseDBLPData_local()
	{
		Set<String> venue = new HashSet<>();
		Set<Integer> year = new HashSet<>();
		try {
				BufferedReader br = new BufferedReader(new FileReader("/home/rtue/workspace/TextClassification/Data/dblp-ref-3.json"));
				String line=null;
				while ((line = br.readLine()) != null) {
					final DBLP fromJson = DBLP.fromJson(line);
					System.out.println(fromJson.getVenue());
					System.out.println(fromJson.getYear());
					venue.add(fromJson.getVenue());
					year.add(fromJson.getYear());
				} 
				System.out.println("Number of years: " + year.size());
				System.out.println("Number of years: " + venue.size());
				br.close();
			}
		catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void analiseDBLPData()
	{
		int countMissingInfo =0;
		List<String> result = new LinkedList<>();
		try {
			final File[] listOfFiles = new File(FILE_DBLP).listFiles();
			Arrays.sort(listOfFiles);
			System.out.println("There are "+listOfFiles.length+" file in the folder");
			for (int i = 0; i < listOfFiles.length; i++) {
				final String file = listOfFiles[i].getPath();
				BufferedReader br = new BufferedReader(new FileReader(file));
				System.out.println("reading "+file);
				String line=null;
				while ((line = br.readLine()) != null) {
					final DBLP fromJson = DBLP.fromJson(line);
					int year =fromJson.getYear();
					String venue=fromJson.getVenue().trim();
					String title=fromJson.getTitle().trim();
					
					if ( !venue.equals("") && !title.equals("")) {

						result.add(fromJson.getYear()+"\t"+fromJson.getVenue()+"\t"+fromJson.getTitle());
					}
					else{
						countMissingInfo++;
//						System.out.println(fromJson.getYear()+"\t"+fromJson.getVenue()+"\t"+fromJson.getTitle());
					}
				} 
				br.close();
			}
			System.out.println("Missing Info = "+countMissingInfo);
			FileUtil.writeDataToFile(result, new File("VenueAndTitle"));
		}
		catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
