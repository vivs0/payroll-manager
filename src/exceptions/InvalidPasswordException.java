package exceptions;

public class InvalidPasswordException extends ArithmeticException
{
	public InvalidPasswordException()
	{
		super("Invalid password.");
	}
}
