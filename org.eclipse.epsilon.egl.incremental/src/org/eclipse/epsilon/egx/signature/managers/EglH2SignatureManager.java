package org.eclipse.epsilon.egx.signature.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.egx.signatures.ModelElementSignature;
import org.eclipse.epsilon.egx.signatures.Signature;

//import org.h2.tools.DeleteDbFiles;

public class EglH2SignatureManager implements ISignatureManager{
	
	private String configurationName;
	private Map<String, HashMap<String,Signature>> allSignatures = new HashMap<String, HashMap<String,Signature>>();
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public static void main(String[] args) throws Exception {
		//EglH2SignatureManager w = new EglH2SignatureManager();
		
		/*
		Signature t = new Signature("11011", "S2Class", "abcdefgh");
		Signature s = new Signature("110121", "S2Class2", "abcdemnb");
		Signature u = new Signature("110121", "S2Class", "some text");
		
		
		List<Signature> list = new ArrayList<Signature>();
		
		list.add(s);
		list.add(t);
		list.add(u);
		
		String workingDir = System.getProperty("user.dir");
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		w.configureSignatureStore("test");
		w.writeSignatures("test");
		w.getSignature("test", workingDir);
		
		System.out.println(map.size());
		*/
		
	}
	
	
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
	
	private void configureSignatureStore(String table) throws Exception{
		//DeleteDbFiles.execute("~", "test", true);
		
		String dropQuery = String.format("drop table %s", table);
		
		String createQuery = String.format("create table %s (object_id varchar, sigValPart varchar)", table);
		
		if(connection == null)
			createConnection(configurationName);
		
		if(statement == null)
			statement = connection.createStatement();
		
		//do wholesale drop of table then create new table
		try{
			statement.execute(dropQuery);
		}catch (SQLException s){
			 System.out.println("Could not drop " + table);
		}
		
		statement.execute(createQuery);
		
		this.writeSignatures(table);
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
			
	private void writeSignatures(String table) throws Exception{
		
		Map<String,Signature> sigMap = new HashMap<String,Signature>(); 
		sigMap.putAll(allSignatures.get(table));
		
		//Statement statement = connection.createStatement();
		
		for (String key : sigMap.keySet()){
			for (Object sigValPart: ((ModelElementSignature) sigMap.get(key)).getObjSignatureValues()){
				String insertQuery = String.format("insert into %s values('%s', '%s')", table, key, sigValPart.toString());
				statement.execute(insertQuery);
				//statement.addBatch(insertQuery);
			}
			//statement.executeBatch();
			//statement.clearBatch();
		}

	}

	@Override
	public Signature getSignature(String objectId, String rule) throws Exception {
		
		if(allSignatures.get(rule) == null)
		{
			allSignatures.put(rule, new HashMap<String, Signature>());
			
			if(connection == null)
				createConnection(configurationName);
			
			if(statement == null)
				statement = connection.createStatement();
	
			resultSet = null;
			try{
				resultSet = statement.executeQuery(String.format("select * from %s", rule));
			}catch(Exception e){
			}		
			
			if(resultSet != null){
				Map<String, Signature> tmp = new HashMap<String,Signature>();
				
				
				while(resultSet.next()){
					String obj_id = resultSet.getString(1);
					Signature s = new ModelElementSignature(obj_id, rule);
					
					if(tmp.containsKey(obj_id)){
						((ModelElementSignature) tmp.get(obj_id)).getObjSignatureValues().add(resultSet.getString(2));
					}
					
					else{
						tmp.put(obj_id, s);
						((ModelElementSignature) s).getObjSignatureValues().add(resultSet.getString(2));
					}

				}	
				for(String r : tmp.keySet()){
					allSignatures.get(rule).put(r,tmp.get(r));
				}
			}		

		}
		return (Signature)allSignatures.get(rule).get(objectId);
	}


	@Override
	public void addSignature(Signature s, String rule) {
		if(allSignatures.get(rule) == null){
			allSignatures.put(rule, new HashMap<String, Signature>());		
		}
		allSignatures.get(rule).put(((Signature) s).getObjId(), s);
	}


	@Override
	public void setConfiguration(String configurationName) {
		this.configurationName = configurationName;
		
	}

	@Override
	public void dispose() {
		for(String rule : allSignatures.keySet()){
			try {
				configureSignatureStore(rule);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		closeConnection();
	}


	public HashMap<String, Signature> getAllSignaturesForRule(String rule) {
		return null;
	}

			
}

