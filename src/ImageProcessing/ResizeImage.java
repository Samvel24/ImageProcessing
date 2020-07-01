
package ImageProcessing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ResizeImage extends JDialog
{   
    private JPanel panel;
    
    private JLabel label1; // Imagen 1
    private JLabel label2; // Imagen 2 (imagen procesada)
    private JLabel label3; // "X" alrededor de Imagen1
    private JLabel label4; // "Y" alrededor de Imagen1
    private JLabel label5; // "Nuevas dimensiones"
    private JLabel label6; // "X" debajo de JTextField
    private JLabel label7; // "Y" debajo de JTextField
    
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    
    private JButton boton1;
    private JButton boton2;
    
    private JTextField cajaTexto1;
    private JTextField cajaTexto2;
    
    private Mat res;
    
    private String dirImage = "";
    
    // Constructor
    public ResizeImage()
    {
        setTitle("Redimensionar imagen"); // Nombre de la ventana
        setBounds(80, 80, 830, 630); // posicion (x, y) de la ventana; tamaño (ancho, alto)    
        
        startComponents();
    }
    
    private void startComponents()
    {
        panel = new JPanel();
        
        panel.setBackground(Color.BLUE);
        panel.setLayout(null); // anulamos el layout por defecto
        this.getContentPane().add(panel); // agregamos el panel a la ventana
        
        // creamos objetos JLabel con texto centrado
        label1 = new JLabel("Imagen1", SwingConstants.CENTER);
        label2 = new JLabel("Imagen2", SwingConstants.CENTER);
        // creamos los componentes de desplazamiento
        scrollPane1 = new JScrollPane(label1); 
        scrollPane2 = new JScrollPane(label2);
        
        // establecemos las propiedades de cada etiqueta
        label1.setOpaque(true);
        label2.setOpaque(true);
        label1.setBackground(Color.GRAY);
        label2.setBackground(Color.GRAY);
        
        // creamos objetos JLabel con texto centrado
        label3 = new JLabel("X", SwingConstants.CENTER);
        label4 = new JLabel("Y", SwingConstants.CENTER);
        
        // establecemos las propiedades de cada etiqueta
        label3.setOpaque(true);
        label4.setOpaque(true);
        label3.setBackground(Color.BLUE);
        label4.setBackground(Color.BLUE);
        
        // establecemos la posicion en ventana de label3 y label4
        label3.setBounds(30, 400, 320, 15);
        label4.setBounds(352, 32, 15, 320);
        label3.setForeground(Color.WHITE);
        label4.setForeground(Color.WHITE);
        
        panel.add(label3);
        panel.add(label4);
        
        // establecemos la posicion en ventana de cada componente de desplazamiento
        /* solo establcemos las posiciones de cada objeto de desplazamiento porque cada
         etiqueta ya se encuentra dentro de cada elemento de desplazamiento
        */
        scrollPane1.setBounds(30, 30, 320, 370);
        scrollPane2.setBounds(400, 30, 400, 500);
        
        // agregamos cada objeto de desplazamiento al panel
        panel.add(scrollPane1);
        panel.add(scrollPane2);
        
        // creamos objetos JButton y establecemos su posicion, tamaño y color
        boton1 = new JButton("Seleccionar imagen");
        boton2 = new JButton("Redimensionar imagen");
        boton1.setBounds(70, 440, 250, 30);
        boton2.setBounds(70, 490, 250, 30);
        boton1.setBackground(Color.GREEN);
        boton2.setBackground(Color.GREEN);
        
        // agregamos los botones al panel
        panel.add(boton1);
        panel.add(boton2);
        
        // Creamos otro label con texto centrado
        label5 = new JLabel("Nuevas dimensiones:", SwingConstants.CENTER);
        label5.setBounds(30, 540, 150, 30);
        label5.setOpaque(true);
        label5.setBackground(Color.GREEN);
        
        panel.add(label5);
        
        cajaTexto1 = new JTextField();
        cajaTexto2 = new JTextField();
        cajaTexto1.setBounds(200, 540, 50, 30);
        cajaTexto2.setBounds(270, 540, 50, 30);
        
        panel.add(cajaTexto1);
        panel.add(cajaTexto2);
        
        label6 = new JLabel("X", SwingConstants.CENTER);
        label7 = new JLabel("Y", SwingConstants.CENTER);
        label6.setBounds(200, 572, 50, 15);
        label7.setBounds(270, 572, 50, 15);
        
        label6.setOpaque(true);
        label6.setBackground(Color.BLUE);
        label7.setOpaque(true);
        label7.setBackground(Color.BLUE);
        label6.setForeground(Color.WHITE);
        label7.setForeground(Color.WHITE);
        
        panel.add(label6);
        panel.add(label7);
        
        // Registramos el manejador de eventos de cada boton para que realice las acciones
        // establecidas para cada uno de ellos
        ManejadorEventos me = new ManejadorEventos();
        boton1.addActionListener(me);
        boton2.addActionListener(me);
    }
    
    private void redimensionar(Mat img, int x, int y)
    {   
        int area = img.cols() * img.rows();
        int interpolacion = 0;
        if((x * y) > area) // agrandar tamanio imagen
        {
           interpolacion = Imgproc.INTER_CUBIC;
        }
        else // disminuir tamanio imagen
        {
            interpolacion = Imgproc.INTER_AREA;
        }
        
        res = new Mat();
        Imgproc.resize(img, res, new Size(x, y), 0, 0, interpolacion);
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
                    System.out.println("No se eligio ninguna imagen");
                }
            }
            if(ae.getSource() == boton2)
            {
                if(dirImage.isEmpty())
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {
                    if((cajaTexto1.getText().isEmpty() || cajaTexto2.getText().isEmpty()) || (cajaTexto1.getText().isEmpty() && cajaTexto2.getText().isEmpty()))
                    {
                        JOptionPane.showMessageDialog(null, "Falta(n) dimension(es) por ingresar");
                    }
                    else
                    {
                        Mat img = Imgcodecs.imread(dirImage);
                        redimensionar(img, Integer.parseInt(cajaTexto1.getText()), Integer.parseInt(cajaTexto2.getText()));
                        label2.setIcon(new ImageIcon(createAwtImage(res)));
                    }
                }
                
            }
        }
    }
}