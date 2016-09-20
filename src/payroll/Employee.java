package payroll;

/*
 * Class for storing variables relating to Employees, includes methods for
 * setting all variables of an existing employee, a constructor for creating a 
 * new employee and getter methods for returning the current value of any variable.
 * a Method for calculating the amount of tax to be deducted from the employees wage,
 * a method to calculate the amount of PRSI to be deducted from their wage and
 * a method to calculate the Universal Social Charge to be applied.
 */

public abstract class Employee implements PayrollInterface
{
	private int empNo;
	private String surname;
	private String firstName;
	private String dob;
	private String ppsNo;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String email;
	private String phone;
	private String startDate;
	private String finishDate;
	private String department;
	private String paymentType;
	
	public Employee(String sn,String fn, String addL1,String addL2, String addL3, String email, String phone,
					String dob, String pps, String sDate, String fDate, String dept, String pType)
	{
		this.surname=sn;
		this.firstName=fn;
		this.addressLine1=addL1;
		this.addressLine2=addL2;
		this.addressLine3=addL3;
		this.email=email;
		this.phone=phone;
		this.dob=dob;
		this.ppsNo=pps;
		this.startDate=sDate;
		this.finishDate=fDate;
		this.department=dept;
		this.paymentType=pType;
	}
	
	public Employee(int no,String sn,String fn, String addL1, String addL2, String addL3, String email, String phone,
					String dob, String pps, String sDate, String fDate, String dept, String pType)
	{
		this.empNo=no;
		this.surname=sn;
		this.firstName=fn;
		this.addressLine1=addL1;
		this.addressLine2=addL2;
		this.addressLine3=addL3;
		this.email=email;
		this.phone=phone;
		this.dob=dob;
		this.ppsNo=pps;
		this.startDate=sDate;
		this.finishDate=fDate;
		this.department=dept;
		this.paymentType=pType;
	}
	
	public int getEmpNo()
	{
		return this.empNo;
	}
	public String getSurname()
	{
		return this.surname;
	}
	public void setSurname(String newSurname)
	{
		this.surname=newSurname;
	}
	public String getFirstName()
	{
		return this.firstName;
	}
	public void setFirstName(String newFName)
	{
		this.firstName=newFName;
	}
	public String getDob()
	{
		return this.dob;
	}
	public void setDob(String newDob)
	{
		this.dob=newDob;
	}
	public String getPps()
	{
		return this.ppsNo;
	}
	public void setPps(String newPps)
	{
		this.ppsNo=newPps;
	}
	public String getAddressL1()
	{
		return this.addressLine1;
	}
	public void setAddressL1(String newAdd)
	{
		this.addressLine1=newAdd;
	}
	public String getAddressL2()
	{
		return this.addressLine2;
	}
	public void setAddressL2(String newAdd)
	{
		this.addressLine2=newAdd;
	}
	public String getAddressL3()
	{
		return this.addressLine3;
	}
	public void setAddressL3(String newAdd)
	{
		this.addressLine3=newAdd;
	}
	public void setEmail(String email)
	{
		this.email=email;
	}
	public String getEmail()
	{
		return this.email;
	}
	public void setPhone(String phone)
	{
		this.phone=phone;
	}
	public String getPhone()
	{
		return this.phone;
	}
	public String getStartDate()
	{
		return this.startDate;
	}
	public void setStartDate(String sDate)
	{
		this.startDate=sDate;
	}
	public String getFinishDate()
	{
		return this.finishDate;
	}
	public void setFinishDate(String fDate)
	{
		this.finishDate=fDate;
	}
	public void setDepartment(String dept)
	{
		this.department=dept;
	}
	public String getDepartment()
	{
		return this.department;
	}
	public void setPaymentType(String type)
	{
		this.paymentType=type;
	}
	public String getPaymentType()
	{
		return this.paymentType;
	}
	
	public double calcTax(double pay, double cutOff, double credit)
	{
		double tax=0.0;
		if(pay<=cutOff)
		{
			tax=pay*LOWER_TAX_RATE;
			if(tax<=credit)
			{
				tax=0.0;
			}
			else
			{
				tax-=credit;
			}
		}
		else
		{
			tax=((cutOff*LOWER_TAX_RATE)+((pay-cutOff)*HIGHER_TAX_RATE))-credit;
		}
		return Math.round(tax);
	}
	
	public double calcPrsi(double pay)
	{
		double prsi=0.0;
		if(pay>352)
		{
			prsi=(pay-127)*0.04;
		}
		return Math.round(prsi);
	}
	
	public double calcUsc(double pay)
	{
		double usc=0.0;
		double annualPay=pay*52;
		
		if(annualPay>USCLevel2)
		{
			usc=(USCLevel1*USC1)+((USCLevel2-USCLevel1)*USC2)+((annualPay-USCLevel2)*USC3);
		}
		else if(annualPay>USCLevel1 && annualPay<=USCLevel2)
		{
			usc=(USCLevel1*USC1)+((annualPay-USCLevel1)*USC2);
		}
		return Math.round(usc/52);
	}
	
	public  String toString()
	{
		String s = "\n\nFirst name:\t\t" + this.firstName
					+"\nLast name:\t\t"+ this.surname
					+"\nPps no:\t\t" + this.ppsNo
					+"\nDate of birth:\t\t" + this.dob
					+"\nAddress:\t\t" + this.addressLine1
					+"\n\t\t" + this.addressLine2
					+"\n\t\t" + this.addressLine3
					+"\n\nStart date:\t\t" + this.startDate
					+"\nFinish date:\t\t" + this.finishDate;
		
		return s;
	}
}
