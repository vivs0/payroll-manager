package Interfaces;

import payroll.Employee;
import payroll.TimeSheet;

public interface IFaceTimeSheet {
	public void insertTimesheet(TimeSheet ts);
	public TimeSheet getTimesheet(Employee emp, int week, int year);
	}
