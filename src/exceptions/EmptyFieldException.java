package exceptions;

/* 
 * If all fields are required to be filled in by the user, this exception can be thrown to
 * ensure the user does so.
*/
public class EmptyFieldException extends ArithmeticException
{
	public EmptyFieldException()
	{
		super("All fields must be filled in.");
	}
}
