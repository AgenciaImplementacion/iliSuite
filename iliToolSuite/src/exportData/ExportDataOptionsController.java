package exportData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.data.AppData;
import application.dialog.MultipleSelectionDialog;
import application.util.navigation.EnumPaths;
import application.util.navigation.Navigable;
import application.util.params.EnumParams;
import application.util.params.ParamsContainer;
import application.util.plugin.PluginsLoader;
import base.IPluginDb;
import base.dbconn.Ili2DbScope;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class ExportDataOptionsController implements Navigable, Initializable {

	@FXML
	private ResourceBundle applicationBundle;
	@FXML
	private Button btn_addDataset;
	@FXML
	private Button btn_addBaskets;
	@FXML
	private Button btn_addTopics;
	@FXML
	private Button btn_addModels;
	@FXML
	private Button btn_browseXtfFile;
	@FXML
	private ToggleGroup tg_selectedScope;
	@FXML
	private RadioButton radio_dataset;
	@FXML
	private RadioButton radio_baskets;
	@FXML
	private RadioButton radio_topics;
	@FXML
	private RadioButton radio_models;
	@FXML
	private TextField tf_dataset;
	@FXML
	private TextField tf_baskets;
	@FXML
	private TextField tf_topics;
	@FXML
	private TextField tf_models;
	@FXML
	private TextField tf_xtfFilePath;
	private ArrayList<Node> disableList;

	@Override
	public boolean validate() {
		
		boolean result = validateFields();
		if(result)
			addParams();
		return result;
	}

	private void addParams() {
		ParamsContainer paramsContainer = AppData.getInstance().getParamsContainer();
		HashMap<String,String> params = paramsContainer.getParamsMap();
		
		paramsContainer.setFinalPath(tf_xtfFilePath.getText());	
		//--------------Export Options---------------------//
		if(radio_dataset.isSelected()){
			params.put(EnumParams.DATASET.getName(), tf_dataset.getText());
			params.remove(EnumParams.BASKETS.getName());
			params.remove(EnumParams.TOPICS.getName());
			params.remove(EnumParams.MODELS.getName());
		}else if(radio_baskets.isSelected()){
			params.put(EnumParams.BASKETS.getName(), tf_baskets.getText());
			params.remove(EnumParams.DATASET.getName());
			params.remove(EnumParams.TOPICS.getName());
			params.remove(EnumParams.MODELS.getName());
		}else if(radio_topics.isSelected()){
			params.put(EnumParams.TOPICS.getName(), tf_topics.getText());
			params.remove(EnumParams.DATASET.getName());
			params.remove(EnumParams.BASKETS.getName());
			params.remove(EnumParams.MODELS.getName());
		}else if(radio_models.isSelected()){
			params.put(EnumParams.MODELS.getName(), tf_models.getText());
			params.remove(EnumParams.DATASET.getName());
			params.remove(EnumParams.BASKETS.getName());
			params.remove(EnumParams.TOPICS.getName());
		}
	}

	@Override
	public EnumPaths getNextPath() {
		return EnumPaths.EXP_DATA_FINISH_DATA_EXPORT;
	}

	@Override
	public boolean isFinalPage() {
		return false;
	}

	public void handleAddButtons(ActionEvent e) throws IOException {
		Button source = (Button) e.getSource();
		
		String pluginKey = AppData.getInstance().getPlugin();
		
		// TODO Verificar si es null
		IPluginDb plugin = (IPluginDb) PluginsLoader.getPluginByKey(pluginKey);
		
		Ili2DbScope scope = plugin.getScope();
		
		if (source == btn_addDataset) {
			enableOnly(tf_dataset, btn_addDataset);
			
			try {
				ArrayList<String> selectedValues = new ArrayList<>();
				
				if(!tf_dataset.getText().isEmpty()){
					selectedValues = new ArrayList<String>(Arrays.asList(tf_dataset.getText().split(";")));
				}
				
				MultipleSelectionDialog dialog = new MultipleSelectionDialog(scope.getDatasetList(), selectedValues, SelectionMode.SINGLE);

				dialog.setTitle("* DATASET *");
				Optional<List<String>> result = dialog.showAndWait();

				if(result.isPresent()){
					tf_dataset.setText(String.join(";", result.get()));
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}else if(source == btn_addBaskets){
			enableOnly(tf_baskets, btn_addBaskets);

			try{
				List<String> baskets = scope.getBasketList();
				// TODO Debería asignarse null inicialmente
				ArrayList<String> selectedValues = new ArrayList<>();
			
				if(!tf_baskets.getText().isEmpty()){
					selectedValues = new ArrayList<String>(Arrays.asList(tf_baskets.getText().split(";")));
				}
				
				MultipleSelectionDialog dialog = 
						new MultipleSelectionDialog(baskets, selectedValues, SelectionMode.MULTIPLE);
				
				dialog.setTitle("* BASKETS *");
				
				Optional<List<String>> result = dialog.showAndWait();

				if(result.isPresent()){
					tf_baskets.setText(String.join(";", result.get()));
				}
			
			}catch(ClassNotFoundException | SQLException e1){
				e1.printStackTrace();
			}

		}else if(source == btn_addTopics){
			enableOnly(tf_topics, btn_addTopics);
			
			try{
				List<String> topics = scope.getTopicList();
				// TODO Debería asignarse null inicialmente
				ArrayList<String> selectedValues = new ArrayList<>();
			
				if(!tf_topics.getText().isEmpty()){
					selectedValues = new ArrayList<String>(Arrays.asList(tf_topics.getText().split(";")));
				}
				
				MultipleSelectionDialog dialog = 
						new MultipleSelectionDialog(topics, selectedValues, SelectionMode.MULTIPLE);
				
				dialog.setTitle("* TOPICS *");
				
				Optional<List<String>> result = dialog.showAndWait();

				if(result.isPresent()){
					tf_topics.setText(String.join(";", result.get()));
				}
			
			}catch(ClassNotFoundException | SQLException e1){
				e1.printStackTrace();
			}
		}else if(source == btn_addModels){
			enableOnly(tf_models, btn_addModels);
						try{
				List<String> models = scope.getModelList();
				// TODO Debería asignarse null inicialmente
				ArrayList<String> selectedValues = new ArrayList<>();
			
				if(!tf_models.getText().isEmpty()){
					selectedValues = new ArrayList<String>(Arrays.asList(tf_models.getText().split(";")));
				}
				
				MultipleSelectionDialog dialog = 
						new MultipleSelectionDialog(models, selectedValues, SelectionMode.MULTIPLE);
				
				dialog.setTitle("* MDOELS *");
				
				Optional<List<String>> result = dialog.showAndWait();

				if(result.isPresent()){
					tf_models.setText(String.join(";", result.get()));
				}
			
			}catch(ClassNotFoundException | SQLException e1){
				e1.printStackTrace();
			}
		}
	}
	
	@FXML
	private void onClickBrowseXtfFile(ActionEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter(applicationBundle.getString("general.file.extension.xtf"),"*.xtf"));
		fileChooser.setTitle(applicationBundle.getString("general.file.saveAs"));
		Window window = ((Node)e.getSource()).getScene().getWindow();
		File selectedFile = fileChooser.showSaveDialog(window);
		if(selectedFile!=null)
			tf_xtfFilePath.setText(selectedFile.getAbsolutePath());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		applicationBundle = resources;
		disableList = new ArrayList<>();
		disableList.add(tf_dataset);
		disableList.add(tf_baskets);
		disableList.add(tf_topics);
		disableList.add(tf_models);
		disableList.add(btn_addDataset);
		disableList.add(btn_addBaskets);
		disableList.add(btn_addTopics);
		disableList.add(btn_addModels);
		disableFields(disableList);
		addInitListeners();
		
	}
	
	private void addInitListeners(){
		tg_selectedScope.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		         if (tg_selectedScope.getSelectedToggle() == radio_dataset){
		        	 enableOnly(tf_dataset, btn_addDataset);
		        	 tf_dataset.setStyle(null);
		        	 tf_baskets.setStyle(null);
		        	 tf_models.setStyle(null);
		        	 tf_topics.setStyle(null);
		         }
		         else if(tg_selectedScope.getSelectedToggle() == radio_baskets){
		        	 enableOnly(tf_baskets, btn_addBaskets);
		        	 tf_dataset.setStyle(null);
		        	 tf_baskets.setStyle(null);
		        	 tf_models.setStyle(null);
		        	 tf_topics.setStyle(null);
		         }
		         else if(tg_selectedScope.getSelectedToggle() == radio_topics){
		        	 enableOnly(tf_topics, btn_addTopics);
		        	 tf_dataset.setStyle(null);
		        	 tf_baskets.setStyle(null);
		        	 tf_models.setStyle(null);
		        	 tf_topics.setStyle(null);
		         }
		         else if(tg_selectedScope.getSelectedToggle() == radio_models){
		        	 enableOnly(tf_models, btn_addModels);
		        	 tf_dataset.setStyle(null);
		        	 tf_baskets.setStyle(null);
		        	 tf_models.setStyle(null);
		        	 tf_topics.setStyle(null);
		         }
		     } 
		});
		tf_xtfFilePath.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue!=null && !newValue.isEmpty() && newValue.endsWith(".xtf"))
					tf_xtfFilePath.setStyle(null);
			}
		});
		tf_dataset.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue!=null && !newValue.isEmpty())
					tf_dataset.setStyle(null);
			}
		});
		tf_baskets.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue!=null && !newValue.isEmpty())
					tf_baskets.setStyle(null);
			}
		});
		tf_topics.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue!=null && !newValue.isEmpty())
					tf_topics.setStyle(null);
			}
		});
		tf_models.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue!=null && !newValue.isEmpty())
					tf_models.setStyle(null);
			}
		});
		
	}
	
	private void enableOnly(Node... fields){
		
		disableFields(disableList);
		for(Node node : fields){
			node.setDisable(false);
		}
	}
	
	private void disableFields(List<Node> disableList){
		for(Node node : disableList){
			node.setDisable(true);
		}
	}
	
	private boolean validateFields(){
		boolean result = true;
		if (tg_selectedScope.getSelectedToggle() == null) {
			tf_dataset.setStyle("-fx-border-color: red ;");
			tf_baskets.setStyle("-fx-border-color: red ;");
			tf_models.setStyle("-fx-border-color: red ;");
			tf_topics.setStyle("-fx-border-color: red ;");
			result = false;
		}else if(tg_selectedScope.getSelectedToggle()==radio_dataset && tf_dataset.getText().isEmpty()){
			tf_dataset.setStyle("-fx-border-color: red ;");
			result = false;
		}else if(tg_selectedScope.getSelectedToggle()==radio_baskets && tf_baskets.getText().isEmpty()){
			tf_baskets.setStyle("-fx-border-color: red ;");
			result = false;
		}else if(tg_selectedScope.getSelectedToggle()==radio_models && tf_models.getText().isEmpty()){
			tf_models.setStyle("-fx-border-color: red ;");
			result = false;
		}else if(tg_selectedScope.getSelectedToggle()==radio_topics && tf_topics.getText().isEmpty()){
			tf_topics.setStyle("-fx-border-color: red ;");
			result = false;
		}
		if(tf_xtfFilePath.getText() == null || tf_xtfFilePath.getText().equals("") || !tf_xtfFilePath.getText().endsWith(".xtf")){
			tf_xtfFilePath.setStyle("-fx-border-color: red ;");
			result = false;
		}
		
		return result;
	}

}