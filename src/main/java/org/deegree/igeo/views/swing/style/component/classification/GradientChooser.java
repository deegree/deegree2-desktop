/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
This file is a modified version of a file that was originally developed as part 
of the SwingLabs SwingX project (https://swingx.dev.java.net/). The original 
file JXGradientChooser is dating from 2009-02-01 and was published under 
LGPL 3.0. It can be obtained from http://swinglabs.org/.

 ---------------------------------------------------------------------------*/

package org.deegree.igeo.views.swing.style.component.classification;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.color.GradientThumbRenderer;
import org.jdesktop.swingx.color.GradientTrackRenderer;
import org.jdesktop.swingx.multislider.Thumb;
import org.jdesktop.swingx.multislider.ThumbListener;

/**
 * <code>GradientChooser</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class GradientChooser extends JXPanel {

    private static final long serialVersionUID = -7208689345569214731L;

    /**
     * The multi-thumb slider to use for the gradient stops
     */
    private JXMultiThumbSlider<Color> slider;

    private JButton deleteThumbButton;

    private JButton addThumbButton;

    private JTextField colorField;

    private JXColorSelectionButton changeColorButton;

    private JSpinner colorLocationSpinner;

    private LinearGradientPaint gradient;

    public GradientChooser() {
        initComponents2();
    }

    private LinearGradientPaint createGradientFromSlider() {
        int thumbsCount = slider.getModel().getThumbCount();
        float[] positions = new float[thumbsCount];
        Color[] colors = new Color[thumbsCount];
        float min = 0;
        float max = 1;
        if ( thumbsCount > 0 ) {

            min = slider.getModel().getThumbAt( 0 ).getPosition();
            max = slider.getModel().getThumbAt( 0 ).getPosition();

            for ( int i = 0; i < thumbsCount; i++ ) {
                Thumb<Color> thumb = slider.getModel().getThumbAt( i );
                positions[i] = thumb.getPosition();
                colors[i] = thumb.getObject();
                min = Math.min( min, positions[i] );
                max = Math.max( max, positions[i] );
            }
        }
        Point2D start = new Point2D.Float( min, min );
        Point2D end = new Point2D.Float( max, max );
        return new LinearGradientPaint( start, end, positions, colors );
    }

    /**
     * Returns the MultipleGradientPaint currently choosen by the user.
     * 
     * @return the currently selected gradient
     */
    public LinearGradientPaint getGradient() {
        return gradient;
    }

    private boolean thumbsMoving = false;

    private Logger log = Logger.getLogger( GradientChooser.class.getName() );

    /**
     * Sets the gradient within this panel to the new gradient. This will delete the old gradient all of it's settings,
     * resetting the slider, gradient type selection, and other gradient configuration options to match the new
     * gradient.
     * 
     * @param mgrad
     *            The desired gradient.
     */
    public void setGradient( LinearGradientPaint mgrad ) {
        if ( gradient == mgrad ) {
            return;
        }
        float[] fracts = mgrad.getFractions();
        Color[] colors = mgrad.getColors();

        if ( !thumbsMoving ) {
            // update the slider properly
            if ( slider.getModel().getThumbCount() != mgrad.getColors().length ) {
                // removing all thumbs;
                while ( slider.getModel().getThumbCount() > 0 ) {
                    slider.getModel().removeThumb( 0 );
                }
                // add them back
                for ( int i = 0; i < fracts.length; i++ ) {
                    slider.getModel().addThumb( fracts[i], colors[i] );
                }
            } else {
                for ( int i = 0; i < fracts.length; i++ ) {
                    slider.getModel().getThumbAt( i ).setObject( colors[i] );
                    slider.getModel().getThumbAt( i ).setPosition( fracts[i] );
                }
            }
        } else {
            log.fine( "not updating because it's moving" );
        }
        LinearGradientPaint old = this.getGradient();
        gradient = mgrad;
        firePropertyChange( "gradient", old, getGradient() );
        repaint();
    }

    private void recalcGradientFromStops() {
        setGradient( createGradientFromSlider() );
    }

    private void updateFromStop( Thumb<Color> thumb ) {
        if ( thumb == null ) {
            updateFromStop( -1, -1, Color.black );
        } else {
            updateFromStop( 1, thumb.getPosition(), thumb.getObject() );
        }
    }

    private void updateFromStop( int thumb, float position, Color color ) {
        log.fine( "updating: " + thumb + " " + position + " " + color );
        if ( thumb == -1 ) {
            colorLocationSpinner.setEnabled( false );
            colorField.setEnabled( false );
            changeColorButton.setEnabled( false );
            changeColorButton.setBackground( Color.black );
            deleteThumbButton.setEnabled( false );
        } else {
            colorLocationSpinner.setEnabled( true );
            colorField.setEnabled( true );
            changeColorButton.setEnabled( true );
            colorLocationSpinner.setValue( (int) ( 100 * position ) );
            colorField.setText( Integer.toHexString( color.getRGB() ).substring( 2 ) );
            changeColorButton.setBackground( color );
            deleteThumbButton.setEnabled( true );
        }
        updateDeleteButtons();
        recalcGradientFromStops();
    }

    private void updateDeleteButtons() {
        if ( slider.getModel().getThumbCount() <= 2 ) {
            deleteThumbButton.setEnabled( false );
        }
    }

    private void updateGradientProperty() {
        firePropertyChange( "gradient", null, getGradient() );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */

    private JPanel topPanel;

    private void initComponents() {
        // declarations for anonymous components
        JPanel jPanel1, jPanel2, jPanel4;
        JLabel jLabel1, jLabel5, jLabel2, jLabel6;
        slider = new JXMultiThumbSlider<Color>();

        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        colorField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        colorLocationSpinner = new javax.swing.JSpinner();
        changeColorButton = new JXColorSelectionButton();
        jPanel4 = new javax.swing.JPanel();
        addThumbButton = new javax.swing.JButton();
        deleteThumbButton = new javax.swing.JButton();

        // setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setLayout( new java.awt.GridBagLayout() );

        topPanel.setLayout( new java.awt.GridBagLayout() );

        Border outer = BorderFactory.createEmptyBorder( 5, 5, 5, 5 );
        Border inner = javax.swing.BorderFactory.createTitledBorder( get( "$MD11034" ) );
        topPanel.setBorder( BorderFactory.createCompoundBorder( outer, inner ) );

        jPanel2.setLayout( new java.awt.GridBagLayout() );

        jLabel1.setText( get( "$MD11037" ) );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets( 4, 4, 4, 4 );
        jPanel2.add( jLabel1, gridBagConstraints );

        jLabel5.setText( "#" );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 4, 0, 4, 4 );
        jPanel2.add( jLabel5, gridBagConstraints );

        colorField.setColumns( 6 );
        colorField.setEnabled( false );
        colorField.setPreferredSize( null );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add( colorField, gridBagConstraints );

        jLabel2.setText( get( "$MD11038" ) );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets( 4, 4, 4, 4 );
        jPanel2.add( jLabel2, gridBagConstraints );

        jLabel6.setText( "%" );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add( jLabel6, gridBagConstraints );

        colorLocationSpinner.setEnabled( false );
        colorLocationSpinner.setPreferredSize( null );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add( colorLocationSpinner, gridBagConstraints );

        changeColorButton.setText( "00" );
        changeColorButton.setEnabled( false );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets( 0, 4, 0, 0 );
        jPanel2.add( changeColorButton, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        topPanel.add( jPanel2, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        topPanel.add( slider, gridBagConstraints );

        jPanel4.setLayout( new java.awt.GridLayout( 1, 0, 2, 0 ) );

        addThumbButton.setText( get( "$MD11035" ) );
        jPanel4.add( addThumbButton );

        deleteThumbButton.setText( get( "$MD11036" ) );
        jPanel4.add( deleteThumbButton );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        topPanel.add( jPanel4, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add( topPanel, gridBagConstraints );

    }// </editor-fold>

    private void initComponents2() {
        this.initComponents();
        setLayout( new BorderLayout() );
        add( topPanel, BorderLayout.NORTH );

        // do event handling stuff
        // create the actions and load them in the action map
        AddThumbAction addThumbAction = new AddThumbAction();
        DeleteThumbAction deleteThumbAction = new DeleteThumbAction();
        deleteThumbAction.setEnabled( false ); // disabled to begin with
        // TODO Add to the action map with proper keys, etc
        ActionMap actions = getActionMap();
        actions.put( "add-thumb", addThumbAction );
        actions.put( "delete-thumb", deleteThumbAction );
        // actions.put("change-color", changeColorAction);
        addThumbButton.setAction( addThumbAction );
        deleteThumbButton.setAction( deleteThumbAction );
        changeColorButton.addPropertyChangeListener( "background", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                selectColorForThumb();
                updateGradientProperty();
            }
        } );
        colorLocationSpinner.addChangeListener( new ChangeLocationListener() );

        SpinnerNumberModel location_model = new SpinnerNumberModel( 100, 0, 100, 1 );
        colorLocationSpinner.setModel( location_model );

        slider.setOpaque( false );
        slider.setPreferredSize( new Dimension( 100, 35 ) );
        slider.getModel().setMinimumValue( 0f );
        slider.getModel().setMaximumValue( 1.0f );

        slider.getModel().addThumb( 0, Color.black );
        slider.getModel().addThumb( 0.5f, Color.red );
        slider.getModel().addThumb( 1.0f, Color.white );

        slider.setThumbRenderer( new GradientThumbRenderer() );
        slider.setTrackRenderer( new GradientTrackRenderer() );
        slider.addMultiThumbListener( new StopListener() );

        recalcGradientFromStops();

    }

    /**
     * 
     * called whenever the color location spinner is changed
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private final class ChangeLocationListener implements ChangeListener {
        public void stateChanged( ChangeEvent evt ) {
            if ( slider.getSelectedIndex() >= 0 ) {
                Thumb<Color> thumb = slider.getModel().getThumbAt( slider.getSelectedIndex() );
                thumb.setPosition( (Integer) colorLocationSpinner.getValue() / 100f );
                updateFromStop( thumb );
                updateGradientProperty();
            }
        }
    }

    /**
     * 
     * The <code>GradientChooser</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private final class AddThumbAction extends AbstractActionExt {

        private static final long serialVersionUID = 795030290741092424L;

        public AddThumbAction() {
            super( get( "$MD11035" ) );
        }

        public void actionPerformed( ActionEvent actionEvent ) {
            float pos = 0.2f;
            Color color = Color.black;
            int num = slider.getModel().addThumb( pos, color );
            recalcGradientFromStops();
            log.fine( "new number = " + num );
            /*
             * for (int i = 0; i < slider.getModel().getThumbCount(); i++) { float pos2 =
             * slider.getModel().getThumbAt(i).getPosition(); if (pos2 < pos) { continue; }
             * slider.getModel().insertThumb(pos, color, i); updateFromStop(i,pos,color); break; }
             */

        }
    }

    /**
     * 
     * The <code>GradientChooser</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private final class DeleteThumbAction extends AbstractActionExt {

        private static final long serialVersionUID = 6416885875952344563L;

        public DeleteThumbAction() {
            super( get( "$MD11036" ) );
        }

        public void actionPerformed( ActionEvent actionEvent ) {
            int index = slider.getSelectedIndex();
            if ( index >= 0 ) {
                slider.getModel().removeThumb( index );
                updateFromStop( -1, -1, null );
            }
        }
    }

    /**
     * 
     * The <code>GradientChooser</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class StopListener implements ThumbListener {

        public StopListener() {
            super();
        }

        public void thumbMoved( int thumb, float pos ) {
            log.fine( "moved: " + thumb + " " + pos );
            Color color = slider.getModel().getThumbAt( thumb ).getObject();
            thumbsMoving = true;
            updateFromStop( thumb, pos, color );
            updateDeleteButtons();
            thumbsMoving = false;

        }

        public void thumbSelected( int thumb ) {

            if ( thumb == -1 ) {
                updateFromStop( -1, -1, Color.black );
                return;
            }
            thumbsMoving = true;
            float pos = slider.getModel().getThumbAt( thumb ).getPosition();
            Color color = slider.getModel().getThumbAt( thumb ).getObject();
            log.fine( "selected = " + thumb + " " + pos + " " + color );
            updateFromStop( thumb, pos, color );
            updateDeleteButtons();
            slider.repaint();
            thumbsMoving = false;

        }

        public void mousePressed( MouseEvent e ) {
            if ( e.getClickCount() > 1 ) {
                selectColorForThumb();
            }
        }
    }

    private void selectColorForThumb() {
        int index = slider.getSelectedIndex();
        if ( index >= 0 ) {
            Color color = changeColorButton.getBackground();
            slider.getModel().getThumbAt( index ).setObject( color );
            updateFromStop( index, slider.getModel().getThumbAt( index ).getPosition(), color );
        }
    }

    /**
     * This static utility method <b>cannot</b> be called from the ETD, or your application will lock up. Call it from a
     * separate thread or create a new Thread with a Runnable.
     * 
     * @param comp
     *            The component to use when finding a top level window or frame for the dialog.
     * @param title
     *            The desired title of the gradient chooser dialog.
     * @param mgrad
     *            The gradient to initialize the chooser too.
     * @return The gradient the user chose.
     */
    public static LinearGradientPaint showDialog( Component comp, String title, LinearGradientPaint mgrad ) {
        Component root = SwingUtilities.getRoot( comp );
        final JDialog dialog = new JDialog( (JFrame) root, title, true );
        final GradientChooser picker = new GradientChooser();
        if ( mgrad != null ) {
            picker.setGradient( mgrad );
        }
        dialog.add( picker );

        JPanel panel = new JPanel();
        JButton cancel = new JButton( "Cancel" );
        cancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                dialog.setVisible( false );
            }
        } );
        JButton okay = new JButton( "Ok" );
        okay.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                dialog.setVisible( false );
            }
        } );
        okay.setDefaultCapable( true );

        GridLayout gl = new GridLayout();
        gl.setHgap( 2 );
        panel.setLayout( gl );
        panel.add( cancel );
        panel.add( okay );

        JPanel p2 = new JPanel();
        p2.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;
        p2.add( panel, gbc );
        dialog.add( p2, "South" );

        dialog.getRootPane().setDefaultButton( okay );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setVisible( true );

        return picker.getGradient();
    }

    /**
     * Creates a string representation of a {@code MultipleGradientPaint}. This string is used for debugging purposes.
     * Its contents cannot be guaranteed between releases.
     * 
     * @param paint
     *            the {@code paint} to create a string for
     * @return a string representing the supplied {@code paint}
     */
    public static String toString( LinearGradientPaint paint ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "Start: " ).append( paint.getStartPoint() );
        buffer.append( " End: " ).append( paint.getEndPoint() );
        Color[] colors = paint.getColors();
        float[] values = paint.getFractions();
        buffer.append( " [" );
        for ( int i = 0; i < colors.length; i++ ) {
            buffer.append( "#" ).append( Integer.toHexString( colors[i].getRGB() ) );
            buffer.append( ":" );
            buffer.append( values[i] );
            buffer.append( ", " );
        }
        buffer.append( "]" );
        return buffer.toString();
    }

}
