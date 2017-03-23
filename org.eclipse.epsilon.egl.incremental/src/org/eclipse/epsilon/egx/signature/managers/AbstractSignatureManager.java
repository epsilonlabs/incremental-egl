package org.eclipse.epsilon.egx.signature.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.egx.signatures.Signature;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;

public abstract class AbstractSignatureManager implements ISignatureManager {

	protected String configurationName = "foo";
	protected Map<String, HashMap<String, Signature>> allSignatures = new HashMap<String, HashMap<String,Signature>>();
	protected Connection connection = null;
	protected Statement statement = null;
	protected ResultSet resultSet = null;
	private ArrayList<String> tables = getTables();

	public void connect() throws EolRuntimeException {
		
		try {
			if(connection == null) {
				Class.forName("org.h2.Driver");
				
				String settings = String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1", configurationName);
//				String settings = String.format("jdbc:h2:file:/Users/jimmysyl/Desktop/db/%s;DB_CLOSE_DELAY=-1", configurationName);
	
				connection = DriverManager.getConnection(settings, "admin", "");
				statement = connection.createStatement();
			}
		
		} catch (Exception e) {
			throw new EolRuntimeException("Error encountered whilst connecting to H2 Database: " + e.getMessage());
		}
		
	}

	protected abstract ArrayList<String> getTables();

	protected abstract String getTableSuffix();

	protected void configureSignatureStore(String rule) throws Exception {
		
		this.writeSignatures(rule);
	}

	private void configureDBTables() throws SQLException {
		assert(connection != null);
		
		for(String table : tables ) {
			
			String dropQuery = String.format("drop table %s", table);
			
			String createQuery = String.format(createTableCommand(table), table);
			
			//do wholesale drop of table then create new table
			try{
				statement.execute(dropQuery);
			}catch (SQLException s){
				 System.out.println("Could not drop " + table);
			}
			
			statement.execute(createQuery);
		}
		
	}
	
	public void setConfiguration(String configurationName) {
		this.configurationName = configurationName;
		
	}

	protected abstract void writeSignatures(String rule) throws Exception;
	
	public void dispose() {
		try {
			configureDBTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (String rule: allSignatures.keySet()) {
			try {
				configureSignatureStore(rule);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			finishWritingSignatures();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	protected void closeConnection() {
		try {
			if(resultSet != null){
				resultSet.close();
			}
			if(statement != null){
				statement.close();
			}
			if(connection != null){
				connection.close();
				connection = null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	protected abstract String createTableCommand(String table);
	
	public void finishWritingSignatures() throws Exception {}

	public void constructSignatures(int mode) throws Exception {}

}
