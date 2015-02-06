package twitter;

public class Keyword extends KeywordSet{

	private String word;

	public Keyword(String s){
		super(new KeywordSet[0], true);
		this.word = s;
	}
	
	@Override
	public boolean isTriggered(String s) {
		return s.toLowerCase().contains(word.toLowerCase());
	}

	public static KeywordSet[] convertArray(String[] words) {
		KeywordSet[] converted = new KeywordSet[words.length];
		for(int i = 0; i < words.length; i++){
			converted[i] = new Keyword(words[i]);
		}
		return converted;
	}

}
