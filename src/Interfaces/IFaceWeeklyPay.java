package Interfaces;

import payroll.Employee;
import payroll.WeeklyPay;

public interface IFaceWeeklyPay {
	public void insertWeeklyPay(WeeklyPay wp);
	public WeeklyPay getWeeklyPay(Employee emp, int week, int year);

}
