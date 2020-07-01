
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BrightnessAndContrast extends JDialog
{
    private JPanel panel;
    
    private JLabel label;
    private JLabel labelB; // "Brillo"
    private JLabel labelC; // "Contraste"        
    
    private JScrollPane scrollPane;
    
    private JSlider sliderB; // Brillo
    private JSlider sliderC; // Contraste
    
    private JButton boton;
    
    private String dirImage = "";
    
    private int B = 0;
    private int C = 1;
    
    private Mat im = new Mat();
    private Mat orig = new Mat();
    private ImageIcon ii;
    
    // Constructor
    public BrightnessAndContrast()
    {
        setTitle("Cambiar brillo y contraste"); // Nombre de la ventana
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
        
        sliderB = new JSlider(-100, 100, 0); // (min, max, posicionInicial)
        sliderB.setMajorTickSpacing(10); // de 10 en 10
        sliderB.setPaintTicks(true); // muestra "barras" en cada numeracion
        sliderB.setPaintLabels(true); // muestra numeracion
        sliderB.setEnabled(false);
        sliderB.setBounds(115, 430, 480, 50);
        
        sliderC = new JSlider(1, 20, 1); // (min, max, posicionInicial)
        sliderC.setMajorTickSpacing(1); // de 1 en 1
        sliderC.setPaintTicks(true); // muestra "barras" en cada numeracion
        sliderC.setPaintLabels(true); // muestra numeracion
        sliderC.setEnabled(false);
        sliderC.setBounds(115, 490, 480, 50);
        
        labelB = new JLabel("Brillo", SwingConstants.CENTER);
        labelC = new JLabel("Contraste", SwingConstants.CENTER);
        
        labelB.setForeground(Color.WHITE);
        labelC.setForeground(Color.WHITE);
        
        labelB.setOpaque(true);
        labelB.setBackground(Color.BLUE);
        labelC.setOpaque(true);
        labelC.setBackground(Color.BLUE);
        
        labelB.setBounds(25, 430, 100, 50);
        labelC.setBounds(25, 490, 100, 50);
        
        boton = new JButton("Elegir imagen");
        boton.setBackground(Color.GREEN);
        boton.setBounds(600, 435, 140, 40);
        
        panel.add(scrollPane);
        panel.add(sliderB);
        panel.add(sliderC);
        panel.add(labelB);
        panel.add(labelC);
        panel.add(boton);
        
        ManejadorEventos me = new ManejadorEventos();
        boton.addActionListener(me);
        
        oyenteSlider();
    }
    
    private void oyenteSlider()
    {
        sliderB.addChangeListener(new ChangeListener() 
        {
            @Override
            public void stateChanged(ChangeEvent e) 
            {
                JSlider source = (JSlider) e.getSource();
                B = source.getValue();
                
                Mat res = beta();
                res.copyTo(im);
                ii = new ImageIcon(createAwtImage(im));
                label.setIcon(ii);
            }
        });
        
        sliderC.addChangeListener(new ChangeListener() 
        {
            @Override
            public void stateChanged(ChangeEvent e) 
            {
                JSlider source = (JSlider) e.getSource();
                C = source.getValue();
                
                Mat res = alfa();
                res.copyTo(im);
                ii = new ImageIcon(createAwtImage(im));
                label.setIcon(ii);
            }
        });
    }
    
    private byte saturate(double val) 
    {
        int iVal = (int) Math.round(val);
        iVal = iVal > 255 ? 255 : (iVal < 0 ? 0 : iVal);
        return (byte) iVal;
    }
    
    // el parametro alfa permite controlar el contraste de la imagen
    private Mat alfa()
    {
        Mat newImage = Mat.zeros(orig.size(), orig.type());
        double alpha = C; /*< Simple contrast control */
        
        byte[] imageData = new byte[(int) (orig.total()*orig.channels())];
        // los cambios se realizan a la imagen original uno a uno y no se realizan cambios
        // sobre los cambios
        /* Por ejemplo, muevo por primera vez la barra y hago cambios a la original, vuelvo 
        a mover por segunda vez la barra y hago esos cambios a la original de nuevo, en 
        vez de aplicar los cambios de la segunda vez a los de la primera vez y asi sucesivamente*/
        orig.get(0, 0, imageData); 
        byte[] newImageData = new byte[(int) (newImage.total()*newImage.channels())];
        for (int y = 0; y < orig.rows(); y++) 
        {
            for (int x = 0; x < orig.cols(); x++) 
            {
                for (int c = 0; c < orig.channels(); c++) 
                {
                    double pixelValue = imageData[(y * orig.cols() + x) * orig.channels() + c];
                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;
                    // Tambien se mantiene el "B" en caso de que se haya movido su barra
                    newImageData[(y * orig.cols() + x) * orig.channels() + c] = saturate(alpha * pixelValue + B);
                }
            }
        }
        
        newImage.put(0, 0, newImageData);
        Mat result = new Mat();
        newImage.copyTo(result);
        return result;
    }
    
    // el parametro beta permite controlar el brillo de la imagen
    private Mat beta()
    {
        Mat newImage = Mat.zeros(orig.size(), orig.type());
        double beta = B; /*< Simple brightness control */
        
        byte[] imageData = new byte[(int) (orig.total()*orig.channels())];
        // En este caso, asi como en alfa(), se hacen cambios uno a uno a la imagen original
        orig.get(0, 0, imageData);
        byte[] newImageData = new byte[(int) (newImage.total()*newImage.channels())];
        for (int y = 0; y < orig.rows(); y++) 
        {
            for (int x = 0; x < orig.cols(); x++) 
            {
                for (int c = 0; c < orig.channels(); c++) 
                {
                    double pixelValue = imageData[(y * orig.cols() + x) * orig.channels() + c];
                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;
                    // Tambien se mantiene el "C" en caso de que se haya movido su barra
                    newImageData[(y * orig.cols() + x) * orig.channels() + c] = saturate(C * pixelValue + beta);
                }
            }
        }
        
        newImage.put(0, 0, newImageData);
        Mat result = new Mat();
        newImage.copyTo(result);
        return result;
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
                    
                    sliderB.setEnabled(true);
                    sliderC.setEnabled(true);
                }
            }
        }
    }
}
