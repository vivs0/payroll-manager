package exceptions;

public class UpdatingEmployeeException extends ArrayIndexOutOfBoundsException
{
	public UpdatingEmployeeException()
	{
		super("You have updated employee");
	}
}
