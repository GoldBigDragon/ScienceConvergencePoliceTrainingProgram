package goldbigdragon.github.io;

import goldbigdragon.github.io.function.menu.main.MainView;
import goldbigdragon.github.io.function.menu.main.threads.TimerThread;
import goldbigdragon.github.io.util.ConfigUtil;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static final String version = "0.0.1";
    public static final String lastUpdate = "2020-06-19-21:37";
    public static final String SPLIT_SYMBOL = "㏇";
    public static int maxThreads = 3;

    public static MainVariables mainVariables = null;

    public static final String dbDirectory = "c:/YSU/SCPTP";

    public static void main(String[] args) {
        mainVariables = new MainVariables();
        try {
            new ConfigUtil().reload();
        } catch(IOException e) {
            mainVariables.playSound = true;
            e.printStackTrace();
        }
        int processors  = Runtime.getRuntime().availableProcessors();
        maxThreads = processors;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new TimerThread();
        new SoundUtil();
        new MainView().view();
    }
}