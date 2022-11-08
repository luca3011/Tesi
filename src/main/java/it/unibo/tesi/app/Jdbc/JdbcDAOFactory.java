package it.unibo.tesi.app.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import it.unibo.tesi.app.ControlloDAO;
import it.unibo.tesi.app.DAOFactory;
import it.unibo.tesi.app.OrdineDiProduzioneDAO;
import it.unibo.tesi.app.SchedaControlloDAO;



public class JdbcDAOFactory extends DAOFactory {

	/**
	 * URI of the database to connect to
	 */
	// String jdbcUrl = "jdbc:sqlserver://mssql.db.server\\mssql_instance;databaseName=my_database";
	public static final String DBURL = "jdbc:sqlserver://IS-ITA-MAGO1\\MICROAREA;databaseName=Test_Intersurgical_Claudia;encrypt=false";

	public static final String USERNAME = "sa";
	public static final String PASSWORD = "cpn053K1";


	// --------------------------------------------
	
	public static Connection createConnection() {
		try {
			Connection conn = DriverManager.getConnection (DBURL, USERNAME, PASSWORD);
			System.out.println(JdbcDAOFactory.class.getName()+".createConnection(): database connection established");
			return conn;
		} 
		catch (Exception e) {
			System.err.println(JdbcDAOFactory.class.getName()+".createConnection(): failed creating connection\n"+e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		}
		catch (Exception e) {
			System.err.println(JdbcDAOFactory.class.getName()+".closeConnection(): failed closing connection\n"+e);
			e.printStackTrace();
		}
	}

	// --------------------------------------------
	

	public OrdineDiProduzioneDAO getOrdineDiProduzioneDAO() {
		return new JdbcOrdineDiProduzioneDAO();
	}

	public SchedaControlloDAO getSchedaControlloDAO() {
		return new JdbcSchedaControlloDAO();
	}

	public ControlloDAO getControlloDAO() {
		return new JdbcControlloDAO();
	}
	
}
