package twitter;

public class NotKeyword extends Keyword {

	public NotKeyword(String s) {
		super(s);
	}

	@Override
	public boolean isTriggered(String s) {
		return !super.isTriggered(s);
	}

	
	
}
