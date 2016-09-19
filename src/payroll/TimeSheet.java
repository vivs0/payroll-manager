package payroll;

/*
 * Class for storing details entered about an employees weekly timesheet. Includes methods
 * to return the values for each variable. Data can only be set in the constructor and may
 * not be edited later on.
 */

public class TimeSheet
{
	private int empNo;
	private int weekNo;
	private int year;
	private String monday;
	private String tuesday;
	private String wednsday;
	private String thursday;
	private String friday;
	private String saturday;
	private String sunday;
	private double totHours;
	private Employee e;
	
	public TimeSheet(Employee e, int week, int year,
						String mon, 
						String tue,
						String wed,
						String thu,
						String fri,
						String sat,
						String sun,
						double total)
	{
		this.empNo=e.getEmpNo();
		this.weekNo=week;
		this.year=year;
		this.monday=mon;
		this.tuesday=tue;
		this.wednsday=wed;
		this.thursday=thu;
		this.friday=fri;
		this.saturday=sat;
		this.sunday=sun;
		this.totHours=total;
	}
	
	public int getEmpNo()
	{
		return this.empNo;
	}
	public int getWeekNo()
	{
		return this.weekNo;
	}
	public int getYear()
	{
		return this.year;
	}
	public String getMon()
	{
		return this.monday;
	}
	public String getTue()
	{
		return this.tuesday;
	}
	public String getWed()
	{
		return this.wednsday;
	}
	public String getThu()
	{
		return this.thursday;
	}
	public String getFri()
	{
		return this.friday;
	}
	public String getSat()
	{
		return this.saturday;
	}
	public String getSun()
	{
		return this.sunday;
	}
	public double getTotHours()
	{
		return this.totHours;
	}
	public String toString()
	{
		String s = 	"\nYear " + this.year + "\tWeek no\t" + this.weekNo
					+ "\n_____________________________________"
					+ "\nMonday\t\t" + this.monday
					+ "\nTuesday\t\t" + this.tuesday
					+ "\nWednsday\t\t" + this.wednsday
					+ "\nThursday\t\t" + this.thursday
					+ "\nFriday\t\t" + this.friday
					+ "\nSaturday\t\t" + this.saturday
					+ "\nSunday\t\t" + this.sunday
					+ "\nTotal hours worked\t" +this.totHours;
		return s;
	}
}
