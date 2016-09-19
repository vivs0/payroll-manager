package exceptions;

/*
 * User must enter the date in the specified format, if they do not do so, this exception is thrown
 * to remind them of the expected format
 */

public class WrongDateFormatException extends ArithmeticException
{
	public WrongDateFormatException()
	{
		super("Wrong date.\nCorrect format: dd/mm/yyyy");
	}

}
