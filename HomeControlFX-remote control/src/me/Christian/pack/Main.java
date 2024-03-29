package me.Christian.pack;



import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.Timer;

import me.Christian.networking.Client;
import me.Christian.other.ChangeOutStream;
import me.Christian.other.ConfigFileStuff;
import me.Christian.other.FeedReader;
import me.Christian.other.OtherStuff;
import me.Christian.threads.Thread_GetWeather;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	public static InetAddress lComputerIP;
	public static Object ComputerMac;
	public static String ComputerName;
	public static String ComputerIP;
	//
	//
	public static final int MainVersion = 1;
	public static final int cfgVersion = 2; 
	public static final int ncfgVersion = 1;
	//
	// SET TO FALSE IF YOU ARE USING ON RASPBERRY!!!!!!
	public static boolean Testbuild = true;
	public static String connectoip = "192.168.178.38";
	public static int connecttoport = 9977;
	public static Client connection = null;
	public static String username = "Totenfluch";
	public static String password = "s123C";
	public static String PrivateKey = "";
	//
	// INTERNAL SERVER PORT
	public static int portz = 9977;
	//
	// START WITH LOGIN SCREEN ??
	public static boolean StartWithLoginScreen = false;
	//
	// THE CITY WE ARE LIVING IN
	public static String City = "Schweinfurt";
	//
	// Delay to refresh the weather in ms
	// default: 600000 (10 minutes)
	public static int WeatherRefreshDelay = 600000;
	//
	// Main Stage - where everything goes thing thing
	public static Stage MainStage;
	// Scene for Root(control GUI) and Scene for Login - both can be places in MainStage.
	public static Scene Sroot, SLogin;
	//
	//Rss Feeds
	public static boolean RssEnabled = true;
	//Rss Refresh delay in ms
	public static int RssRefreshDelay = 15000;
	//
	// Dev prompt
	public static boolean dev_promt_enabled = true;
	//
	// Dev console
	public static boolean dev_console_enabled = true;

	// Root Window Stuff
	public static TextArea Console;
	public static Text GeneralInformation;
	public static Slider Music_Slider;
	public static ImageView Music_Head, Music_prev, Music_next, Music_pause, Music_play;
	public static ImageView Console_Button1, Console_Button2, weathericonlabel, screen_lock;
	public static Text Music_Title, Music_HeadText, Console_ButtonText;

	public static ImageView[] Head_Image = new ImageView[3];
	public static Text[] Head_Text = new Text[3];

	public static ImageView[][] Output_Button = new ImageView[8][2];
	public static ImageView[] Output_Lockquad = new ImageView[8];
	public static ImageView[] Output_Lockcross = new ImageView[8];
	public static ImageView[][] Output_State = new ImageView[8][3];
	public static Text[] Output_Text = new Text[8];
	public static boolean[] Output_Lockstate = new boolean[8];
	public static int[] Output_iState = new int[8];
	public static String[] Output_Name = new String[8];
	public static String[] Head_Name = new String[3];

	// MPC Stuff
	public static String currenttitle = "Fetching Title...";
	//double Music_title_size = 19;
	//static final int MUSIC_TITLE_MAX_WIDTH = 235;
	public static String volume;

	// Login thingy for later
	public static String ActiveUser = "Root";
	// Login thingy for later

	private static Timer WeatherRefreshTimer, RssRefreshTimer, stats_refreshtimer;
	public static int Login_LoginButton1_State = 0, Login_LoginButton2_State = 0, Login_LoginButton3_State = 0, Login_LoginButton4_State = 0, Login_LoginButton5_State = 0, Login_LoginButton6_State = 0;
	public static boolean goLeft, goRight;
	public static int entrypos = 265;

	// Developer in app stuff
	public static PasswordField Dev_masterpw;
	public static TextField Dev_printfield, Dev_cmdfield, Dev_console;
	public static Button Dev_login, Dev_logout, Dev_sendcmd, Dev_sendprint;
	public static boolean isMasterLoggedIn = false;
	public static String MasterPassword;
	public static double masteropacity = 0.0;

	// print queue
	public static String[] todoprint = new String[10000];
	public static int todoprintsize = 0;

	// cmd queue
	public static String[] todocmd = new String[10000];
	public static int todocmdsize = 0;

	// Server and it's thread
	public static Thread Thread_MainServer;

	public static boolean Weatherinit = false;

	public static boolean StartupDone = false;

	// Login Window Stuff
	public static ImageView Login_LoginButton1, Login_LoginButton2, Login_LoginButton3, Login_LoginButton4, Login_LoginButton5, Login_LoginButton6;
	public static ImageView Login_Spark[] = new ImageView[6];
	public static double Login_SparkPos[][] = new double[6][2];
	public static int Login_SparkSeq[] = new int[6];
	public static boolean[] Output_isLocked = new boolean[8];

	// Stats info stuff
	public static String stats_string;
	public static int[] uptime = new int[4];
	public static String stats_serverstatus = "";
	public static boolean isClicked = false;

	public static void main(String[] args) throws IOException{
		if(args.length > 0){
			MasterPassword = args[0];
		}else{
			System.out.println(">>> No Master password set <<<");
			MasterPassword = OtherStuff.GeneratePrivateKey();
		}
		
		try {
			lComputerIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		ComputerMac = OtherStuff.getMacAdress();
		ComputerName = lComputerIP.getHostName();
		ActiveUser = ComputerName;
		ComputerIP = lComputerIP.getHostAddress();
		
		System.out.println("|> checking config files <|");
		// Create config file if empty, load if it's there
		System.out.println("Config File location: " + OtherStuff.jarlocation().toString().replace("/HomeControl.jar", "") + "/config.properties");
		ConfigFileStuff.startup();
		System.out.println("NameConfig File location: " + OtherStuff.jarlocation().toString().replace("/HomeControl.jar", "") + "/NameConfig.properties");
		ConfigFileStuff.customNameStartup();
		System.out.println("|< config files checked >|");

		System.out.println("|> checking Rss feed file <|");
		if(RssEnabled){
			FeedReader.ReadFeedFile();
			FeedReader.CreateFeedObjects();
			System.out.println("RssFeeds File location: " + OtherStuff.jarlocation().toString().replace("/HomeControl.jar", "") + "/RSSFeeds.txt");
			if(RssRefreshDelay > 0){
				RssRefreshTimer = new Timer(RssRefreshDelay, new ActionListener()
				{
					@Override
					public void actionPerformed(java.awt.event.ActionEvent arg0) {
						FeedReader.ReadFeedFile();
						FeedReader.CreateFeedObjects();
						OtherStuff.addToPrintQueue("Refreshed Rss feeds");
					}
				});
				RssRefreshTimer.start();
			}
		}else{
			System.out.println("Rss is disabled.");
		}
		System.out.println("|> checked Rss feed file <|");


		// Make both queues empty
		for(int i = 999; i>-1; i--){
			todoprint[i] = "";
			todocmd[i] = "";
		}
		Platform.setImplicitExit(false);
		// Integer for Server
		System.out.println("Starting [1]: Server");
		// Init user database
		OtherStuff.initDatabase();
		// Get the weather from thread
		Thread_GetWeather.StartCheck(City);

		// Create a object, create a Thread, start the Thread
		try{
			ConnectToServer(connectoip, connecttoport, "login", username + " " + password);
			stats_serverstatus = "ONLINE";
		}catch (Exception e){
			stats_serverstatus = "OFFLINE";
		}
		Thread_MainServer = new Thread(connection);
		Thread_MainServer.start();

		// build Refresh of music title


		stats_refreshtimer = new Timer(1000, new ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				uptime[0]++;
				if(uptime[0] >= 60){
					uptime[0] = 0;
					uptime[1]++;
				}
				if(uptime[1] >= 60){
					uptime[1] = 0;
					uptime[2]++;
				}
				if(uptime[2] >= 24){
					uptime[2] = 0;
					uptime[3]++;
				}
				stats_string = "Serverstatus: " + stats_serverstatus;
				if(stats_serverstatus.equals("ONLINE")){
					stats_string += " seit " + uptime[3] + "d "+ uptime[2] + "h " + uptime[1] + "m " + uptime[0] + "s. ";
				}
				Platform.runLater(new Runnable() {
					@Override public void run() {
						if(!isClicked){
							FeedReader.RssTextObjectTooltip.setFont(Font.font("System Regular", FontWeight.BOLD, 16));
							FeedReader.RssTextObjectTooltip.setText(stats_string);
						}
					}
				});
			}
		});
		stats_refreshtimer.start();

		if(WeatherRefreshDelay > 0){
			WeatherRefreshTimer = new Timer(WeatherRefreshDelay, new ActionListener()
			{
				@Override
				public void actionPerformed(java.awt.event.ActionEvent arg0) {
					refreshweather();
				}
			});
			WeatherRefreshTimer.start();
		}

		// start GUI
		System.out.println("Starting [2]: GUI");
		StartupDone = true;
		launch(args);
	}

	public void start(Stage primaryStage) {
		System.out.println("initializing GUI");
		// define the window, so we can handle it later
		MainStage = primaryStage;
		// multiple threads possible?
		Platform.setImplicitExit(false);
		// name the window
		primaryStage.setTitle("Homecontrol");
		//No resize for you sir!
		primaryStage.setResizable(false);
		// Exit the programm on window close
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});;
		Pane root = new Pane();
		Sroot = new Scene(root, 1024, 600);

		// Background
		ImageView imgView = null;
		// Watermark for Testbuild
		if(!Testbuild){
			imgView = new ImageView(new Image("background3.png"));
			imgView.setFitWidth(1100);
			imgView.setFitHeight(625);
			root.getChildren().add(imgView);
		}else{
			imgView = new ImageView(new Image("background3.png"));
			imgView.setFitWidth(1100);
			imgView.setFitHeight(625);
			root.getChildren().add(imgView);
			ImageView watermark = new ImageView(new Image("watermark.png"));
			watermark.setFitWidth(1100);
			watermark.setFitHeight(625);
			watermark.setOpacity(0.15);
			root.getChildren().add(watermark);
			watermark.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent e) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							FeedReader.RssTextObjectTooltip.setFont(Font.font("System Regular", FontWeight.BOLD, 16));
							FeedReader.RssTextObjectTooltip.setText(stats_string);
							isClicked = false;
						}
					});
				}
			});
		}
		imgView.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent e) {
				Platform.runLater(new Runnable() {
					@Override public void run() {
						FeedReader.RssTextObjectTooltip.setFont(Font.font("System Regular", FontWeight.BOLD, 16));
						FeedReader.RssTextObjectTooltip.setText(stats_string);
						isClicked = false;
					}
				});
			}
		});


		// Refresh timer for anything
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				update();
			}
		}.start();
		System.out.println("Gui objects loaded: 10%");

		GeneralInformation = new Text(OtherStuff.TheNormalTime() + Main.City + ", " + Thread_GetWeather.degree + "�C");
		GeneralInformation.setLayoutX(20);
		GeneralInformation.setLayoutY(40);
		GeneralInformation.setFont(Font.font("Futura", FontWeight.BOLD, 18));
		root.getChildren().add(GeneralInformation);
		System.out.println(GeneralInformation.getLayoutBounds().getWidth());

		// Icon for weather
		weathericonlabel = new ImageView();
		weathericonlabel.setLayoutX(GeneralInformation.getLayoutBounds().getWidth()+30);
		weathericonlabel.setLayoutY(4);
		weathericonlabel.setFitHeight(60);
		weathericonlabel.setFitWidth(60);
		root.getChildren().add(weathericonlabel);
		System.out.println(weathericonlabel.getLayoutBounds().getWidth());
		System.out.println(weathericonlabel.getLayoutX());

		for(int i=0; i<10;i++){
			if(FeedReader.RssTextObject[i] != null)
				root.getChildren().add(FeedReader.RssTextObject[i]);
		}
		FeedReader.RssTextObjectTooltip.setX(20);
		FeedReader.RssTextObjectTooltip.setY(580);
		root.getChildren().add(FeedReader.RssTextObjectTooltip);
		FeedReader.RssTextObjectTooltip.setText(stats_string);

		System.out.println("Gui objects loaded: 20%");
		// Head1 bar image
		Head_Image[0] = new ImageView(new Image("B12.png"));
		Head_Image[0].setLayoutX(60);
		Head_Image[0].setScaleX(1.9);
		Head_Image[0].setLayoutY(72);
		root.getChildren().add(Head_Image[0]);

		// Head1 text
		Head_Text[0] = new Text();
		Head_Text[0].setText(Head_Name[0]);
		Head_Text[0].setLayoutX(41);
		Head_Text[0].setLayoutY(99);
		Head_Text[0].setFont(Font.font("Futura", 20));
		root.getChildren().add(Head_Text[0]);

		// Head2 bar image
		Head_Image[1] = new ImageView(new Image("B12.png"));
		Head_Image[1].setLayoutX(61);
		Head_Image[1].setScaleX(1.9);
		Head_Image[1].setLayoutY(310);
		root.getChildren().add(Head_Image[1]);

		// Head2 text
		Head_Text[1] = new Text();
		Head_Text[1].setText(Head_Name[1]);
		Head_Text[1].setLayoutX(41);
		Head_Text[1].setLayoutY(337);
		Head_Text[1].setFont(Font.font("Futura", 20));
		root.getChildren().add(Head_Text[1]);

		// Head2 bar image
		Head_Image[2] = new ImageView(new Image("iB12.png"));
		Head_Image[2].setLayoutX(809);
		Head_Image[2].setScaleX(1.9);
		Head_Image[2].setLayoutY(260);
		root.getChildren().add(Head_Image[2]);

		// Head2 text
		Head_Text[2] = new Text();
		Head_Text[2].setText(Head_Name[2]);
		Head_Text[2].setLayoutX(860);
		Head_Text[2].setLayoutY(287);
		Head_Text[2].setFont(Font.font("Futura", 20));
		root.getChildren().add(Head_Text[2]);

		System.out.println("Gui objects loaded: 30%");

		// Output_Button[0][0] unpressed
		Output_Button[0][0] = new ImageView(new Image("B12.png"));
		Output_Button[0][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[0][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[0][0].getOnMousePressed();
		Output_Button[0][0].getOnMouseReleased();
		Output_Button[0][0].setLayoutX(60);
		Output_Button[0][0].setLayoutY(125);
		root.getChildren().add(Output_Button[0][0]);

		// Output_Button[0][0] pressed
		Output_Button[0][1] = new ImageView(new Image("B3.png"));
		Output_Button[0][1].setLayoutX(60);
		Output_Button[0][1].setLayoutY(125);
		Output_Button[0][1].setVisible(false);
		root.getChildren().add(Output_Button[0][1]);

		// Output_Button[0][0] text
		Output_Text[0] = new Text();
		Output_Text[0].setText(Output_Name[0]);
		Output_Text[0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[0].getOnMousePressed();
		Output_Text[0].getOnMouseReleased();
		Output_Text[0].setFont(Font.font("Futura", 18));
		Output_Text[0].setLayoutX(80);
		Output_Text[0].setLayoutY(150);
		root.getChildren().add(Output_Text[0]);

		// Output_Lockquad[0] quadrat
		Output_Lockquad[0] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[0].getOnMouseReleased();
		Output_Lockquad[0].setLayoutX(210);
		Output_Lockquad[0].setLayoutY(135);
		Output_Lockquad[0].setFitHeight(40);
		Output_Lockquad[0].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[0]);

		// Output_Lockquad[0] red X
		Output_Lockcross[0] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[0].getOnMouseReleased();
		Output_Lockcross[0].setLayoutX(215);
		Output_Lockcross[0].setLayoutY(140);
		Output_Lockcross[0].setFitHeight(30);
		Output_Lockcross[0].setFitWidth(30);
		Output_Lockcross[0].setVisible(false);
		root.getChildren().add(Output_Lockcross[0]);

		// Output1 state of it
		Output_State[0][0] = new ImageView(new Image("tealorb.png"));
		Output_State[0][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[0][0].getOnMouseReleased();
		Output_State[0][0].setLayoutX(10);
		Output_State[0][0].setLayoutY(125);
		Output_State[0][0].setFitHeight(35);
		Output_State[0][0].setFitWidth(35);
		root.getChildren().add(Output_State[0][0]);

		Output_State[0][1] = new ImageView(new Image("greenorb.png"));
		Output_State[0][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[0][1].getOnMouseReleased();
		Output_State[0][1].setLayoutX(10);
		Output_State[0][1].setLayoutY(125);
		Output_State[0][1].setFitHeight(35);
		Output_State[0][1].setFitWidth(35);
		Output_State[0][1].setVisible(false);
		root.getChildren().add(Output_State[0][1]);

		Output_State[0][2] = new ImageView(new Image("redorb.png"));
		Output_State[0][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[0][2].getOnMouseReleased();
		Output_State[0][2].setLayoutX(10);
		Output_State[0][2].setLayoutY(125);
		Output_State[0][2].setFitHeight(35);
		Output_State[0][2].setFitWidth(35);
		Output_State[0][2].setVisible(false);
		root.getChildren().add(Output_State[0][2]);

		// Button2
		Output_Button[1][0] = new ImageView(new Image("B12.png"));
		Output_Button[1][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[1][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[1][0].getOnMousePressed();
		Output_Button[1][0].getOnMouseReleased();
		Output_Button[1][0].setLayoutX(60);
		Output_Button[1][0].setLayoutY(175);
		root.getChildren().add(Output_Button[1][0]);

		Output_Button[1][1] = new ImageView(new Image("B3.png"));
		Output_Button[1][1].setLayoutX(60);
		Output_Button[1][1].setLayoutY(175);
		Output_Button[1][1].setVisible(false);
		root.getChildren().add(Output_Button[1][1]);

		Output_Text[1] = new Text();
		Output_Text[1].setText(Output_Name[1]);
		Output_Text[1].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[1].getOnMousePressed();
		Output_Text[1].getOnMouseReleased();
		Output_Text[1].setFont(Font.font("Futura", 18));
		Output_Text[1].setLayoutX(80);
		Output_Text[1].setLayoutY(200);
		root.getChildren().add(Output_Text[1]);

		Output_Lockquad[1] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[1].getOnMouseReleased();
		Output_Lockquad[1].setLayoutX(210);
		Output_Lockquad[1].setLayoutY(185);
		Output_Lockquad[1].setFitHeight(40);
		Output_Lockquad[1].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[1]);

		Output_Lockcross[1] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[1].getOnMouseReleased();
		Output_Lockcross[1].setLayoutX(215);
		Output_Lockcross[1].setLayoutY(190);
		Output_Lockcross[1].setFitHeight(30);
		Output_Lockcross[1].setFitWidth(30);
		Output_Lockcross[1].setVisible(false);
		root.getChildren().add(Output_Lockcross[1]);

		Output_State[1][0] = new ImageView(new Image("tealorb.png"));
		Output_State[1][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[1][0].getOnMouseReleased();
		Output_State[1][0].setLayoutX(10);
		Output_State[1][0].setLayoutY(175);
		Output_State[1][0].setFitHeight(35);
		Output_State[1][0].setFitWidth(35);
		root.getChildren().add(Output_State[1][0]);

		Output_State[1][1] = new ImageView(new Image("greenorb.png"));
		Output_State[1][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[1][1].getOnMouseReleased();
		Output_State[1][1].setLayoutX(10);
		Output_State[1][1].setLayoutY(175);
		Output_State[1][1].setFitHeight(35);
		Output_State[1][1].setFitWidth(35);
		Output_State[1][1].setVisible(false);
		root.getChildren().add(Output_State[1][1]);

		Output_State[1][2] = new ImageView(new Image("redorb.png"));
		Output_State[1][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[1][2].getOnMouseReleased();
		Output_State[1][2].setLayoutX(10);
		Output_State[1][2].setLayoutY(175);
		Output_State[1][2].setFitHeight(35);
		Output_State[1][2].setFitWidth(35);
		Output_State[1][2].setVisible(false);
		root.getChildren().add(Output_State[1][2]);


		// Button3
		Output_Button[2][0] = new ImageView(new Image("B12.png"));
		Output_Button[2][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[2][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[2][0].getOnMousePressed();
		Output_Button[2][0].getOnMouseReleased();
		Output_Button[2][0].setLayoutX(60);
		Output_Button[2][0].setLayoutY(225);
		root.getChildren().add(Output_Button[2][0]);

		Output_Button[2][1] = new ImageView(new Image("B3.png"));
		Output_Button[2][1].setLayoutX(60);
		Output_Button[2][1].setLayoutY(225);
		Output_Button[2][1].setVisible(false);
		root.getChildren().add(Output_Button[2][1]);

		Output_Text[2] = new Text();
		Output_Text[2].setText(Output_Name[2]);
		Output_Text[2].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[2].getOnMousePressed();
		Output_Text[2].getOnMouseReleased();
		Output_Text[2].setFont(Font.font("Futura", 18));
		Output_Text[2].setLayoutX(80);
		Output_Text[2].setLayoutY(250);
		root.getChildren().add(Output_Text[2]);

		Output_Lockquad[2] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[2].getOnMouseReleased();
		Output_Lockquad[2].setLayoutX(210);
		Output_Lockquad[2].setLayoutY(235);
		Output_Lockquad[2].setFitHeight(40);
		Output_Lockquad[2].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[2]);

		Output_Lockcross[2] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[2].getOnMouseReleased();
		Output_Lockcross[2].setLayoutX(215);
		Output_Lockcross[2].setLayoutY(240);
		Output_Lockcross[2].setFitHeight(30);
		Output_Lockcross[2].setFitWidth(30);
		Output_Lockcross[2].setVisible(false);
		root.getChildren().add(Output_Lockcross[2]);

		Output_State[2][0] = new ImageView(new Image("tealorb.png"));
		Output_State[2][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[2][0].getOnMouseReleased();
		Output_State[2][0].setLayoutX(10);
		Output_State[2][0].setLayoutY(225);
		Output_State[2][0].setFitHeight(35);
		Output_State[2][0].setFitWidth(35);
		root.getChildren().add(Output_State[2][0]);

		Output_State[2][1] = new ImageView(new Image("greenorb.png"));
		Output_State[2][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[2][1].getOnMouseReleased();
		Output_State[2][1].setLayoutX(10);
		Output_State[2][1].setLayoutY(225);
		Output_State[2][1].setFitHeight(35);
		Output_State[2][1].setFitWidth(35);
		Output_State[2][1].setVisible(false);
		root.getChildren().add(Output_State[2][1]);

		Output_State[2][2] = new ImageView(new Image("redorb.png"));
		Output_State[2][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[2][2].getOnMouseReleased();
		Output_State[2][2].setLayoutX(10);
		Output_State[2][2].setLayoutY(225);
		Output_State[2][2].setFitHeight(35);
		Output_State[2][2].setFitWidth(35);
		Output_State[2][2].setVisible(false);
		root.getChildren().add(Output_State[2][2]);

		System.out.println("Gui objects loaded: 40%");

		// Button4
		Output_Button[3][0] = new ImageView(new Image("B12.png"));
		Output_Button[3][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[3][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[3][0].getOnMousePressed();
		Output_Button[3][0].getOnMouseReleased();
		Output_Button[3][0].setLayoutX(60);
		Output_Button[3][0].setLayoutY(363);
		root.getChildren().add(Output_Button[3][0]);

		Output_Button[3][1] = new ImageView(new Image("B3.png"));
		Output_Button[3][1].setLayoutX(60);
		Output_Button[3][1].setLayoutY(363);
		Output_Button[3][1].setVisible(false);
		root.getChildren().add(Output_Button[3][1]);

		Output_Text[3] = new Text();
		Output_Text[3].setText(Output_Name[3]);
		Output_Text[3].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[3].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[3].getOnMousePressed();
		Output_Text[3].getOnMouseReleased();
		Output_Text[3].setFont(Font.font("Futura", 18));
		Output_Text[3].setLayoutX(80);
		Output_Text[3].setLayoutY(388);
		root.getChildren().add(Output_Text[3]);

		Output_Lockquad[3] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[3].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[3].getOnMouseReleased();
		Output_Lockquad[3].setLayoutX(210);
		Output_Lockquad[3].setLayoutY(373);
		Output_Lockquad[3].setFitHeight(40);
		Output_Lockquad[3].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[3]);

		Output_Lockcross[3] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[3].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[3].getOnMouseReleased();
		Output_Lockcross[3].setLayoutX(215);
		Output_Lockcross[3].setLayoutY(378);
		Output_Lockcross[3].setFitHeight(30);
		Output_Lockcross[3].setFitWidth(30);
		Output_Lockcross[3].setVisible(false);
		root.getChildren().add(Output_Lockcross[3]);

		Output_State[3][0] = new ImageView(new Image("tealorb.png"));
		Output_State[3][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[3][0].getOnMouseReleased();
		Output_State[3][0].setLayoutX(10);
		Output_State[3][0].setLayoutY(363);
		Output_State[3][0].setFitHeight(35);
		Output_State[3][0].setFitWidth(35);
		root.getChildren().add(Output_State[3][0]);

		Output_State[3][1] = new ImageView(new Image("greenorb.png"));
		Output_State[3][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[3][1].getOnMouseReleased();
		Output_State[3][1].setLayoutX(10);
		Output_State[3][1].setLayoutY(363);
		Output_State[3][1].setFitHeight(35);
		Output_State[3][1].setFitWidth(35);
		Output_State[3][1].setVisible(false);
		root.getChildren().add(Output_State[3][1]);

		Output_State[3][2] = new ImageView(new Image("redorb.png"));
		Output_State[3][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[3][2].getOnMouseReleased();
		Output_State[3][2].setLayoutX(10);
		Output_State[3][2].setLayoutY(363);
		Output_State[3][2].setFitHeight(35);
		Output_State[3][2].setFitWidth(35);
		Output_State[3][2].setVisible(false);
		root.getChildren().add(Output_State[3][2]);

		// Button5
		Output_Button[4][0] = new ImageView(new Image("B12.png"));
		Output_Button[4][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[4][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[4][0].getOnMousePressed();
		Output_Button[4][0].getOnMouseReleased();
		Output_Button[4][0].setLayoutX(60);
		Output_Button[4][0].setLayoutY(413);
		root.getChildren().add(Output_Button[4][0]);

		Output_Button[4][1] = new ImageView(new Image("B3.png"));
		Output_Button[4][1].setLayoutX(60);
		Output_Button[4][1].setLayoutY(413);
		Output_Button[4][1].setVisible(false);
		root.getChildren().add(Output_Button[4][1]);

		Output_Text[4] = new Text();
		Output_Text[4].setText(Output_Name[4]);
		Output_Text[4].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[4].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[4].getOnMousePressed();
		Output_Text[4].getOnMouseReleased();
		Output_Text[4].setFont(Font.font("Futura", 18));
		Output_Text[4].setLayoutX(80);
		Output_Text[4].setLayoutY(438);
		root.getChildren().add(Output_Text[4]);

		Output_Lockquad[4] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[4].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[4].getOnMouseReleased();
		Output_Lockquad[4].setLayoutX(210);
		Output_Lockquad[4].setLayoutY(423);
		Output_Lockquad[4].setFitHeight(40);
		Output_Lockquad[4].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[4]);

		Output_Lockcross[4] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[4].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[4].getOnMouseReleased();
		Output_Lockcross[4].setLayoutX(215);
		Output_Lockcross[4].setLayoutY(428);
		Output_Lockcross[4].setFitHeight(30);
		Output_Lockcross[4].setFitWidth(30);
		Output_Lockcross[4].setVisible(false);
		root.getChildren().add(Output_Lockcross[4]);

		Output_State[4][0] = new ImageView(new Image("tealorb.png"));
		Output_State[4][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[4][0].getOnMouseReleased();
		Output_State[4][0].setLayoutX(10);
		Output_State[4][0].setLayoutY(413);
		Output_State[4][0].setFitHeight(35);
		Output_State[4][0].setFitWidth(35);
		root.getChildren().add(Output_State[4][0]);

		Output_State[4][1] = new ImageView(new Image("greenorb.png"));
		Output_State[4][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[4][1].getOnMouseReleased();
		Output_State[4][1].setLayoutX(10);
		Output_State[4][1].setLayoutY(413);
		Output_State[4][1].setFitHeight(35);
		Output_State[4][1].setFitWidth(35);
		Output_State[4][1].setVisible(false);
		root.getChildren().add(Output_State[4][1]);

		Output_State[4][2] = new ImageView(new Image("redorb.png"));
		Output_State[4][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[4][2].getOnMouseReleased();
		Output_State[4][2].setLayoutX(10);
		Output_State[4][2].setLayoutY(413);
		Output_State[4][2].setFitHeight(35);
		Output_State[4][2].setFitWidth(35);
		Output_State[4][2].setVisible(false);
		root.getChildren().add(Output_State[4][2]);

		// Button6
		Output_Button[5][0] = new ImageView(new Image("iB12.png"));
		Output_Button[5][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[5][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[5][0].getOnMousePressed();
		Output_Button[5][0].getOnMouseReleased();
		Output_Button[5][0].setLayoutX(809);
		Output_Button[5][0].setLayoutY(313);
		root.getChildren().add(Output_Button[5][0]);

		Output_Button[5][1] = new ImageView(new Image("iB3.png"));
		Output_Button[5][1].setLayoutX(809);
		Output_Button[5][1].setLayoutY(313);
		Output_Button[5][1].setVisible(false);
		root.getChildren().add(Output_Button[5][1]);

		Output_Text[5] = new Text();
		Output_Text[5].setText(Output_Name[5]);
		Output_Text[5].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[5].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[5].getOnMousePressed();
		Output_Text[5].getOnMouseReleased();
		Output_Text[5].setFont(Font.font("Futura", 18));
		Output_Text[5].setLayoutX(895);
		Output_Text[5].setLayoutY(338);
		root.getChildren().add(Output_Text[5]);

		Output_Lockquad[5] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[5].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[5].getOnMouseReleased();
		Output_Lockquad[5].setLayoutX(785);
		Output_Lockquad[5].setLayoutY(323);
		Output_Lockquad[5].setFitHeight(40);
		Output_Lockquad[5].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[5]);

		Output_Lockcross[5] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[5].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[5].getOnMouseReleased();
		Output_Lockcross[5].setLayoutX(790);
		Output_Lockcross[5].setLayoutY(328);
		Output_Lockcross[5].setFitHeight(30);
		Output_Lockcross[5].setFitWidth(30);
		Output_Lockcross[5].setVisible(false);
		root.getChildren().add(Output_Lockcross[5]);

		Output_State[5][0] = new ImageView(new Image("tealorb.png"));
		Output_State[5][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[5][0].getOnMouseReleased();
		Output_State[5][0].setLayoutX(985);
		Output_State[5][0].setLayoutY(313);
		Output_State[5][0].setFitHeight(35);
		Output_State[5][0].setFitWidth(35);
		root.getChildren().add(Output_State[5][0]);

		Output_State[5][1] = new ImageView(new Image("greenorb.png"));
		Output_State[5][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[5][1].getOnMouseReleased();
		Output_State[5][1].setLayoutX(985);
		Output_State[5][1].setLayoutY(313);
		Output_State[5][1].setFitHeight(35);
		Output_State[5][1].setFitWidth(35);
		Output_State[5][1].setVisible(false);
		root.getChildren().add(Output_State[5][1]);

		Output_State[5][2] = new ImageView(new Image("redorb.png"));
		Output_State[5][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[5][2].getOnMouseReleased();
		Output_State[5][2].setLayoutX(985);
		Output_State[5][2].setLayoutY(313);
		Output_State[5][2].setFitHeight(35);
		Output_State[5][2].setFitWidth(35);
		Output_State[5][2].setVisible(false);
		root.getChildren().add(Output_State[5][2]);

		System.out.println("Gui objects loaded: 50%");

		// Button7
		Output_Button[6][0] = new ImageView(new Image("iB12.png"));
		Output_Button[6][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[6][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[6][0].getOnMousePressed();
		Output_Button[6][0].getOnMouseReleased();
		Output_Button[6][0].setLayoutX(809);
		Output_Button[6][0].setLayoutY(363);
		root.getChildren().add(Output_Button[6][0]);

		Output_Button[6][1] = new ImageView(new Image("iB3.png"));
		Output_Button[6][1].setLayoutX(809);
		Output_Button[6][1].setLayoutY(363);
		Output_Button[6][1].setVisible(false);
		root.getChildren().add(Output_Button[6][1]);

		Output_Text[6] = new Text();
		Output_Text[6].setText(Output_Name[6]);
		Output_Text[6].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[6].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[6].getOnMousePressed();
		Output_Text[6].getOnMouseReleased();
		Output_Text[6].setFont(Font.font("Futura", 18));
		Output_Text[6].setLayoutX(895);
		Output_Text[6].setLayoutY(388);
		root.getChildren().add(Output_Text[6]);

		Output_Lockquad[6] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[6].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[6].getOnMouseReleased();
		Output_Lockquad[6].setLayoutX(785);
		Output_Lockquad[6].setLayoutY(373);
		Output_Lockquad[6].setFitHeight(40);
		Output_Lockquad[6].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[6]);

		Output_Lockcross[6] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[6].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[6].getOnMouseReleased();
		Output_Lockcross[6].setLayoutX(790);
		Output_Lockcross[6].setLayoutY(378);
		Output_Lockcross[6].setFitHeight(30);
		Output_Lockcross[6].setFitWidth(30);
		Output_Lockcross[6].setVisible(false);
		root.getChildren().add(Output_Lockcross[6]);

		Output_State[6][0] = new ImageView(new Image("tealorb.png"));
		Output_State[6][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[6][0].getOnMouseReleased();
		Output_State[6][0].setLayoutX(985);
		Output_State[6][0].setLayoutY(363);
		Output_State[6][0].setFitHeight(35);
		Output_State[6][0].setFitWidth(35);
		root.getChildren().add(Output_State[6][0]);

		Output_State[6][1] = new ImageView(new Image("greenorb.png"));
		Output_State[6][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[6][1].getOnMouseReleased();
		Output_State[6][1].setLayoutX(985);
		Output_State[6][1].setLayoutY(363);
		Output_State[6][1].setFitHeight(35);
		Output_State[6][1].setFitWidth(35);
		Output_State[6][1].setVisible(false);
		root.getChildren().add(Output_State[6][1]);

		Output_State[6][2] = new ImageView(new Image("redorb.png"));
		Output_State[6][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[6][2].getOnMouseReleased();
		Output_State[6][2].setLayoutX(985);
		Output_State[6][2].setLayoutY(363);
		Output_State[6][2].setFitHeight(35);
		Output_State[6][2].setFitWidth(35);
		Output_State[6][2].setVisible(false);
		root.getChildren().add(Output_State[6][2]);

		System.out.println("Gui objects loaded: 60%");

		// Button8
		Output_Button[7][0] = new ImageView(new Image("iB12.png"));
		Output_Button[7][0].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Button[7][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Button[7][0].getOnMousePressed();
		Output_Button[7][0].getOnMouseReleased();
		Output_Button[7][0].setLayoutX(809);
		Output_Button[7][0].setLayoutY(413);
		root.getChildren().add(Output_Button[7][0]);

		Output_Button[7][1] = new ImageView(new Image("iB3.png"));
		Output_Button[7][1].setLayoutX(809);
		Output_Button[7][1].setLayoutY(413);
		Output_Button[7][1].setVisible(false);
		root.getChildren().add(Output_Button[7][1]);

		Output_Text[7] = new Text();
		Output_Text[7].setText(Output_Name[7]);
		Output_Text[7].addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Output_Text[7].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Text[7].getOnMousePressed();
		Output_Text[7].getOnMouseReleased();
		Output_Text[7].setFont(Font.font("Futura", 18));
		Output_Text[7].setLayoutX(895);
		Output_Text[7].setLayoutY(438);
		root.getChildren().add(Output_Text[7]);

		Output_Lockquad[7] = new ImageView(new Image("filledquad.png"));
		Output_Lockquad[7].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockquad[7].getOnMouseReleased();
		Output_Lockquad[7].setLayoutX(785);
		Output_Lockquad[7].setLayoutY(423);
		Output_Lockquad[7].setFitHeight(40);
		Output_Lockquad[7].setFitWidth(40);
		root.getChildren().add(Output_Lockquad[7]);

		Output_Lockcross[7] = new ImageView(new Image("redcross.png"));
		Output_Lockcross[7].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_Lockcross[7].getOnMouseReleased();
		Output_Lockcross[7].setLayoutX(790);
		Output_Lockcross[7].setLayoutY(428);
		Output_Lockcross[7].setFitHeight(30);
		Output_Lockcross[7].setFitWidth(30);
		Output_Lockcross[7].setVisible(false);
		root.getChildren().add(Output_Lockcross[7]);

		Output_State[7][0] = new ImageView(new Image("tealorb.png"));
		Output_State[7][0].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[7][0].getOnMouseReleased();
		Output_State[7][0].setLayoutX(985);
		Output_State[7][0].setLayoutY(413);
		Output_State[7][0].setFitHeight(35);
		Output_State[7][0].setFitWidth(35);
		root.getChildren().add(Output_State[7][0]);

		Output_State[7][1] = new ImageView(new Image("greenorb.png"));
		Output_State[7][1].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[7][1].getOnMouseReleased();
		Output_State[7][1].setLayoutX(985);
		Output_State[7][1].setLayoutY(413);
		Output_State[7][1].setFitHeight(35);
		Output_State[7][1].setFitWidth(35);
		Output_State[7][1].setVisible(false);
		root.getChildren().add(Output_State[7][1]);

		Output_State[7][2] = new ImageView(new Image("redorb.png"));
		Output_State[7][2].addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Output_State[7][2].getOnMouseReleased();
		Output_State[7][2].setLayoutX(985);
		Output_State[7][2].setLayoutY(413);
		Output_State[7][2].setFitHeight(35);
		Output_State[7][2].setFitWidth(35);
		Output_State[7][2].setVisible(false);
		root.getChildren().add(Output_State[7][2]);

		for(int i=0;i<8;i++){
			MatchSize(Output_Text[i], 63, Output_Text[i].getFont().getSize());
			if(i<3){
				MatchSize(Head_Text[i], 150, Head_Text[i].getFont().getSize());
			}
		}

		System.out.println("Gui objects loaded: 70%");

		for(int i=0; i<8; i++){
			Output_Lockcross[i].setDisable(true);
			Output_Lockquad[i].setDisable(true);
		}
		if(dev_console_enabled){
			// Console
			Console = new TextArea();
			Console.setPrefSize(250, 400);
			Console.setLayoutX(500);
			Console.setLayoutY(130);
			Console.setWrapText(true);
			Console.setEditable(false);
			Console.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Console.setFont(Font.font("Futura", 13));
			root.getChildren().add(Console);

			// Permanently scroll down for new text
			Console.textProperty().addListener(new ChangeListener<Object>() {
				@Override
				public void changed(ObservableValue<?> observable, Object oldValue,
						Object newValue) {
					Console.setScrollTop(Double.MIN_VALUE);
				}
			});
		}


		if(dev_console_enabled && dev_promt_enabled){

			// Console Toggle
			Console_Button1 = new ImageView(new Image("iB12.png"));
			Console_Button1.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Console_Button1.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Console_Button1.getOnMousePressed();
			Console_Button1.getOnMouseReleased();
			Console_Button1.setLayoutX(585);
			Console_Button1.setLayoutY(80);
			root.getChildren().add(Console_Button1);

			Console_Button2 = new ImageView(new Image("iB3.png"));
			Console_Button2.setLayoutX(585);
			Console_Button2.setLayoutY(80);
			Console_Button2.setVisible(false);
			root.getChildren().add(Console_Button2);

			Console_ButtonText = new Text();
			Console_ButtonText.setText("Toggle");
			Console_ButtonText.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Console_ButtonText.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Console_ButtonText.getOnMousePressed();
			Console_ButtonText.getOnMouseReleased();
			Console_ButtonText.setFont(Font.font("Futura", 18));
			Console_ButtonText.setLayoutX(675);
			Console_ButtonText.setLayoutY(105);
			root.getChildren().add(Console_ButtonText);

		}

		System.out.println("Gui objects loaded: 80%");

		if(dev_promt_enabled){
			Dev_masterpw = new PasswordField();
			Dev_masterpw.setPromptText("Master Password");
			Dev_masterpw.setLayoutX(265);
			Dev_masterpw.setLayoutY(130);
			Dev_masterpw.setPrefWidth(160);
			Dev_masterpw.setFont(new Font("Arial", 16));
			Dev_masterpw.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			root.getChildren().add(Dev_masterpw);

			Dev_login = new Button("Login");
			Dev_login.setLayoutX(427);
			Dev_login.setLayoutY(130);
			Dev_login.setPrefWidth(58);
			Dev_login.setPrefHeight(31);
			Dev_login.setFont(new Font("Arial", 14));
			Dev_login.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_login.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					if(Dev_masterpw.getText().equals(MasterPassword)){
						String xn = "";
						for(int z=0; z<Dev_masterpw.getText().length(); z++){
							xn = xn+"T";
						}
						Dev_masterpw.setText(xn);
						setMasterLogin(true);
					}
				}
			});
			root.getChildren().add(Dev_login);

			Dev_logout = new Button("Logout");
			Dev_logout.setLayoutX(427);
			Dev_logout.setLayoutY(130);
			Dev_logout.setPrefWidth(58);
			Dev_logout.setPrefHeight(31);
			Dev_logout.setFont(new Font("Arial", 12));
			Dev_logout.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_logout.setVisible(false);
			Dev_logout.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					setMasterLogin(false);
					Dev_masterpw.setText("");
				}
			});
			root.getChildren().add(Dev_logout);

			Dev_cmdfield = new TextField();
			Dev_cmdfield.setPromptText("Enter Command");
			Dev_cmdfield.setLayoutX(265);
			Dev_cmdfield.setLayoutY(170);
			Dev_cmdfield.setPrefWidth(160);
			Dev_cmdfield.setPrefHeight(20);
			Dev_cmdfield.setFont(new Font("Arial", 16));
			Dev_cmdfield.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_cmdfield.setDisable(true);
			root.getChildren().add(Dev_cmdfield);

			Dev_sendcmd = new Button("Send");
			Dev_sendcmd.setLayoutX(430);
			Dev_sendcmd.setLayoutY(173);
			Dev_sendcmd.setPrefWidth(55);
			Dev_sendcmd.setFont(new Font("Arial", 14));
			Dev_sendcmd.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_sendcmd.setDisable(true);
			Dev_sendcmd.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					OtherStuff.addToCmdQueue(Dev_cmdfield.getText());
					Dev_cmdfield.setText("");
				}
			});
			root.getChildren().add(Dev_sendcmd);

			Dev_printfield = new TextField();
			Dev_printfield.setPromptText("Enter Message");
			Dev_printfield.setLayoutX(265);
			Dev_printfield.setLayoutY(210);
			Dev_printfield.setPrefWidth(160);
			Dev_printfield.setPrefHeight(20);
			Dev_printfield.setFont(new Font("Arial", 16));
			Dev_printfield.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_printfield.setDisable(true);
			root.getChildren().add(Dev_printfield);

			Dev_sendprint = new Button("Send");
			Dev_sendprint.setLayoutX(430);
			Dev_sendprint.setLayoutY(213);
			Dev_sendprint.setPrefWidth(55);
			Dev_sendprint.setFont(new Font("Arial", 14));
			Dev_sendprint.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_sendprint.setDisable(true);
			Dev_sendprint.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					OtherStuff.addToPrintQueue(Dev_printfield.getText());
					Dev_printfield.setText("");
				}
			});
			root.getChildren().add(Dev_sendprint);

			Dev_console = new TextField();
			Dev_console.setPromptText("Commandline");
			Dev_console.setLayoutX(265);
			Dev_console.setLayoutY(250);
			Dev_console.setPrefWidth(220);
			Dev_console.setPrefHeight(20);
			Dev_console.setFont(new Font("Arial", 16));
			Dev_console.setStyle("-fx-background-color: #000000; -fx-text-fill: #9400d3;" );
			Dev_console.setDisable(true);
			Dev_console.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					if(Dev_console.getText().equals("lock all")){
						for(int i=0; i<8; i++){
							Output_isLocked[i] = true;
							Output_Lockcross[i].setVisible(true);
						}
					}else if(Dev_console.getText().equals("unlock all")){
						for(int i=0; i<8; i++){
							Output_isLocked[i] = false;
							Output_Lockcross[i].setVisible(false);
						}	
					}else if(Dev_console.getText().equals("enable all")){
						for(int i=0; i<8; i++){
							SetState(Output_State[i][0], Output_State[i][1], Output_State[i][2], 1);

						}	
					}else if(Dev_console.getText().equals("disable all")){
						for(int i=0; i<8; i++){
							SetState(Output_State[i][0], Output_State[i][1], Output_State[i][2], 2);

						}	
					}
					Dev_console.setText("");
				}
			});
			root.getChildren().add(Dev_console);

			setDevVisibility(false);
		}

		if(!dev_console_enabled){
			for(int i=0;i<10;i++){
				FeedReader.RssTextObject[i].setX(500);
			}
		}

		System.out.println("Gui objects loaded: 90%");

		// Music
		Music_Head = new ImageView(new Image("iB12.png"));
		Music_Head.setLayoutX(809);
		Music_Head.setScaleX(1.9);
		Music_Head.setLayoutY(72);
		root.getChildren().add(Music_Head);

		Music_HeadText = new Text();
		Music_HeadText.setText("Musiksteuerung");
		Music_HeadText.setLayoutX(860);
		Music_HeadText.setLayoutY(99);
		Music_HeadText.setFont(Font.font("Futura", 20));
		root.getChildren().add(Music_HeadText);


		Music_Title = new Text();
		Music_Title.setText("Mpc is disabled.");
		Music_Title.setFont(Font.font("Futura", 14));
		Music_Title.setLayoutX(790);
		Music_Title.setLayoutY(135);
		root.getChildren().add(Music_Title);
		MatchSize(Music_Title, 235, 19);

		Music_Slider = new Slider();
		Music_Slider.setMin(0);
		Music_Slider.setMax(100);
		Music_Slider.setValue(55);
		Music_Slider.setShowTickLabels(true);
		Music_Slider.setMajorTickUnit(25);
		Music_Slider.setMinorTickCount(5);
		Music_Slider.setBlockIncrement(10);
		Music_Slider.setLayoutX(840);
		Music_Slider.setLayoutY(210);
		Music_Slider.setShowTickLabels(true);
		Music_Slider.setScaleX(1.5);
		Music_Slider.setScaleY(1.25);
		Music_Slider.setShowTickMarks(true);
		Music_Slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				int volume = (int) Math.floor(new_val.doubleValue());
				Client.processMessage("/AuthAction " + PrivateKey + " Music@volume@"+volume);
			}
		});
		root.getChildren().add(Music_Slider);

		Music_prev = new ImageView(new Image("prev.png"));
		Music_prev.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Music_prev.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Music_prev.getOnMouseReleased();
		Music_prev.getOnMousePressed();
		Music_prev.setLayoutX(820);
		Music_prev.setLayoutY(150);
		Music_prev.setFitHeight(40);
		Music_prev.setFitWidth(40);
		Music_prev.setVisible(true);
		root.getChildren().add(Music_prev);

		Music_pause = new ImageView(new Image("pause.png"));
		Music_pause.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Music_pause.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Music_pause.getOnMouseReleased();
		Music_pause.getOnMousePressed();
		Music_pause.setLayoutX(865);
		Music_pause.setLayoutY(150);
		Music_pause.setFitHeight(40);
		Music_pause.setFitWidth(40);
		Music_pause.setVisible(true);
		root.getChildren().add(Music_pause);

		Music_play = new ImageView(new Image("play.png"));
		Music_play.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Music_play.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Music_play.getOnMouseReleased();
		Music_play.getOnMousePressed();
		Music_play.setLayoutX(910);
		Music_play.setLayoutY(150);
		Music_play.setFitHeight(40);
		Music_play.setFitWidth(40);
		Music_play.setVisible(true);
		root.getChildren().add(Music_play);

		Music_next = new ImageView(new Image("next.png"));
		Music_next.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
		Music_next.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
		Music_next.getOnMouseReleased();
		Music_next.getOnMousePressed();
		Music_next.setLayoutX(955);
		Music_next.setLayoutY(150);
		Music_next.setFitHeight(40);
		Music_next.setFitWidth(40);
		Music_next.setVisible(true);
		root.getChildren().add(Music_next);


		System.out.println("Gui objects loaded: 100%");
		//primaryStage.setScene(Sroot);


		if(StartWithLoginScreen){
			System.out.println("Loading Objects of Login GUI");
			Pane Login = new Pane();

			screen_lock = new ImageView("lock.png");
			screen_lock.setLayoutX(970);
			screen_lock.setLayoutY(15);
			screen_lock.setFitHeight(50);
			screen_lock.setFitWidth(50);
			screen_lock.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			screen_lock.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			screen_lock.getOnMousePressed();
			screen_lock.getOnMouseReleased();
			root.getChildren().add(screen_lock);

			Login.setStyle("-fx-background-color: #000000");

			Login_LoginButton1 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton1.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton1.getOnMouseReleased();
			Login_LoginButton1.getOnMousePressed();
			Login_LoginButton1.setLayoutX(115);
			Login_LoginButton1.setLayoutY(115);
			Login_LoginButton1.setFitHeight(80);
			Login_LoginButton1.setFitWidth(80);
			Login_LoginButton1.setVisible(true);
			Login.getChildren().add(Login_LoginButton1);
			Login_SparkPos[0][0] = Login_LoginButton1.getLayoutX();
			Login_SparkPos[0][1] = Login_LoginButton1.getLayoutY();

			Login_LoginButton2 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton2.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton2.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton2.getOnMouseReleased();
			Login_LoginButton2.getOnMousePressed();
			Login_LoginButton2.setLayoutX(115);
			Login_LoginButton2.setLayoutY(415);
			Login_LoginButton2.setFitHeight(80);
			Login_LoginButton2.setFitWidth(80);
			Login_LoginButton2.setVisible(true);
			Login.getChildren().add(Login_LoginButton2);
			Login_SparkPos[1][0] = Login_LoginButton2.getLayoutX();
			Login_SparkPos[1][1] = Login_LoginButton2.getLayoutY();

			Login_LoginButton3 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton3.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton3.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton3.getOnMouseReleased();
			Login_LoginButton3.getOnMousePressed();
			Login_LoginButton3.setLayoutX(475);
			Login_LoginButton3.setLayoutY(115);
			Login_LoginButton3.setFitHeight(80);
			Login_LoginButton3.setFitWidth(80);
			Login_LoginButton3.setVisible(true);
			Login.getChildren().add(Login_LoginButton3);
			Login_SparkPos[2][0] = Login_LoginButton3.getLayoutX();
			Login_SparkPos[2][1] = Login_LoginButton3.getLayoutY();

			Login_LoginButton4 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton4.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton4.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton4.getOnMouseReleased();
			Login_LoginButton4.getOnMousePressed();
			Login_LoginButton4.setLayoutX(475);
			Login_LoginButton4.setLayoutY(415);
			Login_LoginButton4.setFitHeight(80);
			Login_LoginButton4.setFitWidth(80);
			Login_LoginButton4.setVisible(true);
			Login.getChildren().add(Login_LoginButton4);
			Login_SparkPos[3][0] = Login_LoginButton4.getLayoutX();
			Login_SparkPos[3][1] = Login_LoginButton4.getLayoutY();

			Login_LoginButton5 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton5.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton5.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton5.getOnMouseReleased();
			Login_LoginButton5.getOnMousePressed();
			Login_LoginButton5.setLayoutX(835);
			Login_LoginButton5.setLayoutY(115);
			Login_LoginButton5.setFitHeight(80);
			Login_LoginButton5.setFitWidth(80);
			Login_LoginButton5.setVisible(true);
			Login.getChildren().add(Login_LoginButton5);
			Login_SparkPos[4][0] = Login_LoginButton5.getLayoutX();
			Login_SparkPos[4][1] = Login_LoginButton5.getLayoutY();

			Login_LoginButton6 = new ImageView(new Image("tapbutton.png"));
			Login_LoginButton6.addEventHandler(MouseEvent.MOUSE_RELEASED, new MyEventHandler());
			Login_LoginButton6.addEventHandler(MouseEvent.MOUSE_PRESSED, new MyEventHandler());
			Login_LoginButton6.getOnMouseReleased();
			Login_LoginButton6.getOnMousePressed();
			Login_LoginButton6.setLayoutX(835);
			Login_LoginButton6.setLayoutY(415);
			Login_LoginButton6.setFitHeight(80);
			Login_LoginButton6.setFitWidth(80);
			Login_LoginButton6.setVisible(true);
			Login.getChildren().add(Login_LoginButton6);
			Login_SparkPos[5][0] = Login_LoginButton6.getLayoutX();
			Login_SparkPos[5][1] = Login_LoginButton6.getLayoutY();

			// Set up sparks (flying around the buttons)
			for(int i=0;i<6;i++){
				Login_Spark[i] = new ImageView(new Image("spark.png"));
				Login_Spark[i].setFitHeight(100);
				Login_Spark[i].setFitWidth(100);
				Login.getChildren().add(Login_Spark[i]);
			}

			SLogin = new Scene(Login, 1024, 600);
			primaryStage.setScene(SLogin);
			System.out.println("Finished loading Objects of Login GUI");
		}else{
			primaryStage.setScene(Sroot);
		}
		System.out.println("Launching GUI Now!!!");

		primaryStage.show();
		System.out.println("Finished [2]: GUI");
		System.out.println("Starting [3]: Final init");
		FinalInit();
		System.out.println("|>--- finished loading ---<|");
	}

	public void FinalInit(){
		// Turn LED's off again
		if(!Testbuild){

		}
		for(int i=0;i<8;i++){
			SetState(Output_State[i][0], Output_State[i][1], Output_State[i][2], 2);
		}
		System.out.println("Finished [3]: Final init");
		new ChangeOutStream();
		System.out.println("Stream changed into GUI - now Operating fully in the GUI console. ( only FX Thread )");
		setMasterLogin(true);
	}

	private static void setDevOpacity(double w){
		if(!isMasterLoggedIn){
			Dev_masterpw.setOpacity(w);
			Dev_logout.setOpacity(w);
			Dev_login.setOpacity(w);

			if(!(w < 0.25)){
				w = 0.25;
			}

			Dev_cmdfield.setOpacity(w);
			Dev_printfield.setOpacity(w);
			Dev_console.setOpacity(w);
			Dev_sendcmd.setOpacity(w);
			Dev_sendprint.setOpacity(w);
		}else{
			Dev_cmdfield.setOpacity(w);
			Dev_printfield.setOpacity(w);
			Dev_console.setOpacity(w);
			Dev_sendcmd.setOpacity(w);
			Dev_sendprint.setOpacity(w);
			Dev_login.setOpacity(w);
			Dev_logout.setOpacity(w);

			if(!(w < 0.25)){
				w = 0.25;
			}

			Dev_masterpw.setOpacity(w);
		}
	}

	private void setDevVisibility(boolean l){
		Dev_masterpw.setVisible(l);
		Dev_cmdfield.setVisible(l);
		Dev_printfield.setVisible(l);
		Dev_console.setVisible(l);
		Dev_sendcmd.setVisible(l);
		Dev_sendprint.setVisible(l);
		if(!isMasterLoggedIn){
			Dev_login.setVisible(l);
		}else{
			Dev_logout.setVisible(l);
		}
	}

	public static void setMasterLogin(boolean l){
		isMasterLoggedIn = l;
		if(l){
			Dev_masterpw.setDisable(l);
			Dev_cmdfield.setDisable(!l);
			Dev_printfield.setDisable(!l);
			Dev_console.setDisable(!l);
			Dev_login.setVisible(!l);
			Dev_logout.setVisible(l);
			Dev_sendcmd.setDisable(!l);
			Dev_sendprint.setDisable(!l);
			for(int i=0; i<8; i++){
				Output_Lockcross[i].setDisable(!l);
				Output_Lockquad[i].setDisable(!l);
			}
		}else{
			Dev_masterpw.setDisable(l);
			Dev_cmdfield.setDisable(!l);
			Dev_printfield.setDisable(!l);
			Dev_console.setDisable(!l);
			Dev_login.setVisible(!l);
			Dev_logout.setVisible(l);
			Dev_sendcmd.setDisable(!l);
			Dev_sendprint.setDisable(!l);
			for(int i=0; i<8; i++){
				Output_Lockcross[i].setDisable(!l);
				Output_Lockquad[i].setDisable(!l);
			}
		}
		setDevOpacity(masteropacity);
	}

	private void MatchSize(Text item, int maxwidth, double startsize){
		item.setFont(Font.font ("Futura", startsize));
		item.applyCss();

		double width = item.getLayoutBounds().getWidth();

		if(width > maxwidth){
			startsize = startsize - 0.25;
			MatchSize(item, maxwidth, startsize);
		}else{
			startsize = 19;
		}
	}

	public static void DisconnectFromServer(){
		try {
			connection.din.close();
			connection.dout.close();
			connection.socket.close();
		}catch (Exception e) {}
		try{connection.socket = null;}catch(Exception e){}
		try{connection.din = null;}catch(Exception e){}
		try{connection.dout = null;}catch(Exception e){}
		try{connection.thread = null;}catch(Exception e){}
		try{connection.running = false;}catch(Exception e){}
		try{connection = null;}catch(Exception e){}
	}

	public static boolean ConnectToServer(String ip, int port, String command, String params){
		try{
			connection = new Client(ip, port);
			if(!command.equals("")){
				Client.processMessage("/" + command + " " + params);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return true;
	}


	// Complete handeling for the login screen and the code .. ps: secret code :p
	public static void LoginChecker(Object e){
		if(e == Login_LoginButton1){
			if(Login_LoginButton1_State < 3){
				Login_LoginButton1_State++;
				if(Login_LoginButton1_State == 1){
					Login_Spark[0].setEffect(new Glow(0.33));
				}else if(Login_LoginButton1_State == 2){
					Login_Spark[0].setEffect(new Glow(0.66));
				}else if(Login_LoginButton1_State == 3){
					Login_Spark[0].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton1_State = 0;
				Login_Spark[0].setEffect(new Glow(0));
			}
		}else if(e == Login_LoginButton2){
			if(Login_LoginButton2_State < 3){
				Login_LoginButton2_State++;
				if(Login_LoginButton2_State == 1){
					Login_Spark[1].setEffect(new Glow(0.33));
				}else if(Login_LoginButton2_State == 2){
					Login_Spark[1].setEffect(new Glow(0.66));
				}else if(Login_LoginButton2_State == 3){
					Login_Spark[1].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton2_State = 0;
				Login_Spark[1].setEffect(new Glow(0));
			}
		}else if(e == Login_LoginButton3){
			if(Login_LoginButton3_State < 3){
				Login_LoginButton3_State++;
				if(Login_LoginButton3_State == 1){
					Login_Spark[2].setEffect(new Glow(0.33));
				}else if(Login_LoginButton3_State == 2){
					Login_Spark[2].setEffect(new Glow(0.66));
				}else if(Login_LoginButton3_State == 3){
					Login_Spark[2].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton3_State = 0;
				Login_Spark[2].setEffect(new Glow(0));
			}
		}else if(e == Login_LoginButton4){
			if(Login_LoginButton4_State < 3){
				Login_LoginButton4_State++;
				if(Login_LoginButton4_State == 1){
					Login_Spark[3].setEffect(new Glow(0.33));
				}else if(Login_LoginButton4_State == 2){
					Login_Spark[3].setEffect(new Glow(0.66));
				}else if(Login_LoginButton4_State == 3){
					Login_Spark[3].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton4_State = 0;
				Login_Spark[3].setEffect(new Glow(0));
			}
		}else if(e == Login_LoginButton5){
			if(Login_LoginButton5_State < 3){
				Login_LoginButton5_State++;
				if(Login_LoginButton5_State == 1){
					Login_Spark[4].setEffect(new Glow(0.33));
				}else if(Login_LoginButton5_State == 2){
					Login_Spark[4].setEffect(new Glow(0.66));
				}else if(Login_LoginButton5_State == 3){
					Login_Spark[4].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton5_State = 0;
				Login_Spark[4].setEffect(new Glow(0));
			}
		}else if(e == Login_LoginButton6){
			if(Login_LoginButton6_State < 3){
				Login_LoginButton6_State++;
				if(Login_LoginButton6_State == 1){
					Login_Spark[5].setEffect(new Glow(0.33));
				}else if(Login_LoginButton6_State == 2){
					Login_Spark[5].setEffect(new Glow(0.66));
				}else if(Login_LoginButton6_State == 3){
					Login_Spark[5].setEffect(new Glow(1.0));
				}
			}else{
				Login_LoginButton6_State = 0;
				Login_Spark[5].setEffect(new Glow(0));
			}
		}
		if(Login_LoginButton1_State == 1 && Login_LoginButton4_State == 2 && Login_LoginButton5_State == 1){
			if(Login_LoginButton2_State == 0 && Login_LoginButton3_State == 0 && Login_LoginButton6_State == 0){
				Login_LoginButton1_State = 0;
				Login_LoginButton2_State = 0;
				Login_LoginButton3_State = 0;
				Login_LoginButton4_State = 0;
				Login_LoginButton5_State = 0;
				Login_LoginButton6_State = 0;
				SwitchToMainScene();
			}
		}
	}

	// Let the sparks fly and work the Queues
	protected void update() {
		if(MainStage.getScene() == SLogin){
			for(int i = 0; i < 6; i++){
				Login_Spark[i].setLayoutX(Login_SparkPos[i][0] + 80*Math.cos(Math.toRadians(Login_SparkSeq[i])));
				Login_Spark[i].setLayoutY(Login_SparkPos[i][1] + 80*Math.sin(Math.toRadians(Login_SparkSeq[i])));
				Login_Spark[i].setRotate(Login_SparkSeq[i]);

				Login_SparkSeq[i] = Login_SparkSeq[i]+3;
				if(Login_SparkSeq[i] > 360){
					Login_SparkSeq[i] = 0;
				}
			}
		}else{
			if(goLeft){
				// Starts moving away from the console
				if(entrypos > 265){
					entrypos = entrypos - 6;
					masteropacity = masteropacity-0.0316;
					setDevOpacity(masteropacity);
					for(int i=0;i<10;i++){
						if(FeedReader.RssTextObject[i] != null){
							FeedReader.RssTextObject[i].setX(entrypos);
						}
					}
				}else{
					goLeft = false;
					Console.setVisible(true);
					masteropacity = 0;
					setDevVisibility(false);
					// Hits the left side
				}
			}else if(goRight){
				// Starts moving right towards the console
				setDevVisibility(true);
				Console.setVisible(false);
				if(entrypos < 500){
					entrypos = entrypos + 6;
					masteropacity = masteropacity+0.025;
					setDevOpacity(masteropacity);
					for(int i=0;i<10;i++){
						if(FeedReader.RssTextObject[i] != null){
							FeedReader.RssTextObject[i].setX(entrypos);
						}
					}
				}else{
					goRight = false;
					// hits the right side
					masteropacity = 1;
					setDevOpacity(masteropacity);
				}
			}

			// Print queue
			if(todoprint[0] != ""){
				for(int x = todoprintsize; x > -1; x--){
					System.out.println(todoprint[x]);
					todoprint[x] = "";
					todoprintsize--;
				}
			}
			// Cmd queue
			if(todocmd[0] != ""){
				for(int y = todocmdsize; y > -1; y--){

					todocmd[y] = "";
					todocmdsize--;
				}
			}
			// Get le time
			GeneralInformation.setText((OtherStuff.TheNormalTime() + Main.City + ", " + Thread_GetWeather.degree + "�C"));
			if(Thread_GetWeather.weathericon != null && !Weatherinit){
				refreshweather();
				Weatherinit = true;
			}
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Resets the weater, obviously
	public static void refreshweather(){
		OtherStuff.addToPrintQueue("Refreshed the Weather");
		Thread_GetWeather.StartCheck(City);
		OtherStuff.addToCmdQueue("Refresh@WeatherTextLabel");
		OtherStuff.addToCmdQueue("Refresh@WeatherIconLabel");
	}

	// to change the icons of the check states // Light/door/window ect.
	public static void SetState(ImageView img1, ImageView img2, ImageView img3, int state){
		if(state == 0){
			img1.setVisible(true);
			img2.setVisible(false);
			img3.setVisible(false);
		}else if(state == 1){
			img1.setVisible(false);
			img2.setVisible(true);
			img3.setVisible(false);
		}else if(state == 2){
			img1.setVisible(false);
			img2.setVisible(false);
			img3.setVisible(true);
		}else{
			System.out.println("SetStates Error - Out of bounds");
		}
	}

	// Switches from Main to Login Scene
	public static void SwitchToMainScene(){
		MainStage.setScene(Sroot);
		for(int i=0;i<6;i++){
			Login_Spark[i].setEffect(new Glow(0.0));
		}
	}

	public static void SwitchToLoginScene(){
		MainStage.setScene(SLogin);
	}

	// Refresh of music title
	public static void RefreshMpc(){
		Client.processMessage("/AuthAction " + PrivateKey + " Music@getTitle");
	}
	// EVERY Button EVENT! Release, click, pressed all the cool stuff :p Both login and root scene
	class MyEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent e) {
			// Buttons pressed, set state to pressed and change icon & do smth.
			if((e.getSource() == Output_Button[0][0] || e.getSource() == Output_Text[0]) && !Output_isLocked[0]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output1_Button");
					Output_Button[0][1].setVisible(false);
					Output_Button[0][0].setVisible(true);
					Output_Text[0].setLayoutX(Output_Text[0].getLayoutX()-12);
					Output_Text[0].setLayoutY(Output_Text[0].getLayoutY()-10);
					if(Output_iState[0] == 0){
						SetState(Output_State[0][0], Output_State[0][1], Output_State[0][2], 1);
						Output_iState[0] = 1;
					}else if(Output_iState[0] == 1){
						SetState(Output_State[0][0], Output_State[0][1], Output_State[0][2], 2);
						Output_iState[0] = 2;
					}else if(Output_iState[0] == 2){
						SetState(Output_State[0][0], Output_State[0][1], Output_State[0][2], 1);
						Output_iState[0] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@0");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output1_Button");
					Output_Button[0][0].setVisible(false);
					Output_Button[0][1].setVisible(true);
					Output_Text[0].setLayoutX(Output_Text[0].getLayoutX()+12);
					Output_Text[0].setLayoutY(Output_Text[0].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[0] || e.getSource() == Output_Lockcross[0]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[0].isVisible()){
						Output_Lockcross[0].setVisible(true);
						Output_isLocked[0] = true;
						System.out.println("Locked Output1");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@0");
					}else if(Output_Lockcross[0].isVisible()){
						Output_Lockcross[0].setVisible(false);
						Output_isLocked[0] = false;
						System.out.println("Unlocked Output1");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@0");
					}
				}
			}
			else if((e.getSource() == Output_Button[1][0] || e.getSource() == Output_Text[1]) && !Output_isLocked[1]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output2_Button");
					Output_Button[1][1].setVisible(false);
					Output_Button[1][0].setVisible(true);
					Output_Text[1].setLayoutX(Output_Text[1].getLayoutX()-12);
					Output_Text[1].setLayoutY(Output_Text[1].getLayoutY()-10);
					if(Output_iState[1] == 0){
						SetState(Output_State[1][0], Output_State[1][1], Output_State[1][2], 1);
						Output_iState[1] = 1;
					}else if(Output_iState[1] == 1){
						SetState(Output_State[1][0], Output_State[1][1], Output_State[1][2], 2);
						Output_iState[1] = 2;
					}else if(Output_iState[1] == 2){
						SetState(Output_State[1][0], Output_State[1][1], Output_State[1][2], 1);
						Output_iState[1] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@1");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output2_Button");
					Output_Button[1][0].setVisible(false);
					Output_Button[1][1].setVisible(true);
					Output_Text[1].setLayoutX(Output_Text[1].getLayoutX()+12);
					Output_Text[1].setLayoutY(Output_Text[1].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[1] || e.getSource() == Output_Lockcross[1]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[1].isVisible()){
						Output_Lockcross[1].setVisible(true);
						Output_isLocked[1] = true;
						System.out.println("Locked Output2");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@1");
					}else if(Output_Lockcross[1].isVisible()){
						Output_Lockcross[1].setVisible(false);
						Output_isLocked[1] = false;
						System.out.println("Unlocked Output2");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@1");
					}
				}
			}
			else if((e.getSource() == Output_Button[2][0] || e.getSource() == Output_Text[2]) && !Output_isLocked[2]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output3_Button");
					Output_Button[2][1].setVisible(false);
					Output_Button[2][0].setVisible(true);
					Output_Text[2].setLayoutX(Output_Text[2].getLayoutX()-12);
					Output_Text[2].setLayoutY(Output_Text[2].getLayoutY()-10);
					if(Output_iState[2] == 0){
						SetState(Output_State[2][0], Output_State[2][1], Output_State[2][2], 1);
						Output_iState[2] = 1;
					}else if(Output_iState[2] == 1){
						SetState(Output_State[2][0], Output_State[2][1], Output_State[2][2], 2);
						Output_iState[2] = 2;
					}else if(Output_iState[2] == 2){
						SetState(Output_State[2][0], Output_State[2][1], Output_State[2][2], 1);
						Output_iState[2] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@2");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output3_Button");
					Output_Button[2][0].setVisible(false);
					Output_Button[2][1].setVisible(true);
					Output_Text[2].setLayoutX(Output_Text[2].getLayoutX()+12);
					Output_Text[2].setLayoutY(Output_Text[2].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[2] || e.getSource() == Output_Lockcross[2]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[2].isVisible()){
						Output_Lockcross[2].setVisible(true);
						Output_isLocked[2] = true;
						System.out.println("Locked Output3");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@2");
					}else if(Output_Lockcross[2].isVisible()){
						Output_Lockcross[2].setVisible(false);
						Output_isLocked[2] = false;
						System.out.println("Unlocked Output3");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@2");
					}
				}
			}
			else if((e.getSource() == Output_Button[3][0] || e.getSource() == Output_Text[3]) && !Output_isLocked[3]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output4_Button");
					Output_Button[3][1].setVisible(false);
					Output_Button[3][0].setVisible(true);
					Output_Text[3].setLayoutX(Output_Text[3].getLayoutX()-12);
					Output_Text[3].setLayoutY(Output_Text[3].getLayoutY()-10);
					if(Output_iState[3] == 0){
						SetState(Output_State[3][0], Output_State[3][1], Output_State[3][2], 1);
						Output_iState[3] = 1;
					}else if(Output_iState[3] == 1){
						SetState(Output_State[3][0], Output_State[3][1], Output_State[3][2], 2);
						Output_iState[3] = 2;
					}else if(Output_iState[3] == 2){
						SetState(Output_State[3][0], Output_State[3][1], Output_State[3][2], 1);
						Output_iState[3] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@3");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output4_Button");
					Output_Button[3][0].setVisible(false);
					Output_Button[3][1].setVisible(true);
					Output_Text[3].setLayoutX(Output_Text[3].getLayoutX()+12);
					Output_Text[3].setLayoutY(Output_Text[3].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[3] || e.getSource() == Output_Lockcross[3]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[3].isVisible()){
						Output_Lockcross[3].setVisible(true);
						Output_isLocked[3] = true;
						System.out.println("Locked Output4");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@3");
					}else if(Output_Lockcross[3].isVisible()){
						Output_Lockcross[3].setVisible(false);
						Output_isLocked[3] = false;
						System.out.println("Unlocked Output4");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@3");
					}
				}
			}
			else if((e.getSource() == Output_Button[4][0] || e.getSource() == Output_Text[4]) && !Output_isLocked[4]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output5_Button");
					Output_Button[4][1].setVisible(false);
					Output_Button[4][0].setVisible(true);
					Output_Text[4].setLayoutX(Output_Text[4].getLayoutX()-12);
					Output_Text[4].setLayoutY(Output_Text[4].getLayoutY()-10);
					if(Output_iState[4] == 0){
						SetState(Output_State[4][0], Output_State[4][1], Output_State[4][2], 1);
						Output_iState[4] = 1;
					}else if(Output_iState[4] == 1){
						SetState(Output_State[4][0], Output_State[4][1], Output_State[4][2], 2);
						Output_iState[4] = 2;
					}else if(Output_iState[4] == 2){
						SetState(Output_State[4][0], Output_State[4][1], Output_State[4][2], 1);
						Output_iState[4] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@4");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output5_Button");
					Output_Button[4][0].setVisible(false);
					Output_Button[4][1].setVisible(true);
					Output_Text[4].setLayoutX(Output_Text[4].getLayoutX()+12);
					Output_Text[4].setLayoutY(Output_Text[4].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[4] || e.getSource() == Output_Lockcross[4]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[4].isVisible()){
						Output_Lockcross[4].setVisible(true);
						Output_isLocked[4] = true;
						System.out.println("Locked Output5");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@4");
					}else if(Output_Lockcross[4].isVisible()){
						Output_Lockcross[4].setVisible(false);
						Output_isLocked[4] = false;
						System.out.println("Unlocked Output5");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@4");
					}
				}
			}
			else if((e.getSource() == Output_Button[5][0] || e.getSource() == Output_Text[5]) && !Output_isLocked[5]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output6_Button");
					Output_Button[5][1].setVisible(false);
					Output_Button[5][0].setVisible(true);
					Output_Text[5].setLayoutX(Output_Text[5].getLayoutX()+12);
					Output_Text[5].setLayoutY(Output_Text[5].getLayoutY()-10);
					if(Output_iState[5] == 0){
						SetState(Output_State[5][0], Output_State[5][1], Output_State[5][2], 1);
						Output_iState[5] = 1;
					}else if(Output_iState[5] == 1){
						SetState(Output_State[5][0], Output_State[5][1], Output_State[5][2], 2);
						Output_iState[5] = 2;
					}else if(Output_iState[5] == 2){
						SetState(Output_State[5][0], Output_State[5][1], Output_State[5][2], 1);
						Output_iState[5] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@5");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output6_Button");
					Output_Button[5][0].setVisible(false);
					Output_Button[5][1].setVisible(true);
					Output_Text[5].setLayoutX(Output_Text[5].getLayoutX()-12);
					Output_Text[5].setLayoutY(Output_Text[5].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[5] || e.getSource() == Output_Lockcross[5]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[5].isVisible()){
						Output_Lockcross[5].setVisible(true);
						System.out.println("Locked Output6");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@5");
						Output_isLocked[5] = true;
					}else if(Output_Lockcross[5].isVisible()){
						Output_Lockcross[5].setVisible(false);
						System.out.println("Unlocked Output6");
						Output_isLocked[5] = false;
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@5");
					}
				}
			}
			else if((e.getSource() == Output_Button[6][0] || e.getSource() == Output_Text[6]) && !Output_isLocked[6]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output7_Button");
					Output_Button[6][1].setVisible(false);
					Output_Button[6][0].setVisible(true);
					Output_Text[6].setLayoutX(Output_Text[6].getLayoutX()+12);
					Output_Text[6].setLayoutY(Output_Text[6].getLayoutY()-10);
					if(Output_iState[6] == 0){
						SetState(Output_State[6][0], Output_State[6][1], Output_State[6][2], 1);
						Output_iState[6] = 1;
					}else if(Output_iState[6] == 1){
						SetState(Output_State[6][0], Output_State[6][1], Output_State[6][2], 2);
						Output_iState[6] = 2;
					}else if(Output_iState[6] == 2){
						SetState(Output_State[6][0], Output_State[6][1], Output_State[6][2], 1);
						Output_iState[6] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@6");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output7_Button");
					Output_Button[6][0].setVisible(false);
					Output_Button[6][1].setVisible(true);
					Output_Text[6].setLayoutX(Output_Text[6].getLayoutX()-12);
					Output_Text[6].setLayoutY(Output_Text[6].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[6] || e.getSource() == Output_Lockcross[6]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[6].isVisible()){
						Output_Lockcross[6].setVisible(true);
						System.out.println("Locked Output7");
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@6");
						Output_isLocked[6] = true;
					}else if(Output_Lockcross[6].isVisible()){
						Output_Lockcross[6].setVisible(false);
						System.out.println("Unlocked Outpu7");
						Output_isLocked[6] = false;
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@6");
					}
				}
			}
			else if((e.getSource() == Output_Button[7][0] || e.getSource() == Output_Text[7]) && !Output_isLocked[7]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Output8_Button");
					Output_Button[7][1].setVisible(false);
					Output_Button[7][0].setVisible(true);
					Output_Text[7].setLayoutX(Output_Text[7].getLayoutX()+12);
					Output_Text[7].setLayoutY(Output_Text[7].getLayoutY()-10);
					if(Output_iState[7] == 0){
						SetState(Output_State[7][0], Output_State[7][1], Output_State[7][2], 1);
						Output_iState[7] = 1;
					}else if(Output_iState[7] == 1){
						SetState(Output_State[7][0], Output_State[7][1], Output_State[7][2], 2);
						Output_iState[7] = 2;
					}else if(Output_iState[7] == 2){
						SetState(Output_State[7][0], Output_State[7][1], Output_State[7][2], 1);
						Output_iState[7] = 1;
					}
					Client.processMessage("/AuthAction " + PrivateKey + " Toggle@Output@7");
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Output8_Button");
					Output_Button[7][0].setVisible(false);
					Output_Button[7][1].setVisible(true);
					Output_Text[7].setLayoutX(Output_Text[7].getLayoutX()-12);
					Output_Text[7].setLayoutY(Output_Text[7].getLayoutY()+10);
				}
			}
			else if(e.getSource() == Output_Lockquad[7] || e.getSource() == Output_Lockcross[7]){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					if(!Output_Lockcross[7].isVisible()){
						Output_Lockcross[7].setVisible(true);
						Output_isLocked[7] = true;
						Client.processMessage("/AuthAction " + PrivateKey + " Lock@7");
						System.out.println("Locked Output8");
					}else if(Output_Lockcross[7].isVisible()){
						Output_Lockcross[7].setVisible(false);
						Output_isLocked[7] = false;
						System.out.println("Unlocked Outpu8");
						Client.processMessage("/AuthAction " + PrivateKey + " Unlock@7");
					}
				}
			}
			else if(e.getSource() == Music_next){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					Music_next.setOpacity(1);
					Client.processMessage("/AuthAction " + PrivateKey + " Music@next");
					System.out.println("Triggered *Next Title*");
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					Music_next.setOpacity(0.5);
				}
			}
			else if(e.getSource() == Music_prev){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					Music_prev.setOpacity(1);
					Client.processMessage("/AuthAction " + PrivateKey + " Music@prev");
					System.out.println("Triggered *Prev Title*");
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					Music_prev.setOpacity(0.5);
				}
			}
			else if(e.getSource() == Music_pause){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					Music_pause.setOpacity(1);
					Client.processMessage("/AuthAction " + PrivateKey + " Music@pause");
					System.out.println("Triggered *Pause Music*");
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					Music_pause.setOpacity(0.5);
				}
			}
			else if(e.getSource() == Music_play){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					Music_play.setOpacity(1);
					Client.processMessage("/AuthAction " + PrivateKey + " Music@play");
					System.out.println("Triggered *Play Music*");
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					Music_play.setOpacity(0.5);
				}
			}
			else if(e.getSource() == Console_Button1 || e.getSource() == Console_ButtonText){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					System.out.println("Released & Triggered Console Toggle");
					Console_Button2.setVisible(false);
					Console_Button1.setVisible(true);

					if(Console.isVisible()){
						goRight = true;
						goLeft = false;
					}else if(!Console.isVisible() && goLeft){
						goRight = true;
						goLeft = false;
					}else{
						goRight = false;
						goLeft = true;
					}
					Console_ButtonText.setLayoutX(Console_ButtonText.getLayoutX()+12);
					Console_ButtonText.setLayoutY(Console_ButtonText.getLayoutY()-10);
				}else if (e.getEventType() == MouseEvent.MOUSE_PRESSED){
					System.out.println("Pressed Console Toggle");
					Console_Button1.setVisible(false);
					Console_Button2.setVisible(true);
					Console_ButtonText.setLayoutX(Console_ButtonText.getLayoutX()-12);
					Console_ButtonText.setLayoutY(Console_ButtonText.getLayoutY()+10);
				}
			}

			// Login Stuff
			else if(e.getSource() == Login_LoginButton1 || e.getSource() == Login_LoginButton2 || e.getSource() == Login_LoginButton3 || e.getSource() == Login_LoginButton4 || e.getSource() == Login_LoginButton5 || e.getSource() == Login_LoginButton6){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					((Node) e.getSource()).setOpacity(1);
					LoginChecker(e.getSource());	
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					((Node) e.getSource()).setOpacity(0.5);
				}
			}
			else if(e.getSource() == screen_lock){
				if(e.getEventType() == MouseEvent.MOUSE_RELEASED){
					((Node) e.getSource()).setOpacity(1);
					if(StartWithLoginScreen){
						SwitchToLoginScene();
					}
				}else if(e.getEventType() == MouseEvent.MOUSE_PRESSED){
					((Node) e.getSource()).setOpacity(0.5);
				}
			}
		}
	}
}

