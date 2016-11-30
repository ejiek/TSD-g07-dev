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
package lu.uni.lassy.excalibur.examples.icrash.dev.controller;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import lu.uni.lassy.excalibur.examples.icrash.dev.controller.exceptions.ServerNotBoundException;
import lu.uni.lassy.excalibur.examples.icrash.dev.controller.exceptions.ServerOfflineException;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyAuthenticated.UserType;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActProxyHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.utils.Log4JUtils;
import lu.uni.lassy.excalibur.examples.icrash.dev.model.actors.ActProxyHospitalImpl;

/**
 * The Class HospitalController.
 */
public class HospitalController extends AbstractUserController {

	/**
	 * Instantiates a new hospital controller.
	 *
	 * @param aActHospital the a act hospital
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 */
	public HospitalController(ActHospital aActHospital ) throws RemoteException, NotBoundException{
		super(new ActProxyHospitalImpl(aActHospital));
	}
	
	/**
	 * Gets a list of all crises from the server with the status type of the one provided
	 * The list will be sent to the actor directly, via the ie method.
	 *
	 * @param aEtCrisisStatus The status of the crisis to filter by
	 * @return The success of the method
	 * @throws ServerOfflineException Thrown if the server is offline
	 * @throws ServerNotBoundException Thrown if the server is not bound
	 */
	public PtBoolean oeGetCrisisSet(EtCrisisStatus aEtCrisisStatus) throws ServerOfflineException, ServerNotBoundException{
		if (this.getUserType() == UserType.Hospital){

			ActProxyHospital actHospital = (ActProxyHospital)this.getAuth();
			try {
				return actHospital.oeGetCrisisSet(aEtCrisisStatus);
			} catch (RemoteException e) {
				Log4JUtils.getInstance().getLogger().error(e);
				throw new ServerOfflineException();
			} catch (NotBoundException e) {
				Log4JUtils.getInstance().getLogger().error(e);
				throw new ServerNotBoundException();
			}
		}
		return new PtBoolean(false);
	}
	
	
//	/**
//	 * Gets a list of all crises from the server with the status type of the one provided
//	 * The list will be sent to the actor directly, via the ie method.
//	 *
//	 * @param aEtCrisisStatus The status of the crisis to filter by
//	 * @return The success of the method
//	 * @throws ServerOfflineException Thrown if the server is offline
//	 * @throws ServerNotBoundException Thrown if the server is not bound
//	 */
	public PtBoolean oeGetVictimSet() throws ServerOfflineException, ServerNotBoundException{
		if (this.getUserType() == UserType.Hospital){
			ActProxyHospital actHospital = (ActProxyHospital)this.getAuth();
			try {
				return actHospital.oeGetVictimSet();
			} catch (RemoteException e) {
				Log4JUtils.getInstance().getLogger().error(e);
				throw new ServerOfflineException();
			} catch (NotBoundException e) {
				Log4JUtils.getInstance().getLogger().error(e);
				throw new ServerNotBoundException();
			}
		}
		return new PtBoolean(false);
	}
}
