package exceptions;

/*
 * No tables or entries exist until a company is created, the user cannot continue to use
 * the functionality of the program without first creating a program. If the user tries to
 * do so, this exception is thrown.
 */

public class NoCompanyFoundException extends ArithmeticException
{
	public NoCompanyFoundException()
	{
		super("No company found. Add new company first.");
	}
}
