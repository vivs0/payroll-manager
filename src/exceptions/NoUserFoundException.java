package exceptions;

public class NoUserFoundException extends ArithmeticException
{
	public NoUserFoundException()
	{
		super("No user found. Try again.");
	}

}
