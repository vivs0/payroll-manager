package payroll;

/*
 * Class for storing the totals calculated for each employee
 */

public class TotalPay
{
	private int emp_no;
	private double totalGrossPay;
	private double totalNetPay;
	private double totalTaxPaid;
	private double totalPrsiPaid;
	private double totalUscPaid;
	
	public TotalPay(Employee e, double gross, double net, double tax, double prsi, double usc)
	{
		this.emp_no=e.getEmpNo();
		this.totalGrossPay=gross;
		this.totalNetPay=net;
		this.totalTaxPaid=tax;
		this.totalPrsiPaid=prsi;
		this.totalUscPaid=usc;
	}	
}
