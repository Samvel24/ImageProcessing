
package ImageProcessing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Efectos extends JDialog
{
    private JPanel panel;
    
    private JLabel label;
    
    private JScrollPane scrollPane;
    
    private JButton boton1;
    private JButton boton2;
    private JButton boton3;
    private JButton boton4;
    
    private JButton boton; // seleccionar imagen
    
    private String dirImage = "";
    
    private Mat im = new Mat();
    private Mat orig = new Mat();
    private ImageIcon ii;
    
    // Constructor
    public Efectos()
    {
        setTitle("Efectos"); // Nombre de la ventana
        setBounds(80, 80, 800, 595); // posicion (x, y) de la ventana; tamaño (ancho, alto)
        
        startComponents();
    }
    
    private void startComponents()
    {
        // creamos un objeto JPanel para agregarle componentes
        panel = new JPanel(); 
        
        panel.setBackground(Color.BLUE);
        panel.setLayout(null); // anulamos el layout por defecto
        this.getContentPane().add(panel); // agregamos el panel a la ventana (JDialog)
        
        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.GRAY);
        scrollPane = new JScrollPane(label);
        scrollPane.setBounds(50, 30, 680, 380);
        
        boton1 = new JButton("Sepia");
        boton2 = new JButton("Escala de grises");
        boton3 = new JButton("Inversion de color"); // negativo
        boton4 = new JButton("Bordes");
        boton = new JButton("Elegir imagen");
                
        boton1.setBounds(70, 420, 150, 50);
        boton2.setBounds(240, 420, 150, 50);
        boton3.setBounds(70, 480, 150, 50);
        boton4.setBounds(240, 480, 150, 50);
        boton.setBounds(440, 420, 150, 50);
        
        boton1.setBackground(Color.GREEN);
        boton2.setBackground(Color.GREEN);
        boton3.setBackground(Color.GREEN);
        boton4.setBackground(Color.GREEN);
        boton.setBackground(Color.GREEN);
        
        panel.add(scrollPane);
        
        panel.add(boton1);
        panel.add(boton2);
        panel.add(boton3);
        panel.add(boton4);
        panel.add(boton);
        
        ManejadorEventos me = new ManejadorEventos();
        boton.addActionListener(me);
        boton1.addActionListener(me);
        boton2.addActionListener(me);
        boton3.addActionListener(me);
        boton4.addActionListener(me);
    }
    
    // Fuente sepia: "Holistic Game Development with Unity: An All-in-one Guide to Implementing", Penny De Byl
    private Mat sepia()
    {
        ArrayList <Mat> canalesSEPIA = new ArrayList <Mat>();
        
        for(int c = 0; c < orig.channels(); c++)
        {
            canalesSEPIA.add(new Mat(orig.rows(), orig.cols(), CvType.CV_8U));
            for(int i = 0; i < orig.rows(); i++)
            {
                for(int j = 0; j < orig.cols(); j++)
                {
                    double[] pixeles = orig.get(i, j);
                    double B = pixeles[0];
                    double G = pixeles[1];
                    double R = pixeles[2];
                    double v1 = 0;
                    if(c == 0) // solo para el primer canal de la nueva imagen
                    {
                        v1 = (0.272 * R) + (0.534 * G) + (0.131 * B);
                    }
                    if(c == 1)
                    {
                        v1 = (0.349 * R) + (0.686 * G) + (0.168 * B);
                    }
                    if(c == 2)
                    {
                        v1 = (0.393 * R) + (0.769 * G) + (0.189 * B);
                    }
                    
                    canalesSEPIA.get(c).put(i, j, v1); // m(i, j) = val
                }
            }
        }

        Mat sepia = new Mat();
        Core.merge(canalesSEPIA, sepia);
        return sepia;
    }

    // Fuente negativo: https://es.wikipedia.org/wiki/Inversión_(imagen)
    private Mat negativo()
    {
        ArrayList <Mat> canalesBGR = new ArrayList <Mat>();
        Core.split(orig, canalesBGR);
        
        for(int c = 0; c < orig.channels(); c++)
        {
            for(int i = 0; i < orig.rows(); i++)
            {
                for(int j = 0; j < orig.cols(); j++)
                {
                    double[] val = canalesBGR.get(c).get(i, j);
                    val[0] = 255 - val[0];
                    canalesBGR.get(c).put(i, j, val);
                }
            }
        }
        
        Mat neg = new Mat();
        Core.merge(canalesBGR, neg);
        return neg;
    }
    
    // deteccion de bordes usando el operador de Sobel
    private Mat sobelOperatorEdges()
    {
        Mat gray = new Mat();
        //Mat dst = new Mat();
        //int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        
        Imgproc.cvtColor(orig, gray, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.Laplacian(gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT);
        
        Mat grad_x = new Mat(); 
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        Imgproc.Sobel(gray, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel(gray, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );
        
        Mat grad = new Mat();
        // Convertimos a CvType.CV_8U
        Core.convertScaleAbs( grad_x, abs_grad_x );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
        
        //Mat abs_dst = new Mat();
        // Convertimos a CvType.CV_8U
        //Core.convertScaleAbs(dst, abs_dst);
        
        return grad;
    }
    
    private BufferedImage createAwtImage(Mat img)
    {   
        int type = 0;
        if (img.channels() == 1) 
        {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } 
        else 
        {
            if (img.channels() == 3) 
            {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            else 
            {
                return null;
            }
        } 
        
        BufferedImage bufferedImage = new BufferedImage(img.width(), img.height(), type);
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        img.get(0, 0, data);

        return bufferedImage;
    }
    
    // clase interna privada para manejo de eventos
    private class ManejadorEventos implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            if(ae.getSource() == boton)
            {
                JFileChooser chooser = new JFileChooser();
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                {
                    File f = chooser.getSelectedFile();
                    dirImage = f.getAbsolutePath();
                    
                    im = Imgcodecs.imread(dirImage);
                    im.copyTo(orig);
                    ii = new ImageIcon(createAwtImage(im));
                    label.setIcon(ii);
                    
                    System.out.println(dirImage);
                }
            }
            if(ae.getSource() == boton1) // sepia
            {
                if(orig.empty())
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {
                    Mat res = sepia();
                    res.copyTo(im);
                    ii = new ImageIcon(createAwtImage(im));
                    label.setIcon(ii);
                }
            }
            if(ae.getSource() == boton2) // escala de grises
            {
                if(orig.empty())
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {
                    Mat res = new Mat();
                    Imgproc.cvtColor(orig, res, Imgproc.COLOR_BGR2GRAY);
                    res.copyTo(im);
                    ii = new ImageIcon(createAwtImage(im));
                    label.setIcon(ii);
                }
            }
            if(ae.getSource() == boton3) // inversion
            {
                if(orig.empty())
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {
                    Mat res = negativo();
                    res.copyTo(im);
                    ii = new ImageIcon(createAwtImage(im));
                    label.setIcon(ii);
                }
            }
            if(ae.getSource() == boton4) // bordes
            {
                if(orig.empty())
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {
                    Mat res = sobelOperatorEdges();
                    res.copyTo(im);
                    ii = new ImageIcon(createAwtImage(im));
                    label.setIcon(ii);
                }
            }
        }
    }
}
