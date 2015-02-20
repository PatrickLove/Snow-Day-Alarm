package patricklove.com.snowdayalarm.twitter;

/**
 * Same as {@link KeywordSet} except negated (evaluates true if the associated KeywordSet would not match)
 * @author Patrick Love
 *
 * @see KeywordSet
 * @see Keyword
 * @see NotKeyword
 */
public class NotKeywordSet extends KeywordSet {
	public static final boolean NOR=true;
	public static final boolean NAND=false;
	
	public NotKeywordSet(String[] words, boolean op) {
		super(words, op);
	}
	
	public NotKeywordSet(KeywordSet[] sets, boolean op) {
		super(sets, op);
	}

	@Override
	public boolean isTriggered(String s) {
		// TODO Auto-generated method stub
		return !super.isTriggered(s);
	}
}
