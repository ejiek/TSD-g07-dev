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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.zip.CRC32;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtAlert;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtCrisis;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtHuman;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtAlertID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCRC;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtComment;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtGPSLocation;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtLatitude;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtLongitude;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtPhoneNumber;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtAlertCorruptionKind;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtAlertStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisType;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtHumanKind;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.DtDate;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.DtDateAndTime;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.DtTime;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtReal;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtString;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.utils.ICrashUtils;

/**
 * The Class DbAlerts for updating and retrieving information from the table Alerts in the database.
 */
public class DbAlerts extends DbAbstract {


	/**
	 * Count the number of alerts registered into the database.
	 *
	 */
	static public int countAlerts() {

		int answer = 0; 
		try{

			conn = DriverManager.getConnection(url + dbName, userName, password);
			try{

				String sql = "SELECT COUNT(id)  AS numberOfAlertsl FROM "+ dbName + ".alerts" ;


				Statement statement = conn.createStatement();
				ResultSet  res = statement.executeQuery(sql);

				if (res.next())
					answer = res.getInt("numberOfAlertsl");	

			}catch (SQLException s){
				log.error("SQL statement is not executed!1 "+s);
			}
			conn.close();
			} catch (Exception e) {
			logException(e);
			System.out.println("4");
		}
		return answer;

	}

	private static String alertHashCode(DtAlertID aId, String theStatus,
			DtGPSLocation aDtGPSLocation, DtDateAndTime aInstant, DtComment aDtComment) {
        CRC32 crc = new CRC32();
        crc.update(aId.value.getValue().getBytes());
        crc.update(theStatus.getBytes());
        crc.update(aDtGPSLocation.longitude.value.toString().getBytes());
        crc.update(aDtGPSLocation.latitude.value.toString().getBytes());
        crc.update(aInstant.toString().getBytes());
        crc.update(aDtComment.value.getValue().getBytes());
        return Long.toString((long)crc.getValue());   
    }
	
