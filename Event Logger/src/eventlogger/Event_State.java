/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

/**
 *
 * @author I14746
 */
public enum Event_State {
        FAIL_STATE(0),
        GOOD_STATE(1);
       private int val;
        private Event_State(int val) {
        this.val = val;     
    }
     public int getOrdinal(){
        return val;
    }  
}
