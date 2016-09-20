package payroll;

/*
 * Class for storing details about Admin users for the payroll system.
 */

public class PayrollUser
{
	private String fName;
	private String sName;
	private String alias;
	private String password;
	
	public PayrollUser(String f, String s, String a, String p)
	{
		this.fName=f;
		this.sName=s;
		this.alias=a;
		this.password=p;
	}
	
	public String getFName()
	{
		return this.fName;
	}
	public String getSName()
	{
		return this.sName;
	}
	public String getAlias()
	{
		return this.alias;
	}
	public String getPassword()
	{
		return this.password;
	}
}
