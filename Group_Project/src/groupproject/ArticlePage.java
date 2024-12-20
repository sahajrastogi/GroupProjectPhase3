package groupproject;

import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <p> Class that creates a page for password reset and validation </p>
 */
public class ArticlePage {

	public Scene scene;
	public Button btn;
	public String title = "Help Item List";
	public Button back;
	
	public String role;
	public User u;
    public TextField userField;
    public PasswordField passwordField;
    public PasswordField cPasswordField;
    
    public String lastGroup = "";
    public String lastSearchText = "";
    public String lastLevel = "All";
    
    //List View of articles
    public ObservableList<String> items = FXCollections.observableArrayList();
    public ArrayList<Integer> ids = new ArrayList<>();
    
    
    //Operation Buttons and containing VBox
    public Button create = new Button("Create");
    public Button view = new Button("View");
    public Button delete = new Button("Delete");
    public Button update = new Button("Update");
    public Button backup = new Button("Backup");
    public Button restore = new Button("Restore");

    public VBox buttons = new VBox();
    
    //default empty display
    public VBox display = new VBox();
    //right column of this container gets updated on the button clicks
    public HBox twoColumns = new HBox();

    
    /**
     * constructor that lays out the GUI
     */
	public ArticlePage() {
		if(u == null) u = App.users.get(0);
		role = "admin";
		GridPane grid = new GridPane();
	    grid.setPadding(new Insets(10, 10, 10, 10));
		
	    back = new Button("Back");
	    
        Label helpItemListLabel = new Label("Help Items:");
        
        updateList();
        ListView<String> itemList = new ListView<>(items);
        
        
        buttons.setSpacing(20);
        buttons.getChildren().addAll(view,create,update,delete,backup,restore);
        
        
        //Search Buttons and containers
        Label searchGroupLabel = new Label("Group Specifier:        ");
        TextField searchGroupField = new TextField();
        searchGroupField.setPromptText("Leave empty for none");
        HBox searchGroupBox = new HBox();
        searchGroupBox.getChildren().addAll(searchGroupLabel, searchGroupField);
        Button search = new Button("Search");
        GridPane.setHalignment(search, HPos.RIGHT);
        
        Label searchTextLabel = new Label("Search:                      ");
        TextField searchTextField = new TextField();

        HBox searchTextBox = new HBox();
        searchTextBox.getChildren().addAll(searchTextLabel, searchTextField);
        
        
        ChoiceBox<String> searchLevelBox = new ChoiceBox<>();
        searchLevelBox.setValue("All");
        searchLevelBox.getItems().addAll("Beginner", "Intermediate", "Advanced","All");
        GridPane.setHalignment(searchLevelBox, HPos.RIGHT);

        //Add containers to the main page
        grid.add(searchTextBox, 0, 0);
        grid.add(searchGroupBox, 0, 1);
        grid.add(searchLevelBox, 0, 2);
        grid.add(search, 0, 3);
        grid.add(helpItemListLabel, 0, 4);
        grid.add(itemList, 0, 5);
        grid.add(buttons, 1, 5);
        grid.add(back, 0, 20);
        
        
        
        twoColumns.setSpacing(125);
        twoColumns.getChildren().addAll(grid,display);
        
        //Container for the view button
        VBox viewDisplay = new VBox();
        viewDisplay.setSpacing(10);
 
        //display labels for view button
        Label titleLabel = new Label("Title: ");
        Label descriptionLabel = new Label("Description: ");
        Label authorLabel = new Label("Author: ");
        Label bodyLabel = new Label("Body: ");
        Label keywordsLabel = new Label("Keywords: ");
        Label groupLabel = new Label("Groups: ");
        Label linkLabel = new Label("Links: ");
       
        //on click action for the view button
        view.setOnAction(e ->{
        	int idx = itemList.getSelectionModel().getSelectedIndex();
        	if(idx == -1) return;
        	bodyLabel.setWrapText(true);
            bodyLabel.setPrefWidth(500);
            
            //Set field values and display containers
        	viewDisplay.getChildren().clear();
            viewDisplay.getChildren().addAll(new Label(),titleLabel, descriptionLabel, authorLabel, bodyLabel, keywordsLabel, linkLabel, groupLabel);

            
            int uniq = ids.get(idx);
            HelpItem h = null;
        	for(HelpItem htemp : App.items) {
        		if(htemp.id == uniq) h = htemp;
        	}
        	if(h == null) return;
        	titleLabel.setText("Title: " + h.title);
        	descriptionLabel.setText("Description: " + h.description);
        	authorLabel.setText("Author(s): " + h.author);
        	if(!role.equals("Admin")) bodyLabel.setText("Body: " + h.body);
        	else bodyLabel.setText("Body: " + "You are not allowed to see the body of this article.");
        	keywordsLabel.setText("Keywords: " + HelpItem.listToStringPretty(h.keywords));
        	linkLabel.setText("Links: " + HelpItem.listToStringPretty(h.links));
        	groupLabel.setText("Groups: " + HelpItem.listToStringPretty(h.groups));            
        	twoColumns.getChildren().set(1, viewDisplay);
        });
        
        
        //on click for the delete button
        delete.setOnAction(e->{
        	//Delete confirmation
        	Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete this article?");
            Optional<ButtonType> result = alert.showAndWait();
            boolean res = result.isPresent() && result.get() == ButtonType.OK;
            if(!res) return;
            
            //Perform the deletion
        	int idx = itemList.getSelectionModel().getSelectedIndex();
        	if(idx == -1) return;
        	int itemIndex = ids.get(idx);
        	HelpItem.removeByID(itemIndex);
        	updateList();

        });
        
        //Entry fields for creating and updating help items
        TextField titleEntry = new TextField();
        TextField descriptionEntry = new TextField();
        TextField authorEntry = new TextField();
        TextArea bodyEntry = new TextArea();
        TextField keywordsEntry = new TextField();
        TextField linkEntry = new TextField();
        TextField groupEntry = new TextField();
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setValue("Beginner");
        choiceBox.getItems().addAll("Beginner", "Intermediate", "Advanced");
        keywordsEntry.setPromptText("Enter a comma-separated list");
    	linkEntry.setPromptText("Enter a comma-separated list");
    	groupEntry.setPromptText("Enter a comma-separated list");

    	//create button containers and buttons
        VBox createDisplay = new VBox();
        GridPane fieldGrid = new GridPane();
	    Button createButton = new Button("Create");
	    
	    //on click action for the create button in side the container
	    createButton.setOnAction(e->{
	    	//Ensure that critical fields are filled in
	    	if(titleEntry.getText().equals("") || descriptionEntry.getText().equals("") || bodyEntry.getText().equals("")) {
	    		Alert alert = new Alert(AlertType.ERROR);
        		alert.setHeaderText("Error");
        		alert.setContentText("Title, Description, or Body is missing");
        		alert.showAndWait();
        		return;
	    	}
	    	
	    	for(String g : HelpItem.prettyStringToList(groupEntry.getText())) {
	    		boolean exists = false;
	    		for(User us : App.users) {
	    			if(App.checkAdminAccess(us.username, g)) {
	    				exists = true;
	    			}
	    		}
	    		if(!exists) {
	    			App.adminMapAdd(u.username, g);
	    			App.viewMapAdd(u.username, g);
	    		}
	    	}
	    	
	    	boolean flag = false;
	    	for(String g : HelpItem.prettyStringToList(groupEntry.getText())) {
	    		if(!App.checkAdminAccess(u.username,g)) {
	    			Alert alert = new Alert(AlertType.ERROR);
	        		alert.setHeaderText("Error");
	        		alert.setContentText("You don't have admin access for this group");
	        		flag = true;
	        		alert.showAndWait();
	        		break;
	    		}
	    	}
	    	if(flag) return;
	    	
	    	//Add the help item to the global array
	    	HelpItem.add(titleEntry.getText(), descriptionEntry.getText(), bodyEntry.getText(), keywordsEntry.getText(), linkEntry.getText(), groupEntry.getText(),choiceBox.getSelectionModel().getSelectedItem(),authorEntry.getText());
	    	updateList();
	    });
        
	    //on click action for the outside create button
        create.setOnAction(e->{
        	//Field spacing settings
        	fieldGrid.setPadding(new Insets(10, 10, 10, 10));
    	    fieldGrid.setVgap(3);
    	    fieldGrid.getChildren().clear();
        	titleEntry.setPrefHeight(12);
        	descriptionEntry.setPrefHeight(12);
        	authorEntry.setPrefHeight(12);
        	bodyEntry.setPrefHeight(100);
        	bodyEntry.setMaxHeight(400);
        	bodyEntry.setWrapText(true);
        	keywordsEntry.setPrefHeight(12);
        	linkEntry.setPrefHeight(12);
        	groupEntry.setPrefHeight(12);


        	//Add fields to container
        	fieldGrid.add(titleLabel, 0, 0); fieldGrid.add(titleEntry, 1, 0);
        	fieldGrid.add(descriptionLabel, 0, 1); fieldGrid.add(descriptionEntry, 1, 1);
        	fieldGrid.add(authorLabel,0,2); fieldGrid.add(authorEntry,1,2);
        	fieldGrid.add(bodyLabel, 0, 3); fieldGrid.add(bodyEntry, 1, 3);	
        	fieldGrid.add(keywordsLabel, 0, 4); fieldGrid.add(keywordsEntry, 1, 4);	
        	fieldGrid.add(linkLabel, 0, 5); fieldGrid.add(linkEntry, 1, 5);	
        	fieldGrid.add(groupLabel, 0, 6); fieldGrid.add(groupEntry, 1, 6);
        	fieldGrid.add(choiceBox, 1, 7);
        	fieldGrid.add(createButton, 1, 8);
        	
        	//Set field alignments
        	GridPane.setHalignment(titleLabel, HPos.RIGHT);
        	GridPane.setHalignment(descriptionLabel, HPos.RIGHT);
        	GridPane.setHalignment(bodyLabel, HPos.RIGHT);
        	GridPane.setValignment(bodyLabel, VPos.TOP);
        	GridPane.setHalignment(keywordsLabel, HPos.RIGHT);
        	GridPane.setHalignment(linkLabel, HPos.RIGHT);
        	GridPane.setHalignment(groupLabel, HPos.RIGHT);
        	
        	//Set field values and add to larger container
        	createDisplay.getChildren().clear();
            createDisplay.getChildren().addAll(new Label(),fieldGrid);
            
            titleLabel.setText("Title: ");
        	descriptionLabel.setText("Description: ");
            bodyLabel.setPrefWidth(-1);
        	bodyLabel.setText("Body: ");
        	keywordsLabel.setText("Keywords: ");
        	linkLabel.setText("Links: ");
        	groupLabel.setText("Groups: ");
        	authorLabel.setText("Author(s): ");
        	
        	titleEntry.clear();
        	descriptionEntry.clear();
        	bodyEntry.clear();
        	keywordsEntry.clear();
        	linkEntry.clear();
        	groupEntry.clear();
        	authorEntry.clear();
        	

        	//update main display
        	twoColumns.getChildren().set(1, createDisplay);

        });
        
        //button and container for update button
        Button updateButton = new Button("Update");
        VBox updateDisplay = new VBox();
        
        
        //on click action for the update button outside
        update.setOnAction(e->{
        	//Field spacing settings and retrieve correct id
        	int idx = itemList.getSelectionModel().getSelectedIndex();
        	if(idx == -1) return;
        	fieldGrid.setPadding(new Insets(10, 10, 10, 10));
    	    fieldGrid.setVgap(3);
    	    fieldGrid.getChildren().clear();
        	titleEntry.setPrefHeight(12);
        	descriptionEntry.setPrefHeight(12);
        	bodyEntry.setPrefHeight(100);
        	bodyEntry.setMaxHeight(400);
        	bodyEntry.setWrapText(true);
        	keywordsEntry.setPrefHeight(12);
        	linkEntry.setPrefHeight(12);
        	groupEntry.setPrefHeight(12);
        	authorEntry.setPrefHeight(12);


        	//Add fields to container
        	fieldGrid.add(titleLabel, 0, 0); fieldGrid.add(titleEntry, 1, 0);
        	fieldGrid.add(descriptionLabel, 0, 1); fieldGrid.add(descriptionEntry, 1, 1);
        	fieldGrid.add(authorLabel,0,2); fieldGrid.add(authorEntry,1,2);
        	fieldGrid.add(bodyLabel, 0, 3); fieldGrid.add(bodyEntry, 1, 3);	
        	fieldGrid.add(keywordsLabel, 0, 4); fieldGrid.add(keywordsEntry, 1, 4);	
        	fieldGrid.add(linkLabel, 0, 5); fieldGrid.add(linkEntry, 1, 5);	
        	fieldGrid.add(groupLabel, 0, 6); fieldGrid.add(groupEntry, 1, 6);	
        	fieldGrid.add(updateButton, 1, 7);
        	
        	//Set field alignments
        	GridPane.setHalignment(titleLabel, HPos.RIGHT);
        	GridPane.setHalignment(descriptionLabel, HPos.RIGHT);
        	GridPane.setHalignment(bodyLabel, HPos.RIGHT);
        	GridPane.setValignment(bodyLabel, VPos.TOP);
        	GridPane.setHalignment(keywordsLabel, HPos.RIGHT);
        	GridPane.setHalignment(linkLabel, HPos.RIGHT);
        	GridPane.setHalignment(groupLabel, HPos.RIGHT);
        	
        	
        	//Set field values and add to larger container
        	updateDisplay.getChildren().clear();
        	updateDisplay.getChildren().addAll(new Label(),fieldGrid);
            
            HelpItem h = HelpItem.itemByID(ids.get(idx));
            titleLabel.setText("Title: ");
        	descriptionLabel.setText("Description: ");
            bodyLabel.setPrefWidth(-1);
        	bodyLabel.setText("Body: ");
        	keywordsLabel.setText("Keywords: ");
        	linkLabel.setText("Links: ");
        	groupLabel.setText("Groups: ");
        	authorLabel.setText("Author(s): ");

        	
        	titleEntry.setText(h.title);
        	descriptionEntry.setText(h.description);
        	if(!role.equals("Admin")) bodyEntry.setText(h.body);
        	else bodyEntry.setText("You are not allowed to see the body of this article.");
        	//bodyEntry.setText(h.body);
        	keywordsEntry.setText(HelpItem.listToStringPretty(h.keywords));
        	linkEntry.setText(HelpItem.listToStringPretty(h.links));
        	groupEntry.setText(HelpItem.listToStringPretty(h.groups));
        	authorEntry.setText(h.author);

        	
        	twoColumns.getChildren().set(1, updateDisplay);

        	//on click action for the update button inside the container
        	updateButton.setOnAction(event->{
        		
        		for(String g : HelpItem.prettyStringToList(groupEntry.getText())) {
    	    		boolean exists = false;
    	    		for(User us : App.users) {
    	    			if(App.checkAdminAccess(us.username, g)) {
    	    				exists = true;
    	    			}
    	    		}
    	    		if(!exists) {
    	    			App.adminMapAdd(u.username, g);
    	    			App.viewMapAdd(u.username, g);
    	    		}
    	    		System.out.println(g + " " + exists);
    	    	}
    	    	
    	    	boolean flag = false;
    	    	for(String g : HelpItem.prettyStringToList(groupEntry.getText())) {
    	    		if(!App.checkAdminAccess(u.username,g)) {
    	    			Alert alert = new Alert(AlertType.ERROR);
    	        		alert.setHeaderText("Error");
    	        		alert.setContentText("You don't have admin access for this group");
    	        		flag = true;
    	        		alert.showAndWait();
    	        		break;
    	    		}
    	    	}
    	    	if(flag) return;
    	    	
            	h.title = titleEntry.getText();
            	h.description = descriptionEntry.getText();
            	if(!role.equals("Admin")) h.body = bodyEntry.getText();
            	h.keywords = HelpItem.prettyStringToList(keywordsEntry.getText());
            	h.links = HelpItem.prettyStringToList(linkEntry.getText());
            	h.groups = HelpItem.prettyStringToList(groupEntry.getText());
            	h.author = authorEntry.getText();
            	updateList();
            	//items.set(idx, h.description);
            	
            });
        });
        
        //Restore window display fields
        VBox restoreDisplay = new VBox();
        Label restoreLabel = new Label("File Name:   ");
        TextField restoreField = new TextField();
        CheckBox restoreCheck = new CheckBox();
        Label overwriteLabel = new Label("Overwrite?  ");
        Button restoreButton = new Button("Restore");
        GridPane restoreGrid = new GridPane();
        

        //on click action for the restore button outside
        restore.setOnAction(e->{
        	//set field spacing and add to container
        	restoreGrid.setPadding(new Insets(10, 10, 10, 10));
        	restoreGrid.setVgap(3);
        	restoreGrid.getChildren().clear();
        	restoreGrid.add(restoreLabel, 0, 0); restoreGrid.add(restoreField, 1, 0);
        	restoreGrid.add(overwriteLabel, 0, 1);  restoreGrid.add(restoreCheck, 1, 1);
        	restoreGrid.add(restoreButton, 1, 2);

        	//update larger containers
        	restoreDisplay.getChildren().clear();
        	restoreDisplay.getChildren().addAll(restoreGrid);
 
        	restoreField.clear();
        	restoreCheck.setSelected(true);
        	
        	twoColumns.getChildren().set(1, restoreDisplay);

        });
        
        //on click action for the restore button inside the container
        restoreButton.setOnAction(e->{
        	try {
				HelpItem.restore(restoreField.getText().trim(), !restoreCheck.isSelected());
				updateList();
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.ERROR);
        		alert.setHeaderText("Error");
        		alert.setContentText("Invalid file name");
        		alert.showAndWait();
			}
        });
        
