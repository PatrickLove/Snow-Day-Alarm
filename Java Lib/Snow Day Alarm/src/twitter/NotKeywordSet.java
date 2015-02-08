package twitter;

public class NotKeywordSet extends KeywordSet {

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
