package payroll;

/*
 * Class for storing details about an employees Revenue details including their Tac Cut off Point, 
 * Tax Credits and PRSI class
 */

public class RevenueDetails
{
	private double cutOffPoint;
	private double taxCredit;
	private String prsiClass;
	
	public RevenueDetails(double cut, double tax, String prsi)
	{
		this.cutOffPoint=cut;
		this.taxCredit=tax;
		this.prsiClass=prsi;
	}
	
	public double getCutOffPoint()
	{
		return this.cutOffPoint;
	}
	public double getTaxCredit()
	{
		return this.taxCredit;
	}
	public String getPrsiClass()
	{
		return this.prsiClass;
	}
}
