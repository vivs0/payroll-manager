package payroll;

/*
 * A Subclass of Employee for storing employees who recieve an annual salary rather than 
 * an hourly rate.
 */

public final class SalariedEmployee extends Employee
{
	private double salary;

	public SalariedEmployee(String sn,String fn, String addL1, String addL2, String addL3, String email, String phone,
							String dob, String pps, String sDate,String fDate, String dept,String pType, double salary)
	{
		super(sn,fn,addL1, addL2, addL3 , email, phone, dob,pps, sDate, fDate, dept, pType);
		this.salary=salary;
	}
	public SalariedEmployee(int no,String sn,String fn, String addL1, String addL2, String addL3, String email, String phone,
							String dob, String pps, String sDate, String fDate, String dept,String pType, double salary)
	{
		super(no,sn,fn,addL1,addL2, addL3,email, phone, dob,pps, sDate, fDate, dept, pType);
		this.salary=salary;
	}
	
	public double calcGrossPay(double extra)
	{
		return this.getSalary() + extra;
	}
	
	public double getSalary()
	{
		return this.salary;
	}
	public void setSalary(double newSalary)
	{
		this.salary=newSalary;
	}
}
