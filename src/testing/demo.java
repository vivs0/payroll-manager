package testing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import payroll.Company;

public class demo {
	private static Connection conn;
	private ResultSet rs;
	private ResultSet tsRs;
	private ResultSet wpRs;
	private static PreparedStatement ps;

	private static void connect()
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
	public static void disconnect()
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
	public static void insertCompany()
	{
		try
		{
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("create table if not exists company(company_name varchar(30),reg_no varchar(10)," 
										+"start_week Integer(2), start_year Integer(4))");
			ps.executeUpdate();
			System.out.println("Company table OK");

			ps = conn.prepareStatement("create sequence user_seq start with 1 increment by 1");
			ps.executeUpdate();
			System.out.println("User sequence OK");
			
			conn.commit();
		}
		catch (SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				System.out.println(e1.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		connect();
		insertCompany();
	}

}
