/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author i14746
 */
public class UpdateRTCTime extends EventLoggerView implements Runnable{

    EventLoggerView view = this;
    public UpdateRTCTime(SingleFrameApplication app) {
        super(app);
        EventLoggerApp as = (EventLoggerApp) app;
        view = as.getView();
    }
   
    public void run() {
       view.UpdateRTC();
    }
    
}
