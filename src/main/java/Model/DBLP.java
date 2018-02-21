package Model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class DBLP {
	private List<String> authors;
	private List<String> references;
	private String title;
	private String venue;
	private int year;
	private String id;

	public String toJson() {
		final Gson gson = new Gson();
        return gson.toJson(this);
	}
	public String toPrettyJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final JsonParser jp = new JsonParser();
        final JsonElement je = jp.parse(this.toJson());
        return gson.toJson(je);
    }
	public static DBLP fromJson(String jsonString) {
        final Gson gson = new Gson();
        return gson.fromJson(jsonString, DBLP.class);
    }
	
	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
