package ai.iliSuite.impl;

import java.util.Map;

import ai.iliSuite.impl.dbconn.Ili2DbScope;
import javafx.scene.Parent;

public interface ImplFactory {
	public String getAppName();
	public String getAppVersion();
	public String getNameDB();
	public String getHelpText();
	public Parent getDbConfigPanel();
	public void loadDbConfigPanel(boolean createSchema);
	public Map<String,String> getConnectionsParams();
	public Ili2DbScope getScope();
	public int runMain(String[] args);
	public Map<EnumCustomPanel, PanelCustomizable> getCustomPanels();
}