        //Backup window fields
        VBox backupDisplay = new VBox();
        Label backupLabel = new Label("File Name:   ");
        TextField backupField = new TextField();
        TextField backupGroup = new TextField();
        Label backupGroupLabel = new Label("Group: ");
        Button backupButton = new Button("Backup");
        GridPane backupGrid = new GridPane();
        
        //on click action for the backup button outside
        backup.setOnAction(e->{
        	//set field spacing and add to container
        	backupGrid.setPadding(new Insets(10, 10, 10, 10));
        	backupGrid.setVgap(3);
        	backupGrid.getChildren().clear();
        	backupGrid.add(backupLabel, 0, 0); backupGrid.add(backupField, 1, 0);
        	backupGrid.add(backupGroupLabel, 0, 1);  backupGrid.add(backupGroup, 1, 1);
        	backupGrid.add(backupButton, 1, 2);

        	//update larger containers
        	backupDisplay.getChildren().clear();
        	backupDisplay.getChildren().addAll(backupGrid);
        	backupField.clear();
        	backupGroup.clear();
        	backupGroup.setPromptText("Leave blank to backup all");
        	twoColumns.getChildren().set(1, backupDisplay);
        });
        
        //on click action for the backup button inside the container
        backupButton.setOnAction(e->{
        	try {
        		HelpItem.backup(backupField.getText(), backupGroup.getText());
        	}catch(Exception ex) {
        		Alert alert = new Alert(AlertType.ERROR);
        		alert.setHeaderText("Error");
        		alert.setContentText("Invalid file name");
        		alert.showAndWait();
        	}
        });
        
