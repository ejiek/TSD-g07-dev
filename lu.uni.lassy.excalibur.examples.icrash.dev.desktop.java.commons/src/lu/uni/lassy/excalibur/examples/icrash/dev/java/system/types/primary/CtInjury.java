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
package lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary;

import java.io.Serializable;
import java.rmi.RemoteException;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActCoordinator;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;


/**
 * The Class CtCrisis that holds the crisis details.
 */
public class CtInjury implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 227L;

	/** The id of the Injury. */
	public DtInjuryID id;
	/** The id of the Victim. */
	public DtVictimID victimID;
	
	/** The injuried part of body  */
	public EtInjuryKind kind;

	/**
	 * Initializes the Injury.
	 *
	 * @param aId the id of the injury
	 * @param aVictimId of the related Crisis
	 * @param kind - injured part of body
	 * @return the success of the initialization
	 */
	public PtBoolean init(DtInjuryID aId, DtVictimID aVictimId, EtInjuryKind kind) {

		id = aId;
		victimID = aVictimId;
		this.kind = kind;

		return new PtBoolean(true);

	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.id.value.getValue()+"+"+this.victimID.value.getValue()+"+"+this.kind.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof CtInjury))
			return false;
		CtInjury aCtInjury = (CtInjury)obj;
		if (!aCtInjury.id.value.getValue().equals(this.id.value.getValue()))
			return false;
		if (!aCtInjury.victimID.value.getValue().equals(this.victimID.value.getValue()))
			return false;
		if (!aCtInjury.kind.equals(this.kind))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return this.id.value.getValue().length();
	}
	
//	/**
//	 * Used to provide a given coordinator with current alert information.
//	 *
//	 * @param aActCoordinator the actor coordinator to send the information to
//	 * @return the success of the method
//	 * @throws RemoteException Thrown if the remote destination is unreachable
//	 */
	public PtBoolean isSentToCoordinator(ActCoordinator aActCoordinator) throws RemoteException {
		
		aActCoordinator.ieSendAInjury(this);
		return new PtBoolean(true);
	}
	
//	/**
//	 * Used to provide a given hospital with current alert information.
//	 *
//	 * @param aActHospital the actor hospital to send the information to
//	 * @return the success of the method
//	 * @throws RemoteException Thrown if the remote destination is unreachable
//	 */
	public PtBoolean isSentToHospital(ActHospital aActHospital) throws RemoteException {
		
		aActHospital.ieSendAInjury(this);
		return new PtBoolean(true);
	}
}
