package org.eclipse.epsilon.egx.incremental.hashing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class EglH2TemplateManager {
	
	private String configurationName;
	private Map<String, String> allTemplates = new HashMap<String, String>();
	private Map<String, String> storeTemplates = new HashMap<String, String>();
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private void createConnection(String db) throws SQLException{
			
			try {
				Class.forName("org.h2.Driver");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
	
			String settings = String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1", db);
			//String settings = String.format("jdbc:h2:file:/Users/jimmysyl/Desktop/db/%s;DB_CLOSE_DELAY=-1", db);
	
			connection = DriverManager.getConnection(settings, "admin", "");

			//connection.setAutoCommit(false);
		}
	
	private void closeConnection(){
		try {
			if(resultSet != null){
				resultSet.close();
			}
			if(statement != null){
				statement.close();
			}
			if(connection != null){
				connection.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	

	public void setConfiguration(String configurationName) {
		this.configurationName = configurationName;
		
	}


	public void dispose() {
		for(String rule : allTemplates.keySet()){
			try {
				configureTemplateStore();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		closeConnection();
	}
	
	private void configureTemplateStore() throws Exception{
		
		String dropQuery = String.format("drop table %s", "templates_table");
		
		String createQuery = String.format("create table %s (template_name varchar, template_hash varchar)", "templates_table");
		
		if(connection == null)
			createConnection(configurationName);
		
		if(statement == null)
			statement = connection.createStatement();
		
		//do wholesale drop of table then create new table
		try{
			statement.execute(dropQuery);
		}catch (SQLException s){
			System.out.println("Could not drop templates_table");
		}
		
		statement.execute(createQuery);
		
		this.writeTemplateHashes();
	}
	
	private void writeTemplateHashes() throws Exception{
		
		//statement = connection.createStatement();
		
		for (String key : storeTemplates.keySet()){
			String insertQuery = String.format("insert into %s values('%s', '%s')", "templates_table", key, storeTemplates.get(key));
			statement.execute(insertQuery);
		}
		
	}
	
	public String getTemplateHashes(String template) throws Exception {
		
		if(allTemplates.get(template) == null)
		{
			allTemplates.put(template, "");
			
			if(connection == null)
				createConnection(configurationName);
			
			if(statement == null)
				statement = connection.createStatement();
			
			ResultSet resultSet = null;
			
			try{
				resultSet = statement.executeQuery(String.format("select * from %s", "templates_table"));
			}catch(Exception e){
			}
			
			
			if(resultSet != null){
				
				while(resultSet.next()){
					allTemplates.put(resultSet.getString(1), resultSet.getString(2));
					//System.out.println("template: " + resultSet.getString(1) + " " + resultSet.getString(2) );
				}
			
			}
		}
		//System.out.println("all templates: " + allTemplates.toString());
		//System.out.println("template: " + allTemplates.get(template));
		
		return allTemplates.get(template);
	}
	
	public void addTemplateHash(String template, String hash) {
		if(storeTemplates.get(template) == null)
			storeTemplates.put(template, hash);		
	}

}
