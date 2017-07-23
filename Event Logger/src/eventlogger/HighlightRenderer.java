/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eventlogger;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
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
            if(value==null) return this;
             if(value.toString().contains("FAILURE")
                    ||value.toString().contains("DEFECTIVE")
                     ||value.toString().contains("FAILED")){
                setForeground(Color.RED);
            }else if(value.toString().contains("CLEAR")){
                setForeground(Color.GREEN);
            }else if(value.toString().contains("MISSING")){
                setForeground(Color.PINK);
            }else if(value.toString().contains("NORMAL")){
                setForeground(Color.BLUE);
            }else if(value.toString().contains("OCCUPIED")){
                setForeground(Color.ORANGE);
            }else{
               setForeground(Color.BLACK); 
            }
            return this;
        }

//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            setBackground(null);
//            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            setText(String.valueOf(value));
//            boolean interestingRow = row % 5 == 2;
//            boolean secondColumn = column == 1;
//            if (interestingRow && secondColumn) {
//                setBackground(Color.ORANGE);
//            } else if (interestingRow) {
//                setBackground(Color.YELLOW);
//            } else if (secondColumn) {
//                setBackground(Color.RED);
//            }
//            return this;
//        }
}
