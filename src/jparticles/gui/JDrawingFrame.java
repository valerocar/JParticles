/*
 * DrawinFrame.java
 *
 * Created on 29 March 2001, 15:32
 */

package jparticles.gui;

/**
 *
 * @author  Carlos Valero
 * @version
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;

import jparticles.gui.JDrawingPanel.*;

public class JDrawingFrame extends javax.swing.JFrame implements ItemListener
{
    /** Creates new form DrawinFrame */
    public JDrawingPanel drawingPanel;
    private J2DDrawableObject drawableObject;
    private Object drawingObject;
    private int drawingPanelsCount = 0;
    Vector comboElements = new Vector();    
    JComboBox panelsCombo = new JComboBox(comboElements);
    Object currentDrawingObject;
    
    public JDrawingFrame(int hSize,int vSize)
    {
        initComponents ();
        cardsPanel.setPreferredSize(new Dimension(hSize,vSize));
        drawingScrollPane.setViewportView(cardsPanel);
        
        
        //panelsCombo.setEditable(false);
        panelsCombo.addItemListener(this);
        
        pack ();
    }
    
    public void add(JDrawingPanel drawingPanel)
    {
        cardsPanel.add(drawingPanel, drawingPanel.name);        
        comboElements.add(drawingPanel.name);        
        drawingPanelsCount++;        
        currentDrawingObject = drawingObject;
        if(drawingPanelsCount > 1)
        {
            this.getContentPane().add(panelsCombo, BorderLayout.NORTH);
        }
    }
    
    public void itemStateChanged(java.awt.event.ItemEvent evt)
    {
        CardLayout cl = (CardLayout)(cardsPanel.getLayout());
        cl.show(cardsPanel, (String)evt.getItem());
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        drawingScrollPane = new javax.swing.JScrollPane();
        cardsPanel = new javax.swing.JPanel();
        
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });
        
        cardsPanel.setLayout(new java.awt.CardLayout());
        
        drawingScrollPane.setViewportView(cardsPanel);
        
        getContentPane().add(drawingScrollPane, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit (0);
    }//GEN-LAST:event_exitForm
        
    public void drawInPanel(Graphics2D g2)
    {
        if(drawableObject != null) drawableObject.draw(g2, currentDrawingObject);
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane drawingScrollPane;
    private javax.swing.JPanel cardsPanel;
    // End of variables declaration//GEN-END:variables
    
}
