/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventlogger;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

/**
 * A demo showing four pie charts.
 */
public class PieChart {

    private String title = "";

    /**
     * Creates a new demo instance.
     *
     * @param title the frame title.
     */

    public PieChart(String title) {
        this.title = title;
    }

    public JPanel PieChart(String title) {

        //super(title);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Section 1", 23.3);
        dataset.setValue("Section 2", 56.5);
        dataset.setValue("Section 3", 43.3);
        dataset.setValue("Section 4", 11.1);

        JFreeChart chart1 = ChartFactory.createPieChart("Chart 1", dataset, false, false, false);
        JFreeChart chart2 = ChartFactory.createPieChart("Chart 2", dataset, false, false, false);
        PiePlot plot2 = (PiePlot) chart2.getPlot();
        plot2.setCircular(false);
        JFreeChart chart3 = ChartFactory.createPieChart3D("Chart 3", dataset, false, false, false);
        PiePlot3D plot3 = (PiePlot3D) chart3.getPlot();
        plot3.setForegroundAlpha(0.6f);
        plot3.setCircular(true);
        JFreeChart chart4 = ChartFactory.createPieChart3D("Chart 4", dataset, false, false, false);
        PiePlot3D plot4 = (PiePlot3D) chart4.getPlot();
        plot4.setForegroundAlpha(0.6f);

        panel.add(new ChartPanel(chart1));
        panel.add(new ChartPanel(chart2));
        panel.add(new ChartPanel(chart3));
        panel.add(new ChartPanel(chart4));

        panel.setPreferredSize(new Dimension(800, 600));
        return panel;

    }

}
