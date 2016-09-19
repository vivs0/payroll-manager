package exceptions;

/*
 * Exception is thrown when the username or password entered by the user does not match
 * any of the username/password combinations stored in the database.
 */

public class InvalidLoginException extends ArithmeticException
{
	public InvalidLoginException()
	{
		super("Invalid username or password.");
	}
}
