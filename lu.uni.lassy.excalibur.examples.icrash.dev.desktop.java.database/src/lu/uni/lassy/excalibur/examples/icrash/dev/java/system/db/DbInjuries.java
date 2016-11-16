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
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtInjury;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtVictim;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtInjuryID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtVictimID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtInjuryKind;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtString;

/**
 * The Class used to update or retrieve information from the Injuries table in the database.
 */
public class DbInjuries extends DbAbstract{

	/**
	 * Insert Injury into the database.
	 *
	 * @param aCtInjury The CtInjury of which to use the information to insert into the database
	 * @param aCtVictim The victim that is associated with the injury
	 */
	static public void insertInjury(CtInjury aCtInjury, DtVictimID aDtVictim){
	
		try {
			conn = DriverManager.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Insert
			
			try{
				Statement st = conn.createStatement();
				
				String id = aCtInjury.id.value.getValue();
				String victim_id = aDtVictim.value.getValue();
				String kind = aCtInjury.kind.toString();
	
				log.debug("[DATABASE]-Insert injury");
				int val = st.executeUpdate("INSERT INTO "+ dbName + ".injuries" +
											"(id,victim_id,body_part)" + 
											"VALUES("+"'"+id+"','"+victim_id+"','"+kind+"')");
				
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
	 * Gets a injury details from the database, using the id to retrieve the data.
	 *
	 * @param id The id to use to get the data from the database
	 * @return The injury data that is retrieved from the database. This could be empty
	 */
	static public CtInjury getInjury(DtInjuryID id){
		
		CtInjury aCtInjury = new CtInjury();
		
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//Select
			
			try{
				String sql = "SELECT * FROM "+ dbName + ".injuries WHERE id = " + id;

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet  res = statement.executeQuery(sql);
				
				if(res.next()) {				
					
					aCtInjury = new CtInjury();
					//injury's id
					DtInjuryID aId = new DtInjuryID(new PtString(res.getString("id")));
					DtVictimID aVictimId = new DtVictimID(new PtString(res.getString("victim_id")));
					String theKind = res.getString("kind");
					EtInjuryKind aKind = null;
					if(theKind.equals(EtInjuryKind.arm.name()))
						aKind = EtInjuryKind.arm;
					if(theKind.equals(EtInjuryKind.leg.name()))
						aKind = EtInjuryKind.leg;
					if(theKind.equals(EtInjuryKind.chest.name()))
						aKind = EtInjuryKind.chest;
					if(theKind.equals(EtInjuryKind.head.name()))
						aKind = EtInjuryKind.head;
					aCtInjury.init(aId, aVictimId, aKind);
					
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
		
		return aCtInjury;

	}
	
	/**
	 * Gets all injuries from the database.
	 *
	 * @return A hashtable of the injuries using their id as a key value
	 */
	static public Hashtable<String, CtInjury> getSystemInjuries(){
	
		Hashtable<String, CtInjury> cmpSystemCtInjury = new Hashtable<String, CtInjury>();
		
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//Select
			
			try{
				String sql = "SELECT * FROM "+ dbName + ".injuries ";
				
				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet  res = statement.executeQuery(sql);
				
				CtInjury aCtInjury = null;
				
				while(res.next()) {				
					
					aCtInjury = new CtInjury();
					
					//injury's id
					DtInjuryID aId = new DtInjuryID(new PtString(res.getString("id")));
					DtVictimID aVictimId = new DtVictimID(new PtString(res.getString("victim_id")));
					String theKind = res.getString("body_part");
					EtInjuryKind aKind = null;
					if(theKind.equals(EtInjuryKind.arm.name()))
						aKind = EtInjuryKind.arm;
					if(theKind.equals(EtInjuryKind.leg.name()))
						aKind = EtInjuryKind.leg;
					if(theKind.equals(EtInjuryKind.chest.name()))
						aKind = EtInjuryKind.chest;
					if(theKind.equals(EtInjuryKind.head.name()))
						aKind = EtInjuryKind.head;
					
					aCtInjury.init(aId, aVictimId, aKind);
					
					//add instance to the hash
					cmpSystemCtInjury.put(aCtInjury.id.value.getValue(), aCtInjury);
					
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
		
		return cmpSystemCtInjury;
		
	}
	
	/**
	 * Gets the associated injuries and their victims.
	 *
	 * @param htCtVictim the ht CtVictim
	 * @return Hashtable of the injuries associated with victims. The injury is used as the key value
	 */
	static public Hashtable<CtInjury, CtVictim> getAssCtInjuryCtVictim(Hashtable<String, CtVictim> htCtVictim){
	
		Hashtable<CtInjury, CtVictim> assCtInjuryCtVictim = new Hashtable<CtInjury, CtVictim>();
		
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			
			/********************/
			//Select
			
			try{
				String sql = "SELECT * FROM "+ dbName + ".injuries "+ 
								"INNER JOIN "+ dbName + ".victims ON "+
								 dbName + ".injuries.victim_id = "+ dbName + ".victims.id";
				

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet  res = statement.executeQuery(sql);
				
				CtInjury aCtInjury = null;
				CtVictim aCtVictim = null;
				
				while(res.next()) {				
					
					aCtInjury = new CtInjury();
					
					//injury's id
					DtInjuryID aId = new DtInjuryID(new PtString(res.getString("id")));
					DtVictimID aVictimId = new DtVictimID(new PtString(res.getString("victim_id")));
					String theKind = res.getString("kind");
					EtInjuryKind aKind = null;
					if(theKind.equals(EtInjuryKind.arm.name()))
						aKind = EtInjuryKind.arm;
					if(theKind.equals(EtInjuryKind.leg.name()))
						aKind = EtInjuryKind.leg;
					if(theKind.equals(EtInjuryKind.chest.name()))
						aKind = EtInjuryKind.chest;
					if(theKind.equals(EtInjuryKind.head.name()))
						aKind = EtInjuryKind.head;
					
					aCtInjury.init(aId, aVictimId, aKind);
					
					//add instances to the hash
					assCtInjuryCtVictim.put(aCtInjury, aCtVictim);
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

		
		return assCtInjuryCtVictim;
		
	}
	
	/**
	 * Gets the current highest number used for an Injury ID in the database.
	 *
	 * @return the highest number for an Injury id
	 */
	static public int getMaxInjuryID() {

		int maxInjuryId = 0;

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT MAX(CAST(id AS UNSIGNED)) FROM " + dbName
						+ ".injuries";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				if (res.next()) {
					maxInjuryId = res.getInt(1);
				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed! " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
		}

		return maxInjuryId;

	}
	
	/**
	 * Deletes a injury from the database.
	 *
	 * @param aCtInjury The injury to delete from the database, it will use the ID/id to delete the injury
	 */
	static public void deleteInjury(CtInjury aCtInjury){
	
		try {
			conn = DriverManager.getConnection(url+dbName,userName,password);
			log.debug("Connected to the database");

			/********************/
			//Delete
			
			try{
				String sql = "DELETE FROM "+ dbName+ ".injuries WHERE id = ?";
				String id = aCtInjury.id.value.getValue();

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

}
