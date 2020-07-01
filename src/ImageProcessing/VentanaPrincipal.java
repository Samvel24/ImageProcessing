
package ImageProcessing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaPrincipal extends JFrame
{
    
    private JButton boton1;
    private JButton boton2;
    private JButton boton3;
    private JButton boton5;
    private JButton boton6;
    
    private JPanel panel;
    
    // Constructor
    public VentanaPrincipal()
    {
        setTitle("Procesamiento digital de imagenes"); // Nombre de la ventana
        setBounds(80, 80, 410, 510); // posicion (x, y) de la ventana; tamaño (ancho, alto)
        
        startComponents();
    }
    
    private void startComponents()
    {
        panel = new JPanel(); // creamos un objeto JPanel para agregarle componentes
        panel.setBackground(Color.BLUE);
        panel.setLayout(null); // anulamos el layout por defecto
        this.getContentPane().add(panel); // agregamos el panel a la ventana
        
        boton1 = new JButton("Redimensionar imagen");
        boton2 = new JButton("Recortar imagen");
        boton3 = new JButton("Brillo y contraste");
        boton5 = new JButton("Efectos");
        boton6 = new JButton("Detectar objetos");
        
        boton1.setBounds(110, 70, 170, 40);
        boton2.setBounds(110, 140, 170, 40);
        boton3.setBounds(110, 210, 170, 40);
        boton5.setBounds(110, 280, 170, 40);
        boton6.setBounds(110, 350, 170, 40);
       
        boton1.setBackground(Color.GREEN);
        boton2.setBackground(Color.GREEN);
        boton3.setBackground(Color.GREEN);
        boton5.setBackground(Color.GREEN);
        boton6.setBackground(Color.GREEN);
        
        panel.add(boton1);
        panel.add(boton2);
        panel.add(boton3);
        panel.add(boton5);
        panel.add(boton6);
        
        ManejadorEventos me = new ManejadorEventos();
        boton1.addActionListener(me);
        boton2.addActionListener(me);
        boton3.addActionListener(me);
        boton5.addActionListener(me);
        boton6.addActionListener(me);
    }
    
    // clase interna privada para manejo de eventos
    private class ManejadorEventos implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            if(ae.getSource() == boton1)
            {
                ResizeImage rI = new ResizeImage();
                rI.setVisible(true);
                rI.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
            if(ae.getSource() == boton2)
            {
                CutImage cI = new CutImage();
                cI.setVisible(true);
                cI.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
            if(ae.getSource() == boton3)
            {
                BrightnessAndContrast bC = new BrightnessAndContrast();
                bC.setVisible(true);
                bC.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
            if(ae.getSource() == boton5)
            {
                Efectos ef = new Efectos();
                ef.setVisible(true);
                ef.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
            if(ae.getSource() == boton6)
            {
                MultipleTemplateMatching mtm = new MultipleTemplateMatching();
                mtm.setVisible(true);
                mtm.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
        }
    }
}
