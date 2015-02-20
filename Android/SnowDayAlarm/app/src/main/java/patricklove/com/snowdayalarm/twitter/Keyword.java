package patricklove.com.snowdayalarm.twitter;

/**
 * Lowest level KeywordSet child<br>
 * Contains only one word and will be triggered for any string s such that<br>
 * {@code s.contains(word)} case insensitive
 * @author Patrick Love
 *
 * @see KeywordSet
 * @see NotKeywordSet
 * @see NotKeyword
 */
public class Keyword extends KeywordSet {

	/**
	 * Word matched by this Keyword
	 */
	private String word;

	public Keyword(String s){
		super(new KeywordSet[0], true);
		this.word = s;
	}
	
	public KeywordSet negate(){
		return new NotKeyword(word);
	}
	
	@Override
	public boolean isTriggered(String s) {
		return s.toLowerCase().contains(word.toLowerCase());
	}

	/**
	 * Converts a string array to an array of keywords of those strings
	 * @param words String array of keywords
	 * @return Array of associated Keyword objects
	 */
	public static KeywordSet[] convertArray(String[] words) {
		KeywordSet[] converted = new KeywordSet[words.length];
		for(int i = 0; i < words.length; i++){
			converted[i] = new Keyword(words[i]);
		}
		return converted;
	}

}
