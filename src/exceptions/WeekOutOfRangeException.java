package exceptions;

/*
 * If the user tries search for a week outside the range of 1 to 52, this exception is thrown.
 * week 53 is week 1 of the following year.
 */

public class WeekOutOfRangeException extends ArithmeticException
{
	public WeekOutOfRangeException()
	{
		super("Week no has to be between 1 and 52");
	}
}
