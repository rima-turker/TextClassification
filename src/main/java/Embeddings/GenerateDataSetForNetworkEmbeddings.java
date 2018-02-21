package Embeddings;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import util.AtomicCounter;
import util.Config;
import util.HtmlLink;
import util.Tuple;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ProcessDataset.PreDataset;
import ProcessDataset.StanfordNerRunner;
import it.unimi.dsi.fastutil.Hash;
import util.Document;

public class GenerateDataSetForNetworkEmbeddings {

	private final String FOLDER_WIKI_FILES = Config.getString("FOLDER_WIKI_FILES", "");
	private final String FILE_WIKI_TITLEANDENTITIES = Config.getString("FILE_WIKI_TITLEANDENTITIES", "");
	private final String FILE_WIKI_ENTITIES_NOTPROPER = Config.getString("FILE_WIKI_ENTITIES_NOTPROPER", "");
	private final String FILE_WIKI_DATA_LINE = Config.getString("FILE_WIKI_DATA_LINE", "");
	private static final Logger LOG = Logger.getLogger(PreDataset.class);
	static final Logger secondLOD = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");

	private static ExecutorService executor;
	private AtomicCounter countFile;
	private AtomicCounter countOfURLContainsNewLine;
	private AtomicCounter countContent;
	private AtomicCounter countNoEntityInabstract;

	public GenerateDataSetForNetworkEmbeddings() {
		countContent = new AtomicCounter();
		countOfURLContainsNewLine = new AtomicCounter();
		countNoEntityInabstract = new AtomicCounter();
	}

