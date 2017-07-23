/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import eventlogger.common.SharedData;
import java.util.TimerTask;

/**
 *
 * @author I14746
 */
public class TimerThread extends TimerTask{

    @Override
    public void run() {
        SharedData.getSingletonObject().time_out = true;
    }
    
}
