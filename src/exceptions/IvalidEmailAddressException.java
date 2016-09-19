package exceptions;

/*
 * Exception is thrown to ensure an employees email address is correct and contains the @ symbol.
 */

public class IvalidEmailAddressException extends ArithmeticException
{
	public IvalidEmailAddressException()
	{
		super("Invalid email address.");
	}
}
