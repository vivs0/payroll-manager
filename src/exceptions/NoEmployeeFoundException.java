package exceptions;

/*
 * If the user tries to search the database for an employee where the database is empty
 * this exception is thrown.
 */

public class NoEmployeeFoundException extends ArithmeticException
{
	public NoEmployeeFoundException()
	{
		super("No employees in database");
	}
}
