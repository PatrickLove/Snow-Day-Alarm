package patricklove.com.snowdayalarm.twitter.tweetAnalysis;

/**
 * Highest level word matching object<br>
 * A KeywordSet is evaluated by a call to isTriggered and is the basic matching element used to detect
 * special day types from tweets.  They function by grouping other KeywordSets and performing an AND or OR
 * operation on their evaluations.  Those sets can contain other sets until all evaluations come from
 * {@link Keyword Keywords} (or NotKeywords).  See {@link #MASTER_DELAY_FILTER} or {@link #MASTER_CANCEL_FILTER}
 * for an example of nested KeywordSet construction
 * 
 * @author Patrick Love
 *
 * @see NotKeywordSet
 * @see Keyword
 * @see NotKeyword
 */
public class KeywordSet {
	public static final boolean OR=true;
	public static final boolean AND=false;
	
	/**
	 * Defines the filter used to determine cancellations<p>
	 * Specifically anything which contains "closed" and "all", but does not contain "two hour" or "2 hour" will match<br>
	 * In particular the construction is the following:<br>
	 * <pre>
	 * 	new KeywordSet(
	 *		new KeywordSet[] {
	 *			new Keyword("closed"),
	 *			new Keyword("all"),
	 *			new NotKeywordSet(new String[] {"two hour", "2 hour"}, NotKeywordSet.NOR)
	 *		},
	 *	AND);
	 *</pre>
	 */
	public static final KeywordSet MASTER_CANCEL_FILTER = new KeywordSet(
			new KeywordSet[] {
					new Keyword("closed"),
					new Keyword("all"),
					new KeywordSet(new String[] {"two hour", "2 hour"}, OR).negate()
				},
			AND);
	/**
	 * Defines the filter used to determine delays<p>
	 * Specifically anything which contains any of "two hour delay", "open two hours late", "2 hour delay", and "open 2 hours late",
	 * while also containing "all" will match
	 * In particular the construction is the following:<br>
	 * <pre>
	 * 	new KeywordSet(
	 * 		new KeywordSet[] {
	 *			new KeywordSet(new String[] {"two hour delay", "open two hours late",
	 *						"2 hour delay", 	"open 2 hours late"}, KeywordSet.OR),
	 *			new Keyword("all")
	 *		},
	 *	KeywordSet.AND);
	 *</pre>
	 */
	public static final KeywordSet MASTER_DELAY_FILTER = new KeywordSet(
			new KeywordSet[] {
					new KeywordSet(new String[] {	"two hour delay", 	"open two hours late",
													"2 hour delay", 	"open 2 hours late"}, OR),
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
	
	public KeywordSet negate(){
		return new NotKeywordSet(keywords, opBool);
	}
	
	/**
	 * Keywords or KeywordSets which will be evaluated and then performs either and AND or OR operation on all results
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
