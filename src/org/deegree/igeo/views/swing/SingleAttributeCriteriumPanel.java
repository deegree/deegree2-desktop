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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.model.filterencoding.ComparisonOperation;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>CriteriumPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
class SingleAttributeCriteriumPanel extends JPanel {

    private static final long serialVersionUID = 6406293091094136911L;

    private List<QualifiedName> propertyNames;

    private JComboBox propName;

    private JComboBox compOperator;

    private JTextField valueTF;

    private JComboBox valueCB;

    private Map<Integer, String> comparisonOperators;

    private JCheckBox caseSensitiveCB;

    private JCheckBox select;

    private DictionaryCollection dictCollection;

    private QualifiedName featureType;

    /**
     * @param propertyNames
     *            the list of propertyNames to display in combo box
     * @param featureType
     *            the selected WFSFeatureType
     * @param dictCollection
     *            the defined dictionaries
     */
    SingleAttributeCriteriumPanel( List<QualifiedName> propertyNames, QualifiedName featureType,
                                   DictionaryCollection dictCollection ) {
        this.propertyNames = propertyNames;
        this.featureType = featureType;
        this.dictCollection = dictCollection;

        // all comparison operators
        this.comparisonOperators = new HashMap<Integer, String>();
        this.comparisonOperators.put( OperationDefines.PROPERTYISEQUALTO, Messages.getMessage( Locale.getDefault(),
                                                                                               "$MD10155" ) );
        this.comparisonOperators.put( OperationDefines.PROPERTYISGREATERTHAN, Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10156" ) );
        this.comparisonOperators.put( OperationDefines.PROPERTYISGREATERTHANOREQUALTO,
                                      Messages.getMessage( Locale.getDefault(), "$MD10157" ) );
        this.comparisonOperators.put( OperationDefines.PROPERTYISLESSTHAN, Messages.getMessage( Locale.getDefault(),
                                                                                                "$MD10158" ) );
        this.comparisonOperators.put( OperationDefines.PROPERTYISLESSTHANOREQUALTO,
                                      Messages.getMessage( Locale.getDefault(), "$MD10159" ) );
        this.comparisonOperators.put( OperationDefines.PROPERTYISLIKE, Messages.getMessage( Locale.getDefault(),
                                                                                            "$MD10160" ) );

        // TODO
        // this.operators.put( OperationDefines.PROPERTYISBETWEEN, "< >" );
        // this.operators.put( OperationDefines.PROPERTYISNULL, "IS NULL" );
        init();
    }

    void init() {
        GridBagConstraints gbc = SwingUtils.initPanel( this );

        // check box to select an entry
        select = new JCheckBox();

        // the comboBox for all available propertyNames
        propName = new JComboBox();
        propName.setPreferredSize( new Dimension( 175, 22 ) );
        propName.setRenderer( new QualifiedNameRenderer() );
        for ( QualifiedName name : propertyNames ) {
            propName.addItem( name );
        }
        propName.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateValueField();
            }
        } );

        // list of comparison operators
        compOperator = new JComboBox();
        compOperator.setPreferredSize( new Dimension( 50, 22 ) );
        for ( Integer operatorName : comparisonOperators.keySet() ) {
            String opName = comparisonOperators.get( operatorName );
            compOperator.addItem( opName );
        }
        compOperator.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int selectedOp = getComparisonOperator();
                if ( selectedOp == OperationDefines.PROPERTYISEQUALTO || selectedOp == OperationDefines.PROPERTYISLIKE ) {
                    caseSensitiveCB.setVisible( true );
                } else {
                    caseSensitiveCB.setVisible( false );
                }
            }
        } );

        // text field to enter value
        valueTF = new JTextField();
        valueTF.setPreferredSize( new Dimension( 100, 22 ) );
		// or comboBox to select a value, if a codelist is defined for the selected property/featureType
        valueCB = new JComboBox();
        valueCB.setPreferredSize( new Dimension( 100, 22 ) );

        // check box, is case sensitive
        caseSensitiveCB = new JCheckBox();

        updateValueField();
        gbc.anchor = GridBagConstraints.CENTER;

        this.add( select, gbc );
        ++gbc.gridx;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( propName, gbc );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        ++gbc.gridx;
        this.add( compOperator, gbc );
        ++gbc.gridx;
        this.add( valueTF, gbc );
        this.add( valueCB, gbc );
        ++gbc.gridx;
        this.add( caseSensitiveCB, gbc );
    }

    private void updateValueField() {
        if ( propName.getSelectedItem() != null ) {
            String ftn = ( (QualifiedName) propName.getSelectedItem() ).getLocalName();
            QualifiedName qn = new QualifiedName( featureType.getLocalName() + "/" + ftn, featureType.getNamespace() );
            List<Pair<String, String>> codelist = dictCollection.getCodelist( qn, Locale.getDefault().getLanguage() );
            if ( codelist != null && codelist.size() > 0 ) {
                valueTF.setVisible( false );
                valueCB.setVisible( true );
                valueCB.removeAllItems();
                for ( Pair<String, String> pair : codelist ) {
                    valueCB.addItem( new PairComboBoxItem( pair ) );
                }
                return;
            }
        }
        valueTF.setVisible( true );
        valueCB.setVisible( false );
    }

    /**
     * @return the legend of a single criterium as panel
     */
    static JPanel getLabelPanel() {
        JPanel p = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( p );

        JLabel propNameLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10152" ) );
        propNameLabel.setPreferredSize( new Dimension( 175, 18 ) );
        JLabel opLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10153" ) );
        opLabel.setPreferredSize( new Dimension( 60, 18 ) );
        JLabel valueLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10154" ) );
        valueLabel.setPreferredSize( new Dimension( 75, 18 ) );

        URL iconUrl = SingleAttributeCriteriumPanel.class.getResource( "/org/deegree/igeo/views/images/case_sensitive.png" );
        Icon icon = new ImageIcon( iconUrl );
        JLabel caseSensitiveLabel = new JLabel( icon );
        caseSensitiveLabel.setToolTipText( Messages.getMessage( Locale.getDefault(), "$MD11006" ) );

        gbc.anchor = GridBagConstraints.CENTER;

        gbc.insets = new Insets( 2, 30, 2, 2 );
        ++gbc.gridx;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add( propNameLabel, gbc );
        gbc.insets = new Insets( 0, 0, 0, 0 );
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        ++gbc.gridx;
        p.add( opLabel, gbc );
        gbc.insets = new Insets( 2, 5, 2, 2 );
        ++gbc.gridx;
        p.add( valueLabel, gbc );
        ++gbc.gridx;
        p.add( caseSensitiveLabel, gbc );

        return p;
    }

    /**
     * @return the selected propertyName
     */
    private PropertyName getPropertyName() {
        QualifiedName qn = (QualifiedName) this.propName.getSelectedItem();
        return new PropertyName( qn );
    }

    /**
     * @return the selected comparisopn operator
     */
    private int getComparisonOperator() {
        String compOp = (String) compOperator.getSelectedItem();
        int selectedCompOp = OperationDefines.PROPERTYISEQUALTO;
        for ( Integer operatorId : comparisonOperators.keySet() ) {
            if ( compOp.equals( comparisonOperators.get( operatorId ) ) ) {
                selectedCompOp = operatorId;
            }
        }
        return selectedCompOp;
    }

    private Literal getLiteral() {
        if ( valueCB.isVisible() && valueCB.getSelectedItem() != null ) {
            return new Literal( ( (PairComboBoxItem) valueCB.getSelectedItem() ).pair.first );
        }
        return new Literal( valueTF.getText() );
    }

    /**
     * @return the operation created in a single attribute panel
     */
    Operation getOperation() {
        ComparisonOperation operation = null;
        int operatorId = getComparisonOperator();
        switch ( operatorId ) {
        case OperationDefines.PROPERTYISEQUALTO:
            operation = new PropertyIsCOMPOperation( operatorId, getPropertyName(), getLiteral(),
                                                     caseSensitiveCB.isSelected() );
            break;
        case OperationDefines.PROPERTYISLESSTHAN:
        case OperationDefines.PROPERTYISGREATERTHAN:
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO: {
            operation = new PropertyIsCOMPOperation( operatorId, getPropertyName(), getLiteral() );
            break;
        }
        case OperationDefines.PROPERTYISLIKE: {
            operation = new PropertyIsLikeOperation( getPropertyName(), getLiteral(), '*', '?', '\\',
                                                     caseSensitiveCB.isSelected() );
            break;
        }
            // case OperationDefines.PROPERTYISNULL: {
            // operation = new PropertyIsNullOperation( getPropertyName() );
            // break;
            // }
            // case OperationDefines.PROPERTYISBETWEEN: {
            // operation = new PropertyIsBetweenOperation(getPropertyName(),);
            // break;
            // }

        }
        return operation;
    }

    boolean isSelected() {
        return select.isSelected();
    }

    private class PairComboBoxItem {

        private Pair<String, String> pair;

        public PairComboBoxItem( Pair<String, String> pair ) {
            this.pair = pair;
        }

        @Override
        public String toString() {
            return pair.second + " [code: " + pair.first + "]";
        }
    }
}
