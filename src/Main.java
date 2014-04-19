import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Robot;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main
{
    final static int SIZE = 200;
    static BufferedImage img = null;
    public static void main(String[] args)
    {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image File", "png", "jpg", "jpeg", "bmp", "gif");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION)
        {
            System.out.println("Bad file chosen");
            System.exit(1);
        }

        String pictureFilePath = chooser.getSelectedFile().getAbsolutePath();
        System.out.println("" + chooser.getSelectedFile().getAbsolutePath());


        try
        {
            img = ImageIO.read(new File(pictureFilePath));
        }
        catch (IOException e) {}

        BufferedImage newImg = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_ARGB);
        newImg.getGraphics().drawImage(img.getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT),0,0,null);
        img = newImg;

        Robot rob = null;
        try
        {
           rob = new Robot();
        }
        catch (Exception e){}

        int totalGrayscale = 0;
        for (int x = 0; x < img.getWidth(); x++)
        {
            for (int y = 0; y < img.getHeight(); y++)
            {
                int rawColor = img.getRGB(x, y);
                int grayscale = colorToGrayscale(rawColor);
                int newColor = 0xFF;
                newColor = (newColor << 8) | grayscale;
                newColor = (newColor << 8) | grayscale;
                newColor = (newColor << 8) | grayscale;
                img.setRGB(x, y, newColor);
                totalGrayscale += grayscale;
            }
        }

        int averageGrayscale = totalGrayscale / (SIZE * SIZE);

        rob.delay(5000);
        Point cursorStart = MouseInfo.getPointerInfo().getLocation();

        for (int x = 0; x < img.getWidth(); x+=3)
        {
            for (int y = 0; y < img.getHeight(); y+=3)
            {
                int color = (0xFF & img.getRGB(x,y));
                if (color < averageGrayscale)
                {
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                    rob.mouseRelease(InputEvent.BUTTON1_MASK);
                    rob.mouseMove(cursorStart.x + x, cursorStart.y + y);
                    //rob.delay(2);
                }

                //rob.delay(10);
            }
        }

        JFrame gui = new JFrame()
        {
            public void paint(Graphics g)
            {
                g.drawImage(img, 0, 0, null);
            }
        };

        gui.setVisible(true);
        gui.setSize(SIZE, SIZE);
    }
    private static int colorToGrayscale(int color)
    {
        int grayscale = 0;
        grayscale += ((0x00FF0000 & color) >> 16) * 0.3;
        grayscale += ((0x0000FF00 & color) >> 8)  * 0.59;
        grayscale += ((0x000000FF & color) >> 0)  * 0.11;
        return grayscale;
    }
}
