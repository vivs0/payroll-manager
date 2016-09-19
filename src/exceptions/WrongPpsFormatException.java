package exceptions;

/*
 * User must enter the PPS number of the employee in the specified format, 
 * if they do not do so, this exception is thrown to remind them of the expected format.
 */

public class WrongPpsFormatException extends ArithmeticException
{
	public WrongPpsFormatException()
	{
		super("Wrong Pps.\nCorrect format: 1234567A");
	}
}
