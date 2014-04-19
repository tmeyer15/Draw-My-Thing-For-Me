import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main
{
    public static void main(String[] args)
    {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image File", "png", "jpg", "jpeg", "bmp");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION)
        {
            System.out.println("Bad file chosen");
            System.exit(1);
        }

        String audioFilePath = chooser.getSelectedFile().getAbsolutePath();
        System.out.println("" + chooser.getSelectedFile().getAbsolutePath());
    }
}