	/**
	 * Insert an alert into the database.
	 *
	 * @param aCtAlert the class of the alert to insert
	 */
	static public void insertAlert(CtAlert aCtAlert) {
		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database"); 
			
			backupConn = DriverManager
					.getConnection(url + dbBackUpName, userName, password);
			log.debug("Connected to the backup database"); 
			/********************/
			//Insert

			try {
				Statement st = conn.createStatement();
				Statement backupSt = backupConn.createStatement();

				String id = aCtAlert.id.value.getValue();
				String status = aCtAlert.status.toString();
				double latitude = aCtAlert.location.latitude.value.getValue();
				double longitude = aCtAlert.location.longitude.value.getValue();

				int year = aCtAlert.instant.date.year.value.getValue();
				int month = aCtAlert.instant.date.month.value.getValue();
				int day = aCtAlert.instant.date.day.value.getValue();

				int hour = aCtAlert.instant.time.hour.value.getValue();
				int min = aCtAlert.instant.time.minute.value.getValue();
				int sec = aCtAlert.instant.time.second.value.getValue();

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Calendar calendar = new GregorianCalendar(year, month, day,
						hour, min, sec);
				String instant = sdf.format(calendar.getTime());

				String comment = aCtAlert.comment.value.getValue();
				String crc = alertHashCode(aCtAlert.id, status, aCtAlert.location, aCtAlert.instant, aCtAlert.comment);
				String corruption = aCtAlert.corruption.toString();

				log.debug("[DATABASE]-Insert alert");
				int val = st.executeUpdate("INSERT INTO " + dbName + ".alerts"
						+ "(id,status,latitude,longitude,instant,comment,crc,corruption)"
						+ "VALUES(" + "'" + id + "'" + ",'" + status + "', "
						+ latitude + ", " + longitude + ", '" + instant + "','"
						+ comment + "', '" + crc + "', '" + corruption + "')");
				
				log.debug("[DATABASE BACKUP]-Insert alert");
				int backupVal = backupSt.executeUpdate("INSERT INTO " + dbBackUpName + ".alerts"
						+ "(id,status,latitude,longitude,instant,comment,crc,corruption)"
						+ "VALUES(" + "'" + id + "'" + ",'" + status + "', "
						+ latitude + ", " + longitude + ", '" + instant + "','"
						+ comment + "', '" + crc + "', '" + corruption + "')");

				log.debug(val + " row affected");
				log.debug(backupVal + " row affected");
			} catch (SQLException s) {
				log.error("SQL statement is not executed!2 " + s);
			}

			conn.close();
			backupConn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
			System.out.println("36");
		}

	}

	/**
	 * Gets an alert from the database by the alert ID.
	 *
	 * @param alertId The ID of the alert to retrieve from the database
	 * @return the class of the alert to retrieve
	 */
	static public CtAlert getAlert(String alertId) {

		CtAlert aCtAlert = new CtAlert();
			
		aCtAlert = getInfo(alertId, conn, dbName);

		return aCtAlert;

	}

	static private CtAlert getInfo(String alertId, Connection aConn, String ADbName){
		try {
			aConn = DriverManager
					.getConnection(url + ADbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select
			
			try {
				String sql = "SELECT * FROM " + ADbName + ".alerts WHERE id = "
						+ alertId;

				PreparedStatement statement = aConn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				if (res.next()) {

					CtAlert aCtAlert = new CtAlert();
					//alert's id
					DtAlertID aId = new DtAlertID(new PtString(
							res.getString("id")));

					//alert's status -> [pending, valid, invalid]
					String theStatus = res.getString("status");
					EtAlertStatus aStatus = null;
					if (theStatus.equals(EtAlertStatus.pending.name()))
						aStatus = EtAlertStatus.pending;
					if (theStatus.equals(EtAlertStatus.invalid.name()))
						aStatus = EtAlertStatus.invalid;
					if (theStatus.equals(EtAlertStatus.valid.name()))
						aStatus = EtAlertStatus.valid;

					//alert's corruption -> [regular,corrupted,restored]
					String theCorruption = res.getString("corruption");
					EtAlertCorruptionKind aCorruption = null;
					if (theCorruption.equals(EtAlertCorruptionKind.regular.name()))
						aCorruption = EtAlertCorruptionKind.regular;
					if (theCorruption.equals(EtAlertCorruptionKind.restored.name()))
						aCorruption = EtAlertCorruptionKind.restored;
					if (theCorruption.equals(EtAlertCorruptionKind.corrupted.name()))
						aCorruption = EtAlertCorruptionKind.corrupted;
					
					//alert's location
					DtLatitude aDtLatitude = new DtLatitude(new PtReal(
							res.getDouble("latitude")));
					DtLongitude aDtLongitude = new DtLongitude(new PtReal(
							res.getDouble("longitude")));
					DtGPSLocation aDtGPSLocation = new DtGPSLocation(
							aDtLatitude, aDtLongitude);

					//alert's instant
					Timestamp instant = res.getTimestamp("instant");
					Calendar cal = Calendar.getInstance();
					cal.setTime(instant);

					int d = cal.get(Calendar.DATE);
					int m = cal.get(Calendar.MONTH);
					int y = cal.get(Calendar.YEAR);
					DtDate aDtDate = ICrashUtils.setDate(y, m, d);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int sec = cal.get(Calendar.SECOND);
					DtTime aDtTime = ICrashUtils.setTime(h, min, sec);
					DtDateAndTime aInstant = new DtDateAndTime(aDtDate, aDtTime);

					DtComment aDtComment = new DtComment(new PtString(
							res.getString("comment")));
					
					if (alertHashCode(aId, theStatus, aDtGPSLocation, aInstant, aDtComment).equals(res.getString("crc")) ){
						
						if(ADbName.equals(dbBackUpName)){
							
						} else{
							
						}
						
						
					} 
					else {
						if(ADbName.equals(dbBackUpName)){

						} else {
							return getInfo(alertId, backupConn, dbBackUpName);
						}
						
					}
					
					aCtAlert.init(aId, aStatus, aDtGPSLocation, aInstant,
							aDtComment, aCorruption);
					return aCtAlert;

				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed!3 " + s);
			}
			aConn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
			System.out.println("1");
		}
		return null;
	}
	
	/**
	 * Gets the current highest number used for an alert ID in the database.
	 *
	 * @return the highest number for an alert id
	 */
	static public int getMaxAlertID() {

		int maxAlertId = 0;

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT MAX(CAST(id AS UNSIGNED)) FROM " + dbName
						+ ".alerts";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				if (res.next()) {
					maxAlertId = res.getInt(1);
				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed!4 " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
			System.out.println("19");
		}

		return maxAlertId;

	}

	/**
	 * Gets all of the alerts currently in the database.
	 *
	 * @return a hashtable of the alerts using the ID of the alert as a key
	 */
	static public Hashtable<String, CtAlert> getSystemAlerts() {

		Hashtable<String, CtAlert> cmpSystemCtAlert = new Hashtable<String, CtAlert>();

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT * FROM " + dbName + ".alerts ";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				CtAlert aCtAlert = null;

				while (res.next()) {

					aCtAlert = new CtAlert();
					//alert's id
					DtAlertID aId = new DtAlertID(new PtString(
							res.getString("id")));

					//alert's status -> [pending, valid, invalid]
					String theStatus = res.getString("status");
					EtAlertStatus aStatus = null;
					if (theStatus.equals(EtAlertStatus.pending.name()))
						aStatus = EtAlertStatus.pending;
					if (theStatus.equals(EtAlertStatus.invalid.name()))
						aStatus = EtAlertStatus.invalid;
					if (theStatus.equals(EtAlertStatus.valid.name()))
						aStatus = EtAlertStatus.valid;

					//alert's corruption -> [regular,corrupted,restored]
					String theCorruption = res.getString("corruption");
					EtAlertCorruptionKind aCorruption = null;
					if (theCorruption.equals(EtAlertCorruptionKind.regular.name()))
						aCorruption = EtAlertCorruptionKind.regular;
					if (theCorruption.equals(EtAlertCorruptionKind.restored.name()))
						aCorruption = EtAlertCorruptionKind.restored;
					if (theCorruption.equals(EtAlertCorruptionKind.corrupted.name()))
						aCorruption = EtAlertCorruptionKind.corrupted;

					//alert's location
					DtLatitude aDtLatitude = new DtLatitude(new PtReal(
							res.getDouble("latitude")));
					DtLongitude aDtLongitude = new DtLongitude(new PtReal(
							res.getDouble("longitude")));
					DtGPSLocation aDtGPSLocation = new DtGPSLocation(
							aDtLatitude, aDtLongitude);

					//alert's instant
					Timestamp instant = res.getTimestamp("instant");
					Calendar cal = Calendar.getInstance();
					cal.setTime(instant);

					int d = cal.get(Calendar.DATE);
					int m = cal.get(Calendar.MONTH);
					int y = cal.get(Calendar.YEAR);
					DtDate aDtDate = ICrashUtils.setDate(y, m, d);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int sec = cal.get(Calendar.SECOND);
					DtTime aDtTime = ICrashUtils.setTime(h, min, sec);
					DtDateAndTime aInstant = new DtDateAndTime(aDtDate, aDtTime);

					DtComment aDtComment = new DtComment(new PtString(
							res.getString("comment")));
					
					if (alertHashCode(aId, theStatus, aDtGPSLocation, aInstant, aDtComment).equals(res.getString("crc")) ){
						aCtAlert.init(aId, aStatus, aDtGPSLocation, aInstant,
								aDtComment, aCorruption);
						
					} else {
						aCtAlert = getInfo(aId.value.getValue(), backupConn, dbBackUpName);
					}
					

					//add instance to the hash
					cmpSystemCtAlert
					.put(aCtAlert.id.value.getValue(), aCtAlert);

				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed!5 " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
			System.out.println("20");
		}

		return cmpSystemCtAlert;

	}

	/**
	 * Gets the Alerts and their associated crises and inserts them into a hashtable, using the alert as a key.
	 *
	 * @return the hashtable of the associated crises and alerts
	 */
	static public Hashtable<CtAlert, CtCrisis> getAssCtAlertCtCrisis() {

		Hashtable<CtAlert, CtCrisis> assCtAlertCtCrisis = new Hashtable<CtAlert, CtCrisis>();

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT * FROM " + dbName + ".alerts "
						+ "INNER JOIN " + dbName + ".crises ON " + dbName
						+ ".alerts.crisis = " + dbName + ".crises.id";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				CtAlert aCtAlert = null;
				CtCrisis aCtCrisis = null;

				while (res.next()) {

					aCtAlert = new CtAlert();
					//alert's id
					DtAlertID aId = new DtAlertID(new PtString(
							res.getString("alerts.id")));

					//alert's status -> [pending, valid, invalid]
					String theStatus = res.getString("alerts.status");
					EtAlertStatus aStatus = null;
					if (theStatus.equals(EtAlertStatus.pending.name()))
						aStatus = EtAlertStatus.pending;
					if (theStatus.equals(EtAlertStatus.invalid.name()))
						aStatus = EtAlertStatus.invalid;
					if (theStatus.equals(EtAlertStatus.valid.name()))
						aStatus = EtAlertStatus.valid;

					//alert's corruption -> [regular,corrupted,restored]
					String theCorruption = res.getString("corruption");
					EtAlertCorruptionKind aCorruption = null;
					if (theCorruption.equals(EtAlertCorruptionKind.regular.name()))
						aCorruption = EtAlertCorruptionKind.regular;
					if (theCorruption.equals(EtAlertCorruptionKind.restored.name()))
						aCorruption = EtAlertCorruptionKind.restored;
					if (theCorruption.equals(EtAlertCorruptionKind.corrupted.name()))
						aCorruption = EtAlertCorruptionKind.corrupted;

					//alert's location
					DtLatitude aDtLatitude = new DtLatitude(new PtReal(
							res.getDouble("alerts.latitude")));
					DtLongitude aDtLongitude = new DtLongitude(new PtReal(
							res.getDouble("alerts.longitude")));
					DtGPSLocation aDtGPSLocation = new DtGPSLocation(
							aDtLatitude, aDtLongitude);

					//alert's instant
					Timestamp instant = res.getTimestamp("alerts.instant");
					Calendar cal = Calendar.getInstance();
					cal.setTime(instant);

					int d = cal.get(Calendar.DATE);
					int m = cal.get(Calendar.MONTH);
					int y = cal.get(Calendar.YEAR);
					DtDate aDtDate = ICrashUtils.setDate(y, m, d);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int sec = cal.get(Calendar.SECOND);
					DtTime aDtTime = ICrashUtils.setTime(h, min, sec);
					DtDateAndTime aInstant = new DtDateAndTime(aDtDate, aDtTime);

					//alert's comment  
					DtComment aDtComment = new DtComment(new PtString(
							res.getString("alerts.comment")));
					
					if (alertHashCode(aId, theStatus, aDtGPSLocation, aInstant, aDtComment).equals(res.getString("alerts.crc")) ){
						aCtAlert.init(aId, aStatus, aDtGPSLocation, aInstant,
								aDtComment, aCorruption);
						
					} else {
						aCtAlert = getInfo(aId.value.getValue(), backupConn, dbBackUpName);
					}

					//*************************************
					aCtCrisis = new CtCrisis();
					//crisis's id
					DtCrisisID aCrisisId = new DtCrisisID(new PtString(
							res.getString("crises.id")));

					//crisis' type -> [small, medium, huge]
					String theCrisisType = res.getString("crises.type");
					EtCrisisType aCrisisType = null;
					if (theCrisisType.equals(EtCrisisType.small.name()))
						aCrisisType = EtCrisisType.small;
					if (theCrisisType.equals(EtCrisisType.medium.name()))
						aCrisisType = EtCrisisType.medium;
					if (theCrisisType.equals(EtCrisisType.huge.name()))
						aCrisisType = EtCrisisType.huge;

					//crisis's status -> [pending, handled, solved, closed]
					String theCrisisStatus = res.getString("crises.status");
					EtCrisisStatus aCrisisStatus = null;
					if (theCrisisStatus.equals(EtCrisisStatus.pending.name()))
						aCrisisStatus = EtCrisisStatus.pending;
					if (theCrisisStatus.equals(EtCrisisStatus.handled.name()))
						aCrisisStatus = EtCrisisStatus.handled;
					if (theCrisisStatus.equals(EtCrisisStatus.solved.name()))
						aCrisisStatus = EtCrisisStatus.solved;
					if (theCrisisStatus.equals(EtCrisisStatus.closed.name()))
						aCrisisStatus = EtCrisisStatus.closed;

					//crisis's location
					DtLatitude aCrisisDtLatitude = new DtLatitude(new PtReal(
							res.getDouble("crises.latitude")));
					DtLongitude aCrisisDtLongitude = new DtLongitude(
							new PtReal(res.getDouble("crises.longitude")));
					DtGPSLocation aCrisisDtGPSLocation = new DtGPSLocation(
							aCrisisDtLatitude, aCrisisDtLongitude);

					//crisis's instant
					instant = res.getTimestamp("crises.instant");
					cal.setTime(instant);

					d = cal.get(Calendar.DATE);
					m = cal.get(Calendar.MONTH);
					y = cal.get(Calendar.YEAR);
					aDtDate = ICrashUtils.setDate(y, m, d);
					h = cal.get(Calendar.HOUR_OF_DAY);
					min = cal.get(Calendar.MINUTE);
					sec = cal.get(Calendar.SECOND);
					aDtTime = ICrashUtils.setTime(h, min, sec);
					DtDateAndTime aCrisisInstant = new DtDateAndTime(aDtDate,
							aDtTime);

					//crisis's comment  
					DtComment aCrisisDtComment = new DtComment(new PtString(
							res.getString("crises.comment")));

					aCtCrisis.init(aCrisisId, aCrisisType, aCrisisStatus,
							aCrisisDtGPSLocation, aCrisisInstant,
							aCrisisDtComment);

					//add instances to the hash
					assCtAlertCtCrisis.put(aCtAlert, aCtCrisis);

				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed!6 " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
			System.out.println("11");
		}

		return assCtAlertCtCrisis;

	}

	/**
	 * Gets alerts and their associated humans and inserts them into a hashtable, using the alert as a key.
	 *
	 * @return the hashtable of the associated humans and alerts
	 */
	static public Hashtable<CtAlert, CtHuman> getAssCtAlertCtHuman() {

		Hashtable<CtAlert, CtHuman> assCtAlertCtHuman = new Hashtable<CtAlert, CtHuman>();

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			/********************/
			//Select

			try {
				String sql = "SELECT * FROM " + dbName + ".alerts "
						+ "INNER JOIN " + dbName + ".humans ON " + dbName
						+ ".alerts.human = " + dbName + ".humans.phone";

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet res = statement.executeQuery(sql);

				CtAlert aCtAlert = null;
				CtHuman aCtHuman = null;

				while (res.next()) {

					aCtAlert = new CtAlert();
					//alert's id
					DtAlertID aId = new DtAlertID(new PtString(
							res.getString("alerts.id")));

					//alert's status -> [pending, valid, invalid]
					String theStatus = res.getString("alerts.status");
					EtAlertStatus aStatus = null;
					if (theStatus.equals(EtAlertStatus.pending.name()))
						aStatus = EtAlertStatus.pending;
					if (theStatus.equals(EtAlertStatus.invalid.name()))
						aStatus = EtAlertStatus.invalid;
					if (theStatus.equals(EtAlertStatus.valid.name()))
						aStatus = EtAlertStatus.valid;
					
					//alert's corruption -> [regular,corrupted,restored]
					String theCorruption = res.getString("corruption");
					EtAlertCorruptionKind aCorruption = null;
					if (theCorruption.equals(EtAlertCorruptionKind.regular.name()))
						aCorruption = EtAlertCorruptionKind.regular;
					if (theCorruption.equals(EtAlertCorruptionKind.restored.name()))
						aCorruption = EtAlertCorruptionKind.restored;
					if (theCorruption.equals(EtAlertCorruptionKind.corrupted.name()))
						aCorruption = EtAlertCorruptionKind.corrupted;

					//alert's location
					DtLatitude aDtLatitude = new DtLatitude(new PtReal(
							res.getDouble("alerts.latitude")));
					DtLongitude aDtLongitude = new DtLongitude(new PtReal(
							res.getDouble("alerts.longitude")));
					DtGPSLocation aDtGPSLocation = new DtGPSLocation(
							aDtLatitude, aDtLongitude);

					//alert's instant
					Timestamp instant = res.getTimestamp("alerts.instant");
					Calendar cal = Calendar.getInstance();
					cal.setTime(instant);

					int d = cal.get(Calendar.DATE);
					int m = cal.get(Calendar.MONTH);
					int y = cal.get(Calendar.YEAR);
					DtDate aDtDate = ICrashUtils.setDate(y, m, d);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int sec = cal.get(Calendar.SECOND);
					DtTime aDtTime = ICrashUtils.setTime(h, min, sec);
					DtDateAndTime aInstant = new DtDateAndTime(aDtDate, aDtTime);

					//alert's comment  
					DtComment aDtComment = new DtComment(new PtString(
							res.getString("alerts.comment")));
					
					if (alertHashCode(aId, theStatus, aDtGPSLocation, aInstant, aDtComment).equals(res.getString("alerts.crc")) ){
						aCtAlert.init(aId, aStatus, aDtGPSLocation, aInstant,
								aDtComment, aCorruption);
						
					} else {
						aCtAlert = getInfo(aId.value.getValue(), backupConn, dbBackUpName);
					}

					//*************************************
					aCtHuman = new CtHuman();
					//human's id
					DtPhoneNumber aId1 = new DtPhoneNumber(new PtString(
							res.getString("phone")));
					//human's kind  -> [witness,victim,anonym]
					String theKind = res.getString("kind");
					EtHumanKind aKind = null;
					if (theKind.equals(EtHumanKind.witness.name()))
						aKind = EtHumanKind.witness;
					if (theKind.equals(EtHumanKind.victim.name()))
						aKind = EtHumanKind.victim;
					if (theKind.equals(EtHumanKind.anonym.name()))
						aKind = EtHumanKind.anonym;

					aCtHuman.init(aId1, aKind);

					//add instances to the hash
					assCtAlertCtHuman.put(aCtAlert, aCtHuman);

				}

			} catch (SQLException s) {
				log.error("SQL statement is not executed!7 " + s);
			}
			conn.close();
			log.debug("Disconnected from database");

		} catch (Exception e) {
			logException(e);
			System.out.println("12");
		}

		return assCtAlertCtHuman;

	}

	/**
	 * Deletes an alert from the database, it will use the ID from the CtAlert to delete it.
	 *
	 * @param aCtAlert The alert to delete from the database
	 */
	static public void deleteAlert(CtAlert aCtAlert) {

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			backupConn = DriverManager
					.getConnection(url + dbBackUpName, userName, password);
			log.debug("Connected to the backup database"); 
			/********************/
			//Delete

			try {
				String sql = "DELETE FROM " + dbName + ".alerts WHERE id = ?";
				String backupSql = "DELETE FROM " + dbBackUpName + ".alerts WHERE id = ?";
				String id = aCtAlert.id.value.getValue();

				PreparedStatement statement = conn.prepareStatement(sql);
				PreparedStatement backupSt = backupConn.prepareStatement(backupSql);
				statement.setString(1, id);
				backupSt.setString(1, id);
				int rows = statement.executeUpdate();
				int backRows = backupSt.executeUpdate();
				log.debug(rows + " row deleted");
				log.debug(backRows + " row deleted");
			} catch (SQLException s) {
				log.error("SQL statement is not executed!8 " + s);
			}

			conn.close();
			backupConn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
			System.out.println("10");
		}
	}

	/**
	 * Binds a crisis onto an alert in the database.
	 *
	 * @param aCtAlert The alert to bind the crisis to
	 * @param aCtCrisis The crisis to bind to to the alert
	 */
	static public void bindAlertCrisis(CtAlert aCtAlert, CtCrisis aCtCrisis) {
		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			backupConn = DriverManager
					.getConnection(url + dbBackUpName, userName, password);
			log.debug("Connected to the backup database"); 
			/********************/
			//Update

			try {
				String sql = "UPDATE " + dbName
						+ ".alerts SET crisis =?, `crc` = ?, `corruption` = ? WHERE id = ?";
				String backupSql = "UPDATE " + dbBackUpName
						+ ".alerts SET crisis =?, `crc` = ?, `corruption` = ? WHERE id = ?";
				String id = aCtAlert.id.value.getValue();
				String crisiId = aCtCrisis.id.value.getValue();
				String crc = alertHashCode( aCtAlert.id, aCtAlert.status.toString(),
						aCtAlert.location, aCtAlert.instant, aCtAlert.comment);
				String corruption = EtAlertCorruptionKind.regular.toString();

				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, crisiId);
				statement.setString(2, crc);
				statement.setString(3, corruption);
				statement.setString(4, id);
				int rows = statement.executeUpdate();
				log.debug(rows + " row affected");
				
				PreparedStatement backupStatement = backupConn.prepareStatement(backupSql);
				backupStatement.setString(1, crisiId);
				backupStatement.setString(2, crc);
				backupStatement.setString(3, corruption);
				backupStatement.setString(4, id);
				int backupRows = backupStatement.executeUpdate();
				log.debug(backupRows + " row affected");
			} catch (SQLException s) {
				log.error("SQL statement is not executed!9 " + s);
			}
			backupConn.close();
			conn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
			System.out.println("2");
		}

	}

	/**
	 * Binds a human onto an alert in the database.
	 *
	 * @param aCtAlert The alert to bind the human to
	 * @param aCtHuman The human to bind to the alert
	 */
	static public void bindAlertHuman(CtAlert aCtAlert, CtHuman aCtHuman) {
		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			backupConn = DriverManager
					.getConnection(url + dbBackUpName, userName, password);
			log.debug("Connected to the backup database"); 
			/********************/
			//Update

			bindAlertHuman(dbName, conn, aCtAlert, aCtHuman);
			bindAlertHuman(dbBackUpName, backupConn, aCtAlert, aCtHuman);

			conn.close();
			backupConn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
			System.out.println("3");
		}

	}

	
	static private void bindAlertHuman(String AdbName, Connection aConn, CtAlert aCtAlert, CtHuman aCtHuman){
		try {
		String sql = "UPDATE " + AdbName
				+ ".alerts SET human =?, `crc` = ?, `corruption` = ? WHERE id = ?";
		String id = aCtAlert.id.value.getValue();
		String humanPhone = aCtHuman.id.value.getValue();
		String crc = alertHashCode( aCtAlert.id, aCtAlert.status.toString(),
				aCtAlert.location, aCtAlert.instant, aCtAlert.comment);
		String corruption = EtAlertCorruptionKind.regular.toString();

		PreparedStatement statement = aConn.prepareStatement(sql);
		statement.setString(1, humanPhone);
		statement.setString(2, crc);
		statement.setString(3, corruption);
		statement.setString(4, id);
		int rows = statement.executeUpdate();
		log.debug(rows + " row affected");
		} catch (SQLException s) {
			log.error("SQL statement is not executed!10 " + s);
		}
	}
	/**
	 * Updates a alert in the database to have the same details as the CtAlert
	 * It will update the alert with the same ID as the ID in the CtAlert.
	 *
	 * @param aCtAlert The alert to update
	 */
	static public void updateAlert(CtAlert aCtAlert) {

		try {
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			log.debug("Connected to the database");

			backupConn = DriverManager
					.getConnection(url + dbBackUpName, userName, password);
			log.debug("Connected to the backup database"); 
			/********************/
			//Update

			try {
				log.debug("[DATABASE]-Update alert");
				String sql = "UPDATE "
						+ dbName
						+ ".alerts SET `status` = ?, `latitude` = ?, `longitude` = ?,"
						+ " `instant` = ?, `comment` = ?, `crc` = ?, `corruption` = ? WHERE id = ?";
				String backupSql = "UPDATE "
						+ dbBackUpName
						+ ".alerts SET `status` = ?, `latitude` = ?, `longitude` = ?,"
						+ " `instant` = ?, `comment` = ?, `crc` = ?, `corruption` = ? WHERE id = ?";
				String id = aCtAlert.id.value.getValue();
				String status = aCtAlert.status.toString();
				String corruption = aCtAlert.corruption.toString();
				double latitude = aCtAlert.location.latitude.value.getValue();
				double longitude = aCtAlert.location.longitude.value.getValue();

				int year = aCtAlert.instant.date.year.value.getValue();
				int month = aCtAlert.instant.date.month.value.getValue();
				int day = aCtAlert.instant.date.day.value.getValue();

				int hour = aCtAlert.instant.time.hour.value.getValue();
				int min = aCtAlert.instant.time.minute.value.getValue();
				int sec = aCtAlert.instant.time.second.value.getValue();

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Calendar calendar = new GregorianCalendar(year, month, day,
						hour, min, sec);
				String instant = sdf.format(calendar.getTime());

				String comment = aCtAlert.comment.value.getValue();
				
				String crc = alertHashCode( aCtAlert.id, aCtAlert.status.toString(),
						aCtAlert.location, aCtAlert.instant, aCtAlert.comment);

				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, status);
				statement.setDouble(2, latitude);
				statement.setDouble(3, longitude);
				statement.setString(4, instant);
				statement.setString(5, comment);
				statement.setString(6, crc);
				statement.setString(7, corruption);
				statement.setString(8, id);
				int rows = statement.executeUpdate();
				log.debug(rows + " row affected");
				PreparedStatement backupStatement = conn.prepareStatement(backupSql);
				backupStatement.setString(1, status);
				backupStatement.setDouble(2, latitude);
				backupStatement.setDouble(3, longitude);
				backupStatement.setString(4, instant);
				backupStatement.setString(5, comment);
				backupStatement.setString(6, crc);
				backupStatement.setString(7, corruption);
				backupStatement.setString(8, id);	
				int backupRows = backupStatement.executeUpdate();
				log.debug(backupRows + " row affected");
			} catch (SQLException s) {
				log.error("SQL statement is not executed!11 " + s);
			}
			backupConn.close();
			conn.close();
			log.debug("Disconnected from database");
		} catch (Exception e) {
			logException(e);
			System.out.println("4*20+10+7");
		}

	}

}
