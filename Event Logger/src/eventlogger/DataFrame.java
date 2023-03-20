/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

/**
 *
 * @author I14746
 */
public class DataFrame {

    public byte CPU_address;
    public byte CMD;
    public byte[] data;
    public byte Csum = 0;

    public DataFrame() {

    }
}
