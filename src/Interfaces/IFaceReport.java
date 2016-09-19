package Interfaces;

import java.sql.ResultSet;

public interface IFaceReport {
	public ResultSet getReport(String type, int num);

}
