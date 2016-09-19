package Interfaces;

import payroll.Employee;

public interface IFaceEmployeeDetail {
	public void updateEmpDetails(Employee emp, double cutOff, double taxCredit, String prsi,
			String bankName, String accountNo, String sortCode);
	public void insertEmployee(Employee emp);
	}
