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
package lu.uni.lassy.excalibur.examples.icrash.dev.view.gui.hospital;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.environment.actors.ActHospital;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary.DtHospitalID;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.utils.Log4JUtils;
import lu.uni.lassy.excalibur.examples.icrash.dev.view.gui.abstractgui.CreatedWindows;

import java.net.URL;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The Class MainICrashGUI that allows creation of a Hospital iCrash window.
 */
public class CreateICrashHospitalGUI implements CreatedWindows {
	
	/**
	 * Instantiates a new window for the Hospital to use iCrash.
	 *
	 * @param aDtHospitalID the ID of the Hospital associated with this window
	 * @param aActHospital the actor associated with this window
	 */
	public CreateICrashHospitalGUI(DtHospitalID aDtHospitalID, ActHospital aActHospital) {
		this._aDtHospitalID = aDtHospitalID;
		start(aActHospital);
	}

	/** The Hospital ID that was used to create this window, it's used to work out which windows to close when a Hospital has been deleted. */
	private DtHospitalID _aDtHospitalID;
	
	/**
	 * Gets the data type of the Hospital's ID.
	 *
	 * @return the datatype of the Hospital's ID
	 */
	public DtHospitalID getDtHospitalID(){
		return this._aDtHospitalID;
	}
	
	/** The stage that the form will be held in. */
	private Stage stage;
	
	/**
	 * Starts the system and opens the window up (If no exceptions have been thrown).
	 *
	 * @param aActHospital the actor associated with this window
	 */
	private void start(ActHospital aActHospital) {
		try {
			URL url = this.getClass().getResource("ICrashHospitalGUI.fxml");
			FXMLLoader loader = new FXMLLoader(url);
			Parent root = (Parent)loader.load();
            stage = new Stage();
            stage.setTitle("iCrash Hospital - " + aActHospital.getLogin().value.getValue());
            stage.setScene(new Scene(root));
            stage.show();
            ((ICrashHospitalGUIController)loader.getController()).setActor(aActHospital);
            ((ICrashHospitalGUIController)loader.getController()).setWindow(stage);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					((ICrashHospitalGUIController)loader.getController()).closeForm();
				}
			});
		} catch(Exception e) {
			Log4JUtils.getInstance().getLogger().error(e);
		}
	}
	
	/**
	 * Allows the other screens to force this window to close. Will be used if the Hospital has been deleted
	 */
	@Override
	public void closeWindow(){
		if (stage != null)
			stage.close();
	}
}
