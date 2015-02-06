package twitter;

public class KeywordSet {
	public static final boolean OR=true;
	public static final boolean AND=false;
	
	
	public static final KeywordSet masterFilter = new KeywordSet(
			new KeywordSet[] {
					new KeywordSet(new String[] {"are closed", "will be closed"}, OR),
					new Keyword("all")
				},
			AND);
	
	
	public KeywordSet(KeywordSet[] words, boolean op){
		this.keywords = words;
		this.opBool = op;
	}
	public KeywordSet(String[] words, boolean op){
		this.keywords = Keyword.convertArray(words);
		this.opBool = op;
	}
	
	/**
	 * Keywords or KeywordSets which will be checked and 
	 */
	private KeywordSet[] keywords;
	
	/**
	 * Boolean which defines whether this set is ANDed or ORed<br>
	 * 
	 * true = or;<br>
	 * false = and;
	 * 
	 * See {@link #isTriggered} for the explanation of this
	 * 
	 * @see #isTriggered(String)
	 */
	private boolean opBool;
	
	/**
	 * Determines if a given string should be flagged as a snow day based on this KeywordSet<p>
	 * To do so, it iterates through {@link #keywords} and calls {@code isTriggered()} on each.  If one returns
	 * {@code opBool}, then {@code opBool} is return, otherwise it returns {@code !opBool}<br>
	 * This has the effect of running an AND operation when {@code opBool = false} (fail immediately once one is false, otherwise true)
	 * and an OR operation if it is true (return true immediately once one is true, otherwise false).
	 * @param s The string to analyze
	 * @return Whether a match was found for this KeywordSet in the string
	 * 
	 * @see #opBool
	 */
	public boolean isTriggered(String s){
		for(KeywordSet set : keywords){
			if(set.isTriggered(s) == opBool){
				return opBool;
			}
		}
		return !opBool;
	}
}