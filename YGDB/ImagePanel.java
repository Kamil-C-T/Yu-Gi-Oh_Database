//PACKAGE
package YGDB;

//IMPORTS
import javax.swing.*;
import java.awt.*;

//CLASS
public class ImagePanel extends JPanel {
    private Image bg;

    public ImagePanel(Image background) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int)size.getWidth(),(int)size.getHeight());
        this.bg = background;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getWidth(), this.getHeight());
    }
}