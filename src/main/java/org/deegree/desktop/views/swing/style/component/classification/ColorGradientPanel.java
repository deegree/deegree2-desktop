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
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.views.swing.style.component.classification;

import static org.deegree.desktop.i18n.Messages.get;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;
import org.deegree.desktop.settings.ColorListEntry;
import org.deegree.desktop.settings.GraphicOptions;
import org.deegree.desktop.style.model.LinearGradient;
import org.deegree.desktop.views.swing.style.renderer.ColorComboBoxItemRenderer;
import org.deegree.desktop.views.swing.util.panels.PanelDialog;
import org.deegree.desktop.views.swing.util.panels.PanelDialog.OkCheck;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>ColorGradientPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ColorGradientPanel extends JPanel {

    private static final long serialVersionUID = 8142000920292735146L;

    private static final ILogger LOG = LoggerFactory.getLogger( ColorGradientPanel.class );

    private JComboBox classesColorPatternCB;

    private JButton createColorRampBt;

    private JButton editColorRampBt;

    private JButton removeColorRampBt;

    private GraphicOptions graphicOptions;

    private ChangeListener changeListener;

    /**
     * @param graphicOptions
     *            the graphic options
     * @param changeListener
     *            the change listener to inform, when selection of gradient changed and selected
     *            item is not null
     */
    public ColorGradientPanel( GraphicOptions graphicOptions, ChangeListener changeListener ) {
        this.graphicOptions = graphicOptions;
        this.changeListener = changeListener;
        init();
    }

    /**
     * @param gradient
     *            the gradient to set
     */
    public void setLinearGradient( LinearGradient gradient ) {
        for ( int i = 0; i < classesColorPatternCB.getItemCount(); i++ ) {
            if ( ( (LinearGradient) classesColorPatternCB.getItemAt( i ) ).equals( gradient ) ) {
                classesColorPatternCB.setSelectedIndex( i );
            }
        }
    }

    /**
     * @return the gradient
     */
    public LinearGradient getLinearGradient() {
        return (LinearGradient) classesColorPatternCB.getSelectedItem();
    }

    private void init() {
        classesColorPatternCB = new JComboBox();
        classesColorPatternCB.setMinimumSize( new Dimension( 100, 10 ) );
        classesColorPatternCB.setRenderer( new ColorComboBoxItemRenderer() );
        Map<String, List<ColorListEntry>> cl = graphicOptions.getColorSchemes();
        for ( String name : cl.keySet() ) {
            try {
                LinearGradient newGradient = new LinearGradient( name, cl.get( name ) );
                classesColorPatternCB.addItem( newGradient );
            } catch ( Exception e1 ) {
                LOG.logInfo( "could not create a linear gradient out of the color list entries from the settings" );
            }
        }
        classesColorPatternCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( classesColorPatternCB.getSelectedItem() != null ) {
                    editColorRampBt.setEnabled( true );
                    removeColorRampBt.setEnabled( true );
                    if ( changeListener != null ) {
                        changeListener.valueChanged( new ValueChangedEvent() {
                            @Override
                            public Object getValue() {
                                return true;
                            }
                        } );
                    }
                } else {
                    editColorRampBt.setEnabled( false );
                    removeColorRampBt.setEnabled( false );
                }
            }
        } );

        createColorRampBt = new JButton( get( "$MD11030" ) );
        createColorRampBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                openColorRampDlg( true );
            }
        } );

        editColorRampBt = new JButton( get( "$MD11032" ) );
        editColorRampBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                openColorRampDlg( false );
            }
        } );
        if ( classesColorPatternCB.getSelectedItem() == null ) {
            editColorRampBt.setEnabled( false );
        }

        removeColorRampBt = new JButton( get( "$MD11157" ) );
        removeColorRampBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                removeColorRamp();
            }
        } );
        if ( classesColorPatternCB.getSelectedItem() == null ) {
            removeColorRampBt.setEnabled( false );
        }

        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addGriddedButtons( new JButton[] { createColorRampBt, removeColorRampBt } );

        FormLayout fl = new FormLayout( "center:pref:grow(1.0)", "$cpheight, $cpheight, $cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        builder.add( classesColorPatternCB );

        builder.nextLine();
        builder.add( editColorRampBt );
        builder.nextLine();
        builder.add( bbBuilder.getPanel() );

        add( builder.getPanel() );
    }

    private void openColorRampDlg( boolean createNew ) {
        String title;
        LinearGradientPaint gradient = null;
        String name = "";
        LinearGradient selectedGradient = (LinearGradient) classesColorPatternCB.getSelectedItem();
        if ( createNew ) {
            title = get( "$MD11029" );
        } else {
            if ( selectedGradient != null ) {
                gradient = selectedGradient.getGradient();
                name = selectedGradient.getName();
            }
            title = get( "$MD11031" );
        }
        NamedGradientChooser gc = new NamedGradientChooser( gradient, title, name );
        PanelDialog dlg = new PanelDialog( gc, true, gc );
        dlg.setLocation( getLocationOnScreen() );
        dlg.setVisible( true );
        if ( dlg.clickedOk ) {
            gradient = gc.getGradient();
            if ( gradient != null ) {
                LinearGradient newGradient = new LinearGradient( gc.getGradientName(), gradient );
                if ( !createNew && selectedGradient != null ) {
                    classesColorPatternCB.removeItem( selectedGradient );
                }
                classesColorPatternCB.addItem( newGradient );
                classesColorPatternCB.setSelectedItem( newGradient );
                graphicOptions.addColorScheme( newGradient.getName(), newGradient.getAsColorListEntry() );
            }
        }
    }

    private void removeColorRamp() {
        if ( classesColorPatternCB.getSelectedItem() != null
             && classesColorPatternCB.getSelectedItem() instanceof LinearGradient ) {
            LinearGradient selectedGradient = (LinearGradient) classesColorPatternCB.getSelectedItem();
            int result = JOptionPane.showOptionDialog( this, get( "$MD11158", selectedGradient.getName() ),
                                                       get( "$MD11159" ), JOptionPane.OK_CANCEL_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE, null, null, null );
            if ( result == JOptionPane.OK_OPTION ) {
                classesColorPatternCB.removeItem( selectedGradient );
                graphicOptions.removeColorScheme( selectedGradient.getName() );
            }
        }
    }

    public class NamedGradientChooser extends JPanel implements OkCheck {

        private static final long serialVersionUID = -8692214817685337122L;

        private GradientChooser gradientChooser;

        private JTextField nameTF;

        private String title;

        public NamedGradientChooser( LinearGradientPaint gradient, String title, String name ) {
            this.title = title;
            initComponents( gradient, name );
        }

        private void initComponents( LinearGradientPaint gradient, String name ) {
            nameTF = new JTextField();
            nameTF.setText( name );
            gradientChooser = new GradientChooser();
            if ( gradient != null ) {
                gradientChooser.setGradient( gradient );
            }
            FormLayout fl = new FormLayout( "pref, $rgap, fill:pref:grow(1.0)", "$cpheight, $rgap, pref" );
            DefaultFormBuilder builder = new DefaultFormBuilder( fl );
            builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

            CellConstraints cc = new CellConstraints();

            builder.addLabel( get( "$MD11033" ), cc.xy( 1, 1 ) );
            builder.add( nameTF, cc.xy( 3, 1 ) );
            builder.add( gradientChooser, cc.xyw( 1, 3, 3 ) );

            add( builder.getPanel() );
        }

        /**
         * @return the gradient
         */
        public LinearGradientPaint getGradient() {
            return gradientChooser.getGradient();
        }

        /**
         * @return the nameTF
         */
        public String getGradientName() {
            return nameTF.getText();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.Component#toString()
         */
        @Override
        public String toString() {
            return this.title;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.igeo.views.swing.util.panels.PanelDialog.OkCheck#isOk()
         */
        public boolean isOk() {
            if ( getGradientName() != null && getGradientName().length() > 0 ) {
                return true;
            }
            return false;
        }

    }

}
