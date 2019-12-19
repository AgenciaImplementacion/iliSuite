package ai.iliSuite.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ai.iliSuite.base.IliExecutable;
import ai.iliSuite.impl.DbDescription;
import ai.iliSuite.impl.EnumCustomPanel;
import ai.iliSuite.impl.ImplFactory;
import ai.iliSuite.impl.PanelCustomizable;
import ai.iliSuite.impl.controller.IController;
import ai.iliSuite.impl.dbconn.AbstractConnection;
import ai.iliSuite.util.exception.ExitException;
import ai.iliSuite.util.params.EnumParams;
import ai.iliSuite.util.params.ParamsUtil;
import ai.iliSuite.util.plugin.PluginsLoader;
import ai.iliSuite.util.wizard.BuilderWizard;
import ai.iliSuite.view.DatabaseOptionsView;
import ai.iliSuite.view.DatabaseSelectionView;
import ai.iliSuite.view.FinishActionView;
import ai.iliSuite.view.ModelConvertOptionsView;
import ai.iliSuite.view.wizard.EmptyWizardException;
import ai.iliSuite.view.wizard.Wizard;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;

public class GeneratePhysicalModelController implements ParamsController, DbSelectorController {

	private IliExecutable model;
	private List<Map<String, String>> paramsList;
	private Wizard wizard;
	
	private DatabaseOptionsView dbSelectionScreen;
	private ModelConvertOptionsView modelConvertOptions;
	
	private EventHandler<ActionEvent> finishHandler;
	private EventHandler<ActionEvent> goBackHandler;
	
	private Map<String, DbDescription> lstDbDescription;
	
	private ImplFactory dbImpl;
	
	private Thread commandExecutionThread;
	
	public GeneratePhysicalModelController() throws IOException {
		paramsList = new ArrayList<Map<String, String>>();
		initLstDbDescription();
		initWizard();
	}

	private void initWizard() throws IOException {
		EventHandler<ActionEvent> goBack = 
				(ActionEvent e) -> { if(goBackHandler != null) { goBackHandler.handle(e); }};
		EventHandler<ActionEvent> finish = 
				(ActionEvent e) -> { if(finishHandler != null) { finishHandler.handle(e); }};
		
		dbSelectionScreen = new DatabaseOptionsView(this, this);
		modelConvertOptions = new ModelConvertOptionsView(this);

		wizard = BuilderWizard.buildMainWizard();
		
		wizard.setOnBack(goBack);
		wizard.setOnCancel(goBack);
		wizard.setOnFinish(finish);
		wizard.setOnExecute((ActionEvent e) -> execute());

		wizard.add(new DatabaseSelectionView(this, lstDbDescription));
		wizard.add(dbSelectionScreen);
		wizard.add(modelConvertOptions);
		wizard.add(new FinishActionView(this));

		try {
			wizard.init();
		} catch (EmptyWizardException e) {
			e.printStackTrace();
		}
	}
	
	// FIX Repeated code
	private void initLstDbDescription() {
		Map<String, ImplFactory> lstPlugin = PluginsLoader.getPlugins();
		lstDbDescription = new HashMap<String, DbDescription>();
		
		for(Entry<String, ImplFactory> item : lstPlugin.entrySet()) {
			ImplFactory pluginItem = (ImplFactory) item.getValue();
			DbDescription description = pluginItem.getDbDescription();
			String dbKey = item.getKey();
			
			lstDbDescription.put(dbKey, description);
		}
	}
	@Override
	public void addParams(Map<String, String> params) {
		paramsList.add(params);
	}

	@Override
	public void removeParams(Map<String, String> params) {
		paramsList.remove(params);
	}

	@Override
	public Parent getGraphicComponent() {
		return wizard.getGraphicComponent();
	}

	@Override
	public String getTextParams() {
		List<String> command = getCommand();
		return ParamsUtil.getStringArgs(command, true);
	}
	
	private List<String> getCommand(){
		Map<String, String> params = new HashMap<String, String>();
		params.put(EnumParams.SCHEMA_IMPORT.getName(), null);
		for(Map<String, String> item:paramsList) {
			params.putAll(item);
		}
		return ParamsUtil.getCommand(params);
	}

	@Override
	public boolean execute() {
		// FIX repeated body
		SimpleBooleanProperty booleanResult = new SimpleBooleanProperty();
		List<String> command = getCommand();
		
		Task<Boolean> task = new Task<Boolean>(){
			@Override
			protected Boolean call() throws Exception {
				try {
					model.run(command);
					return true;
				} catch (ExitException e) {
					System.out.println(e.status);
					return false;
				}
			}
		};
		task.setOnSucceeded(workerStateEvent ->{
			wizard.setNextDisable(false);
			if(booleanResult.getValue())
				wizard.setExecuted(true);
		});
		
		task.setOnFailed(workerStateEvent -> {
			wizard.setNextDisable(false);
		});
		
		booleanResult.bind(task.valueProperty());
		wizard.setNextDisable(true);
		
		commandExecutionThread = new Thread(task);
		commandExecutionThread.start();
		
		return true;
	}

	@Override
	public void setOnFinish(EventHandler<ActionEvent> handler) {
		this.finishHandler = handler;
	}
	
	@Override
	public void setOnGoBack(EventHandler<ActionEvent> handler) {
		this.goBackHandler = handler;
	}

	@Override
	public void databaseSelected(String dbKey) {
		dbImpl = PluginsLoader.getPluginByKey(dbKey);
		model = dbImpl.getInterlisExecutable();
		
		AbstractConnection connection = dbImpl.getConnector();
		IController dbPanel;
		try {
			dbPanel = dbImpl.getController(connection, true);
			dbSelectionScreen.setDbPanel(dbPanel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PanelCustomizable customPanel = dbImpl.getCustomPanel(EnumCustomPanel.SCHEMA_IMPORT);

		if(customPanel != null) {
			modelConvertOptions.setCustomPanel(customPanel);
		}
	}

	@Override
	public boolean databaseConnecting(Map<String, String> connectionParams) {
		// TODO Auto-generated method stub
		return true;
	}

}