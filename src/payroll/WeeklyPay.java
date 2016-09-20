package payroll;

public class WeeklyPay
{
	private int empNo;
	private int weekNo;
	private int year;
	private double grossPay;
	private double netPay;
	private double taxPaid;
	private double prsiPaid;
	private double uscPaid;
	private Employee e;
	
	public WeeklyPay(Employee e, int week, int year,double gross, double net, double tax, double prsi, double usc)
	{
		this.empNo=e.getEmpNo();
		this.weekNo=week;
		this.year=year;
		this.grossPay=gross;
		this.netPay=net;
		this.taxPaid=tax;
		this.prsiPaid=prsi;
		this.uscPaid=usc;
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
	public void setGrossPay(double gPay)
	{
		this.grossPay=gPay;
	}
	public double getGrossPay()
	{
		return this.grossPay;
	}
	public void setNetPay(double nPay)
	{
		this.netPay=nPay;
	}
	public double getNetPay()
	{
		return this.netPay;
	}
	public void setTaxPaid(double tax)
	{
		this.taxPaid=tax;
	}
	public double getTaxPaid()
	{
		return this.taxPaid;
	}
	public void setPrsiPaid(double prsi)
	{
		this.prsiPaid=prsi;
	}
	public double getPrsiPaid()
	{
		return this.prsiPaid;
	}
	public void setUscPaid(double usc)
	{
		this.uscPaid=usc;
	}
	public double getUscPaid()
	{
		return this.uscPaid;
	}
	public String toString()
	{
		String s = "\n\nGross pay:\t\t" + this.grossPay
					+ "\nNet pay:\t\t" + this.netPay
					+ "\nPAYE:\t\t" + this.taxPaid
					+ "\nPRSI:\t\t" + this.prsiPaid
					+ "\nUSC:\t\t" + this.uscPaid;
		return s;
	}
}
