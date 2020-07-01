
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
import javax.swing.SwingConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MultipleTemplateMatching extends JDialog
{   
    private JPanel panel;
    
    private JButton boton1;
    private JButton boton2;
    private JButton boton3;
    
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private JScrollPane scrollPane3;
    
    private String dirImage = "";
    private String dirTemplate = "";
    private String dirResult = "";
    
    private Mat res;
    
    // Constructor
    public MultipleTemplateMatching()
    {   
        setTitle("Deteccion de objetos - Template Matching"); // Nombre de la ventana
        setBounds(80, 80, 900, 500); // posicion (x, y) de la ventana; tamaño (ancho, alto)
        
        //Lamina lamina = new Lamina();
        //add(lamina);    
        
        startComponents();
    }
    
    // Iniciamos componentes que estarán en ventana
    private void startComponents()
    {
        panel = new JPanel(); // creamos un objeto JPanel para agregarle componentes
        panel.setBackground(Color.BLUE);
        panel.setLayout(null); // anulamos el layout por defecto
        this.getContentPane().add(panel); // agregamos el panel a la ventana
    
        // creamos objetos JLabel con texto centrado
        label1 = new JLabel("Imagen1", SwingConstants.CENTER);
        label2 = new JLabel("Imagen2", SwingConstants.CENTER);
        label3 = new JLabel("Imagen3", SwingConstants.CENTER);
        // creamos los componentes de desplazamiento
        scrollPane1 = new JScrollPane(label1); 
        scrollPane2 = new JScrollPane(label2);
        scrollPane3 = new JScrollPane(label3);
        
        // establecemos las propiedades de cada etiqueta
        label1.setOpaque(true);
        label2.setOpaque(true);
        label3.setOpaque(true);
        label1.setBackground(Color.GRAY);
        label2.setBackground(Color.GRAY);
        label3.setBackground(Color.GRAY);
        // establecemos la posicion en ventana de cada componente de desplazamiento
        /* solo establcemos las posiciones de cada objeto de desplazamiento porque cada
         etiqueta ya se encuentra dentro de cada elemento de desplazamiento
        */
        scrollPane1.setBounds(30, 30, 250, 300);
        scrollPane2.setBounds(300, 30, 250, 300);
        scrollPane3.setBounds(570, 30, 250, 300);
        
        // agregamos cada objeto de desplazamiento al panel
        panel.add(scrollPane1);
        panel.add(scrollPane2);
        panel.add(scrollPane3);
        
        // creamos objetos JButton y establecemos su posicion, tamaño y color
        boton1 = new JButton("Seleccionar imagen");
        boton2 = new JButton("Seleccionar objeto");
        boton3 = new JButton("Detectar objetos");
        boton1.setBounds(30, 350, 250, 30);
        boton2.setBounds(300, 350, 250, 30);
        boton3.setBounds(570, 350, 250, 30);
        boton1.setBackground(Color.GREEN);
        boton2.setBackground(Color.GREEN);
        boton3.setBackground(Color.GREEN);
        
        // agregamos los botones al panel
        panel.add(boton1);
        panel.add(boton2);
        panel.add(boton3);
        
        // Registramos el manejador de eventos de cada boton para que realice las acciones
        // establecidas para cada uno de ellos
        ManejadorEventos me = new ManejadorEventos();
        boton1.addActionListener(me);
        boton2.addActionListener(me);
        boton3.addActionListener(me);
    }
    
    // funcion para detectar un mismo objeto de multiples posiciones de una imagen
    private void templateMatching_MultipleObjects()
    {
        Mat result = new Mat();
        
        Mat img = Imgcodecs.imread(dirImage);
        
        Mat limpia = new Mat();
        img.copyTo(limpia);
        
        Mat templ = Imgcodecs.imread(dirTemplate);
        
        Mat grayImg = new Mat();
        Mat grayTempl = new Mat();
        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(templ, grayTempl, Imgproc.COLOR_BGR2GRAY);
        
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        
        int matchMethod = Imgproc.TM_CCOEFF_NORMED; // TM_CCORR_NORMED // TM_CCOEFF_NORMED
        
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        
        Imgproc.matchTemplate(grayImg, grayTempl, result, matchMethod);
        
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        
        ArrayList <Integer> x = new ArrayList <Integer>();
        ArrayList <Integer> y = new ArrayList <Integer>();
        
        int i, j;
        int cont = 0; // Contador para el número de puntos que cumplen la condición (data[0] >= threshold)
        Point[] loc;// loc = localidades o coordenadas
        
        double threshold = .84; // Umbral para detectar múltiples objetos
        for(i = 0; i < result.rows(); i++)
        {
            for(j = 0; j < result.cols(); j++)
            {
                double[] data = result.get(i, j);
                if(data[0] >= threshold)
                {
                    x.add(j);
                    y.add(i);
                    cont++;
                }
            }
        }
        
        loc = new Point[cont]; // Asignamos memoria para un arreglo con "cont" objetos de tipo Point
        //System.err.println("No. de coordenadas: " +cont);
        
        for(i = 0; i < x.size(); i++) // Es NECESARIO asignar memoria para cada arreglo de tipo Point
            loc[i] = new Point(0, 0); // asignamos memoria para cada punto de arreglo tipo Point
        
        // Guardamos en cada objeto Point las coordenadas que cumplen con el umbral
        for(i = 0; i < x.size(); i++)
        {
            loc[i].x = x.get(i);
            loc[i].y = y.get(i);
        }
        
        // Imprimimos las coordenadas y el valor del pixel de cada coordenada
        /*for(i = 0; i < loc.length; i++)
        {
            //System.out.println("Coordenada: " +loc[i]);
            System.out.print("Coordenada: ");
            System.out.print("(" +loc[i].x + ", ");
            System.out.println(loc[i].y +")");
            
            double[] val = result.get((int)loc[i].y, (int)loc[i].x);
            System.out.println("Valor del pixel en la coordenada anterior: " +val[0]);
        }*/
        
        // Dibujamos un rectángulo en cada coordenada encontrada
        for(Point p : loc) // la variable p accede a cada elemento del arreglo "loc" // p <-> loc[i]
        {
            //System.out.println(p);
            Imgproc.rectangle(img, p, new Point(p.x + templ.cols(), p.y + templ.rows()),
                new Scalar(255, 0, 0), 2, 8, 0);
        }
        
        //Imgproc.resize(img, img, new Size(0, 0), 0.3, 0.3, Imgproc.INTER_CUBIC);
        int fin = dirImage.indexOf(".");
        String dirSinExtension = dirImage.substring(0, fin);
        dirResult = dirSinExtension +"_1" +".jpg";
        Imgcodecs.imwrite(dirResult, img);
        
        res = new Mat();
        img.copyTo(res);
    }
    
    private BufferedImage createAwtImage(Mat mat)
    {   
        int type = 0;
        if (mat.channels() == 1) 
        {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } 
        else 
        {
            if (mat.channels() == 3) 
            {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            else 
            {
                return null;
            }
        } 
        
        BufferedImage bufferedImage = new BufferedImage(mat.width(), mat.height(), type);
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);

        return bufferedImage;
    }
    
    // clase interna privada para manejo de eventos
    private class ManejadorEventos implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            if(ae.getSource() == boton1)
            {
                JFileChooser chooser = new JFileChooser();
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                {
                    File f = chooser.getSelectedFile();
                    dirImage = f.getAbsolutePath();
                    System.out.println(dirImage);
                        
                    label1.setIcon(new ImageIcon(f.getAbsolutePath()));
                }       
                else 
                {
                    System.out.println("No se eligio ninguna imagen 1");
                }
            }
            if(ae.getSource() == boton2)
            {
                JFileChooser chooser2 = new JFileChooser();
                if(chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                {
                    File f = chooser2.getSelectedFile();
                    dirTemplate = f.getAbsolutePath();
                    System.out.println(dirTemplate);
                    
                    label2.setIcon(new ImageIcon(f.getAbsolutePath()));
                }       
                else 
                {
                    System.out.println("No se eligio ninguna imagen 2");
                }
            }
            if(ae.getSource() == boton3)
            {
                if((dirImage.isEmpty() && dirTemplate.isEmpty()) || (dirImage.isEmpty() || dirTemplate.isEmpty()))
                {
                    JOptionPane.showMessageDialog(null, "Falta(n) imagen(es) por seleccionar");
                }
                else
                {
                    //label3.setIcon(null);
                    templateMatching_MultipleObjects();
                    label3.setIcon(new ImageIcon(createAwtImage(res)));
                }
            }
        }
    }
}

