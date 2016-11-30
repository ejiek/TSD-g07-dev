/*******************************************************************************_t7 recomment
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

package lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtAlert;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtCrisis;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtInjury;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtVictim;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.EtCrisisStatus;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;

/**
 * The Interface ActProxyCoordinator that allows the client to access the server via RMI.
 */
public interface ActProxyHospital extends ActProxyAuthenticated {


	/**
	 * Requests a crisis to be sent to the coordinator with the same status as the one provided.
	 *
	 * @param aEtCrisisStatus The crises with this status type will be retrieved
	 * @return The success of the method
	 * @throws RemoteException Thrown if the server is offline
	 * @throws NotBoundException Thrown if the server has not been bound correctly in RMI settings
	 */
	public PtBoolean oeGetCrisisSet(EtCrisisStatus aEtCrisisStatus) throws RemoteException, NotBoundException;
	
//	/**
//	 * Requests a crisis to be sent to the coordinator with the same status as the one provided.
//	 *
//	 * @param aEtCrisisStatus The crises with this status type will be retrieved
//	 * @return The success of the method
//	 * @throws RemoteException Thrown if the server is offline
//	 * @throws NotBoundException Thrown if the server has not been bound correctly in RMI settings
//	 */
	public PtBoolean oeGetVictimSet() throws RemoteException, NotBoundException;

	/**
	 * A message and crisis is received by the user.
	 *
	 * @param aCtCrisis The crisis received by the user
	 * @return The success of the method
	 * @throws RemoteException Thrown if the server is offline
	 */
	public PtBoolean ieSendACrisis(CtCrisis aCtCrisis) throws RemoteException;
	
//	/**
//	 * A message and crisis is received by the user.
//	 *
//	 * @param aCtCrisis The crisis received by the user
//	 * @return The success of the method
//	 * @throws RemoteException Thrown if the server is offline
//	 */
	public PtBoolean ieSendAVictim(CtVictim aCtVictim) throws RemoteException;
	
//	/**
//	 * A message and crisis is received by the user.
//	 *
//	 * @param aCtCrisis The crisis received by the user
//	 * @return The success of the method
//	 * @throws RemoteException Thrown if the server is offline
//	 */
	public PtBoolean ieSendAInjury(CtInjury aCtInjury) throws RemoteException;
	
	/**
	 * A message and alert is received by the user.
	 *
	 * @param aCtAlert The alert received by the user
	 * @return The success of the method
	 * @throws RemoteException Thrown if the server is offline
	 */
	public PtBoolean ieSendAnAlert(CtAlert aCtAlert) throws RemoteException;

}
