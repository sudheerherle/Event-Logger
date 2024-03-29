/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author I14746
 */
public class HighlightRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText(String.valueOf(value));
        if (value == null) {
            return this;
        }
        if (value.toString().contains("Failure")
                || value.toString().contains("Defective")
                || value.toString().contains("Failed")
                || value.toString().contains("Mismatch")
                || value.toString().contains("Direct")
                || value.toString().contains("Pulsating")
                || value.toString().contains("NOT Detecting")
                || value.toString().contains("Influence")
                || value.toString().contains("Theft")
                || value.toString().contains("Door Open")
                || value.toString().contains("BAD")) {
            setForeground(Color.RED);
        } else if (value.toString().contains("Clear")
                || value.toString().contains("Restored")) {
            setForeground(Color.GREEN);
        } else if (value.toString().contains("Missing")) {
            setForeground(Color.PINK);
        } else if (value.toString().contains("Normal")) {
            setForeground(Color.BLUE);
        } else if (value.toString().contains("Occupied")) {
            setForeground(Color.ORANGE);
        } else {
            setForeground(Color.BLACK);
        }
        return this;
    }
}
