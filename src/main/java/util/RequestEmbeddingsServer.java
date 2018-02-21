package util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class RequestEmbeddingsServer {

	private static final String BASE_URL = Config.getString("SERVER_BASE_URL", "");

	public static void main(String[] args) throws Exception {
		System.err.println(getSimilarity("hello","who","WORD2VEC"));
	}

	public static List<String> getMostSimilarWords(String entity1, String model, int n) {
		try{
			//http://10.10.4.10:4567/getMostSimilarWords?entity1=2696&model=LINE_1st&number=10
			String url = BASE_URL+"getMostSimilarWords?"+"entity1="+entity1+"&model="+model+"&number="+n;
			final HttpGet request = new HttpGet(url);
			final CloseableHttpClient client =  HttpClients.createDefault();
			final HttpResponse response = client.execute(request);
			final BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			String line = rd.readLine();
			line= line.replace("[", "").replace("]", "");
			final String[] split = line.split(",");
			final List<String> result =new ArrayList<String>();
			for(int i=0;i<split.length;i++){
				result.add(split[i].trim());
			}
			client.close();
			return result;
		}catch(Exception e){
			return null;
		}
	}
	public static Double getSimilarity(String entity1, String entity2, String model) {
		try{
			String url = BASE_URL+"similarity?"+"entity1="+entity1+"&entity2="+entity2+"&model="+model;
			final HttpGet request = new HttpGet(url);
			final CloseableHttpClient client =  HttpClients.createDefault();
			final HttpResponse response = client.execute(request);
			final BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			final StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			client.close();
			return Double.parseDouble(result.toString());
		}catch(Exception e){
			return null;
		}
	}

	public static double[] getWordVector(String word){
		try{
			String url = BASE_URL+"wordvector?"+"entity1="+word;
			final HttpGet request = new HttpGet(url);
			final CloseableHttpClient client =  HttpClients.createDefault();
			final HttpResponse response = client.execute(request);
			final BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line = rd.readLine();
			line= line.replace("[", "").replace("]", "");
			final String[] split = line.split(",");
			final double[] result = new double[split.length];
			for(int i=0;i<split.length;i++){
				result[i] = Double.parseDouble(split[i].trim());
			}
			client.close();
			return result;
		}catch(Exception e){  
			return null;
		}

	}
}