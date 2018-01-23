package Preprocess;

import java.util.ArrayList;
import java.util.List;

import util.Tuple;

public class PipeLine {
	
	public static void main(String[] args) {
		String sentence = "He was born in Germany. His name is Albert. He is the owner of Google";
		AnalyseDataset ana = new AnalyseDataset();
		List<Tuple> listTagged = new ArrayList<Tuple>(ana.tagSentence(sentence));
		for(Tuple str: listTagged){
			System.out.println(str.getA_mention()+ " " +str.getB_link());
		}
	}

	private static void initilaze() {
		
	}

}
