package payroll;

/*
 * Class to control the connection to the database.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;


public class DbConn
{
	private Connection conn;
	private ResultSet rs;
	private ResultSet tsRs;
	private ResultSet wpRs;
	private PreparedStatement ps;
	static int TOT_EMPLOYEE;
	
	/*
	 * Reusable method which establishes a connection to the database for
	 * use in other methods.
	 */
	private void connect()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:D:\\experience Project\\sqlite-dll-win32-x86-3140200\\sqlite-tools-win32-x86-3140200\\test.db");
		    System.out.println("Connected");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Reusable method to close an open connection to the database for
	 * use in other methods
	 */
	public void disconnect()
	{
		try
		{
			conn.close();
			System.out.println("Connection closed");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method search by user alias return an instance of PayrollUser 
	 * with that alias.
	 */
	public PayrollUser getPayrollUser(String alias)
	{
		PayrollUser user=null;
		String queryString = "select * from payroll_user where user_alias=?";
		try
		{
			connect();
			ps = conn.prepareStatement(queryString,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, alias);
			rs = ps.executeQuery();
			
			if(rs.next()) //If a user with the specified alias exits
			{
				//Create a user using the details of that row.
				user = new PayrollUser(rs.getString("user_fname"),
													rs.getString("user_lname"),
													rs.getString("user_alias"),
													rs.getString("user_password"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			disconnect();
			JOptionPane.showMessageDialog(null, "Unexpected error. System will close.");
			System.exit(0);
		}
		return user; //return the created PayrollUser
	}

	/*
	 * Method for returning a scrollable and updateable Resultset
	 * with all companies stored in the database
	 */
	public ResultSet getCompany()
	{
		try
		{
			connect();
			rs=null;
			ps = conn.prepareStatement("select * from company",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return rs;
		}
		return rs;
	}
	
	/*
	 * Method to search for all details related to a specified users name held in the database
	 * The method returns a resultset which is ordered by surname if more than one user with
	 * that name is found.
	 */
	public ResultSet getSearchEmp(String name)
	{
		try
		{
			String queryString = "select * from employee, revdetails, bankdetails " +
									"where employee.emp_no=revdetails.emp_no " +
									"and employee.emp_no=bankdetails.emp_no and employee.emp_surname like '%"+name+ "%' order by emp_surname";
			
			ps = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			//ps.setString(1, name);
			rs = ps.executeQuery();
		}
		catch(Exception e)
		{
			disconnect();
			JOptionPane.showMessageDialog(null, "Unexpected error. System will close.");
			System.exit(0);
			e.printStackTrace();
		}
		return rs;
	}
	
	/*
	 * Method to return a resultset containing all information stored about all employees
	 * in the database and to set the static variable TOT_EMPLOYEE to the total number of
	 * employees in the databse at the time of calling the method.
	 */
	public ResultSet getAllEmployees()
	{
		try
		{
			String queryString = "select * from employee, revdetails, bankdetails " +
									"where employee.emp_no=revdetails.emp_no " +
									"and employee.emp_no=bankdetails.emp_no order by emp_surname";
			
			ps = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = ps.executeQuery();
			rs.last();
			TOT_EMPLOYEE=rs.getRow();
			rs.beforeFirst();
		}
		catch(Exception e)
		{
			disconnect();
			JOptionPane.showMessageDialog(null, "Unexpected error. System will close.");
			System.exit(0);
			e.printStackTrace();
		}
		return rs;
	}
	
	/*
	 * 
	 */
	public void insertTimesheet(TimeSheet ts)
	{
		String insertString = "insert into timesheet values(?,?,?,?,?,?,?,?,?,?,?)";
		try
		{
			System.out.println("Inserting timesheet...");
			ps = conn.prepareStatement(insertString);
			ps.setInt(1, ts.getEmpNo());
			ps.setInt(2, ts.getWeekNo());
			ps.setInt(3, ts.getYear());
			ps.setString(4, ts.getMon());
			ps.setString(5, ts.getTue());
			ps.setString(6, ts.getWed());
			ps.setString(7, ts.getThu());
			ps.setString(8, ts.getFri());
			ps.setString(9, ts.getSat());
			ps.setString(10, ts.getSun());
			ps.setDouble(11, ts.getTotHours());
			ps.executeUpdate();
			System.out.println("Timesheet inserted");
			
			ps = conn.prepareStatement("update company set start_week=?, start_year=?");
			ps.setInt(1, ts.getWeekNo());
			ps.setInt(2, ts.getYear());
			ps.executeUpdate();
			System.out.println("Start week changed to " + ts.getWeekNo());
			System.out.println("Start year changed to " + ts.getYear());
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to return a timesheet object for the specified employee for the specified week and year
	 */
	public TimeSheet getTimesheet(Employee emp, int week, int year)
	{
		TimeSheet ts = null;
		String queryString = "select * from timesheet where emp_no=? and week_no=? and year=?";
		try
		{
			System.out.println("Retriving timesheet...");
			ps = conn.prepareStatement(queryString,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ps.setInt(1, emp.getEmpNo());
			ps.setInt(2, week);
			ps.setInt(3, year);
			tsRs = ps.executeQuery();
			while(tsRs.next())
			{
				ts = new TimeSheet(emp, tsRs.getInt("week_no"),tsRs.getInt("year"),
									tsRs.getString("monday"),
									tsRs.getString("tuesday"),
									tsRs.getString("wednsday"),
									tsRs.getString("thursday"),
									tsRs.getString("friday"),
									tsRs.getString("saturday"),
									tsRs.getString("sunday"),
									tsRs.getDouble("tot_hours"));
			}
			System.out.println("Timesheet retrived.");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return ts;
	}
	
	/*
	 * Method to update all totals fields in the database.
	 */
	public void insertTotalPay(WeeklyPay wp)
	{
		try
		{
			ps = conn.prepareStatement("update totalpay set tot_gross_pay=tot_gross_pay+?,"
										+"tot_net_pay=tot_net_pay+?,"
										+"tot_tax_paid=tot_tax_paid+?,"
										+"tot_prsi_paid=tot_prsi_paid+?,"
										+"tot_usc_paid=tot_usc_paid+? where emp_no=?");
			
			ps.setDouble(1, wp.getGrossPay());
			ps.setDouble(2, wp.getNetPay());
			ps.setDouble(3, wp.getTaxPaid());
			ps.setDouble(4, wp.getPrsiPaid());
			ps.setDouble(5, wp.getUscPaid());
			ps.setInt(6, wp.getEmpNo());
			ps.executeUpdate();
			System.out.println("TotalPay updated");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to fill values for the weekly pay and associated fields.
	 */
	public void insertWeeklyPay(WeeklyPay wp)
	{
		String insertString = "insert into weeklypay values(?,?,?,?,?,?,?,?)";
		try
		{
			System.out.println("Inserting weeklypay...");
			ps = conn.prepareStatement(insertString);
			ps.setInt(1, wp.getEmpNo());
			ps.setInt(2, wp.getWeekNo());
			ps.setInt(3, wp.getYear());
			ps.setDouble(4, wp.getGrossPay());
			ps.setDouble(5, wp.getNetPay());
			ps.setDouble(6, wp.getTaxPaid());
			ps.setDouble(7, wp.getPrsiPaid());
			ps.setDouble(8, wp.getUscPaid());
			ps.executeUpdate();
			System.out.println("Weeklypay inserted");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to return a WeeklyPay object for the specified employee for the specified week and year
	 */
	public WeeklyPay getWeeklyPay(Employee emp, int week, int year)
	{
		WeeklyPay wp = null;
		String queryString = "select * from weeklypay where emp_no=? and week_no=? and year=?";
		try
		{
			System.out.println("Retriving weeklypay...");
			ps = conn.prepareStatement(queryString,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ps.setInt(1, emp.getEmpNo());
			ps.setInt(2, week);
			ps.setInt(3, year);
			wpRs = ps.executeQuery();
			while(wpRs.next())
			{
				wp = new WeeklyPay(emp, wpRs.getInt("week_no"),wpRs.getInt("year"),
									wpRs.getDouble("gross_pay"),
									wpRs.getDouble("net_pay"),
									wpRs.getDouble("tax_paid"),
									wpRs.getDouble("prsi_paid"),
									wpRs.getDouble("usc_paid"));
			}
			System.out.println("Weeklypay retrived.");
		}
		catch (SQLException e)
		{
			disconnect();
			JOptionPane.showMessageDialog(null, "Unexpected error. System will close.");
			System.exit(0);
			e.printStackTrace();
		}
		return wp;
	}
	
	/*
	 * Method to update employee details related to a specified employee ID.
	 */
	public void updateEmpDetails(Employee emp, double cutOff, double taxCredit, String prsi,
									String bankName, String accountNo, String sortCode)
	{
		try
		{
			//Update Employee table details for employee
			conn.setAutoCommit(false);
			System.out.println("Updating...");
			ps = conn.prepareStatement("update employee set emp_surname=?, "
										+"emp_fname=?, emp_dob=?, emp_address_l1=?,emp_address_l2=?,emp_address_l3=?,"
										+" emp_email=?, emp_phone=?,emp_pps=?, emp_start_date=?,"
										+" emp_department=?, emp_pay_type=?, emp_rate=? where emp_no=?");
			
			ps.setString(1,emp.getSurname());
			ps.setString(2,emp.getFirstName());
			ps.setString(3,emp.getDob());
			ps.setString(4,emp.getAddressL1());
			ps.setString(5,emp.getAddressL2());
			ps.setString(6,emp.getAddressL3());
			ps.setString(7,emp.getEmail());
			ps.setString(8,emp.getPhone());
			ps.setString(9,emp.getPps());
			ps.setString(10, emp.getStartDate());
			ps.setString(11, emp.getDepartment());
			ps.setString(12, emp.getPaymentType());
			if(emp instanceof HourlyEmployee)
			{
				HourlyEmployee he=(HourlyEmployee)emp;
				ps.setDouble(13, he.getRate());
			}
			else if(emp instanceof SalariedEmployee)
			{
				SalariedEmployee se=(SalariedEmployee)emp;
				ps.setDouble(13, se.getSalary());
			}
			ps.setInt(14,emp.getEmpNo());
			ps.executeUpdate();
			System.out.println("Updated employee");
			
			ps = conn.prepareStatement("update revdetails set cut_off=?, tax_credit=?, prsi_class=? where emp_no=?");
			//Update RevDetails table details for employee
			ps.setDouble(1, cutOff);
			ps.setDouble(2, taxCredit);
			ps.setString(3, prsi);
			ps.setInt(4,emp.getEmpNo());
			ps.executeUpdate();
			System.out.println("Updated revenue details");
			
			ps = conn.prepareStatement("update bankdetails set bank_name=?, account_no=?, sort_code=? where emp_no=?");
			//Update BankDetails table details for employee
			ps.setString(1, bankName);
			ps.setString(2, accountNo);
			ps.setString(3, sortCode);
			ps.setInt(4, emp.getEmpNo());
			ps.executeUpdate();
			System.out.println("Updated bank details");
			conn.commit();
		}
		catch(Exception e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}	
	}
	
	/*
	 * Method to create a new Employee in the database
	 */
	public void insertEmployee(Employee emp)
	{
		try
		{
			ps = conn.prepareStatement("insert into employee(emp_no, emp_surname, emp_fname," +
										"emp_address_l1,emp_address_l2,emp_address_l3, emp_email, emp_phone," +
										"emp_dob,emp_pps,emp_start_date,emp_pay_type, emp_rate, emp_hourly) " +
										"values(emp_no_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
			ps.setString(1,emp.getSurname());
			ps.setString(2,emp.getFirstName());
			ps.setString(3,emp.getAddressL1());
			ps.setString(4,emp.getAddressL2());
			ps.setString(5,emp.getAddressL3());
			ps.setString(6,emp.getEmail());
			ps.setString(7,emp.getPhone());
			ps.setString(8,emp.getDob());
			ps.setString(9,emp.getPps());
			ps.setString(10, emp.getStartDate());
			ps.setString(11, emp.getPaymentType());
			if(emp instanceof HourlyEmployee)
			{
				HourlyEmployee he = (HourlyEmployee)emp;
				ps.setDouble(12,he.getRate());
				ps.setString(13, "Y");
			}
			else if(emp instanceof SalariedEmployee)
			{
				SalariedEmployee se = (SalariedEmployee)emp;
				ps.setDouble(12, se.getSalary());
				ps.setString(13, "N");
			}
			ps.executeUpdate();
			System.out.println("Employee inserted.");	
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	/*
	 * Method to cease current employees employment, fills values for finish date
	 */
	public void ceaseEmployment(Employee emp)
	{
		try
		{
			ps = conn.prepareStatement("update employee set emp_finish_date=? where emp_no=?");
			ps.setString(1, emp.getFinishDate());
			ps.setInt(2, emp.getEmpNo());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to create a new company in the database. Tied to this are all other tables to allow
	 * the program to function properly
	 * 
	 * Creates the sequence:
	 * 		user_seq
	 * 		emp_seq
	 * Creates the tables:
	 * 		company
	 * 			Fills with specified details from the Company object passed into the method
	 * 		payroll_user
	 * 			Fills with values for 3 admin users; shane, magic and seamus
	 * 		employee
	 * 		timesheet
	 * 		weeklyPay
	 * 		totalPay
	 * 		revDetails
	 * 		bankDetails
	 * Creates trigger:
	 * 		details trigger
	 */
	public void insertCompany(Company c)
	{
		try
		{
			deleteCompany();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("create table company(company_name varchar(30),reg_no varchar(10)," 
										+"start_week Integer(2), start_year Integer(4))");
			ps.executeUpdate();
			System.out.println("Company table OK");
			
			ps = conn.prepareStatement("insert into company values(?,?,?,?)");
			ps.setString(1, c.getCompName());
			ps.setString(2, c.getCompRegNo());
			ps.setInt(3, c.getStartWeek());
			ps.setInt(4, c.getYear());
			ps.executeUpdate();
			
/*			ps = conn.prepareStatement("create sequence user_seq start with 1 increment by 1");
			ps.executeUpdate();
			System.out.println("User sequence OK");*/
			
			ps = conn.prepareStatement("create table payroll_user (user_id Integer primary key autoincrement not null,"
										+ "user_fname varchar(20),"
										+"user_lname varchar(30),user_alias varchar(20),user_password varchar(20))"
										);
			ps.executeUpdate();
			System.out.println("PayrollUser table OK");
			
			ps = conn.prepareStatement("insert into payroll_user(user_fname,user_lname,user_alias,user_password) values (?,?,?,?)");
			ps.setString(1, "Maciej");
			ps.setString(2, "Macierzynski");
			ps.setString(3, "magic");
			ps.setString(4, "mac2012");
			ps.executeUpdate();
			System.out.println("User1 OK");
			
			ps.setString(1, "James");
			ps.setString(2, "Madden");
			ps.setString(3, "seamus");
			ps.setString(4, "mad2012");
			ps.executeUpdate();
			System.out.println("User2 OK");
			
			ps.setString(1, "Shane");
			ps.setString(2, "Murphy");
			ps.setString(3, "shane");
			ps.setString(4, "mur2012");
			ps.executeUpdate();
			System.out.println("User3 OK");
			
/*			ps = conn.prepareStatement("create sequence emp_no_seq start with 1 increment by 1");
			ps.executeUpdate();
			System.out.println("Employee sequence OK");
*/			
			ps = conn.prepareStatement("create table employee(emp_no integer primary key autoincrement not null ,"
										+"emp_surname varchar(30) ,"
										+"emp_fname varchar(30) ,emp_address_l1 varchar(30) ,"
										+"emp_address_l2 varchar(30),emp_address_l3 varchar(30),"
										+"emp_email varchar(50),emp_phone varchar(15),"
										+"emp_dob varchar(10),emp_pps varchar(10) ,"
										+"emp_start_date varchar(10),emp_finish_date varchar(10),"
										+"emp_department varchar(10),emp_pay_type varchar(10),"
										+"emp_rate float, emp_hourly char(1))"
										);
			ps.executeUpdate();
			System.out.println("Employee table OK");
			
			ps = conn.prepareStatement("create table timesheet(emp_no Integer,week_no number(2), year number(4),"
										+"monday varchar(13),tuesday varchar(13),wednsday varchar(13),"
										+"thursday varchar(13),friday varchar(13),saturday varchar(13),"
										+"sunday varchar(13),tot_hours float,"
										+"foreign key(emp_no) references employee(emp_no))");
			ps.executeUpdate();
			System.out.println("Timesheet table OK");
			
			ps = conn.prepareStatement("create table weeklyPay(emp_no Integer,week_no number(2),year number(4),"
										+"gross_pay float, net_pay float, tax_paid float,"
										+"prsi_paid float,usc_paid float," 
										+"foreign key(emp_no) references employee(emp_no))");
			ps.executeUpdate();
			System.out.println("Weeklypay table OK");
			
			ps = conn.prepareStatement("create table totalPay(emp_no Integer,year number(4),tot_gross_pay float,"
										+"tot_net_pay float,tot_tax_paid float,"
										+"tot_prsi_paid float,tot_usc_paid float,"
										+"foreign key (emp_no) references employee(emp_no))");
			ps.executeUpdate();
			System.out.println("TotalPay table OK");
			
			ps = conn.prepareStatement("create table revDetails(emp_no Integer,cut_Off float,"
										+"tax_Credit float, prsi_Class varchar(10),"
										+"foreign key (emp_no) references employee(emp_no))");
			ps.executeUpdate();
			System.out.println("RevDetails table OK");
			
			ps = conn.prepareStatement("create table bankDetails(emp_no Integer,bank_name varchar(20),"
										+"account_no varchar(20),sort_code varchar(20),"
										+"foreign key (emp_no) references employee(emp_no))");
			ps.executeUpdate();
			System.out.println("BankDetails table OK");
			Statement stm = conn.createStatement();
			ResultSet seq_val = stm.executeQuery("SELECT last_insert_rowid()");
			int val = seq_val.getInt(1);
			
			ps = conn.prepareStatement("create trigger if not exists details_trigger after insert on employee"
										+" for each row "
										+" begin "
			+"insert into revdetails values ("+val+", 0.0,0.0, 'A');"
			+"insert into bankdetails values ("+val+", '','','');"
			+"insert into totalpay values ("+val+", 0, 0.0, 0.0,0.0,0.0,0.0);"
										+" end;");
			ps.executeUpdate();
			System.out.println("Trigger OK");
			conn.commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Deletes company by dropping all tables associated with the current company
	 */
	public void deleteCompany()
	{
		try
		{
			connect();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("drop table if exists timesheet");
			ps.executeUpdate();
			System.out.println("Timesheet table dropped.");
			
			ps = conn.prepareStatement("drop table if exists revdetails");
			ps.executeUpdate();
			System.out.println("RevDetails table dropped.");
			
			ps = conn.prepareStatement("drop table if exists bankdetails");
			ps.executeUpdate();
			System.out.println("BankDetails table dropped.");
			
			ps = conn.prepareStatement("drop table if exists weeklypay");
			ps.executeUpdate();
			System.out.println("WeeklyPay table dropped.");
			
			ps = conn.prepareStatement("drop table if exists totalpay");
			ps.executeUpdate();
			System.out.println("TotalPay table dropped.");
			
			ps = conn.prepareStatement("drop table if exists company");
			ps.executeUpdate();
			System.out.println("Company table dropped.");
			
			ps = conn.prepareStatement("drop table if exists employee");
			ps.executeUpdate();
			System.out.println("Employee table dropped.");
			
			
			ps = conn.prepareStatement("drop table if exists payroll_user");
			ps.executeUpdate();
			System.out.println("User table dropped.");
			
			
			conn.commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Method for getting values from the database to be displayed in the reports section. The values
	 * are returned in a resultset
	 */
	public ResultSet getReport(String type, int num)
	{
		if(type.equalsIgnoreCase("w")) // Weekly Report
		{
			try
			{
				ps = conn.prepareStatement("select week_no, sum(gross_pay), " +
											"sum(net_pay), sum(tax_paid), sum(prsi_paid), " +
											"sum(usc_paid) from weeklypay where week_no=? group by week_no");
				ps.setInt(1, num);
				rs = ps.executeQuery();
				return rs;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if(type.equalsIgnoreCase("p45")) // P45 Report
		{
			try
			{
				ps = conn.prepareStatement("select count(week_no), sum(gross_pay), " +
											"sum(net_pay), sum(tax_paid), sum(prsi_paid), " +
											"sum(usc_paid) from weeklypay where emp_no=? group by emp_no");
				ps.setInt(1, num);
				rs = ps.executeQuery();
				return rs;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if(type.equalsIgnoreCase("p60")) //P60 Report
		{
			try
			{
				ps = conn.prepareStatement("select count(week_no), sum(gross_pay), " +
											"sum(net_pay), sum(tax_paid), sum(prsi_paid), " +
											"sum(usc_paid) from weeklypay where emp_no=? group by emp_no");
				ps.setInt(1, num);
				rs = ps.executeQuery();
				return rs;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if(type.equalsIgnoreCase("p30")) //P30 Report
		{
			try
			{
				ps = conn.prepareStatement("select sum(tot_tax_paid), sum(tot_prsi_paid)" +
											"from totalpay");
				rs = ps.executeQuery();
				return rs;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if(type.equalsIgnoreCase("p35")) //P35 Report
		{
			try
			{
				ps = conn.prepareStatement("select sum(tot_tax_paid), sum(tot_prsi_paid)" +
											"from totalpay");
				rs = ps.executeQuery();
				return rs;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return rs;
	}
}
