import javax.swing.JOptionPane;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URLConnection;
import java.net.URL;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import org.json.*;

public class Main
{
    final static int SIZE = 200;
    static BufferedImage img = null;
	static Color GREY;
	static Color BLACK;
	static Color WHITE;
	static Color RED;
	static Color GREEN;
	static Color BLUE;
	static Color LIGHTBLUE;
	static Color YELLOW;
	static Color ORANGE;
	static Color PURPLE;
	static Color PINK;
	static Color TAN;
	static Color BROWN;

    public static void main(String[] args)
    {
    	if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
	    {
		    GREY = new Color(109,109,109);
		    BLACK = new Color(  7,  7,  7);
		    WHITE = new Color(255,255,255);
		    RED = new Color(222,  0, 22);
		    GREEN = new Color( 67,199,  0);
		    BLUE = new Color(  0,  0,255);
		    LIGHTBLUE = new Color(155,182,255);
		    YELLOW = new Color(238,240,  0);
		    ORANGE = new Color(238,130,  0);
		    PURPLE = new Color( 79, 22,132);
		    PINK = new Color(226,161,244);
		    TAN = new Color(206,158,109);
		    BROWN = new Color( 75, 43, 10);
		} else {
			GREY = new Color(128,128,128);
			BLACK = new Color(5,5,5);
			WHITE = new Color(255,255,255);
			RED = new Color(237,28,36);
			GREEN = new Color(54,204,5);
			BLUE = new Color(0,0,255);
			LIGHTBLUE = new Color(170,198,255);
			YELLOW = new Color(240,238,0);
			ORANGE = new Color(247,147,30);
			PURPLE = new Color(102,45,145);
			PINK = new Color(236,181,242);
			TAN = new Color(218,173,133);
			BROWN = new Color(96,56,19);
		}

		/*// To use, add a '/' at the begining of the line.
        Robot robo = null;
        try
        {
           robo = new Robot();
        }
        catch (Exception e){}

        for(int asdf = 0; asdf < 10; asdf++)
        {

            //Point p = MouseInfo.getPointerInfo().getLocation();
            System.out.println(robo.getPixelColor(p.x, p.y));

            robo.delay(2000);
        }//*/ //This will stop all comment blocks

        //Pick the image we want to draw
    	/*
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
        catch (IOException e) {}*/

    	try
        {
    	    String searchQuery =  JOptionPane.showInputDialog(null).replace(' ', '+');
    	    URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + searchQuery);
            URLConnection connection = url.openConnection();

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject json = new JSONObject(builder.toString());
            String pictureURL = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getString("url");

            img = ImageIO.read(new URL(pictureURL));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("SOMETHING WENT WRONG READING FROM GOOGLE!");
        }



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