        //on click action for the search button
        search.setOnAction(e->{
        	lastGroup = searchGroupField.getText().trim();
        	lastSearchText = searchTextField.getText().trim();
        	lastLevel = searchLevelBox.getSelectionModel().getSelectedItem();
//        	System.out.println(lastGroup);
//        	System.out.println(lastGroup.length());
        	updateList();
        });
        
        //overall page layout containers
        BorderPane totalPage = new BorderPane();
        totalPage.setLeft(twoColumns);
        BorderPane.setMargin(twoColumns, new Insets(100));
        twoColumns.setAlignment(Pos.CENTER);
        scene = new Scene(totalPage, App.WIDTH, App.HEIGHT);

	}

	
	/**
	 * Updates the list view of articles based on the most recent search parameters
	 */
	public void updateList() {
		items.clear();
		ids.clear();
		
		ArrayList<HelpItem> checkList = new ArrayList<>();
		
		if(lastGroup.equals("")) {
			for(String group : App.viewMap.get(u.username)) {
				//System.out.println(group);
				for(HelpItem h : App.items) {
					if(h.groups.contains(group) && !checkList.contains(h)) {
						checkList.add(h);
					}
				}
			}
		} else {
			for(HelpItem h : App.items) {
				if(h.groups.contains(lastGroup) && !checkList.contains(h)) {
					checkList.add(h);
				}
			}
		}

		//System.out.println(checkList.size());


		ArrayList<HelpItem> checkListTemp = new ArrayList<>(checkList);
		if(!lastLevel.equals("All")) {
	        for(HelpItem h : checkList) {
	        	if(!h.level.equals(lastLevel)) checkListTemp.remove(h);
	        }
		}
		checkList = new ArrayList<>(checkListTemp);

		if(!lastSearchText.trim().equals("")) {
			for(HelpItem h : checkList) {
				int x=-1;
				try {
					x = Integer.parseInt(lastSearchText);
				}catch(Exception e) {
					x = -1;
				}
				if(!(h.title.contains(lastSearchText) || h.description.contains(lastSearchText) || h.author.contains(lastSearchText) || x == h.id)) {
					checkListTemp.remove(h);
				}
			}
		}
		checkList = new ArrayList<>(checkListTemp);

		for(HelpItem h : checkList) {
			items.add("#" + Integer.toString(h.id) + ": " + h.title + " by " + h.author);
			ids.add(h.id);
		}

	}
	
	/**
	 * ensures that students can only see the view button
	 */
	public void updateBoxFromRole() {
		if(role.equals("Student")) {
			buttons.getChildren().clear();
	        buttons.getChildren().addAll(view);
		} else {
			buttons.getChildren().clear();
	        buttons.getChildren().addAll(view,create,update,delete,backup,restore);
	        view.setStyle("-fx-text-base-color: black;");
		}
	}
	
	/*
	 * empties display upon reentry
	 */
	public void emptyDisplay() {
        twoColumns.getChildren().set(1,display);
	}
        
	
	
   
}

