package exceptions;

/*
 * If there is a limit to the number of characters for a field to be stored in the database
 * this exception can be thrown to ensure database integrity and have the user reenter
 * a value of an acceptable length.
 */

public class InputLengthExceededException extends ArithmeticException
{
	public InputLengthExceededException(int length, String field)
	{
		super("Value too long.\n Maximum length of " + field 
				+ " field is " + length + " characters.");
	}

}
