package rad.gui;

import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon {

    Color c;
    int size;
    public ColorIcon(Color c,int size)
    {
        this.c = c;
        this.size = size;
    }
    @Override
    public void paintIcon(Component comp, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D)g.create();

        g2d.setColor(c);
        g2d.fillRect(x+1,y+1,size-2,size-2);
        g2d.setColor(Color.black);
        g2d.drawRect(x,y,size,size);

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
