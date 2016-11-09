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
import java.util.ArrayList;
import lu.uni.lassy.excalibur.examples.icrash.dev.controller.exceptions.ServerNotBoundException;
import lu.uni.lassy.excalibur.examples.icrash.dev.controller.exceptions.ServerOfflineException;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.CtVictim;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtCrisisID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.utils.Log4JUtils;
import lu.uni.lassy.excalibur.examples.icrash.dev.model.Server;

/**
 * The Victim controller deals with any functions to do with Victims in the system. This entails retrieval,
 * of data. It extends the AbstractController to handle checking of Dt information is correct
 */
public class VictimController {
	
	/** Parameter that allows the system to gain server access, the server function lives in the model of the client and  has RMI calls to access the server. */
	private Server server = Server.getInstance();
	
	/**
	 * Returns a list of all Victim in the system.
	 *
	 * @return Returns an ArrayList of type CtVictim, which contains all Victims currently within the iCrashSystem
	 * @throws ServerOfflineException is an error that is thrown when the server is offline or not reachable
	 * @throws ServerNotBoundException is only thrown when attempting to access a server which has no current binding. This shouldn't happen, but you never know!
	 */
	public ArrayList<CtVictim> getAllVictims() throws ServerOfflineException, ServerNotBoundException{
		try {
//			if (server.sys().getAllCtVictims().isEmpty())
//				System.out.println("NOTHING IN LIST!!!!!!!!!!!!!!!!!!!!");
//			else System.out.println("SMTH IN LIST!!!!!!!!!!!!!!!!!!!!");
			return server.sys().getAllCtVictims();
		} catch (RemoteException e) {
			Log4JUtils.getInstance().getLogger().error(e);
			throw new ServerOfflineException();
		} catch (NotBoundException e) {
			Log4JUtils.getInstance().getLogger().error(e);
			throw new ServerNotBoundException();
		}
	}
	
	/**
	 * Returns a list of all Victim of the crisis.
	 *
	 * @return Returns an ArrayList of type CtVictim, which contains all Victims of the crisis
	 * @throws ServerOfflineException is an error that is thrown when the server is offline or not reachable
	 * @throws ServerNotBoundException is only thrown when attempting to access a server which has no current binding. This shouldn't happen, but you never know!
	 */
	public ArrayList<CtVictim> getCrisisVictims(DtCrisisID aCrisis_id) throws ServerOfflineException, ServerNotBoundException{
		try {
//			if (server.sys().getAllCtVictims().isEmpty())
			System.out.println("Hilory FOR THE WIN!111!!!!!!!!!!!!!!!!!!!!");
			System.out.println(server.sys().getCrisisCtVictims(aCrisis_id));
			return server.sys().getCrisisCtVictims(aCrisis_id);
		} catch (RemoteException e) {
			Log4JUtils.getInstance().getLogger().error(e);
			throw new ServerOfflineException();
		} catch (NotBoundException e) {
			Log4JUtils.getInstance().getLogger().error(e);
			throw new ServerNotBoundException();
		}
	}
	
	/**
	 * Closes the server connection that is open at the moment.
	 */
	public void closeServerConnection(){
		server.disconnectServer();
	}
}
