package Embeddings;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.HtmlLink;


public class HTMLLinkExtractor {

	private Pattern patternTag, patternLink;
	private Matcher matcherTag, matcherLink;

	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	private static final String HTML_A_HREF_TAG_PATTERN =
			"\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";


	public HTMLLinkExtractor() {
		patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
		patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
	}

	public static String cleanAnchorTexts(String sentenceString) {
		return sentenceString.replaceAll("<[^>]*>", "");
	}
	/**
	 * Validate html with regular expression
	 *
	 * @param html
	 *            html content for validation
	 * @return Vector links and link text
	 */
	public Vector<HtmlLink> grabHTMLLinks(final String sentenceString) {
		Vector<HtmlLink> result = new Vector<HtmlLink>();
		matcherTag = patternTag.matcher(sentenceString);

		final String sentenceWithoutHtmlTag = sentenceString.replaceAll("<[^>]*>", "");
		
		while (matcherTag.find()) {

			String href = matcherTag.group(1);
			String linkText = matcherTag.group(2);
			matcherLink = patternLink.matcher(href);

			
			while (matcherLink.find()) {

				String link = matcherLink.group(1); // link
				HtmlLink obj = new HtmlLink();
				obj.setUrl(link);
				obj.setAnchorText(linkText);
				obj.setFullSentence(sentenceWithoutHtmlTag);
				obj.setPostion(matcherTag.start(),matcherTag.end());
				result.add(obj);
			}
		}
		return result;
	}
	
	public static String removeAHrefTags(String text) {
		return text.replaceAll("<a.*?>", "").replaceAll("</a>", "");
	}
}


