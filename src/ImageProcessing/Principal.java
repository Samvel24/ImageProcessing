
package ImageProcessing;


import javax.swing.JFrame;
import org.opencv.core.Core;

public class Principal 
{
    public static void main(String[] args) 
    {   
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        VentanaPrincipal vP = new VentanaPrincipal();
        vP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vP.setVisible(true);
        vP.setResizable(false);
    }
}
