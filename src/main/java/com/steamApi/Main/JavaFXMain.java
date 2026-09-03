package com.steamApi.Main;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.steamApi.Client.SteamClient;
import com.steamApi.Model.Game;
import com.steamApi.Parser.JSONParser;
import com.steamApi.Service.Monitor;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;



public class JavaFXMain {
    
    private boolean collapsed = false;
    private static final double EXPANDED_WIDTH = 300;
    private static Monitor monitor = new Monitor();
    private static boolean isImageView = true;    
    private ObservableList<Game> masterList  = null;
    private URLImageView imageView = new URLImageView();
    private TableView<Game> table = new TableView<>();
    private UserSession session;
    public static Label downloadStatus = new Label("");
    public static Button logout = new Button("Logout");
    private final Runnable logoutCallback;
    public static ProgressBar downloadProgress = new ProgressBar();
    
    
    //Current Game Status
   public static Label currentStatus = new Label("");
   
   //constructor
   public JavaFXMain(UserSession session , Runnable logoutCallback){

    this.session = session;
    this.logoutCallback = logoutCallback;

   }
    
    @SuppressWarnings("unchecked")
	public Scene createScene(UserSession session) throws Exception {
        

        currentStatus.setWrapText(true);
        ScrollPane viewBox = imageView.createViewBox(session , this::refreshLibrary);

        SteamClient client = new SteamClient("A5A6206490E64785D3271BC7B3A4A734" ,  session.getSteamId());
        
        Label welcome = new Label("Logged in as SteamID: " + session.getSteamId() );
        welcome.setFont(Font.font(16));
        

        TableColumn<Game, String> nameCol = new TableColumn<>("Name");
        //nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        nameCol.setCellFactory(col -> new TableCell<>(){

            private final Hyperlink link = new Hyperlink();
            
          
            {
            
            link.getStylesheets().addAll(getClass().getResource("./styles.css").toExternalForm());
            link.setOnAction( e -> {

                System.out.println("Clicked on link");
                Game game = getTableView().getItems().get(getIndex());

                try{
                    
                    //Desktop.getDesktop().browse(URI.create("steam steam://run/" + game.getAppId()));
                    ProcessBuilder pb = new ProcessBuilder("steam" , "steam://run/" + game.getAppId());
                    pb.start();
                    
                    //monitor.stop();

                    monitor = new Monitor();

                    monitor.start(game, running->{
                        
                        String res ="";
                        
                        for(String s : running.keySet()){

                            String status = running.get(s) ? "Running" : "Stopped";
                            res += s + " : " + status + "\n";

                        }

                        //System.out.println(res);
                        currentStatus.setText(res);
                        
                        
                        
                            
                    });

                    
                    
                    
                }catch(Exception ex){

                    ex.printStackTrace();

                }


            });

        }

        @Override
        protected void updateItem(String name , boolean empty){

            super.updateItem(name , empty);

            if(empty)
            {
                setGraphic(null);
                return;
            }

             Game game = getTableView().getItems().get(getIndex());


               if ( !game.getGameSize().equals("Not Installed") && 
                        Double.parseDouble(game.getGameSize().substring(0, 5)) > 0) {
                            link.setText(game.getName());
                    setGraphic(link);
                } else {
                    setGraphic(new Label(game.getName()));
                }


        }

        });

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        
        TableColumn<Game, LocalDateTime> lastPlayedCol = new TableColumn<>("Last Played");
        lastPlayedCol.setCellValueFactory(new PropertyValueFactory<>("lastPlayedFormatted"));

        TableColumn<Game, Integer> playtimeCol = new TableColumn<>("PlayTime (Hours)");
        playtimeCol.setCellValueFactory(new PropertyValueFactory<>("playtimeHours"));

        TableColumn<Game, Long> idCol = new TableColumn<>("AppID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appId"));

        TableColumn<Game, Double> size = new TableColumn<>("SizeOnDisk");
        size.setCellValueFactory(new PropertyValueFactory<>("gameSize"));

        // TableColumn<Game, String> gameSize = new TableColumn<>("GameSize");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("gameSize"));

        table.getColumns().addAll(idCol,nameCol, playtimeCol, lastPlayedCol,size);

        JsonObject json = client.getOwnedGames();
        List<Game> games = JSONParser.parseOwnedGames(json);
        table.getItems().addAll(games);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle("-fx-font-size: 14px;");

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Search Games...");
        searchField.setMaxWidth(500);

        // Refresh Button
        Button refreshBtn = new Button();
        refreshBtn.setText("Refresh");
        
        
        // list filter logic
        masterList = FXCollections.observableArrayList(games);
        FilteredList<Game> filteredList = new FilteredList<>(masterList , p->true);
        SortedList<Game> sortedList = new SortedList<>(filteredList);
        
        
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            
            filteredList.setPredicate(game -> {
                
                if (newV == null || newV.isEmpty())
                    return true;
                return game.getName().toLowerCase().contains(newV.toLowerCase());
            });
            
            //System.out.println(filteredList);
            imageView.refresh(filteredList , this::refreshLibrary);
            
            
        });
        
        //filter table with the filtered list
        table.setItems(sortedList);


        refreshBtn.setOnMouseReleased(e->{
            
            //recursion
            try {
                
                refreshLibrary();
                
			} catch (Exception e1) {
                // TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
        
        //Progress Bar for download Progress 
        downloadProgress.setPadding(new Insets(0,0,0,10));
        downloadProgress.setMinWidth(0);
        downloadProgress.setVisible(false);
        
        // bottom status bar
        Label statusLabel = new Label("Loaded " + games.size() + " games");
        HBox statusBar = new HBox(statusLabel , downloadProgress,downloadStatus );
        
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: rgba(40, 72, 168, 1)");
        statusLabel.setStyle("-fx-text-fill: #ebe83dff");
        
        downloadStatus.setStyle("-fx-text-fill : #2fe42f");
        downloadStatus.setPadding(new Insets(0,0,0,10));
        
        
        currentStatus.setPadding(new Insets(0,0,0,10));
        currentStatus.setStyle("-fx-text-fill: #ffffff");
        
        // side nav
        Label totalGamesLabel = new Label("Total Games: " + games.size());
        Label totalHoursLabel = new Label("Total Hours Played: " +
        games.stream().mapToInt(Game::getPlaytimeHours).sum());
        Label computerInfo = new Label(getComputerHWInfo());
        
        VBox sidebar = new VBox(10, totalGamesLabel, totalHoursLabel , currentStatus , computerInfo);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2b2b2bff");
        totalGamesLabel.setStyle("-fx-text-fill: white");
        totalHoursLabel.setStyle("-fx-text-fill: white");
        computerInfo.setStyle("-fx-text-fill: #72eb72");
        
        sidebar.setMaxSize(400, 1000);
        
        //Toggle Sidebar Buttonn
        Button toggle = new Button("<<");
        
        
        //Toggle Image View Button
        Button toggleImageView = new Button("::");
        
        
        //Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        
        //logout button style
        logout.getStyleClass().add("logout-btn");
        
        // Create A Top-Nav Bar
        HBox topBar = new HBox(10,toggle,searchField, refreshBtn , toggleImageView , spacer , logout);
        
        topBar.getStyleClass().add("my-hbox");
        
        
        if (Integer.parseInt(totalHoursLabel.getText().split(": ")[1]) > 1000) {
            totalHoursLabel.setText("Total Hours Played: " +
            games.stream().mapToInt(Game::getPlaytimeHours).sum() + "\nEase on the gaming Bro");
        }
        
        
        // Adding UI Elements
        BorderPane root = new BorderPane();
        root.setTop(topBar); // search Box + refresh button
        root.setLeft(sidebar);
        root.setBottom(statusBar); // Status info
        
        //Add stackPane with it's children table
        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(table);
        
        
        //Adding Split Pane
        SplitPane splitPane = new SplitPane();
        sidebar.setPrefWidth(200);
        table.setPrefWidth(800);
        splitPane.getItems().addAll(sidebar, contentPane);
        
        
        // Sidebar occupies 25%
        splitPane.setDividerPositions(0.25);
        root.setCenter(splitPane);
        
        if(isImageView){
            contentPane.getChildren().setAll(viewBox);
        }
        else
            contentPane.getChildren().setAll(table);
        
        contentPane.setBackground(Background.fill(Paint.valueOf("#1d5ab4")));
        sidebar.getStyleClass().add("sidebar-expanded");
        sidebar.setMaxWidth(EXPANDED_WIDTH);
        sidebar.setPrefWidth(EXPANDED_WIDTH);
        sidebar.setMinWidth(0);
        
        

        
        
        
        toggle.setOnAction(e->{
            toggleSidebar(sidebar , splitPane , toggle);
        });

    
    
        Scene scene = new Scene(root, 1420, 600);
        scene.getStylesheets().add(getClass().getResource("./styles.css").toExternalForm());
        
        
        toggleImageView.setOnAction(e->{
            
            isImageView = !isImageView;
            
            if(isImageView)
                contentPane.getChildren().setAll(viewBox);
            else
                contentPane.getChildren().setAll(table);
           
        });


        logout.setOnMouseClicked(event ->{ logoutCallback.run();});

        
        return scene;
        
    }
    
    
    private  void refreshLibrary(){
        
        try {
			List<Game> games = JSONParser.parseOwnedGames(new SteamClient("A5A6206490E64785D3271BC7B3A4A734", session.getSteamId()).getOwnedGames());

            masterList = FXCollections.observableArrayList(games);

            table.setItems(masterList);
            imageView.refresh(masterList , this::refreshLibrary);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }


    
    private void toggleSidebar(VBox sidebar , SplitPane splitpane , Button btn) {
        
        double initialWidth = sidebar.getWidth();
        double start = collapsed ? 0 : initialWidth;
        double end = collapsed ? EXPANDED_WIDTH : 0;
        
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_BOTH);
            }
            
            @Override
            protected void interpolate(double frac) {
                
                double width = start + (end - start) * frac;
                sidebar.setMaxWidth(width);
                forceSplitPaneLayout(splitpane , sidebar);
                sidebar.setOpacity(collapsed ? 1 - frac : frac);
                
                //                   System.out.println(
                    //     "width=" + width +
                    //     " managed=" + sidebar.isManaged() +
                    //     " visible=" + sidebar.isVisible()
                    // );
                    //         
                    
                }
                
                
            };
            
            
            if(collapsed){
                sidebar.setVisible(true);
                sidebar.setManaged(true);
            }
            
            transition.play();
            collapsed = !collapsed;
            
            btn.setText(collapsed ? ">>" : "<<");
            
        }


        private void forceSplitPaneLayout(SplitPane pane, VBox sidebar) {
            
            double total = pane.getWidth();
            if (total <= 0) return;
            
            double ratio = sidebar.getMaxWidth() / total;
            pane.getDividers().get(0).setPosition(Math.min(0.4, Math.max(0.1, ratio)));
            
        }
        
        private static String getComputerHWInfo(){

            ProcessBuilder pb = new ProcessBuilder("neofetch" , "resolution" , "cpu" , "gpu");
            ProcessBuilder pb2 = new ProcessBuilder("glxinfo" , "-B" );
            ProcessBuilder pb3 = new ProcessBuilder("grep" , "-i" , "Video Memory");

            
            String details = "";
            try {
                
                BufferedReader br = pb.start().inputReader();
                
                String line;
                while((line = br.readLine()) != null){
                    
                    details += String.valueOf(line.charAt(0)).toUpperCase()+line.substring(1)+"\n";
                    
                }
                
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            try {

                List<Process> pipeline = ProcessBuilder.startPipeline(List.of(pb2,pb3));
                Process lastProcess = pipeline.get(pipeline.size() - 1);
        
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(lastProcess.getInputStream()))) 
                {
                
                String line;
                if ((line = reader.readLine()) != null) {
        
                    line = line.trim();
                    details+= line+"\n";
                    
                }

            }
        
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return details;
        }
        
    }
    
    