/*******************************************************************************
 * Copyright (c) 2014-2015 University of Luxembourg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alfredo Capozucca - initial API and implementation
 *     Christophe Kamphaus - Remote implementation of Actors
 *     Thomas Mortimer - Updated client to MVC and added new design patterns
 ******************************************************************************/
package lu.uni.lassy.excalibur.examples.icrash.dev.java.system.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtHospitalID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtLogin;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtPassword;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtString;

/**
 * The Class DbHospitals for updating or retrieving information on the hospitals table.
 */
public class DbHospitals extends DbAbstract{

	/**
	 * Creates a new Hospital in the database using the data from the CtHospital passed.
	 *
	 * @param aCtHospital The CtHospital of which to use the data of to create the row in the database
	 */
	static public void insertHospital(CtHospital aCtHospital){
	
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//Insert
			
			try{
				Statement st = conn.createStatement();
				
				String id = aCtHospital.id.value.getValue();
				String login =  aCtHospital.login.value.getValue();
				String pwd =  aCtHospital.pwd.value.getValue();
	
				log.debug("[DATABASE]-Insert Hospital");
				int val = st.executeUpdate("INSERT INTO "+ dbName+ ".hospitals" +
											"(id,login,pwd)" + 
											"VALUES("+"'"+id+"'"+",'"+login+"','"+pwd+"')");
				
				log.debug(val + " row affected");
			}
			catch (SQLException s){
				log.error("SQL statement is not executed! "+s);
			}

	
			conn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
		}
	}
	
	/**
	 * Gets a Hospital details from the database, depending on the ID used and creates a CtHospital class to house the data.
	 *
	 * @param hospitalId The ID of which Hospital to get the data of
	 * @return The Hospital that holds the data retrieved. It could be an empty class!
	 */
	static public CtHospital getHospital(String hospitalId){
		
		CtHospital aCtHospital = new CtHospital();
		
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			

			/********************/
			//Select
			
			try{
				String sql = "SELECT * FROM "+ dbName + ".hospitals WHERE id = " + hospitalId;
				

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet  res = statement.executeQuery(sql);
				
				if(res.next()) {				
					
					aCtHospital = new CtHospital();
					//Hospital's id
					DtHospitalID aId = new DtHospitalID(new PtString(res.getString("id")));
					//Hospital's login
					DtLogin aLogin = new DtLogin(new PtString(res.getString("login")));
					//Hospital's pwd
					DtPassword aPwd = new DtPassword(new PtString(res.getString("pwd")));

					aCtHospital.init(aId, aLogin,aPwd);
					
				}
								
			}
			catch (SQLException s){
				log.error("SQL statement is not executed! "+s);
			}
			conn.close();
			log.debug("Disconnected from database");
			
			
		} catch (Exception e) {
			logException(e);
		}
		
		return aCtHospital;

	}
	
	/**
	 * Delete a Hospital from the database.
	 *
	 * @param aCtHospital The Hospital to delete from the database
	 */
	static public void deleteHospital(CtHospital aCtHospital){
		
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//Delete
			
			try{
				String sql = "DELETE FROM "+ dbName+ ".hospitals WHERE id = ?";
				String id = aCtHospital.id.value.getValue();

				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, id);
				int rows = statement.executeUpdate();
				log.debug(rows+" row deleted");				
			}
			catch (SQLException s){
				log.error("SQL statement is not executed! "+s);
			}


			conn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
		}
	}
	
	/**
	 * Updates a Hospital with the data passed. It uses the ID to update the details in the database
	 *
	 * @param aCtHospital The Hospital to update
	 * @return the pt boolean
	 */
	static public PtBoolean updateHospital(CtHospital aCtHospital){
		PtBoolean success = new PtBoolean(false);
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//edit
			
			try{
				Statement st = conn.createStatement();
				String id = aCtHospital.id.value.getValue();
				String login =  aCtHospital.login.value.getValue();
				String pwd =  aCtHospital.pwd.value.getValue();
				String statement = "UPDATE "+ dbName+ ".hospitals" +
						" SET pwd='"+pwd+"',  login='"+ login+"' " +
						"WHERE id='"+id+"'";
				int val = st.executeUpdate(statement);
				log.debug(val+" row updated");
				success = new PtBoolean(val == 1);
			}
			catch (SQLException s){
				log.error("SQL statement is not executed! "+s);
			}
			conn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
		}
		return success;
	}
	
	/**
	 * Gets a list of all Hospitals from the database. It's stored in a hashtable of the ID and the Hospital class
	 *
	 * @return Return a hashtable of Hospital ID's and their class
	 */
	static public Hashtable<String, CtHospital> getSystemHospitals(){
		Hashtable<String, CtHospital> cmpSystemCtHospital = new Hashtable<String, CtHospital>();

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT * FROM " + dbName + ".hospitals ";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				CtHospital aCtHospital = null;

				while (res.next()) {

					aCtHospital = new CtHospital();
					//alert's id
					DtHospitalID aId = new DtHospitalID(new PtString(
							res.getString("id")));
					DtLogin aLogin = new DtLogin(new PtString(res.getString("login")));
					DtPassword aPwd = new DtPassword(new PtString(res.getString("pwd")));
					//init aCtAlert instance
					aCtHospital.init(aId, aLogin, aPwd);
					
					//add instance to the hash
					cmpSystemCtHospital
							.put(aCtHospital.id.value.getValue(), aCtHospital);
				}
			} catch (SQLException s) {
				log.error("SQL statement is not executed! " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
		}

		return cmpSystemCtHospital;
	}
	

}
