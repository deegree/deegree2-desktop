/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

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
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FILLCOLOR;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINESTYLE;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.VALUE;
import static org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE.EQUAL;
import static org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE.QUALITY;
import static org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE.QUANTILE;
import static org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE.UNIQUE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.desktop.style.LayerCache.CachedLayer;
import org.deegree.desktop.style.classification.ClassificationFromSld;
import org.deegree.desktop.style.model.DashArray;
import org.deegree.desktop.style.model.GraphicSymbol;
import org.deegree.desktop.style.model.Histogram;
import org.deegree.desktop.style.model.PropertyValue;
import org.deegree.desktop.style.model.RandomColors;
import org.deegree.desktop.style.model.SldProperty;
import org.deegree.desktop.style.model.SldValues;
import org.deegree.desktop.style.model.WellKnownMark;
import org.deegree.desktop.style.model.classification.AbstractClassification;
import org.deegree.desktop.style.model.classification.ClassificationTableRow;
import org.deegree.desktop.style.model.classification.Column;
import org.deegree.desktop.style.model.classification.EqualIntervalClassification;
import org.deegree.desktop.style.model.classification.IllegalClassificationException;
import org.deegree.desktop.style.model.classification.Intervallable;
import org.deegree.desktop.style.model.classification.ManualClassification;
import org.deegree.desktop.style.model.classification.QualityClassification;
import org.deegree.desktop.style.model.classification.QuantileClassification;
import org.deegree.desktop.style.model.classification.ThematicGrouping;
import org.deegree.desktop.style.model.classification.ThematicGroupingInformation;
import org.deegree.desktop.style.model.classification.UniqueValueGrouping;
import org.deegree.desktop.style.model.classification.ValueRange;
import org.deegree.desktop.style.model.classification.Intervallables.DateIntervallable;
import org.deegree.desktop.style.model.classification.Intervallables.DoubleIntervallable;
import org.deegree.desktop.style.model.classification.Intervallables.StringIntervallable;
import org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE;
import org.deegree.desktop.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.desktop.views.swing.style.SingleItem;
import org.deegree.desktop.views.swing.style.SingleItemDisableComboBox;
import org.deegree.desktop.views.swing.style.StyleDialog;
import org.deegree.desktop.views.swing.style.StyleDialogUtils;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;
import org.deegree.desktop.views.swing.style.component.font.FontHelper;
import org.deegree.desktop.views.swing.style.editor.ClassificationValuesEditor;
import org.deegree.desktop.views.swing.style.editor.ColorTableCellEditor;
import org.deegree.desktop.views.swing.style.editor.FillTableCellEditor;
import org.deegree.desktop.views.swing.style.editor.PointCellEditor;
import org.deegree.desktop.views.swing.style.editor.SpinnerTableCellEditor;
import org.deegree.desktop.views.swing.style.renderer.ClassificationValuesRenderer;
import org.deegree.desktop.views.swing.style.renderer.ColorTableCellRenderer;
import org.deegree.desktop.views.swing.style.renderer.DashArrayRenderer;
import org.deegree.desktop.views.swing.style.renderer.LocaleTableCellRenderer;
import org.deegree.desktop.views.swing.style.renderer.SldPropertyCellRenderer;
import org.deegree.desktop.views.swing.style.renderer.SldPropertyRenderer;
import org.deegree.desktop.views.swing.style.renderer.SymbolRenderer;
import org.deegree.desktop.views.swing.style.renderer.SymbolTableCellRenderer;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.Rule;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>AbstractClassificationPanel</code> TODO add class documentation
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class AbstractClassificationPanel extends JPanel implements ActionListener, TableModelListener {

    private static final long serialVersionUID = -7782382934136012149L;

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractClassificationPanel.class );

    private SingleItem unknownClassification;

    private SingleItem uniqueValue;

    private SingleItem equalInterval;

    private SingleItem quantile;

    private SingleItem quality;

    public enum SYMBOLIZERTYPE {
        POLYGON, POINT, LINE, LABEL
    }

    protected VisualPropertyPanel assignedVisualPropPanel;

    protected JComboBox propertyCB;

    private ClassificationTableModel<?> model;

    private JCheckBox status;

    private SingleItemDisableComboBox classificationTypeCB;

    private JLabel isManual = new JLabel( get( "$MD10735" ) );

    private JSpinner numberOfClassesSpinner;

    private JTable classesTable;

    private boolean isUpdating = false;

    private boolean isInitialising = true;

    private JButton addRowBt;

    private JButton removeRowBt;

    private JButton openHistogramBt;

    private String datePattern;

    private String decimalPattern;

    private Histogram histogram;

    private JRadioButton fullDatabase;

    private JRadioButton extentDatabase;

    /**
     * 
     * @param assignedVisualPropPanel
     */
    public AbstractClassificationPanel( VisualPropertyPanel assignedVisualPropPanel ) {
        this.assignedVisualPropPanel = assignedVisualPropPanel;
        datePattern = assignedVisualPropPanel.getOwner().getSettings().getFormatsOptions().getPattern( "datePattern" );
        decimalPattern = assignedVisualPropPanel.getOwner().getSettings().getFormatsOptions().getPattern( "decimalPattern" );
        setLayout( new BorderLayout() );
        init();
    }

    /**
     * @return true, if classification is enabled; false otherwise
     */
    public boolean isActive() {
        return status.isSelected();
    }

    /**
     * @return the list of rules, representing the classification
     */
    public List<Rule> getRules() {
        PropertyName propertyName = new PropertyName( (QualifiedName) propertyCB.getSelectedItem() );
        return model.getClassifiedData( getSymbolizerType(), propertyName );
    }

    /**
     * Constructs a manual classification out of the given rules. Only the first symbolizer of each rule will be
     * considered, if it is a PolygonSymbolizer!.
     * 
     * @param the
     *            rules to construct a classification
     * @param type
     *            the type of the featuretypeName
     * @throws FilterEvaluationException
     */
    public void setValues( List<Rule> rules, FeatureType featureType )
                            throws IllegalClassificationException, FilterEvaluationException {

        PropertyName propertyName = ClassificationFromSld.detectPropertyName( rules );
        if ( propertyName == null ) {
            throw new IllegalClassificationException( "PopertyNames are not valid!" );
        }
        // TODO: check if this is necessary!?
        boolean isTypeCorrect = ClassificationFromSld.isTypeCorrect( rules, getSymbolizerType() );
        if ( !isTypeCorrect ) {
            throw new IllegalClassificationException(
                                                      "Could not create list of value ranges - symbolizer is not of type "
                                                                              + getSymbolizerType() );
        }

        int propertyType = ClassificationFromSld.detectPropertyType( propertyName, featureType );

        switch ( propertyType ) {
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.BIGINT:
        case Types.DOUBLE:
        case Types.FLOAT:
            setPropertyCB( propertyName );
            PropertyValue<?> pvDouble = getPropertyValue();
            List<Intervallable<Double>> doubleData = getDoubleData( pvDouble );
            ThematicGroupingInformation<Double> tgiDouble = ClassificationFromSld.createDoubleClassification( rules,
                                                                                                              doubleData,
                                                                                                              propertyType,
                                                                                                              decimalPattern,
                                                                                                              assignedVisualPropPanel.getOwner().getSettings() );
            ThematicGrouping<Double> groupingDouble;
            groupingDouble = getGrouping( tgiDouble );
            numberOfClassesSpinner.setValue( tgiDouble.getRows().size() );

            groupingDouble.setData( doubleData );
            ClassificationTableModel<Double> tableModelDouble = new ClassificationTableModel<Double>(
                                                                                                      getColumns(),
                                                                                                      assignedVisualPropPanel.getOwner() );
            tableModelDouble.setClassification( tgiDouble.getRows(), tgiDouble.getType() );
            tableModelDouble.setThematicGrouping( groupingDouble, tgiDouble.getType() );
            configureClassesTable( tableModelDouble,
                                   groupingDouble.getAttributeHeader(),
                                   new ClassificationValuesRenderer<Double>(),
                                   new ClassificationValuesEditor<Double>(
                                                                           new DoubleIntervallable( 0.0, decimalPattern ) ) );
            break;
        case Types.DATE:
            setPropertyCB( propertyName );
            PropertyValue<?> pvDate = getPropertyValue();
            List<Intervallable<Date>> dateData = getDateData( pvDate );
            ThematicGroupingInformation<Date> tgiDate = ClassificationFromSld.createDateClassification( rules,
                                                                                                        dateData,
                                                                                                        propertyType,
                                                                                                        datePattern,
                                                                                                        assignedVisualPropPanel.getOwner().getSettings() );
            ThematicGrouping<Date> groupingDate;
            groupingDate = getGrouping( tgiDate );
            numberOfClassesSpinner.setValue( tgiDate.getRows().size() );

            groupingDate.setData( dateData );
            ClassificationTableModel<Date> tableModelDate = new ClassificationTableModel<Date>(
                                                                                                getColumns(),
                                                                                                assignedVisualPropPanel.getOwner() );
            tableModelDate.setClassification( tgiDate.getRows(), tgiDate.getType() );
            tableModelDate.setThematicGrouping( groupingDate, tgiDate.getType() );
            configureClassesTable( tableModelDate,
                                   groupingDate.getAttributeHeader(),
                                   new ClassificationValuesRenderer<Date>(),
                                   new ClassificationValuesEditor<Date>(
                                                                         new DateIntervallable( new Date(), datePattern ) ) );
            break;
        case Types.VARCHAR:
            setPropertyCB( propertyName );
            PropertyValue<?> pvString = getPropertyValue();
            List<Intervallable<String>> stringData = getStringData( pvString );
            ThematicGroupingInformation<String> tgiString = ClassificationFromSld.createStringClassification( rules,
                                                                                                              stringData,
                                                                                                              propertyType,
                                                                                                              assignedVisualPropPanel.getOwner().getSettings() );
            ThematicGrouping<String> groupingString;
            switch ( tgiString.getType() ) {
            case UNIQUE:
                groupingString = new UniqueValueGrouping<String>();
                classificationTypeCB.setSelectedItem( uniqueValue );
                numberOfClassesSpinner.setEnabled( false );
                break;
            case QUALITY:
                groupingString = new QualityClassification<String>();
                classificationTypeCB.setSelectedItem( quality );
                numberOfClassesSpinner.setEnabled( false );
                break;
            default:
                groupingString = new ManualClassification<String>();
                numberOfClassesSpinner.setValue( tgiString.getRows().size() );
                unknownClassification.setEnabled( true );
                classificationTypeCB.setSelectedItem( unknownClassification );
                break;
            }
            numberOfClassesSpinner.setValue( tgiString.getRows().size() );
            numberOfClassesSpinner.setEnabled( false );

            groupingString.setData( stringData );
            ClassificationTableModel<String> tableModelString = new ClassificationTableModel<String>(
                                                                                                      getColumns(),
                                                                                                      assignedVisualPropPanel.getOwner() );
            tableModelString.setClassification( tgiString.getRows(), tgiString.getType() );
            tableModelString.setThematicGrouping( groupingString, tgiString.getType() );
            configureClassesTable( tableModelString, groupingString.getAttributeHeader(),
                                   new ClassificationValuesRenderer<String>(),
                                   new ClassificationValuesEditor<String>( new StringIntervallable( "dummy" ) ) );
            break;
        default:
            LOG.logInfo( "not supported type:  " + propertyType );
            break;
        }

        status.setSelected( true );
    }

    private PropertyValue<?> getPropertyValue() {
        StyleDialog styleDialog = assignedVisualPropPanel.getOwner();
        if ( fullDatabase.isSelected() ) {
            return styleDialog.getAllPropertyValue( (QualifiedName) this.propertyCB.getSelectedItem() );
        } else if ( extentDatabase.isSelected() ) {
            return styleDialog.getExtentPropertyValue( (QualifiedName) this.propertyCB.getSelectedItem() );
        }
        return styleDialog.getPropertyValue( (QualifiedName) this.propertyCB.getSelectedItem() );
    }

    private <V extends Comparable<V>> ThematicGrouping<V> getGrouping( ThematicGroupingInformation<V> thematicGrouping ) {
        GROUPINGTYPE type = thematicGrouping.getType();
        // TODO: ask user for classification
//        if ( !thematicGrouping.getType().equals( UNIQUE ) && !thematicGrouping.getType().equals( QUANTILE )
//             && !thematicGrouping.getType().equals( EQUAL ) && !thematicGrouping.getType().equals( QUALITY ) ) {
//             type = askUserForClassificationType();
//        }
        ThematicGrouping<V> grouping;
        switch ( type ) {
        case UNIQUE:
            grouping = new UniqueValueGrouping<V>();
            classificationTypeCB.setSelectedItem( uniqueValue );
            numberOfClassesSpinner.setEnabled( false );
            break;
        case QUANTILE:
            grouping = new QuantileClassification<V>();
            classificationTypeCB.setSelectedItem( quantile );
            break;
        case EQUAL:
            grouping = new EqualIntervalClassification<V>();
            classificationTypeCB.setSelectedItem( equalInterval );
            break;
        case QUALITY:
            grouping = new QualityClassification<V>();
            classificationTypeCB.setSelectedItem( quality );
            break;
        default:
            grouping = new ManualClassification<V>();
            unknownClassification.setEnabled( true );
            classificationTypeCB.setSelectedItem( unknownClassification );
            break;
        }
        return grouping;
    }

    protected ClassificationTableModel<?> getModel() {
        if ( model == null ) {
            updateTable();
        }
        return model;
    }

    private void init() {
        status = new JCheckBox();
        status.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( isActive() ) {
                    assignedVisualPropPanel.setActive( true );
                }
            }
        } );
        classesTable = new ClassesTable();
        // nr of properties which can be classified

        List<QualifiedName> pvList = assignedVisualPropPanel.getOwner().getPropertyNames( Types.FLOAT, Types.DOUBLE,
                                                                                          Types.INTEGER,
                                                                                          Types.SMALLINT, Types.BIGINT,
                                                                                          Types.DATE );
        List<QualifiedName> pvListString = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );

        boolean isStringClassEnabled = true;
        boolean isClassEnabled = true;
        if ( pvList.size() == 0 ) {
            isClassEnabled = false;
            if ( pvListString.size() == 0 ) {
                isStringClassEnabled = false;
            }
        }

        uniqueValue = new SingleItem( get( "$MD10724" ), true );
        equalInterval = new SingleItem( get( "$MD10725" ), isClassEnabled );
        quantile = new SingleItem( get( "$MD10726" ), isClassEnabled );
        quality = new SingleItem( get( "$MD11531" ), isStringClassEnabled );
        unknownClassification = new SingleItem( get( "$MD11849" ), false );

        List<SingleItem> classificationItems = new ArrayList<SingleItem>( 4 );
        classificationItems.add( uniqueValue );
        classificationItems.add( equalInterval );
        classificationItems.add( quantile );
        classificationItems.add( quality );
        classificationItems.add( unknownClassification );

        classificationTypeCB = new SingleItemDisableComboBox( classificationItems );

        classificationTypeCB.addActionListener( this );

        propertyCB = new JComboBox();
        QualifiedNameRenderer renderer = new QualifiedNameRenderer();
        renderer.setPreferredSize( new Dimension( 100, 18 ) );
        propertyCB.setRenderer( renderer );
        propertyCB.addActionListener( this );

        SpinnerModel noClassesModel = new SpinnerNumberModel( AbstractClassification.DEFAULTNOOFCLASSES, 1,
                                                              Integer.MAX_VALUE, 1 );
        numberOfClassesSpinner = new JSpinner( noClassesModel );
        numberOfClassesSpinner.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( !isManual.isVisible() ) {
                    model.getThematicGrouping().setNoOfClasses( (Integer) numberOfClassesSpinner.getValue() );
                    model.update( VALUE, true );
                }
                setClassificationActive();
            }
        } );

        openHistogramBt = new JButton( get( "$MD11049" ) );
        openHistogramBt.addActionListener( this );
        openHistogramBt.setVisible( true );
        histogram = new Histogram();
        histogram.addWindowListener( new WindowAdapter() {
            public void windowClosed( java.awt.event.WindowEvent e ) {
                openHistogramBt.setText( get( "$MD11049" ) );
            };
        } );

        initDatabaseButtonGroup();

        classificationTypeCB.setSelectedItem( uniqueValue );
        isManual.setVisible( false );

        addRowBt = new JButton( get( "$MD10739" ), IconRegistry.getIcon( "textfield_add.png" ) );
        addRowBt.addActionListener( this );
        removeRowBt = new JButton( get( "$MD10740" ), IconRegistry.getIcon( "textfield_delete.png" ) );
        removeRowBt.setEnabled( false );
        removeRowBt.addActionListener( this );

        // set selection mode
        classesTable.setRowSelectionAllowed( true );
        classesTable.setColumnSelectionAllowed( false );
        classesTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        classesTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                if ( classesTable.getSelectedRows().length > 0 ) {
                    removeRowBt.setEnabled( true );
                } else {
                    removeRowBt.setEnabled( false );
                }
            }
        } );

        // mouseListener
        classesTable.addMouseListener( new TableMouseListener( classesTable ) );

        // set column model (width of the columns and editors)
        TableColumnModel columnModel = classesTable.getColumnModel();
        for ( int i = 0; i < classesTable.getColumnCount(); i++ ) {
            TableColumn column = columnModel.getColumn( i );
            switch ( model.getColumnType( i ) ) {
            case VALUE:
                column.setPreferredWidth( 100 );
                break;
            case FILLCOLOR:
            case LINECOLOR:
            case FILLTRANSPARENCY:
            case LINETRANSPARENCY:
            case FONTTRANSPARENCY:
            case LINEWIDTH:
            case LINESTYLE:
            case SIZE:
            case LINECAP:
            case FONTFAMILY:
            case HALOCOLOR:
            case HALORADIUS:
            case FONTCOLOR:
            case FONTSIZE:
            case FONTSTYLE:
            case FONTWEIGHT:
            case ROTATION:
                column.setPreferredWidth( 75 );
                break;
            case SYMBOL:
                column.setPreferredWidth( 100 );
                break;
            }
        }
        updateTable();

        int nrOfClasses = 6;
        if ( model != null ) {
            nrOfClasses = model.getRows().size();
        }
        numberOfClassesSpinner.setValue( nrOfClasses );
        // numberOfClassesSpinner.setValue( (Integer) model.getRows().size() );

        JTableHeader header = classesTable.getTableHeader();
        header.addMouseListener( new TableHeaderMouseListener( classesTable, assignedVisualPropPanel ) );
        header.setReorderingAllowed( false );

        JScrollPane scrollPane = new JScrollPane( classesTable );
        classesTable.setPreferredScrollableViewportSize( new Dimension( 150, 190 ) );

        // layout
        FormLayout fl = new FormLayout(
                                        "10dlu, left:min:grow(0.2), min:grow(0.3), $rgap, min:grow(0.3), $rgap, min:grow(0.2)",
                                        "bottom:15dlu, $cpheight, $cpheight, $cpheight, default:grow(1.0), $cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.setBorder( StyleDialogUtils.createStyleAttributeBorder( get( "$MD10719" ) ) );

        CellConstraints cc = new CellConstraints();
        builder.add( status, cc.xy( 1, 1 ) );
        builder.addLabel( get( "$MD10720" ), cc.xy( 2, 1 ) );

        builder.addLabel( get( "$MD10721" ), cc.xy( 2, 2 ) );
        builder.add( classificationTypeCB, cc.xy( 3, 2 ) );
        builder.add( isManual, cc.xy( 5, 2 ) );
        builder.addLabel( get( "$MD10722" ), cc.xy( 2, 3 ) );
        builder.add( propertyCB, cc.xywh( 3, 3, 3, 1 ) );
        builder.add( openHistogramBt, cc.xy( 5, 4 ) );
        builder.addLabel( get( "$MD10723" ), cc.xy( 2, 4 ) );
        builder.add( numberOfClassesSpinner, cc.xy( 3, 4 ) );

        builder.add( getDatabasePanel(), cc.xywh( 7, 2, 1, 3 ) );
        builder.add( scrollPane, cc.xyw( 1, 5, 7 ) );
        builder.add( buildTableChangerButtonBar(), cc.xyw( 1, 6, 5, CellConstraints.CENTER, CellConstraints.BOTTOM ) );

        add( builder.getPanel(), BorderLayout.CENTER );
        isInitialising = false;
    }

    private void initDatabaseButtonGroup() {
        fullDatabase = new JRadioButton( get( "$MD11867" ) );
        fullDatabase.addActionListener( this );
        extentDatabase = new JRadioButton( get( "$MD11868" ) );
        extentDatabase.addActionListener( this );
        ButtonGroup bgDatabase = new ButtonGroup();
        bgDatabase.add( fullDatabase );
        bgDatabase.add( extentDatabase );
        extentDatabase.setSelected( true );
    }

    private JPanel getDatabasePanel() {
        FormLayout fl = new FormLayout( "10dlu, left:default", "default, default, default" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();
        builder.addLabel( get( "$MD11866" ), cc.xyw( 1, 1, 2 ) );
        builder.add( extentDatabase, cc.xy( 2, 2 ) );
        builder.add( fullDatabase, cc.xy( 2, 3 ) );
        JPanel panel = builder.getPanel();
        CachedLayer cachedLayer = assignedVisualPropPanel.getOwner().getCachedLayer();
        if ( cachedLayer.isFullLoadingSupported() ) {
            panel.setVisible( true );
        } else {
            panel.setVisible( false );
        }
        return panel;
    }

    private void setPropertyCB( PropertyName propertyName )
                            throws IllegalClassificationException {
        boolean isPropertyNameValid = false;
        // init Property ComboBox
        for ( int i = 0; i < propertyCB.getItemCount(); i++ ) {
            PropertyPath itemAsPath = PropertyPathFactory.createPropertyPath( (QualifiedName) propertyCB.getItemAt( i ) );
            if ( ClassificationFromSld.equalsPropertyNameWithotNS( itemAsPath, propertyName.getValue() ) ) {
                propertyCB.setSelectedIndex( i );
                isPropertyNameValid = true;
                break;
            }
        }

        if ( !isPropertyNameValid ) {
            throw new IllegalClassificationException(
                                                      "property name used in the classification is not available for classification" );
        }
    }

    private void configureClassesTable( ClassificationTableModel<?> model, String valueHeader,
                                        ClassificationValuesRenderer<?> renderer, ClassificationValuesEditor<?> editor ) {
        classesTable.setModel( model );
        int valueIndex = model.getColumnIndex( VALUE );
        classesTable.getColumnModel().getColumn( valueIndex ).setHeaderValue( valueHeader );
        classesTable.getColumnModel().getColumn( valueIndex ).setCellRenderer( renderer );
        classesTable.getColumnModel().getColumn( valueIndex ).setCellEditor( editor );
        model.addTableModelListener( this );
        this.model = model;
        editor.addCellEditorListener( model );
    }

    // returns a list of double intervallables for TYPES.INTEGER and TYPES.DOUBLE types
    private List<Intervallable<Double>> getDoubleData( PropertyValue<?> pv ) {
        List<Intervallable<Double>> data = new ArrayList<Intervallable<Double>>();
        for ( Object o : pv.getValues() ) {
            double doubleValue = Double.NaN;
            try {
                if ( o != null && o instanceof String && ( (String) o ).length() > 0 ) {
                    doubleValue = Double.parseDouble( (String) o );
                } else if ( o instanceof Number ) {
                    doubleValue = ( (Number) o ).doubleValue();
                }
            } catch ( Exception e ) {
                LOG.logError( "Could not cast value to double, where type is INTEGER or DOUBLE" );
            }
            data.add( new DoubleIntervallable( doubleValue, decimalPattern ) );
        }
        return data;
    }

    private List<Intervallable<Date>> getDateData( PropertyValue<?> pv ) {
        List<Intervallable<Date>> data = new ArrayList<Intervallable<Date>>();
        for ( Object o : pv.getValues() ) {
            data.add( new DateIntervallable( (Date) o, datePattern ) );
        }
        return data;
    }

    private List<Intervallable<String>> getStringData( PropertyValue<?> pv ) {
        List<Intervallable<String>> data = new ArrayList<Intervallable<String>>();
        for ( Object o : pv.getValues() ) {
            String stringValue = "";
            try {
                if ( o != null && ( (String) o ).length() > 0 ) {
                    stringValue = (String) o;
                }
            } catch ( Exception e ) {
                LOG.logError( "Could not cast value to string, where type is VARCHAR" );
            }
            data.add( new StringIntervallable( stringValue ) );
        }
        return data;
    }

    private void updateTable() {
        if ( this.propertyCB.getSelectedItem() != null ) {
            PropertyValue<?> pv = getPropertyValue();

            switch ( pv.getDatatyp() ) {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.BIGINT:
                List<Intervallable<Double>> dataInteger = new ArrayList<Intervallable<Double>>();
                for ( Object o : pv.getValues() ) {
                    if ( pv.getDatatyp() == Types.BIGINT ) {
                        dataInteger.add( new DoubleIntervallable( ( (BigInteger) o ).doubleValue(), decimalPattern ) );
                    } else {
                        dataInteger.add( new DoubleIntervallable( (double) ( (Integer) o ), decimalPattern ) );
                    }
                }
                ClassificationTableModel<Double> tableModelInteger = new ClassificationTableModel<Double>(
                                                                                                           getColumns(),
                                                                                                           assignedVisualPropPanel.getOwner() );
                ThematicGrouping<Double> groupingInteger = getGrouping( dataInteger,
                                                                        (Integer) numberOfClassesSpinner.getValue() );
                tableModelInteger.setThematicGrouping( groupingInteger, getGroupingType() );
                configureClassesTable( tableModelInteger,
                                       groupingInteger.getAttributeHeader(),
                                       new ClassificationValuesRenderer<Double>(),
                                       new ClassificationValuesEditor<Double>( new DoubleIntervallable( 0.0,
                                                                                                        decimalPattern ) ) );
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
                List<Intervallable<Double>> dataDouble = new ArrayList<Intervallable<Double>>();
                for ( Object o : pv.getValues() ) {
                    dataDouble.add( new DoubleIntervallable( (Double) o, decimalPattern ) );
                }

                ClassificationTableModel<Double> tableModelDouble = new ClassificationTableModel<Double>(
                                                                                                          getColumns(),
                                                                                                          assignedVisualPropPanel.getOwner() );
                ThematicGrouping<Double> groupingDouble = getGrouping( dataDouble,
                                                                       (Integer) numberOfClassesSpinner.getValue() );
                tableModelDouble.setThematicGrouping( groupingDouble, getGroupingType() );
                configureClassesTable( tableModelDouble,
                                       groupingDouble.getAttributeHeader(),
                                       new ClassificationValuesRenderer<Double>(),
                                       new ClassificationValuesEditor<Double>( new DoubleIntervallable( 0.0,
                                                                                                        decimalPattern ) ) );
                break;
            case Types.DATE:
                ClassificationTableModel<Date> tableModelDate = new ClassificationTableModel<Date>(
                                                                                                    getColumns(),
                                                                                                    assignedVisualPropPanel.getOwner() );
                List<Intervallable<Date>> dataDate = new ArrayList<Intervallable<Date>>();
                for ( Object o : pv.getValues() ) {
                    dataDate.add( new DateIntervallable( (Date) o, datePattern ) );
                }
                ThematicGrouping<Date> groupingDate = getGrouping( dataDate,
                                                                   (Integer) numberOfClassesSpinner.getValue() );
                tableModelDate.setThematicGrouping( groupingDate, getGroupingType() );
                configureClassesTable( tableModelDate, groupingDate.getAttributeHeader(),
                                       new ClassificationValuesRenderer<Date>(),
                                       new ClassificationValuesEditor<Date>( new DateIntervallable( new Date(),
                                                                                                    datePattern ) ) );
                break;
            case Types.VARCHAR:
            default:
                ClassificationTableModel<String> tableModelString = new ClassificationTableModel<String>(
                                                                                                          getColumns(),
                                                                                                          assignedVisualPropPanel.getOwner() );
                List<Intervallable<String>> dataString = new ArrayList<Intervallable<String>>();
                for ( Object o : pv.getValues() ) {
                    dataString.add( new StringIntervallable( (String) o ) );
                }
                ThematicGrouping<String> groupingString = getGrouping( dataString,
                                                                       (Integer) numberOfClassesSpinner.getValue() );

                tableModelString.setThematicGrouping( groupingString, getGroupingType() );
                configureClassesTable( tableModelString, groupingString.getAttributeHeader(),
                                       new ClassificationValuesRenderer<String>(),
                                       new ClassificationValuesEditor<String>( new StringIntervallable( "dummy" ) ) );
                break;
            }
            if ( classificationTypeCB.getSelectedItem() == uniqueValue
                 || classificationTypeCB.getSelectedItem() == quality ) {
                getModel().getThematicGrouping().setFillColor( new RandomColors() );
                model.update( FILLCOLOR, true );
            }
            model.update( VALUE, true );
        }

    }

    private GROUPINGTYPE getGroupingType() {
        if ( classificationTypeCB.getSelectedItem() == equalInterval ) {
            return EQUAL;
        } else if ( classificationTypeCB.getSelectedItem() == quantile ) {
            return QUANTILE;
        } else if ( classificationTypeCB.getSelectedItem() == quality ) {
            return QUALITY;
        } else if ( classificationTypeCB.getSelectedItem() == uniqueValue ) {
            return UNIQUE;
        }
        return GROUPINGTYPE.MANUAL;
    }

    private <V extends Comparable<V>> ThematicGrouping<V> getGrouping( List<Intervallable<V>> data, int noOfClasses ) {
        ThematicGrouping<V> grouping;
        if ( classificationTypeCB.getSelectedItem() == equalInterval ) {
            grouping = new EqualIntervalClassification<V>();
        } else if ( classificationTypeCB.getSelectedItem() == quantile ) {
            grouping = new QuantileClassification<V>();
        } else if ( classificationTypeCB.getSelectedItem() == quality ) {
            grouping = new QualityClassification<V>();
        } else {
            grouping = new UniqueValueGrouping<V>();
        }
        grouping.setData( data );
        grouping.setNoOfClasses( noOfClasses );
        return grouping;
    }

    private JPanel buildTableChangerButtonBar() {
        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addGriddedButtons( new JButton[] { addRowBt, removeRowBt } );
        return bbBuilder.getPanel();
    }

    private void fillPropertyCB( List<QualifiedName> properties ) {
        QualifiedName selectedQn = null;
        if ( propertyCB.getSelectedItem() != null ) {
            selectedQn = (QualifiedName) propertyCB.getSelectedItem();
        }
        propertyCB.removeAllItems();
        for ( QualifiedName qn : properties ) {
            propertyCB.addItem( qn );
            if ( selectedQn != null && qn.equals( selectedQn ) ) {
                propertyCB.setSelectedItem( qn );
            }
        }
    }

    private void updateAfterClassificationChanged() {
        List<QualifiedName> pvList;
        boolean isClassesSpinnerEnabled = false;
        if ( classificationTypeCB.getSelectedItem() == equalInterval ) {
            pvList = assignedVisualPropPanel.getOwner().getPropertyNames( Types.FLOAT, Types.DOUBLE, Types.INTEGER,
                                                                          Types.DATE, Types.BIGINT, Types.SMALLINT );
            isClassesSpinnerEnabled = true;
            numberOfClassesSpinner.setValue( 6 );
        } else if ( classificationTypeCB.getSelectedItem() == quantile ) {
            pvList = assignedVisualPropPanel.getOwner().getPropertyNames( Types.FLOAT, Types.DOUBLE, Types.INTEGER,
                                                                          Types.DATE, Types.BIGINT, Types.SMALLINT );
            isClassesSpinnerEnabled = true;
            numberOfClassesSpinner.setValue( 6 );
        } else {
            pvList = assignedVisualPropPanel.getOwner().getPropertyNames();
        }
        isUpdating = true;
        fillPropertyCB( pvList );
        isUpdating = false;
        numberOfClassesSpinner.setEnabled( isClassesSpinnerEnabled );
        updateTable();
        numberOfClassesSpinner.setValue( model.getClassification().size() );
        initColumnValues();
    }

    private void setClassificationActive() {
        if ( !isInitialising ) {
            status.setSelected( true );
            assignedVisualPropPanel.setActive( true );
        }
    }

    private void openHistogram() {
        if ( propertyCB.getSelectedItem() != null ) {
            String title = get( "$MD11050", ( (QualifiedName) propertyCB.getSelectedItem() ).getLocalName() );
            List<ValueRange<?>> values = new ArrayList<ValueRange<?>>();
            int dataSize = this.model.getThematicGrouping().getNumberOfData();
            int classes = this.model.getRowCount();
            if ( dataSize / classes > 2 ) {
                for ( ClassificationTableRow<?> row : this.model.getRows() ) {
                    values.add( row.getValue() );
                }
                histogram.update( title, values );
                openHistogramBt.setText( get( "$MD11504" ) );
            } else {
                JOptionPane.showMessageDialog( this, get( "$MD11530", dataSize, classes, 2 ), get( "$MD11529" ),
                                               JOptionPane.INFORMATION_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( this, get( "$MD11051" ), get( "$DI10018" ), JOptionPane.INFORMATION_MESSAGE );
        }
    }

    private void addManuellClassificationItem() {
        if ( classificationTypeCB.getSelectedItem() != uniqueValue
             && ( ( classificationTypeCB.getItemCount() == 0 ) || !isManual.isVisible() ) ) {
            isManual.setVisible( true );
            numberOfClassesSpinner.setEnabled( false );
            propertyCB.setEnabled( false );
        }
    }

    private void removeManuellClassificationItem() {
        isManual.setVisible( false );
        numberOfClassesSpinner.setEnabled( true );
        propertyCB.setEnabled( true );
    }

    /**
     * transfers the values from the single elmements of the style to the classification
     */
    abstract public void initColumnValues();

    abstract protected List<Column> getColumns();

    abstract protected SYMBOLIZERTYPE getSymbolizerType();

    // //////////////////////////////////////////////////////////////////////////////
    // ACTIONLISTENER
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {
        Object src = e.getSource();
        if ( src == classificationTypeCB ) {
            if ( isManual.isVisible() ) {
                removeManuellClassificationItem();
            }
            unknownClassification.setEnabled( false );
            updateAfterClassificationChanged();
            setClassificationActive();
        } else if ( src == propertyCB ) {
            if ( this.propertyCB.getSelectedItem() != null && !isUpdating ) {
                updateTable();
            }
            setClassificationActive();
        } else if ( src == addRowBt ) {
            int row = classesTable.getSelectedRow();
            if ( row < 0 ) {
                row = model.getRowCount();
            }
            model.addRowBefore( row );
            numberOfClassesSpinner.setValue( classesTable.getRowCount() );
            setClassificationActive();
        } else if ( src == removeRowBt ) {
            // stop cell editing before removing selected rows
            int editingRow = classesTable.getEditingRow();
            int editingColumn = classesTable.getEditingColumn();
            if ( editingRow > -1 && editingColumn > -1
                 && classesTable.getCellEditor( editingRow, editingColumn ) != null ) {
                classesTable.getCellEditor( editingRow, editingColumn ).cancelCellEditing();
            }
            model.removeRows( classesTable.getSelectedRows() );
            numberOfClassesSpinner.setValue( classesTable.getRowCount() );
            setClassificationActive();
        } else if ( src == openHistogramBt ) {
            openHistogram();
        } else if ( src == fullDatabase || src == extentDatabase ) {
            updateTable();
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // TABLEMODELLISTENER
    // //////////////////////////////////////////////////////////////////////////////

    public void tableChanged( TableModelEvent e ) {
        // mark as manuell classification, if user changed a class limit in the table or
        // inserted/removed a row
        if ( ( model.getColumnIndex( VALUE ) == e.getColumn() && !isManual.isVisible() )
             || ( e.getType() == TableModelEvent.INSERT ) || ( e.getType() == TableModelEvent.DELETE ) ) {
            addManuellClassificationItem();
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // inner classes
    // //////////////////////////////////////////////////////////////////////////////

    private class ClassesTable extends JTable {

        private static final long serialVersionUID = 8303943959907931881L;

        @Override
        public TableCellRenderer getCellRenderer( int row, int column ) {
            switch ( model.getColumnType( column ) ) {
            case FILLCOLOR:
            case LINECOLOR:
            case FONTCOLOR:
            case HALOCOLOR:
                return new ColorTableCellRenderer();
            case SYMBOL:
                return new SymbolTableCellRenderer();
            case LINECAP:
                return new SldPropertyCellRenderer();
            default:
                return new LocaleTableCellRenderer();
            }
        }

        @Override
        public TableCellEditor getCellEditor( int row, int column ) {
            TableCellEditor editor;
            switch ( model.getColumnType( column ) ) {
            case FILLCOLOR:
                editor = new FillTableCellEditor( assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions(),
                                                  SldValues.getDefaultColor() );
                break;
            case LINECOLOR:
            case HALOCOLOR:
            case FONTCOLOR:
                editor = new ColorTableCellEditor();
                break;
            case FILLTRANSPARENCY:
            case LINETRANSPARENCY:
            case FONTTRANSPARENCY:
                int opAsInt = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
                editor = new SpinnerTableCellEditor( opAsInt, 0, 100, 1 );
                break;
            case LINEWIDTH:
                editor = new SpinnerTableCellEditor( SldValues.getDefaultLineWidth(), 0.0, Integer.MAX_VALUE, 0.5 );
                break;
            case LINESTYLE:
                JComboBox lineStyleCB = new JComboBox();
                int lineStyleCol = model.getColumnIndex( LINESTYLE );
                lineStyleCB.setRenderer( new DashArrayRenderer(
                                                                getTableHeader().getColumnModel().getColumn( lineStyleCol ).getWidth(),
                                                                15 ) );
                for ( DashArray da : SldValues.getDashArrays() ) {
                    lineStyleCB.addItem( da );
                }
                Map<String, DashArray> dashArrays = assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions().getDashArrays();
                for ( DashArray da : dashArrays.values() ) {
                    lineStyleCB.addItem( da );
                }
                editor = new DefaultCellEditor( lineStyleCB );
                break;
            case SIZE:
                editor = new SpinnerTableCellEditor( SldValues.getDefaultSize(), 0.00001, Integer.MAX_VALUE, 1.0 );
                break;
            case SYMBOL:
                JComboBox symbolCB = new JComboBox();
                symbolCB.setRenderer( new SymbolRenderer() );
                for ( WellKnownMark mark : SldValues.getWellKnownMarks() ) {
                    symbolCB.addItem( mark );
                }
                try {
                    Map<String, GraphicSymbol> symbols = assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions().getSymbolDefinitions();
                    List<GraphicSymbol> values = new ArrayList<GraphicSymbol>();
                    values.addAll( symbols.values() );
                    Collections.sort( (List<GraphicSymbol>) values );
                    for ( GraphicSymbol symbol : values ) {
                        symbolCB.addItem( symbol );
                    }
                } catch ( MalformedURLException e ) {
                    JOptionPane.showMessageDialog( this, get( "$MD10788" ), get( "$DI10017" ),
                                                   JOptionPane.ERROR_MESSAGE );
                }
                editor = new DefaultCellEditor( symbolCB );
                break;
            case LINECAP:
                JComboBox lineCapCB = new JComboBox();
                lineCapCB.setRenderer( new SldPropertyRenderer() );
                for ( SldProperty lc : SldValues.getLineCaps() ) {
                    lineCapCB.addItem( lc );
                }
                editor = new DefaultCellEditor( lineCapCB );
                break;
            case FONTFAMILY:
                FontHelper fhf = new FontHelper();
                editor = new DefaultCellEditor( fhf.createFontFamilyChooser() );
                break;
            case FONTSTYLE:
                FontHelper fhs = new FontHelper();
                editor = new DefaultCellEditor( fhs.createFontStyleChooser() );
                break;
            case FONTWEIGHT:
                FontHelper fhw = new FontHelper();
                editor = new DefaultCellEditor( fhw.createFontWeightChooser() );
                break;
            case FONTSIZE:
                editor = new SpinnerTableCellEditor( SldValues.getDefaultFontSize(), 1.0, Double.MAX_VALUE, 1.0 );
                break;
            case HALORADIUS:
                editor = new SpinnerTableCellEditor( SldValues.getDefaultHaloRadius(), 0d, 50d, 1d );
                break;
            case ROTATION:
                editor = new SpinnerTableCellEditor( SldValues.getDefaultRotation(), 0.0, 360.0, 5.0d );
                break;
            case DISPLACEMENT:
                editor = new PointCellEditor( SldValues.getDefaultDisplacement() );
                break;
            case ANCHORPOINT:
                editor = new PointCellEditor( SldValues.getDefaultAnchorPoint() );
                break;
            default:
                return super.getCellEditor( row, column );
            }
            // add cell editor listener, to be informed, when cell editing was stopped or
            // canceled; was necessary, because of problems after deleting an editing row!
            editor.addCellEditorListener( this );
            return editor;
        }

        // Implement table header to show tool tips.
        @Override
        protected JTableHeader createDefaultTableHeader() {

            return new JTableHeader( columnModel ) {

                private static final long serialVersionUID = 7937684822965281600L;

                public String getToolTipText( MouseEvent e ) {
                    java.awt.Point p = e.getPoint();
                    int index = columnModel.getColumnIndexAtX( p.x );
                    int realIndex = columnModel.getColumn( index ).getModelIndex();
                    return model.getColumnTooltip( realIndex );
                }
            };
        }
    };

}
