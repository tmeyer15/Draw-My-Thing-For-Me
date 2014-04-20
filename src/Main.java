import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.awt.Color;
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
import java.awt.Toolkit;
import java.awt.Dimension;


public class Main
{
    final static int SIZE = 200;
    static BufferedImage img = null;

    final static Color GREY = new Color(109,109,109);
    final static Color BLACK = new Color(  7,  7,  7);
    final static Color WHITE = new Color(255,255,255);
    final static Color RED = new Color(222,  0, 22);
    final static Color GREEN = new Color( 67,199,  0);
    final static Color BLUE = new Color(  0,  0,255);
    final static Color LIGHTBLUE = new Color(155,182,255);
    final static Color YELLOW = new Color(238,240,  0);
    final static Color ORANGE = new Color(238,130,  0);
    final static Color PURPLE = new Color( 79, 22,132);
    final static Color PINK = new Color(226,161,244);
    final static Color TAN = new Color(206,158,109);
    final static Color BROWN = new Color( 75, 43, 10);

    public static void main(String[] args)
    {
        /*
        Robot robo = null;
        try
        {
           robo = new Robot();
        }
        catch (Exception e){}

        for(int asdf = 0; asdf < 100000; asdf++)
        {

            Point p = MouseInfo.getPointerInfo().getLocation();
            System.out.println(robo.getPixelColor(p.x, p.y));

            robo.delay(2000);
        }*/

        //Pick the image we want to draw
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

        //Create the set of colors that we will use
        HashSet<Color> expectedColors = new HashSet<Color>();
        expectedColors.add(GREY);
        expectedColors.add(BLACK);
        //expectedColors.add(WHITE);
        expectedColors.add(RED);
        expectedColors.add(GREEN);
        expectedColors.add(BLUE);
        expectedColors.add(LIGHTBLUE);
        expectedColors.add(YELLOW);
        expectedColors.add(ORANGE);
        expectedColors.add(PURPLE);
        expectedColors.add(PINK);
        expectedColors.add(TAN);
        expectedColors.add(BROWN);

        HashSet<Color> searchColors = new HashSet<Color>();
        searchColors.add(GREY);
        searchColors.add(BLACK);
        searchColors.add(RED);
        searchColors.add(GREEN);
        searchColors.add(BLUE);
        searchColors.add(LIGHTBLUE);
        searchColors.add(YELLOW);
        searchColors.add(ORANGE);
        searchColors.add(PURPLE);
        searchColors.add(PINK);
        searchColors.add(TAN);
        searchColors.add(BROWN);


        //Resize the image into a square
        BufferedImage newImg = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_ARGB);
        newImg.getGraphics().drawImage(img.getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT),0,0,null);
        img = newImg;

        //Initialize the robot and wait five seconds for the user to position the mouse
        Robot rob = null;
        try
        {
           rob = new Robot();
        }
        catch (Exception e){}
        rob.delay(5000);

        //Search the around the mouse pointer for the colors
        Point cursorStart = MouseInfo.getPointerInfo().getLocation();

        System.out.println("Starting search");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double width = screenSize.getWidth();
        final double height = screenSize.getHeight();

        HashMap<Color, Point> colorLocations = new HashMap<Color, Point>();
        final int SEARCHSIZE = 400;
        final int XSEARCHWIDTH = 120;
        final int YSEARCHWIDTH = 20;
        final int XCOLORWIDTH = 10;
        for (int x = 0; x < width && colorLocations.size()<14; x+=XSEARCHWIDTH)
        {
            for (int y = 0; y < height && colorLocations.size()<14; y+=YSEARCHWIDTH)
            {
                Color col = rob.getPixelColor(x, y);
                if (searchColors.contains(col))
                {
                    int xi = x;
                    col = rob.getPixelColor(xi, y);
                    int missed = 0;
                    while (missed<3)
                    {
                        if (expectedColors.contains(col))
                        {
                            missed = 0;
                            colorLocations.put(col, new Point(xi, y));
                        } else {
                            missed++;
                        }
                        xi-=XCOLORWIDTH;
                        col = rob.getPixelColor(xi, y);
                    }
                    xi = x;
                    col = rob.getPixelColor(xi, y);
                    missed = 0;
                    while (missed<3)
                    {
                        if (expectedColors.contains(col))
                        {
                            missed = 0;
                            colorLocations.put(col, new Point(xi, y));
                        } else {
                            missed++;
                        }
                        xi+=XCOLORWIDTH;
                        col = rob.getPixelColor(xi, y);
                    }
                }

            }
        }

        System.out.println("Ending search, " + colorLocations.size());
        final int DELAY = 20;

        //Draw the image
        for (int x = 0; x < img.getWidth(); x+=3)
        {
            rob.mousePress(InputEvent.BUTTON1_MASK);
            rob.mouseRelease(InputEvent.BUTTON1_MASK);
            rob.delay(DELAY);

            Color[] columnColors = new Color[img.getHeight()];
            for (int y = 0; y < img.getHeight(); y++)
            {
                Color nearestColor = getNearestColor(colorLocations.keySet(), new Color(img.getRGB(x, y)));
                columnColors[y] = nearestColor;
            }

            for (int y = 0; y < img.getHeight(); y++)
            {
                if (y==0)
                {
                    //Move mouse to color and select it
                    Point colorPos = colorLocations.get(columnColors[y]);
                    rob.mouseMove(colorPos.x, colorPos.y);
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                    rob.mouseRelease(InputEvent.BUTTON1_MASK);
                    rob.delay(DELAY);

                    //Move mouse to where we are drawing and draw
                    rob.mouseMove(cursorStart.x + x, cursorStart.y + y);
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                }
                else
                {
                    if (columnColors[y-1].equals(columnColors[y]))
                    {
                        rob.mouseMove(cursorStart.x + x, cursorStart.y + y);
                        rob.delay(DELAY);
                    }
                    else
                    {
                        rob.mouseRelease(InputEvent.BUTTON1_MASK);
                        rob.delay(DELAY);

                        //Move mouse to color and select it
                        Point colorPos = colorLocations.get(columnColors[y]);
                        rob.mouseMove(colorPos.x, colorPos.y);
                        rob.mousePress(InputEvent.BUTTON1_MASK);
                        rob.mouseRelease(InputEvent.BUTTON1_MASK);
                        rob.delay(DELAY);

                        //Move mouse to where we are drawing and draw
                        rob.mouseMove(cursorStart.x + x, cursorStart.y + y);
                        rob.mousePress(InputEvent.BUTTON1_MASK);
                    }
                }
            }
        }
        rob.mouseRelease(InputEvent.BUTTON1_MASK);

        //Display the image in a frame
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
        //Use the eye's intensity for RGB to convert to grayscale
        int grayscale = 0;
        grayscale += ((0x00FF0000 & color) >> 16) * 0.3;
        grayscale += ((0x0000FF00 & color) >> 8)  * 0.59;
        grayscale += ((0x000000FF & color) >> 0)  * 0.11;
        return grayscale;
    }

    private static Color getNearestColor(Set<Color> colors, Color color)
    {
        //returns the color in the set that is closest to the color
        Color closestColor = null;
        double closestDistance = Double.MAX_VALUE;
        for (Color c: colors)
        {
            double dist = colorDistance(c, color);
            if (dist < closestDistance)
            {
                closestColor = c;
                closestDistance = dist;
            }
        }
        return closestColor;
    }

    private static double colorDistance(Color c1, Color c2)
    {
        return Math.sqrt( 0.3 * Math.pow(c1.getRed() - c2.getRed(),2.0) + 0.59 * Math.pow(c1.getGreen() - c2.getGreen(),2.0) + 0.11 * Math.pow(c1.getBlue() - c2.getBlue(),2.0));
        //return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(),2.0) + Math.pow(c1.getGreen() - c2.getGreen(),2.0) + Math.pow(c1.getBlue() - c2.getBlue(),2.0));
    }
}