        //Resize the image into a square
        double scaling = Math.min((double)SIZE / img.getHeight(), (double)SIZE / img.getWidth());
        scaling = Math.min(1.0, scaling);
        BufferedImage newImg = new BufferedImage((int)(scaling * img.getHeight()),(int)(scaling * img.getWidth()),BufferedImage.TYPE_INT_ARGB);
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
        final int XCOLORWIDTH = 8;
        for (int x = 0; x < width && colorLocations.size()<14; x+=XSEARCHWIDTH)
        {
            for (int y = 0; y < height && colorLocations.size()<14; y+=YSEARCHWIDTH)
            {
                Color col = rob.getPixelColor(x, y);
                if (expectedColors.contains(col))
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

        if (colorLocations.containsKey(RED) && colorLocations.containsKey(BLACK))
        {
        	Point redPoint = colorLocations.get(RED);
        	Point blackPoint = colorLocations.get(BLACK);
        	colorLocations.put(WHITE, new Point((redPoint.x+blackPoint.x)/2,(redPoint.y+blackPoint.y)/2));
        }

        System.out.println("Ending search, " + colorLocations.size());

        Color[][] columnColors = new Color[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++)
        {
            for (int y = 0; y < img.getHeight(); y++)
            {
                Color nearestColor = getNearestColor(colorLocations.keySet(), new Color(img.getRGB(x, y)));
                columnColors[x][y] = nearestColor;
            }
        }

        final int DELAY = 40;
        //Scan the image
        HashMap<Color, LinkedHashSet<Point>> colorVectors = new HashMap<Color, LinkedHashSet<Point>>(15);
        for (int x = 0; x < img.getWidth(); x+=3)
        {
            rob.mouseRelease(InputEvent.BUTTON1_MASK);

            for (int y = 0; y < img.getHeight(); y+=3)
            {
            	if (!colorVectors.containsKey(columnColors[x][y]))
            	{
            		colorVectors.put(columnColors[x][y],new LinkedHashSet<Point>());
            	}
            	colorVectors.get(columnColors[x][y]).add(new Point(x,y));
            }
        }
        rob.mouseRelease(InputEvent.BUTTON1_MASK);

        //Draw my thing!
        Object[] colorsInPicture = colorVectors.keySet().toArray();

        System.out.println("" + colorsInPicture.length + " colors in picture.");
        for (int n = 0; n < colorsInPicture.length; n++)
        {
            //Move mouse to color and select it
            Point colorPos = colorLocations.get((Color) colorsInPicture[n]);
            rob.mouseMove(colorPos.x, colorPos.y);
            rob.delay(DELAY);
            rob.mousePress(InputEvent.BUTTON1_MASK);
            rob.mouseRelease(InputEvent.BUTTON1_MASK);

        	LinkedHashSet<Point> pointSet = colorVectors.get((Color) colorsInPicture[n]);
        	Point[] colorVector = pointSet.toArray(new Point[pointSet.size()]);
        	System.out.println("Color " + n + " has " + colorVector.length + " points.");

        	Point current = new Point(colorPos.x - cursorStart.x, colorPos.y - cursorStart.y);
        	for (int m = 0; m < colorVector.length; m++)
        	{
                Point breakoutMouse = MouseInfo.getPointerInfo().getLocation();
                if (breakoutMouse.x != cursorStart.x + current.x || breakoutMouse.y != cursorStart.y + current.y)
                {
                    System.out.println("Quitting");
                    return;
                }
        		current = colorVector[m];

                //Move mouse to where we are drawing and draw
                rob.mouseMove(cursorStart.x + current.x, cursorStart.y + current.y);
                rob.delay(DELAY);
                rob.mousePress(InputEvent.BUTTON1_MASK);

        		m++;
        		while (m < colorVector.length && Math.abs(current.x-colorVector[m].x)+Math.abs(current.y-colorVector[m].y)==1)
        		{

                    breakoutMouse = MouseInfo.getPointerInfo().getLocation();
                    if (breakoutMouse.x != cursorStart.x + current.x || breakoutMouse.y != cursorStart.y + current.y)
                    {
                        System.out.println("Quitting");
                        return;
                    }
        			current = colorVector[m];

                    rob.mouseMove(cursorStart.x + current.x, cursorStart.y + current.y);
                    rob.delay(DELAY);
        			while (m < colorVector.length && Math.abs(current.x-colorVector[m].x)==1 && current.y==colorVector[m].y)
        			{
                        breakoutMouse = MouseInfo.getPointerInfo().getLocation();
                        if (breakoutMouse.x != cursorStart.x + current.x || breakoutMouse.y != cursorStart.y + current.y)
                        {
                            System.out.println("Quitting");
                            return;
                        }
        				current = colorVector[m];
        				m++;
        			}

                    rob.mouseMove(cursorStart.x + current.x, cursorStart.y + current.y);
                    rob.delay(DELAY);
        			while (m < colorVector.length && Math.abs(current.y-colorVector[m].y)==1 && current.x==colorVector[m].x)
        			{
                        breakoutMouse = MouseInfo.getPointerInfo().getLocation();
                        if (breakoutMouse.x != cursorStart.x + current.x || breakoutMouse.y != cursorStart.y + current.y)
                        {
                            System.out.println("Quitting");
                            return;
                        }
        				current = colorVector[m];
        				m++;
        			}

                    rob.mouseMove(cursorStart.x + current.x, cursorStart.y + current.y);
                    rob.delay(DELAY);
        		}
        		m--;

                rob.mouseRelease(InputEvent.BUTTON1_MASK);
        	}
        }


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
        /*
        double totalColor1_1 = Math.abs(c1.getRed() - c1.getGreen());
        double totalColor1_2 = Math.abs(c1.getGreen() - c1.getBlue());
        double totalColor1_3 = Math.abs(c1.getRed() - c1.getBlue());

        double totalColor2_1 = Math.abs(c2.getRed() - c2.getGreen());
        double totalColor2_2 = Math.abs(c2.getGreen() - c2.getBlue());
        double totalColor2_3 = Math.abs(c2.getRed() - c2.getBlue());

        double euclidean = 0.2 * Math.pow(totalColor1_1 - totalColor2_1, 2.0) + 0.2 * Math.pow(totalColor1_2 - totalColor2_2, 2.0) + 0.2 * Math.pow(totalColor1_3 - totalColor2_3, 2.0) + 0.3 * Math.pow(c1.getRed() - c2.getRed(),2.0) + .59 * Math.pow(c1.getGreen() - c2.getGreen(),2.0) + 0.11 * Math.pow(c1.getBlue() - c2.getBlue(),2.0);
        return euclidean;*/
        //return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(),2.0) + Math.pow(c1.getGreen() - c2.getGreen(),2.0) + Math.pow(c1.getBlue() - c2.getBlue(),2.0));
        return 0.3 * Math.pow(c1.getRed() - c2.getRed(),2.0) + .59 * Math.pow(c1.getGreen() - c2.getGreen(),2.0) + 0.11 * Math.pow(c1.getBlue() - c2.getBlue(),2.0);
    }
}