	public void removeLinesMatchingLinesAtSpecificPosition() {

		Set<String> hset = new HashSet<>();
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_WIKI_ENTITIES_NOTPROPER))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				hset.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("The size of the hashset is " + hset.size());

		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(FILE_WIKI_DATA_LINE), StandardCharsets.UTF_8);
			System.out.println("File size " + lines.size());
			for (int i = 0; i < lines.size(); i++) {
				final String line = lines.get(i);

				String[] strSpaceSplit = line.split(" ");
				String strpotantialTobeRemoved = strSpaceSplit[1];
				if (!hset.contains(strpotantialTobeRemoved)) {
					secondLOD.info(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("countNoEntityInabstract=" + countNoEntityInabstract.value());
	}

	public void generateDatasetForLineTitlesAndEntities() {

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(FILE_WIKI_TITLEANDENTITIES), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final List<Pattern> patterns = new ArrayList<>();

		final Set<String> unnecessaryChars = new HashSet<>();
		unnecessaryChars.add("HTTPS://");
		unnecessaryChars.add("HTTP://");
		unnecessaryChars.add("www.");
		unnecessaryChars.add("FTP://");
		for (String text : unnecessaryChars) {
			patterns.add(Pattern.compile("(?im)" + Pattern.quote(text)));
		}

		Set<String> hsetTitles = new HashSet<>();

		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);

			String[] strTabSplit = line.split("\t");
			String title = strTabSplit[0];
			isValidEntity(patterns, title);
			if (isValidEntity(patterns, title)) {
				hsetTitles.add(title);
			}
		}

		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);

			String[] strTabSplit = line.split("\t");
			String title = strTabSplit[0];

			if (strTabSplit.length > 1) {
				String[] URIs = strTabSplit[1].split(" ");
				for (int j = 0; j < URIs.length; j++) {
					if (hsetTitles.contains(URIs[j])) {
						resultLog.info(title + "\t" + URIs[j] + "\t"+"1");
					}
				}
			} else {
				// TODO Maybe you need to extract all the DBpedia files from the begining
				// System.out.println("Tab Split size is smaller then 1 so
				// no entitiy in its page, line="+line);
				countNoEntityInabstract.increment();
			}
		}
		System.out.println("countNoEntityInabstract=" + countNoEntityInabstract.value());
	}

	public boolean isValidEntity(final List<Pattern> patterns, String title) {
		Matcher matcher = null;
		for (Pattern pattern : patterns) {
			matcher = pattern.matcher(title);
			if (matcher.find()) {
				return false;
			}
		}
		return true;
	}

	public void readWikipediaExtracted_parallel() {
		int NUMBER_OF_THREADS = 50;
		try {
			executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
			final File[] listOfFolders = new File(FOLDER_WIKI_FILES).listFiles();
			Arrays.sort(listOfFolders);
			for (int i = 0; i < listOfFolders.length; i++) {
				final String folder = listOfFolders[i].getPath();
				System.out.println("inside readWikipediaExtracted_parallel() will process a folder " + folder);
				final File[] listOfFiles = new File(folder).listFiles();
				Arrays.sort(listOfFiles);
				for (int j = 0; j < listOfFiles.length; j++) {
					// System.out.println("inside
					// readWikipediaExtracted_parallel() will process a file
					// "+listOfFiles[j].getName() );
					executor.submit(handleFile(listOfFiles[j]));
				}
				// countFolder.increment();
				// System.out.println("number of folder processed "+
				// countFolder.value());
			}
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.out.println("Count of URL contains New Line " + countOfURLContainsNewLine.value());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Runnable handleFile(File file) {
		return () -> {

			// System.out.println(file.getPath()+ " is sent to process");
			List<Document> lstDocuments = new ArrayList<>(WikipediaFilesUtil.getDocuments(file.getPath()));
			for (int i = 0; i < lstDocuments.size(); i++) {
				countContent.increment();
				List<String> content = new ArrayList<>(lstDocuments.get(i).getContent());
				processContentOfAFile(content, lstDocuments.get(i).getTitle());
			}

			System.out.println(countContent.value() + " files processed.");

		};
	}

	private void processContentOfAFile(List<String> content, String title) {
		final HTMLLinkExtractor htmlLinkExtractor = new HTMLLinkExtractor();
		StringBuilder strBuild = new StringBuilder();
		for (int i = 0; i < content.size(); i++) {
			final Vector<HtmlLink> links = htmlLinkExtractor.grabHTMLLinks(content.get(i));
			for (final Iterator<?> iterator = links.iterator(); iterator.hasNext();) {
				final HtmlLink htmlLink = (HtmlLink) iterator.next();
				final String enUrl = htmlLink.getDecodedUrl();
				if (!enUrl.contains("\n")) {
					strBuild.append(enUrl + " ");
					// countOfURLContainsNewLine.increment();
					// System.out.println("URL contains /n "+enUrl);
				}

			}
		}
		secondLOD.info(title + "\t" + strBuild.toString());

	}

	public void generateTrainSetforEntityEntity() {
		final HTMLLinkExtractor htmlLinkExtractor = new HTMLLinkExtractor();
		try (final BufferedReader br = new BufferedReader(new FileReader(" "))) {
			String line;
			while ((line = br.readLine()) != null) {
				final Map<String, String> punctuationHelper = new HashMap<>();
				int offset = 0;
				StringBuilder resultLine = new StringBuilder(line);
				final Vector<HtmlLink> links = htmlLinkExtractor.grabHTMLLinks(line);
				for (final Iterator<?> iterator = links.iterator(); iterator.hasNext();) {
					final HtmlLink htmlLink = (HtmlLink) iterator.next();
					final String enUrl = htmlLink.getUrl();
					String finalEnUrl = "dbr:" + enUrl;
					// punctuationHelper.put(randomString, finalEnUrl);
				}

				resultLine = new StringBuilder(
						resultLine.toString().replaceAll("[^\\w\\s]", "").replaceAll("[\\d]", "").toLowerCase());
				String finalResultLine = new String(resultLine.toString());
				for (Entry<String, String> entry : punctuationHelper.entrySet()) {
					finalResultLine = finalResultLine.replace(entry.getKey(), entry.getValue());
				}

				finalResultLine = finalResultLine.toString().replaceAll(" +", " ").trim();
				if (finalResultLine.contains("dbr:")) {
					LOG.info(finalResultLine);
				}
				// System.err.println(line);
				// System.err.println(finalResultLine);
				// System.err.println("---------------------------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
