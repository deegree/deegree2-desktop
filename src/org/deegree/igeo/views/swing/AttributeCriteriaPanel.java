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

package org.deegree.igeo.views.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.i18n.Messages;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;

import com.jgoodies.forms.builder.ButtonBarBuilder;

/**
 * <code>AttributeCriteriaPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AttributeCriteriaPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -110707035721285626L;

    private static final String ADD_BT = "add";

    private static final String REMOVE_BT = "remove";

    private String operation = OperationDefines.getNameById( OperationDefines.AND );

    private JPanel attCritPanel;

    private List<QualifiedName> propertyNames;

    private DictionaryCollection dictCollection;

    private QualifiedName featureType;

    /**
     * @param propertyNames
     *            list of all properties which are not from type geomtery
     * @param featureType
     *            the selected WFSFeatureType
     * @param dictCollection
     *            the defined dictionaries
     */
    public AttributeCriteriaPanel( List<QualifiedName> propertyNames, QualifiedName featureType,
                            DictionaryCollection dictCollection ) {
        this.propertyNames = propertyNames;
        this.featureType = featureType;
        this.dictCollection = dictCollection;
        init();
    }

    private void init() {
        GridBagConstraints gbc = SwingUtils.initPanel( this );

        // collects all attribute criteria
        this.attCritPanel = new JPanel();
        this.attCritPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 4 ) );
        this.attCritPanel.setPreferredSize( new Dimension( 290, 30 ) );
        JScrollPane attCritScroll = new JScrollPane( this.attCritPanel );
        attCritScroll.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( Locale.getDefault(), "$MD10146" ) ) );

        this.attCritPanel.add( SingleAttributeCriteriumPanel.getLabelPanel() );

        // the buttons to add a new criteria or remove the selected
        JButton addCriterium = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10147" ) );
        JButton removeCriteria = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10148" ) );
        addCriterium.setName( ADD_BT );
        addCriterium.addActionListener( this );
        removeCriteria.setName( REMOVE_BT );
        removeCriteria.addActionListener( this );
        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addGriddedButtons( new JButton[] { addCriterium, removeCriteria } );
        JPanel buttonPanel = bbBuilder.getPanel();

        // actionListener to get the logical operation
        ActionListener opBtAl = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JRadioButton rb = (JRadioButton) e.getSource();
                operation = rb.getActionCommand();
            }
        };
        // panel containing the buttons to choose the logical operation of the filter (AND or OR)
        JPanel operationPanel = new JPanel();
        operationPanel.setMinimumSize( new Dimension( 150, 60 ) );
        operationPanel.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( Locale.getDefault(),
                                                                                         "$MD10149" ) ) );
        JRadioButton andOperation = new JRadioButton( Messages.getMessage( Locale.getDefault(), "$MD10150" ) );
        JRadioButton orOperation = new JRadioButton( Messages.getMessage( Locale.getDefault(), "$MD10151" ) );
        ButtonGroup bg = new ButtonGroup();
        bg.add( andOperation );
        bg.add( orOperation );
        operationPanel.add( andOperation );
        operationPanel.add( orOperation );
        andOperation.setSelected( true );
        andOperation.setActionCommand( OperationDefines.getNameById( OperationDefines.AND ) );
        orOperation.setActionCommand( OperationDefines.getNameById( OperationDefines.OR ) );
        andOperation.addActionListener( opBtAl );
        orOperation.addActionListener( opBtAl );

        // add components to panel
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add( attCritScroll, gbc );
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        ++gbc.gridy;
        add( buttonPanel, gbc );
        ++gbc.gridy;
        add( operationPanel, gbc );
    }

    /**
     * @return the logical operation of the filter
     */
    public String getOperation() {
        return this.operation;
    }

    /**
     * @return the attribute criteria created in this panel
     */
    public List<Operation> getOperations() {
        List<Operation> operations = new ArrayList<Operation>();
        for ( int i = 0; i < this.attCritPanel.getComponentCount(); i++ ) {
            if ( this.attCritPanel.getComponent( i ) instanceof SingleAttributeCriteriumPanel ) {
                SingleAttributeCriteriumPanel attCrits = (SingleAttributeCriteriumPanel) this.attCritPanel.getComponent( i );
                Operation op = attCrits.getOperation();
                if ( op != null ) {
                    operations.add( op );
                }
            }
        }
        return operations;
    }

    /**
     * removes the selected criteria
     */
    private void removeSelectedAttCriteria() {
        List<JPanel> panelsToRemove = new ArrayList<JPanel>();
        // collect all selected panels
        for ( int i = 0; i < this.attCritPanel.getComponentCount(); i++ ) {
            if ( this.attCritPanel.getComponent( i ) instanceof SingleAttributeCriteriumPanel ) {
                SingleAttributeCriteriumPanel critPanel = (SingleAttributeCriteriumPanel) this.attCritPanel.getComponent( i );
                if ( critPanel.isSelected() ) {
                    panelsToRemove.add( critPanel );
                }
            }
        }

        // and remove them
        for ( JPanel panel : panelsToRemove ) {
            this.attCritPanel.remove( panel );
        }
        int i = this.attCritPanel.getComponentCount() * 34;
        this.attCritPanel.setPreferredSize( new Dimension( 290, i ) );
        this.attCritPanel.revalidate();
        this.attCritPanel.repaint();

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent event ) {
        JButton srcBt = (JButton) event.getSource();
        if ( srcBt.getName().equals( ADD_BT ) ) {

            JPanel newPanel = new SingleAttributeCriteriumPanel( propertyNames, featureType, dictCollection );
            this.attCritPanel.add( newPanel );
            int i = this.attCritPanel.getComponentCount() * 34;
            this.attCritPanel.setPreferredSize( new Dimension( 290, i ) );
            this.attCritPanel.revalidate();
        } else if ( srcBt.getName().equals( REMOVE_BT ) ) {
            removeSelectedAttCriteria();
        }
    }
}
