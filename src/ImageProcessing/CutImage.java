
package ImageProcessing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
import javax.swing.SwingConstants;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class CutImage extends JDialog
{
    private JLabel label; // Imagen
    private JLabel label2; // Coordenadas
    
    private int xInicio;
    private int yInicio;
    private int x2;
    private int y2;
    
    private JPanel panel;
    
    private JButton boton1;
    private JButton boton2;
    
    private JScrollPane scrollPane1;
    
    private Rectangle r;
    
    private String dirImage = "";
    
    private ImageIcon ii;
    private Mat im = new Mat();
    
    // Constructor
    public CutImage()
    {
        setTitle("Recortar Imagen"); // Nombre de la ventana
        setBounds(80, 80, 800, 600); // posicion (x, y) de la ventana; tamaño (ancho, alto)
        
        startComponents();
    }
    
    private void startComponents()
    {
        // creamos un objeto JPanel para agregarle componentes
        panel = new JPanel(); 
        
        panel.setBackground(Color.BLUE);
        panel.setLayout(null); // anulamos el layout por defecto
        this.getContentPane().add(panel); // agregamos el panel a la ventana
        
        label = new JLabel()
        {
            @Override
            public void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                
                g.setColor(Color.YELLOW);
                g.drawRect(xInicio, yInicio, x2 - xInicio, y2 - yInicio);
                
                r = new Rectangle(xInicio, yInicio, x2 - xInicio, y2 - yInicio);
                /*Shape s = g.getClip();
                Rectangle r = s.getBounds();
                System.out.println("S - (x, y): "  +"(" +r.x +", " +r.y +")" 
                                   +"; width, height: " +r.width +", " +r.width);
                System.out.println("O - (x, y): "  +"(" +xInicio +", " +yInicio +")" 
                                   +"; width, height: " +(x2 - xInicio) +", " +(y2 - yInicio));*/
            }
        };
        label.setOpaque(true);
        label.setBackground(Color.GRAY);
        
        // Ponemos en (0, 0) la imagen de JLabel
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.TOP);
        
        scrollPane1 = new JScrollPane(label);
        scrollPane1.setBounds(30, 30, 700, 400);
        
        label2 = new JLabel();
        label2.setOpaque(true);
        label2.setBackground(Color.BLUE);
        label2.setForeground(Color.WHITE);
        label2.setBounds(30, 435, 220, 30);
        
        boton1 = new JButton("Seleccionar imagen");
        boton1.setBounds(280, 460, 150, 30);
        boton1.setBackground(Color.GREEN);
        
        boton2 = new JButton("Recortar imagen");
        boton2.setBounds(280, 500, 150, 30);
        boton2.setBackground(Color.GREEN);
        
        panel.add(scrollPane1);
        panel.add(label2);
        panel.add(boton1);
        panel.add(boton2);
        
        // Registramos el manejador de eventos del componente JLabel para que realice 
        // las acciones asociadas a la interaccion con este componente
        ManejadorRaton manejador = new ManejadorRaton();
        label.addMouseListener(manejador);
        label.addMouseMotionListener(manejador);
        
        ManejadorEventos me = new ManejadorEventos();
        boton1.addActionListener(me);
        boton2.addActionListener(me);
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
    
    private class ManejadorRaton implements MouseListener, MouseMotionListener
    {
        @Override
        public void mouseClicked(MouseEvent e) 
        {
            // MouseListener
        }

        @Override
        public void mousePressed(MouseEvent e) 
        {
            // MouseListener
            label2.setText("Coordenadas: (" +e.getX() +", " +e.getY() +")");
            xInicio = e.getX();
            yInicio = e.getY();
        }
        
        // este metodo se invoca cuando se suelta el boton del raton
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            // MouseListener
            System.out.println("O - (x, y): "  +"(" +xInicio +", " +yInicio +")" 
                                   +"; width, height: " +(x2 - xInicio) +", " +(y2 - yInicio));
        }

        // Cuando el raton entre en contacto con el JLabel entramos a esta funcion
        @Override
        public void mouseEntered(MouseEvent e) 
        {
            // MouseListener
            label2.setText("Coordenadas: (" +e.getX() +", " +e.getY() +")");
        }

        @Override
        public void mouseExited(MouseEvent e) 
        {
            // MouseListener
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            // MouseMotionListener
            label2.setText("Coordenadas: (" +e.getX() +", " +e.getY() +")");
            x2 = e.getX();
            y2 = e.getY();
            
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) 
        {
            // MouseMotionListener
            label2.setText("Coordenadas: (" +e.getX() +", " +e.getY() +")");
        }
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
                    ii = new ImageIcon(f.getAbsolutePath());
                    label.setIcon(ii);
                    im = Imgcodecs.imread(dirImage);
                    System.out.println(im.size());
                }
                else 
                {
                    System.out.println("No se eligio ninguna imagen");
                }
            }
            if(ae.getSource() == boton2)
            {   
                if(im.empty()) // si no se ha cargado ninguna imagen
                {
                    JOptionPane.showMessageDialog(null, "Falta imagen por seleccionar");
                }
                else
                {                       
                    // si el ancho y el alto de la imagen son positivos
                    if(r.width > 0 && r.height > 0)
                    {    
                        // si la imagen cargada es mas pequeña que el area de JLabel
                        if((ii.getIconHeight() < label.getHeight()) && (ii.getIconWidth() < label.getWidth()))
                        {
                            // verificar que rectangulo dibujado este dentro de area de imagen
                            if(xInicio < ii.getIconWidth() && yInicio < ii.getIconHeight())
                            {
                                if(x2 < ii.getIconWidth() && y2 < ii.getIconHeight())
                                {   
                                    Rect r = new Rect(xInicio, yInicio, 
                                            x2 - xInicio, y2 - yInicio);
                                    Mat recorte = new Mat(im, r);
                                    Mat aux = new Mat();
                                    recorte.copyTo(aux);
                                    aux.copyTo(im);
                                    ii = new ImageIcon(createAwtImage(im));
                                    label.setIcon(ii);
                                }
                                // si el rectangulo dibujado es mas grande que el area de la imagen
                                else
                                {
                                    // si x2 y y2 (del rectangulo) estan fuera del area de la imagen
                                    if(x2 > ii.getIconWidth() && y2 > ii.getIconHeight())
                                    {
                                        // recortar solo desde (xInicio, yInicio) hasta la esquina inferior derecha de imagen
                                        Rect r = new Rect(xInicio, yInicio, 
                                                    ii.getIconWidth() - xInicio, ii.getIconHeight() - yInicio);
                                        Mat recorte = new Mat(im, r);
                                        Mat aux = new Mat();
                                        recorte.copyTo(aux);
                                        aux.copyTo(im);
                                        ii = new ImageIcon(createAwtImage(im));
                                        label.setIcon(ii);
                                    }
                                    // si solo x2 (del rectangulo) esta fuera del area de la imagen
                                    else if(x2 > ii.getIconWidth() && y2 < ii.getIconHeight())
                                    {
                                        Rect r = new Rect(xInicio, yInicio, 
                                                    ii.getIconWidth() - xInicio, y2 - yInicio);
                                        Mat recorte = new Mat(im, r);
                                        Mat aux = new Mat();
                                        recorte.copyTo(aux);
                                        aux.copyTo(im);
                                        ii = new ImageIcon(createAwtImage(im));
                                        label.setIcon(ii);
                                    }
                                    // si solo y2 (del rectangulo) esta fuera del area de la imagen
                                    else if(x2 < ii.getIconWidth() && y2 > ii.getIconHeight())
                                    {
                                        Rect r = new Rect(xInicio, yInicio, 
                                                    x2 - xInicio, ii.getIconHeight() - yInicio);
                                        Mat recorte = new Mat(im, r);
                                        Mat aux = new Mat();
                                        recorte.copyTo(aux);
                                        aux.copyTo(im);
                                        ii = new ImageIcon(createAwtImage(im));
                                        label.setIcon(ii);
                                    }
                                }
                            }
                        }
                        else // si la imagen cargada es mas grande que el area de JLabel
                        {
                            if((x2 > ii.getIconWidth() && y2 > ii.getIconHeight()))
                            {
                                // recortar solo desde (xInicio, yInicio) hasta la esquina inferior derecha de imagen
                                Rect r = new Rect(xInicio, yInicio, 
                                    ii.getIconWidth() - xInicio, ii.getIconHeight() - yInicio);
                                Mat recorte = new Mat(im, r);
                                Mat aux = new Mat();
                                recorte.copyTo(aux);
                                aux.copyTo(im);
                                ii = new ImageIcon(createAwtImage(im));
                                label.setIcon(ii);
                            }
                            // si solo x2 (del rectangulo) esta fuera del area de la imagen
                            else if(x2 > ii.getIconWidth() && y2 < ii.getIconHeight())
                            {
                                Rect r = new Rect(xInicio, yInicio, 
                                    ii.getIconWidth() - xInicio, y2 - yInicio);
                                Mat recorte = new Mat(im, r);
                                Mat aux = new Mat();
                                recorte.copyTo(aux);
                                aux.copyTo(im);
                                ii = new ImageIcon(createAwtImage(im));
                                label.setIcon(ii);
                            }
                            // si solo y2 (del rectangulo) esta fuera del area de la imagen
                            else if(x2 < ii.getIconWidth() && y2 > ii.getIconHeight())
                            {
                                Rect r = new Rect(xInicio, yInicio, 
                                    x2 - xInicio, ii.getIconHeight() - yInicio);
                                Mat recorte = new Mat(im, r);
                                Mat aux = new Mat();
                                recorte.copyTo(aux);
                                aux.copyTo(im);
                                ii = new ImageIcon(createAwtImage(im));
                                label.setIcon(ii);
                            }
                            else // si x2 y  y2 (del rectangulo) esta dentro del area de la imagen
                            {
                                Rect r = new Rect(xInicio, yInicio, 
                                        x2 - xInicio, y2 - yInicio);
                                Mat recorte = new Mat(im, r);
                                Mat aux = new Mat();
                                recorte.copyTo(aux);
                                aux.copyTo(im);
                                ii = new ImageIcon(createAwtImage(im));
                                label.setIcon(ii);
                            }
                        }
                    }
                    else // si el area del rectangulo dibujado no es mayor a 0
                    {
                        JOptionPane.showMessageDialog(null, "El area seleccionada es invalida");
                    }
                }
            }
        }
    }
}
