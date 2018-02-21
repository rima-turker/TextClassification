package ProcessDataset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.Token;
import util.Tuple;


public class StanfordNerRunner {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(55);
		
		for(int i=1;i<2000;i++){
			final int j=i;
			es.submit(()->{
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				new StanfordNerRunner().run(""); 
				new StanfordNerRunner().run("");
				System.err.println(j);
				});
		}
		es.shutdown();
		es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);StanfordNerRunner ner = new StanfordNerRunner();
	}

	public List<Tuple> run(String noTaggedLine) {
		List<Tuple> result = new ArrayList<>();
		try {
			final Process p = new ProcessBuilder("curl","--data",noTaggedLine,"http://10.10.4.10:9000/?properties={%22annotators%22%3A%22tokenize%2Cssplit%2Cpos%2Cner%22%2C%22outputFormat%22%3A%22xml%22}").start();			
			p.waitFor();
			final BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream())); 
			String s;
			StringBuilder outputXml = new StringBuilder();
			while ((s = stdInput.readLine()) != null) {
				outputXml.append(s);
			}
			if(outputXml == null || outputXml.toString().isEmpty()){
				System.out.println(noTaggedLine);
				throw new IllegalArgumentException("outputXml can not be null or empty");
			}
			List<Token> nerXmlParser = nerXmlParser(outputXml.toString());
			nerXmlParser = aggregateNerTagPositions(nerXmlParser);
			
			nerXmlParser.forEach(x -> {
				if(x.getContent().get("NER").equals("PERSON")||x.getContent().get("NER").equals("LOCATION")||x.getContent().get("NER").equals("ORGANIZATION")){
					result.add(new Tuple(x.getContent().get("word"),x.getContent().get("NER")));
				}
			});

		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Token> aggregateNerTagPositions(final List<Token> tokens) {
		final List<Token> result = new ArrayList<>();
		for (int i = 0; i < tokens.size(); i++) {
			final Token t = tokens.get(i);
			final String possibleNerTag = t.getContent().get("NER");
			if (possibleNerTag != null) {
				Token newToken = new Token();
				newToken.getContent().putAll(t.getContent());
				for (int j = i + 1; j < tokens.size(); j++, i++) {
					final Token tt = tokens.get(j);
					final String possibleNerTag2 = tt.getContent().get("NER");
					if (possibleNerTag2 != null 
							&& possibleNerTag2.equals(possibleNerTag)) {
						newToken.getContent().put("word",
								newToken.getContent().get("word") + " " + tt.getContent().get("word"));
						newToken.getContent().put("CharacterOffsetEnd", tt.getContent().get("CharacterOffsetEnd"));
						newToken.getContent().put("POS", null);
						newToken.getContent().put("lemma", null);
					} else {
						break;
					}
				}
				newToken.getContent().put("NER", possibleNerTag);
				result.add(newToken);
			}else {
				result.add(t);
			}

		}
		return result;
	}

	public  List<Token> nerXmlParser(final String xml) {
		try {
			if(xml == null || xml.isEmpty()) {
				throw new IllegalArgumentException("XML can not be null");
			}
			final List<Token> result = new ArrayList<>();
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			final org.w3c.dom.Document document = docBuilder
					.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

			final NodeList nodeList = document.getElementsByTagName("*");
			int wordCounter = 0;
			for (int i = 0; i < nodeList.getLength(); i++) {
				final Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("token")) {
					if (node.hasChildNodes()) {
						Token t = new Token();

						for (int j = 0; j < node.getChildNodes().getLength(); j++) {
							final Node childNode = node.getChildNodes().item(j);
							if (childNode.getNodeType() == Node.ELEMENT_NODE) {
								t.getContent().put(childNode.getNodeName(), childNode.getTextContent());
							}
						}
						t.getContent().put("ID", String.valueOf(wordCounter++));
						result.add(t);
					}
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
