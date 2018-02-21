package ProcessDataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import util.Config;
import util.NERTagger;
import util.Tuple;
import util.AtomicCounter;

public class PreDataset 
{
	private static final Logger LOG = Logger.getLogger(PreDataset.class);
	static final Logger secondLOD = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");
	private static final String DATA_SET_20NG = //"/home/rtue/Documents/TestDatasets/TextClassificationTestDatasets/20_newsgroups";//
			Config.getString("DATA_SET_20NG", "");
	private Map<String, Integer> mapTestSetStatistics;
	private static ExecutorService executor;
	//private static StanfordNerRunner ner = new StanfordNerRunner();
	private AtomicCounter countFile;
	private AtomicCounter countFolder;

	public PreDataset()
	{
		mapTestSetStatistics = new HashMap<>();
		countFile= new AtomicCounter();
		countFolder=new AtomicCounter();
	}

	private void print() {
		for(Entry<String , Integer> ent:mapTestSetStatistics.entrySet())
		{
			System.out.println(ent.getKey()+" "+ent.getValue());
		}
	}
	public void readAndTagSentenceFromFolderAndFile_parallel() {
		int NUMBER_OF_THREADS = 5;
		try {
			executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
			final File[] listOfFolders = new File(DATA_SET_20NG).listFiles();
			Arrays.sort(listOfFolders);
			for (int i = 0; i < listOfFolders.length; i++) {
				final String folder = listOfFolders[i].getPath();
				final File[] listOfFiles = new File(folder).listFiles();
				Arrays.sort(listOfFiles);
				mapTestSetStatistics.put(listOfFolders[i].getName(), listOfFiles.length);
				for (int j = 0; j < listOfFiles.length; j++) {
					executor.submit(handle(listOfFiles[j]));
				}
				countFolder.increment();
				System.out.println("number of folder processed "+ countFolder.value());
			} 
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);StanfordNerRunner ner = new StanfordNerRunner();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private Runnable handle(File file) {
		return () -> {
			try {
				int countNERPerFile=0;
				BufferedReader br = new BufferedReader(new FileReader(file.getPath())); 
				String line=null;
				StanfordNerRunner ner = new StanfordNerRunner();
				while ((line = br.readLine()) != null) {
					List<Tuple> lst = new ArrayList<>(ner.run(line));
					countNERPerFile+=lst.size();
					if (lst.size()>0) {
						LOG.info(file.getName()+"\t"+line);

						for(Tuple tp : lst){
							LOG.info(tp.getA()+ "\t" +tp.getB());
						}
						LOG.info("");
					}
				}
				br.close();
				secondLOD.info(file.getPath()+"\t"+file.getName()+"\t"+countNERPerFile);
				countNERPerFile=0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			countFile.increment();
			System.out.println("number of file "+countFile.value()+" "+file.getPath()+" file is proccessed");
		};
	}

	public void readAndTagSentenceFromFolderAndFile() {


		StanfordNerRunner ner = new StanfordNerRunner();
		final File[] listOfFolders = new File(DATA_SET_20NG).listFiles();
		Arrays.sort(listOfFolders);
		for (int i = 0; i < listOfFolders.length; i++) {
			final long startTime = System.nanoTime();
			final String folder = listOfFolders[i].getPath();
			final File[] listOfFiles = new File(folder).listFiles();
			Arrays.sort(listOfFiles);
			mapTestSetStatistics.put(listOfFolders[i].getName(), listOfFiles.length);

			for (int j = 0; j < listOfFiles.length; j++) {
				int countNERPerFile=0;
				try (BufferedReader br = new BufferedReader(new FileReader(listOfFiles[j].getPath()))) {
					String line=null;
					while ((line = br.readLine()) != null) {
						List<Tuple> lst = new ArrayList<>(ner.run(line));
						countNERPerFile+=lst.size();
						if (lst.size()>0) {
							LOG.info(listOfFiles[j].getName()+"\t"+line);

							for(Tuple tp : lst){
								LOG.info(tp.getA()+ "\t" +tp.getB());
							}
							LOG.info("");
						}
					}
					secondLOD.info(listOfFiles[j].getName()+"\t"+countNERPerFile);
					countNERPerFile=0;
				} catch (Exception e) {
					e.printStackTrace();
				}	
				//countFile++;
				System.out.println("number of file "+countFile+" "+listOfFiles[j].getPath()+" file is proccessed");
			}
			//countFolder++;
			System.out.println("number of folder "+countFolder+" "+listOfFolders[i].getPath()+" folder is proccessed");
			final long duration = System.nanoTime() - startTime;
			System.out.println(duration/1000 + " took to process the folder");
		}
	}



}
