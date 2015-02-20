package patricklove.com.snowdayalarm.twitter;

/**
 * Same as {@link Keyword} except negated (evaluates true if the string does not contain the word)
 * @author Patrick Love
 *
 * @see KeywordSet
 * @see NotKeywordSet
 * @see Keyword
 */
public class NotKeyword extends Keyword {

	public NotKeyword(String s) {
		super(s);
	}

	@Override
	public boolean isTriggered(String s) {
		return !super.isTriggered(s);
	}

	
	
}
