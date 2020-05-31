package rad.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class StatusPanel extends JPanel {
    GraphPanel p;
    public StatusPanel(GraphPanel panel)
    {
        super(new GridLayout(2,4,10,10));
        this.p = panel;
        JSlider divSlide = new JSlider(JSlider.HORIZONTAL,10, 100, 20);
        divSlide.addChangeListener(e -> panel.segnum = divSlide.getValue());

        JSlider gridSlide = new JSlider(JSlider.HORIZONTAL, 5, 50, 10);
        gridSlide.addChangeListener(e -> panel.gridDensity = gridSlide.getValue());

        JSlider fidelitySlide = new JSlider(JSlider.HORIZONTAL, 1, 20, 4);
        gridSlide.addChangeListener(e -> {
            p.maxError = 1/fidelitySlide.getValue();
            p.maxDepth = fidelitySlide.getValue()*2;
            p.repaint();
        });

        JCheckBox divBox = new JCheckBox("Automatyczny krok rysowania",true);
        divBox.addItemListener(e -> {
            int i = e.getStateChange();
            if(i == 1) {p.autostep = true; divSlide.setEnabled(false); fidelitySlide.setEnabled(true);}
            else {p.autostep =false; divSlide.setEnabled(true); fidelitySlide.setEnabled(false);}
            p.repaint();
        });

        JCheckBox divdrawBox = new JCheckBox("Rysowanie segmentow",false);
        divdrawBox.addItemListener(e -> {
            int i = e.getStateChange();
            if(i == 1) p.showDivisions = true;
            else p.showDivisions =false;
            p.repaint();
        });
        JButton clrbutton = new JButton("CZYSC");
        clrbutton.addActionListener(e -> {
            p.functions.clear();
            p.repaint();
        });
        JButton helpbtn = new JButton("POMOCY!");
        helpbtn.addActionListener(e -> {
            showHelp();
        });
        JPanel p1=new JPanel(),p2=new JPanel(),p3=new JPanel();
        p1.add(new JLabel("Ilosc segmentow:"));
        p1.add(divSlide);
        p2.add(new JLabel("Dokladnosc wykresu:"));
        p2.add(fidelitySlide);
        p3.add(new JLabel("Gestosc podzialki:"));
        p3.add(gridSlide);

        add(p1);
        add(p2);
        add(p3);
        add(divBox);
        add(divdrawBox);
        add(clrbutton);
        add(helpbtn);
    }
    public void showHelp()
    {
        try {
            URL url = this.getClass().getClassLoader().getResource("./helppage.html");
            Desktop.getDesktop().browse(new URI("file://"+url.getPath()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
