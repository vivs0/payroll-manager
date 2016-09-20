package payroll;

/*
 * Class for storing variables relating to the Company, includes methods for
 * setting all variables of an existing company, a constructor for creating a 
 * new company and getter methods for returning the current value of any variable.
 */

public class Company
{
	private String companyName;
	private String compRegNo;
	private int startWeekNo;
	private int year;
	
	public Company(String name, String reg, int week, int year)
	{
		this.companyName=name;
		this.compRegNo=reg;
		this.startWeekNo=week;
		this.year=year;
	}
	
	public void setCompName(String name)
	{
		this.companyName=name;
	}
	public String getCompName()
	{
		return this.companyName;
	}
	public void setCompRegNo(String reg)
	{
		this.compRegNo=reg;
	}
	public String getCompRegNo()
	{
		return this.compRegNo;
	}
	public void setStartWeek(int week)
	{
		this.startWeekNo=week;
	}
	public int getStartWeek()
	{
		return this.startWeekNo;
	}
	public void setYear(int year)
	{
		this.year=year;
	}
	public int getYear()
	{
		return this.year;
	}
	public String toString()
	{
		String s = "\nCompany name:\t" + this.companyName
					+"\nRegistration no:\t" + this.compRegNo
					+"\nYear: \t\t" + this.year;
		return s;
	}
}
