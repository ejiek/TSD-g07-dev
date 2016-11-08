/******************************************************************************* _t7
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
package lu.uni.lassy.excalibur.examples.icrash.dev.model.actors;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtAlert;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtCrisis;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtAlertID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtComment;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtAlertStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisType;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.utils.Log4JUtils;
import lu.uni.lassy.excalibur.examples.icrash.dev.model.Message;
import lu.uni.lassy.excalibur.examples.icrash.dev.model.Message.MessageType;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * The Class ActProxyHospitalImpl, the client side actor class for Hospitals.
 */
public class ActProxyHospitalImpl extends ActProxyAuthenticatedImpl
		implements ActProxyHospital {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 227L;

	/**
	 * Instantiates a new client side actor for Hospitals.
	 *
	 * @param user The server side actor to be associated with
	 * @throws RemoteException Thrown if the server is offline
	 * @throws NotBoundException Thrown if the server has not been bound in the RMI settings
	 */
	public ActProxyHospitalImpl(ActHospital user)
			throws RemoteException, NotBoundException {
		super(user);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeGetCrisisSet(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus)
	 */
	synchronized public PtBoolean oeGetCrisisSet(EtCrisisStatus aEtCrisisStatus)
			throws RemoteException, NotBoundException {
		if (getServerSideActor() != null){
			MapOfCtCrisis.clear();
			
			return ((ActHospital) getServerSideActor())
					.oeGetCrisisSet(aEtCrisisStatus);
		}
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeGetAlertsSet(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtAlertStatus)
	 */
	synchronized public PtBoolean oeGetAlertsSet(EtAlertStatus aEtAlertStatus)
			throws RemoteException, NotBoundException {
		if (getServerSideActor() != null){
			MapOfCtAlerts.clear();
			return ((ActHospital) getServerSideActor())
					.oeGetAlertsSet(aEtAlertStatus);
		}
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeSetCrisisHandler(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID)
	 */
	synchronized public PtBoolean oeSetCrisisHandler(DtCrisisID aDtCrisisID)
			throws RemoteException, NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor())
					.oeSetCrisisHandler(aDtCrisisID);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeValidateAlert(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtAlertID)
	 */
	synchronized public PtBoolean oeValidateAlert(DtAlertID aDtAlertID)
			throws RemoteException, NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor())
					.oeValidateAlert(aDtAlertID);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeInvalidateAlert(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtAlertID)
	 */
	synchronized public PtBoolean oeInvalidateAlert(DtAlertID aDtAlertID)
			throws RemoteException, NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor())
					.oeInvalidateAlert(aDtAlertID);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeSetCrisisStatus(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID, lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus)
	 */
	synchronized public PtBoolean oeSetCrisisStatus(DtCrisisID aDtCrisisID,
			EtCrisisStatus aEtCrisisStatus) throws RemoteException,
			NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor()).oeSetCrisisStatus(
					aDtCrisisID, aEtCrisisStatus);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeSetCrisisType(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID, lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisType)
	 */
	synchronized public PtBoolean oeSetCrisisType(DtCrisisID aDtCrisisID,
			EtCrisisType aEtCrisisType) throws RemoteException,
			NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor()).oeSetCrisisType(
					aDtCrisisID, aEtCrisisType);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeReportOnCrisis(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID, lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtComment)
	 */
	synchronized public PtBoolean oeReportOnCrisis(DtCrisisID aDtCrisisID,
			DtComment aDtComment) throws RemoteException, NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor()).oeReportOnCrisis(
					aDtCrisisID, aDtComment);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#oeCloseCrisis(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID)
	 */
	synchronized public PtBoolean oeCloseCrisis(DtCrisisID aDtCrisisID)
			throws RemoteException, NotBoundException {

		if (getServerSideActor() != null)
			return ((ActHospital) getServerSideActor())
					.oeCloseCrisis(aDtCrisisID);
		else
			return new PtBoolean(false);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#ieSendACrisis(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtCrisis)
	 */
	public PtBoolean ieSendACrisis(CtCrisis aCtCrisis) {
		Logger log = Log4JUtils.getInstance().getLogger();
		log.info("message ActHospital.ieSendACrisis received from system");
		log.info("crisis id '" + aCtCrisis.id.value.getValue().toString()
				+ "' " + "in status '" + aCtCrisis.status.toString() + "'");
		listOfMessages.add(new Message(MessageType.ieSendACrisis, "Crisis " + aCtCrisis.id.value.getValue() + " was sent"));
		this.MapOfCtCrisis.put(aCtCrisis.id.value.getValue(), aCtCrisis);
		return new PtBoolean(true);
	}
	
	/** The list of class type crises this user has retrieved from the server. */
	private Hashtable<String, CtCrisis> _listOfCtCrisis = new Hashtable<String, CtCrisis>(); 
	
	/** The list of class type alerts this user has retrieved from the server */
	private Hashtable<String, CtAlert> _listOfCtAlerts = new Hashtable<String, CtAlert>();
	
	/** The observable map of class type crises this user has retrieved from the server. 
	 * Being observable, listeners can be attached to it to force the an action once updated*/
	public ObservableMap<String, CtCrisis> MapOfCtCrisis = FXCollections.observableMap(_listOfCtCrisis);
	
	/** The observable map of class type alerts this user has retrieved from the server. 
	 * Being observable, listeners can be attached to it to force the an action once updated*/
	public ObservableMap<String, CtAlert> MapOfCtAlerts = FXCollections.observableMap(_listOfCtAlerts);

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital#ieSendAnAlert(lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtAlert)
	 */
	public PtBoolean ieSendAnAlert(CtAlert aCtAlert) {

		Logger log = Log4JUtils.getInstance().getLogger();

		log.info("message ActHospital.ieSendAnAlert received from system");
		log.info("alert id '" + aCtAlert.id.value.getValue() + "' "
				+ " with comment '" + aCtAlert.comment.value.getValue() + "'"
				+ " in status '" + aCtAlert.status.toString() + "'");
		listOfMessages.add(new Message(MessageType.ieSendAnAlert, "Alert " + aCtAlert.id.value.getValue() + " was sent"));
		this.MapOfCtAlerts.put(aCtAlert.id.value.getValue(), aCtAlert);
		return new PtBoolean(true);
	}

	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.model.actors.ActProxyAuthenticatedImpl#oeLogout()
	 */
	@Override
	public PtBoolean oeLogout() throws RemoteException, NotBoundException {
		return super.oeLogout();
	}

}
