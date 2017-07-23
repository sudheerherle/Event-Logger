/*
 * EventLoggerApp.java
 */

package eventlogger;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class EventLoggerApp extends SingleFrameApplication {
     EventLoggerView evlogView;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
         if(evlogView==null){
            evlogView = new EventLoggerView(this);
            show(evlogView);
        }
    }

     public EventLoggerView getView(){
       return this.evlogView;
   }
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of EventLoggerApp
     */
    public static EventLoggerApp getApplication() {
        return Application.getInstance(EventLoggerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(EventLoggerApp.class, args);
    }
}
