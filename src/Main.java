import java.util.ArrayList;
import java.awt.Color;
import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Robot;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main {
    final static int SIZE = 100;
    static BufferedImage img = null;
    private static ArrayList<Color> expectedColors = new ArrayList<Color>();

    public static void main(String[] args) throws Exception {


        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image File", "png", "jpg", "jpeg", "bmp", "gif");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            System.out.println("Bad file chosen");
            System.exit(1);
        }

        img = ImageIO.read(chooser.getSelectedFile());


        expectedColors.add(new Color(109, 109, 109, 0));
        expectedColors.add(new Color(7, 7, 7, 1));
        expectedColors.add(new Color(255, 255, 255, 2));
        expectedColors.add(new Color(222, 0, 22, 3));
        expectedColors.add(new Color(67, 199, 0, 4));
        expectedColors.add(new Color(0, 0, 255, 5));
        expectedColors.add(new Color(155, 182, 255, 6));
        expectedColors.add(new Color(238, 240, 0, 7));
        expectedColors.add(new Color(238, 130, 0, 8));
        expectedColors.add(new Color(79, 22, 132, 9));
        expectedColors.add(new Color(226, 161, 244, 10));
        expectedColors.add(new Color(206, 158, 109, 11));
        expectedColors.add(new Color(75, 43, 10, 12));

        int w = img.getWidth(), h = img.getHeight();
        if (w > h) {
            BufferedImage newImg = new BufferedImage(SIZE, SIZE * h / w, BufferedImage.TYPE_INT_ARGB);
            newImg.getGraphics().drawImage(img.getScaledInstance(SIZE, SIZE * h / w, Image.SCALE_DEFAULT), 0, 0, null);
            img = newImg;
        } else {
            BufferedImage newImg = new BufferedImage(SIZE * w / h, SIZE, BufferedImage.TYPE_INT_ARGB);
            newImg.getGraphics().drawImage(img.getScaledInstance(SIZE * w / h, SIZE, Image.SCALE_DEFAULT), 0, 0, null);
            img = newImg;
        }

        Robot rob = new Robot();


        final BufferedImage resImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Thread.sleep(3000);
        Point cursorStart = MouseInfo.getPointerInfo().getLocation();

        //Draw the image
        for (Color c : expectedColors) {
            rob.mouseMove((int) (cursorStart.x + 12 * c.getAlpha() + 1), cursorStart.y);

            rob.delay(5);
            rob.mousePress(InputEvent.BUTTON1_MASK);
            rob.delay(5);
            rob.mouseRelease(InputEvent.BUTTON1_MASK);


            for (int y = 0; y < img.getHeight(); y += 1) {
                for (int x = 0; x < img.getWidth(); x += 1) {
                    Color nearestColor = getNearestColor(img.getRGB(x, y));
                    if (nearestColor == c && c.getAlpha()==1) {

                        resImage.setRGB(x, y, (nearestColor.getRGB() & 0x00ffffff) | 0xff000000);


                        rob.mouseMove(cursorStart.x + x * 3, cursorStart.y - 320 + y * 3);

                        rob.mousePress(InputEvent.BUTTON1_MASK);
                        rob.mouseRelease(InputEvent.BUTTON1_MASK);

                    }
                }
            }
        }
        rob.mouseRelease(InputEvent.BUTTON1_MASK);
        //Display the image in a frame
        JFrame gui = new JFrame() {
            public void paint(Graphics g) {
                g.drawImage(resImage, 0, 0, null);
                try {
                    Thread.sleep(500);
                    repaint();
                } catch (Exception ignore) {
                }
            }
        };
        gui.setVisible(true);
        gui.setSize(SIZE, SIZE);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static int colorToGrayscale(int color) {
        //Use the eye's intensity for RGB to convert to grayscale
        int grayScale = 0;
        grayScale += ((0xff0000 & color) >> 16) * 0.3;
        grayScale += ((0xff00 & color) >> 8) * 0.59;
        grayScale += (0xff & color) * 0.11;
        return grayScale;
    }

    private static Color getNearestColor(int color) {
        //returns the color in the set that is closest to the color
        Color closestColor = null;
        double closestDistance = Double.MAX_VALUE;
        for (Color c : expectedColors) {
            double dist = colorDistance(c, color);
            if (dist < closestDistance) {
                closestColor = c;
                closestDistance = dist;
            }
        }
        return closestColor;
    }

    private static double colorDistance(Color c1, int c2) {
        float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB((c2 >> 16) & 0xff, (c2 >> 8) & 0xff, c2 & 0xff, null);

        float dh = hsb1[0] - hsb2[0];
        float ds = hsb1[1] - hsb2[1];
        float dv = hsb1[2] - hsb2[2];

        return dh * dh + ds * ds + dv * dv;

    }
}
