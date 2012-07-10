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

package org.deegree.igeo.views.swing.style.component.classification;

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.igeo.settings.GraphicOptions;
import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.Fill;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.classification.IntegerRange;
import org.deegree.igeo.style.model.classification.Column.COLUMNTYPE;
import org.deegree.igeo.views.swing.style.SymbolPanel;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.AnchorPointClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.DisplacementClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.FontFamilyClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.FontSizeClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.FontStyleClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.FontWeightClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.HaloRadiusClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.edit.RotationClassificationPanel;
import org.deegree.igeo.views.swing.style.renderer.DashArrayRenderer;
import org.deegree.igeo.views.swing.style.renderer.SldPropertyRenderer;
import org.deegree.igeo.views.swing.util.panels.PanelDialog;

/**
 * <code>TableHeaderMouseListener</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class TableHeaderMouseListener extends MouseAdapter implements ItemPerformedListener {

    private GraphicOptions go;

    private DefaultColumnHeaderPopUpMenu fillColorPopupMenu;

    private DefaultColumnHeaderPopUpMenu lineColorPopupMenu;

    private DefaultColumnHeaderPopUpMenu lineTransparencyPopupMenu;

    private DefaultColumnHeaderPopUpMenu fillTransparencyPopupMenu;

    private DefaultColumnHeaderPopUpMenu lineWidthPopupMenu;

    private DefaultColumnHeaderPopUpMenu lineStylePopupMenu;

    private DefaultColumnHeaderPopUpMenu valuePopupMenu;

    private DefaultColumnHeaderPopUpMenu sizePopupMenu;

    private DefaultColumnHeaderPopUpMenu symbolPopupMenu;

    private DefaultColumnHeaderPopUpMenu lineCapPopupMenu;

    private DefaultColumnHeaderPopUpMenu countPopupMenu;

    private DefaultColumnHeaderPopUpMenu fontColorPopupMenu;

    private DefaultColumnHeaderPopUpMenu fontFamilyPopupMenu;

    private DefaultColumnHeaderPopUpMenu fontWeightPopupMenu;

    private DefaultColumnHeaderPopUpMenu fontStylePopupMenu;

    private DefaultColumnHeaderPopUpMenu fontSizePopupMenu;

    private DefaultColumnHeaderPopUpMenu anchorPointPopupMenu;

    private DefaultColumnHeaderPopUpMenu displacementPopupMenu;

    private DefaultColumnHeaderPopUpMenu rotationPopupMenu;

    private DefaultColumnHeaderPopUpMenu haloRadiusPopupMenu;

    private DefaultColumnHeaderPopUpMenu haloColorPopupMenu;

    private DefaultColumnHeaderPopUpMenu fontTransparencyPopupMenu;

    private JTable classesTable;

    private VisualPropertyPanel assignedVisualPropPanel;

    public TableHeaderMouseListener( JTable classesTable, VisualPropertyPanel assignedVisualPropPanel ) {
        this.classesTable = classesTable;
        this.assignedVisualPropPanel = assignedVisualPropPanel;
        go = assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions();
        initMenus();
    }

    private ClassificationTableModel<?> getModel() {
        return (ClassificationTableModel<?>) classesTable.getModel();
    }

    private void initMenus() {
        fillColorPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10803" ), get( "$MD10804" ),
                                                               get( "$MD10805" ) );
        lineColorPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10811" ), get( "$MD10812" ),
                                                               get( "$MD10813" ) );
        lineTransparencyPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10807" ), get( "$MD10808" ),
                                                                      get( "$MD10809" ) );
        fillTransparencyPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10816" ), get( "$MD10817" ),
                                                                      get( "$MD10818" ) );
        lineWidthPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10820" ), get( "$MD10821" ),
                                                               get( "$MD10827" ) );
        lineStylePopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10903" ), get( "$MD10904" ),
                                                               get( "$MD10905" ) );
        lineStylePopupMenu.setSortItemEnabled( false );

        valuePopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10906" ) );

        sizePopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10912" ), get( "$MD10913" ), get( "$MD10914" ) );
        symbolPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10915" ), get( "$MD10916" ),
                                                            get( "$MD10917" ) );
        symbolPopupMenu.setSortItemEnabled( false );

        lineCapPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD10932" ), get( "$MD10933" ),
                                                             get( "$MD10934" ) );
        lineCapPopupMenu.setSortItemEnabled( false );

        fontFamilyPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11674" ), get( "$MD11675" ), null );
        fontWeightPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11681" ), get( "$MD11682" ), null );
        fontStylePopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11679" ), get( "$MD11680" ), null );
        fontSizePopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11683" ), get( "$MD11684" ), null );
        fontColorPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11716" ), get( "$MD11715" ), null );
        anchorPointPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11695" ), get( "$MD11694" ), null );
        displacementPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11699" ), get( "$MD11698" ), null );
        rotationPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11697" ), get( "$MD11696" ), null );
        haloColorPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11703" ), get( "$MD11702" ), null );
        haloRadiusPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11701" ), get( "$MD11700" ), null );
        fontTransparencyPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11721" ), get( "$MD11720" ), null );

        countPopupMenu = new DefaultColumnHeaderPopUpMenu( this, get( "$MD11048" ) );

    }

    @Override
    public void mousePressed( MouseEvent e ) {
        maybeShowPopup( e );
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        maybeShowPopup( e );
    }

    public void maybeShowPopup( MouseEvent e ) {
        if ( e.isPopupTrigger() ) {
            TableColumnModel colModel = classesTable.getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX( e.getX() );
            int modelIndex = colModel.getColumn( columnModelIndex ).getModelIndex();
            switch ( getModel().getColumnType( modelIndex ) ) {
            case FILLCOLOR:
                fillColorPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case LINECOLOR:
                lineColorPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FILLTRANSPARENCY:
                fillTransparencyPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case LINETRANSPARENCY:
                lineTransparencyPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case LINEWIDTH:
                lineWidthPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case LINESTYLE:
                lineStylePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case VALUE:
                valuePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case SIZE:
                sizePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case SYMBOL:
                symbolPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case LINECAP:
                lineCapPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTCOLOR:
                fontColorPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTFAMILY:
                fontFamilyPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTWEIGHT:
                fontWeightPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTSTYLE:
                fontStylePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTSIZE:
                fontSizePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case FONTTRANSPARENCY:
                fontTransparencyPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case ANCHORPOINT:
                anchorPointPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case DISPLACEMENT:
                displacementPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case ROTATION:
                rotationPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case HALORADIUS:
                haloRadiusPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case HALOCOLOR:
                haloColorPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            case COUNT:
                countPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                break;
            }
        }
    }

    private void openFillColorDlg() {
        FillGraphicClassificationPanel fillColorFrame = new FillGraphicClassificationPanel(
                                                                                            go,
                                                                                            getModel().getThematicGrouping().getFillColor(),
                                                                                            get( "$MD10729" ) );

        PanelDialog fillColorDlg = new PanelDialog( fillColorFrame, true );
        fillColorDlg.setLocation( classesTable.getLocationOnScreen() );
        fillColorDlg.setVisible( true );
        if ( fillColorDlg.clickedOk ) {
            getModel().getThematicGrouping().setFillColor( fillColorFrame.getValue() );
            getModel().update( COLUMNTYPE.FILLCOLOR, true );
        }
    }

    private void openHaloColorDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );
        ColorClassificationPanel haloColorFrame = new ColorClassificationPanel(
                                                                                assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions(),
                                                                                getModel().getThematicGrouping().getFillColor(),
                                                                                get( "$MD11705" ),
                                                                                SldValues.getDefaultHaloColor(), true,
                                                                                propertyNames );
        PanelDialog haloColorDlg = new PanelDialog( haloColorFrame, true );
        haloColorDlg.setLocation( classesTable.getLocationOnScreen() );
        haloColorDlg.setVisible( true );
        if ( haloColorDlg.clickedOk ) {
            getModel().getThematicGrouping().setHaloColor( haloColorFrame.getValue() );
            getModel().update( COLUMNTYPE.HALOCOLOR, true );
        }
    }

    private void openHaloRadiusDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );

        HaloRadiusClassificationPanel haloColorFrame = new HaloRadiusClassificationPanel(
                                                                                          getModel().getThematicGrouping().getHaloRadius(),
                                                                                          get( "$MD11704" ),
                                                                                          propertyNames );

        PanelDialog haloColorDlg = new PanelDialog( haloColorFrame, true );
        haloColorDlg.setLocation( classesTable.getLocationOnScreen() );
        haloColorDlg.setVisible( true );
        if ( haloColorDlg.clickedOk ) {
            getModel().getThematicGrouping().setHaloRadius( haloColorFrame.getValue() );
            getModel().update( COLUMNTYPE.HALORADIUS, true );
        }
    }

    private void openAnchorPointDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );

        AnchorPointClassificationPanel anchorPointFrame = new AnchorPointClassificationPanel(
                                                                                              getModel().getThematicGrouping().getAnchorPoint(),
                                                                                              get( "$MD11706" ),
                                                                                              propertyNames );

        PanelDialog anchorPointDlg = new PanelDialog( anchorPointFrame, true );
        anchorPointDlg.setLocation( classesTable.getLocationOnScreen() );
        anchorPointDlg.setVisible( true );
        if ( anchorPointDlg.clickedOk ) {
            getModel().getThematicGrouping().setAnchorPoint( anchorPointFrame.getValue() );
            getModel().update( COLUMNTYPE.ANCHORPOINT, true );
        }
    }

    private void openRotationDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );

        RotationClassificationPanel rotationFrame = new RotationClassificationPanel(
                                                                                     getModel().getThematicGrouping().getRotation(),
                                                                                     get( "$MD11708" ), propertyNames );

        PanelDialog rotationDlg = new PanelDialog( rotationFrame, true );
        rotationDlg.setLocation( classesTable.getLocationOnScreen() );
        rotationDlg.setVisible( true );
        if ( rotationDlg.clickedOk ) {
            getModel().getThematicGrouping().setRotation( rotationFrame.getValue() );
            getModel().update( COLUMNTYPE.ROTATION, true );
        }
    }

    private void openDisplacementDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );

        DisplacementClassificationPanel displacementFrame = new DisplacementClassificationPanel(
                                                                                                 getModel().getThematicGrouping().getDisplacement(),
                                                                                                 get( "$MD11707" ),
                                                                                                 propertyNames );

        PanelDialog displacementDlg = new PanelDialog( displacementFrame, true );
        displacementDlg.setLocation( classesTable.getLocationOnScreen() );
        displacementDlg.setVisible( true );
        if ( displacementDlg.clickedOk ) {
            getModel().getThematicGrouping().setDisplacement( displacementFrame.getValue() );
            getModel().update( COLUMNTYPE.DISPLACEMENT, true );
        }
    }

    private void openLineColorDlg() {
        ColorClassificationPanel lineColorPanel = new ColorClassificationPanel(
                                                                                assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions(),
                                                                                getModel().getThematicGrouping().getLineColor(),
                                                                                get( "$MD10768" ),
                                                                                SldValues.getDefaultLineColor(), false,
                                                                                null );
        PanelDialog lineColorDlg = new PanelDialog( lineColorPanel, true );
        lineColorDlg.setLocation( classesTable.getLocationOnScreen() );
        lineColorDlg.setVisible( true );
        if ( lineColorDlg.clickedOk && lineColorPanel.getValue() instanceof Fill ) {
            getModel().getThematicGrouping().setLineColor( (Fill) lineColorPanel.getValue() );
            getModel().update( LINECOLOR, true );
        }
    }

    private void openFillTransparencyDlg() {
        int opAsInt = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        TransparencyClassificationPanel fillTransparencyPanel = new TransparencyClassificationPanel(
                                                                                                     getModel().getThematicGrouping().getFillTransparency(),
                                                                                                     get( "$MD10767" ),
                                                                                                     opAsInt, 0, 100,
                                                                                                     1, false, null );
        PanelDialog fillTransparencyDlg = new PanelDialog( fillTransparencyPanel, true );
        fillTransparencyDlg.setLocation( classesTable.getLocationOnScreen() );
        fillTransparencyDlg.setVisible( true );
        if ( fillTransparencyDlg.clickedOk && fillTransparencyPanel.getValue() instanceof IntegerRange ) {
            getModel().getThematicGrouping().setFillTransparency( (IntegerRange) fillTransparencyPanel.getValue() );
            getModel().update( FILLTRANSPARENCY, true );
        }
    }

    private void openFontColorDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );
        ColorClassificationPanel fontColorPanel = new ColorClassificationPanel(
                                                                                assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions(),
                                                                                getModel().getThematicGrouping().getLineColor(),
                                                                                get( "$MD11717" ),
                                                                                SldValues.getDefaultLineColor(), true,
                                                                                propertyNames );
        PanelDialog fontColorDlg = new PanelDialog( fontColorPanel, true );
        fontColorDlg.setLocation( classesTable.getLocationOnScreen() );
        fontColorDlg.setVisible( true );
        if ( fontColorDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontColor( fontColorPanel.getValue() );
            getModel().update( COLUMNTYPE.FONTCOLOR, true );
        }
    }

    private void openFontFamilyDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );
        Object fontFamily = getModel().getThematicGrouping().getFontFamily();
        FontFamilyClassificationPanel qualifiedNamePanel = new FontFamilyClassificationPanel( fontFamily,
                                                                                              get( "$MD11676" ),
                                                                                              propertyNames );
        PanelDialog fontFamilyDlg = new PanelDialog( qualifiedNamePanel, true );
        fontFamilyDlg.setLocation( classesTable.getLocationOnScreen() );
        fontFamilyDlg.setVisible( true );
        if ( fontFamilyDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontFamily( qualifiedNamePanel.getValue() );
            getModel().update( COLUMNTYPE.FONTFAMILY, true );
        }

    }

    private void openFontWeightDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );
        Object fontWeight = getModel().getThematicGrouping().getFontWeight();
        FontWeightClassificationPanel qualifiedNamePanel = new FontWeightClassificationPanel( fontWeight,
                                                                                              get( "$MD11686" ),
                                                                                              propertyNames );
        PanelDialog fontWeightDlg = new PanelDialog( qualifiedNamePanel, true );
        fontWeightDlg.setLocation( classesTable.getLocationOnScreen() );
        fontWeightDlg.setVisible( true );
        if ( fontWeightDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontWeight( qualifiedNamePanel.getValue() );
            getModel().update( COLUMNTYPE.FONTWEIGHT, true );
        }

    }

    private void openFontStyleDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.VARCHAR );
        Object fontStyle = getModel().getThematicGrouping().getFontStyle();
        FontStyleClassificationPanel qualifiedNamePanel = new FontStyleClassificationPanel( fontStyle,
                                                                                            get( "$MD11685" ),
                                                                                            propertyNames );
        PanelDialog fontStyleDlg = new PanelDialog( qualifiedNamePanel, true );
        fontStyleDlg.setLocation( classesTable.getLocationOnScreen() );
        fontStyleDlg.setVisible( true );
        if ( fontStyleDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontStyle( qualifiedNamePanel.getValue() );
            getModel().update( COLUMNTYPE.FONTSTYLE, true );
        }

    }

    private void openFontSizeDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );
        Object fontSize = getModel().getThematicGrouping().getFontSize();
        FontSizeClassificationPanel qualifiedNamePanel = new FontSizeClassificationPanel( fontSize, get( "$MD11687" ),
                                                                                          propertyNames );
        PanelDialog fontsizeDlg = new PanelDialog( qualifiedNamePanel, true );
        fontsizeDlg.setLocation( classesTable.getLocationOnScreen() );
        fontsizeDlg.setVisible( true );
        if ( fontsizeDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontSize( qualifiedNamePanel.getValue() );
            getModel().update( COLUMNTYPE.FONTSIZE, true );
        }

    }

    private void openFontTransparencyDlg() {
        List<QualifiedName> propertyNames = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                                 Types.DOUBLE,
                                                                                                 Types.FLOAT,
                                                                                                 Types.BIGINT,
                                                                                                 Types.SMALLINT );
        int opAsInt = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        TransparencyClassificationPanel fontTransparencyPanel = new TransparencyClassificationPanel(
                                                                                                     getModel().getThematicGrouping().getFontTransparency(),
                                                                                                     get( "$MD10769" ),
                                                                                                     opAsInt, 0, 100,
                                                                                                     1, true,
                                                                                                     propertyNames );

        PanelDialog lineTransparencyDlg = new PanelDialog( fontTransparencyPanel, true );
        lineTransparencyDlg.setLocation( classesTable.getLocationOnScreen() );
        lineTransparencyDlg.setVisible( true );
        if ( lineTransparencyDlg.clickedOk ) {
            getModel().getThematicGrouping().setFontTransparency( fontTransparencyPanel.getValue() );
            getModel().update( FONTTRANSPARENCY, true );
        }
    }

    private void openLineTransparencyDlg() {
        int opAsInt = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        TransparencyClassificationPanel lineTransparencyPanel = new TransparencyClassificationPanel(
                                                                                                     getModel().getThematicGrouping().getLineTransparency(),
                                                                                                     get( "$MD10769" ),
                                                                                                     opAsInt, 0, 100,
                                                                                                     1, false, null );

        PanelDialog lineTransparencyDlg = new PanelDialog( lineTransparencyPanel, true );
        lineTransparencyDlg.setLocation( classesTable.getLocationOnScreen() );
        lineTransparencyDlg.setVisible( true );
        if ( lineTransparencyDlg.clickedOk && lineTransparencyPanel.getValue() instanceof IntegerRange ) {
            getModel().getThematicGrouping().setLineTransparency( (IntegerRange) lineTransparencyPanel.getValue() );
            getModel().update( LINETRANSPARENCY, true );
        }
    }

    private void openLineWidthDlg() {
        DoubleValueClassificationPanel lineWidthPanel = new DoubleValueClassificationPanel(
                                                                                            getModel().getThematicGrouping().getLineWidth(),
                                                                                            get( "$MD10770" ),
                                                                                            SldValues.getDefaultLineWidth(),
                                                                                            0, 100, 0.5 );

        PanelDialog lineWidthDlg = new PanelDialog( lineWidthPanel, true );
        lineWidthDlg.setLocation( classesTable.getLocationOnScreen() );
        lineWidthDlg.setVisible( true );
        if ( lineWidthDlg.clickedOk ) {
            getModel().getThematicGrouping().setLineWidth( lineWidthPanel.getValue() );
            getModel().update( LINEWIDTH, true );
        }
    }

    private void openLineStyleDlg() {
        JPanel p = new JPanel() {
            private static final long serialVersionUID = 1349382316700756075L;

            @Override
            public String toString() {
                return get( "$MD10801" );
            }
        };

        JComboBox lineStyleCB = new JComboBox();
        lineStyleCB.setRenderer( new DashArrayRenderer( 200, 22 ) );
        for ( DashArray da : SldValues.getDashArrays() ) {
            lineStyleCB.addItem( da );
        }
        p.add( lineStyleCB );
        p.setBorder( BorderFactory.createEmptyBorder( 20, 20, 18, 20 ) );
        PanelDialog lineStyleDlg = new PanelDialog( p, true );
        lineStyleDlg.setLocation( classesTable.getLocationOnScreen() );
        lineStyleDlg.setVisible( true );
        if ( lineStyleDlg.clickedOk ) {
            getModel().getThematicGrouping().setLineStyle( (DashArray) lineStyleCB.getSelectedItem() );
            getModel().update( LINESTYLE, true );
        }
    }

    private void openSizeDlg() {
        DoubleValueClassificationPanel sizePanel = new DoubleValueClassificationPanel(
                                                                                       getModel().getThematicGrouping().getSize(),
                                                                                       get( "$MD10910" ),
                                                                                       SldValues.getDefaultSize(), 1.0,
                                                                                       100.0, 1.0 );

        PanelDialog sizeDlg = new PanelDialog( sizePanel, true );
        sizeDlg.setLocation( classesTable.getLocationOnScreen() );
        sizeDlg.setVisible( true );
        if ( sizeDlg.clickedOk ) {
            getModel().getThematicGrouping().setSize( sizePanel.getValue() );
            getModel().update( SIZE, true );
        }
    }

    private void openSymbolDlg() {
        SymbolPanel symbolPanel = new SymbolPanel( assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions() ) {
            private static final long serialVersionUID = 3740598135642487460L;

            @Override
            public String toString() {
                return get( "$MD10908" );
            }
        };
        symbolPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        PanelDialog symbolDlg = new PanelDialog( symbolPanel, true );
        symbolDlg.setLocation( classesTable.getLocationOnScreen() );
        symbolDlg.setVisible( true );
        if ( symbolDlg.clickedOk ) {
            getModel().getThematicGrouping().setSymbol( symbolPanel.getValue() );
            getModel().update( SYMBOL, true );
        }
    }

    private void openLineCapDlg() {
        JPanel lineCapPanel = new JPanel() {

            private static final long serialVersionUID = -5659436333391914113L;

            @Override
            public String toString() {
                return get( "$MD10935" );
            }
        };

        JComboBox lineCapCB = new JComboBox();
        lineCapCB.setRenderer( new SldPropertyRenderer() );
        for ( SldProperty lc : SldValues.getLineCaps() ) {
            lineCapCB.addItem( lc );
        }
        lineCapPanel.add( lineCapCB );
        lineCapPanel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 18, 20 ) );
        PanelDialog lineCapDlg = new PanelDialog( lineCapPanel, true );
        lineCapDlg.setLocation( classesTable.getLocationOnScreen() );
        lineCapDlg.setVisible( true );
        if ( lineCapDlg.clickedOk ) {
            getModel().getThematicGrouping().setLineCap( (SldProperty) lineCapCB.getSelectedItem() );
            getModel().update( LINECAP, true );
        }

    }

    public void itemPerformed( ItemPerformedEvent event ) {
        if ( event.getSource() == fillColorPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( FILLCOLOR, true );
                break;
            case ATTRIBUTE:
                openFillColorDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFillColor( getModel().getThematicGrouping().getFillColor() );
                getModel().update( FILLCOLOR, true );
                break;
            }
        } else if ( event.getSource() == lineColorPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( LINECOLOR, true );
                break;
            case ATTRIBUTE:
                openLineColorDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setLineColor( getModel().getThematicGrouping().getLineColor() );
                getModel().update( LINECOLOR, true );
                break;
            }
        } else if ( event.getSource() == fillTransparencyPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( FILLTRANSPARENCY, true );
                break;
            case ATTRIBUTE:
                openFillTransparencyDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFillTransparency(
                                                                      getModel().getThematicGrouping().getFillTransparency() );
                getModel().update( FILLTRANSPARENCY, true );
                break;
            }
        } else if ( event.getSource() == lineTransparencyPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( LINETRANSPARENCY, true );
                break;
            case ATTRIBUTE:
                openLineTransparencyDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setLineTransparency(
                                                                      getModel().getThematicGrouping().getLineTransparency() );
                getModel().update( LINETRANSPARENCY, true );
                break;
            }
        } else if ( event.getSource() == lineWidthPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( LINEWIDTH, true );
                break;
            case ATTRIBUTE:
                openLineWidthDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setLineWidth( getModel().getThematicGrouping().getLineWidth() );
                getModel().update( LINEWIDTH, true );
                break;
            }
        } else if ( event.getSource() == lineStylePopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openLineStyleDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setLineStyle( getModel().getThematicGrouping().getLineStyle() );
                getModel().update( LINESTYLE, true );
                break;
            }
        } else if ( event.getSource() == valuePopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( VALUE, true );
                break;
            }
        } else if ( event.getSource() == sizePopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( SIZE, true );
                break;
            case ATTRIBUTE:
                openSizeDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setSize( getModel().getThematicGrouping().getSize() );
                getModel().update( SIZE, true );
                break;
            }
        } else if ( event.getSource() == symbolPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openSymbolDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setSymbol( getModel().getThematicGrouping().getSymbol() );
                getModel().update( SYMBOL, true );
                break;
            }
        } else if ( event.getSource() == lineCapPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openLineCapDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setLineCap( getModel().getThematicGrouping().getLineCap() );
                getModel().update( LINECAP, true );
                break;
            }
        } else if ( event.getSource() == fontColorPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openFontColorDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontColor( getModel().getThematicGrouping().getFontColor() );
                getModel().update( COLUMNTYPE.FONTCOLOR, true );
                break;
            }
        } else if ( event.getSource() == fontFamilyPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openFontFamilyDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontFamily( getModel().getThematicGrouping().getFontFamily() );
                getModel().update( COLUMNTYPE.FONTFAMILY, true );
                break;
            }
        } else if ( event.getSource() == fontWeightPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openFontWeightDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontWeight( getModel().getThematicGrouping().getFontWeight() );
                getModel().update( COLUMNTYPE.FONTWEIGHT, true );
                break;
            }
        } else if ( event.getSource() == fontStylePopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openFontStyleDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontStyle( getModel().getThematicGrouping().getFontStyle() );
                getModel().update( COLUMNTYPE.FONTSTYLE, true );
                break;
            }
        } else if ( event.getSource() == fontSizePopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openFontSizeDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontSize( getModel().getThematicGrouping().getFontSize() );
                getModel().update( COLUMNTYPE.FONTSIZE, true );
                break;
            }

        } else if ( event.getSource() == fontTransparencyPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( FONTTRANSPARENCY, true );
                break;
            case ATTRIBUTE:
                openFontTransparencyDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setFontTransparency(
                                                                      getModel().getThematicGrouping().getFontTransparency() );
                getModel().update( FONTTRANSPARENCY, true );
                break;
            }
        } else if ( event.getSource() == haloRadiusPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openHaloRadiusDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setHaloRadius( getModel().getThematicGrouping().getHaloRadius() );
                getModel().update( COLUMNTYPE.HALORADIUS, true );
                break;
            }

        } else if ( event.getSource() == haloColorPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openHaloColorDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setHaloColor( getModel().getThematicGrouping().getHaloColor() );
                getModel().update( COLUMNTYPE.HALOCOLOR, true );
                break;
            }

        } else if ( event.getSource() == anchorPointPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openAnchorPointDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setAnchorPoint( getModel().getThematicGrouping().getAnchorPoint() );
                getModel().update( COLUMNTYPE.ANCHORPOINT, true );
                break;
            }

        } else if ( event.getSource() == displacementPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openDisplacementDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setDisplacement( getModel().getThematicGrouping().getDisplacement() );
                getModel().update( COLUMNTYPE.DISPLACEMENT, true );
                break;
            }
        } else if ( event.getSource() == rotationPopupMenu ) {
            switch ( event.getItem() ) {
            case ATTRIBUTE:
                openRotationDlg();
                break;
            case RESET:
                getModel().getThematicGrouping().setRotation( getModel().getThematicGrouping().getRotation() );
                getModel().update( COLUMNTYPE.ROTATION, true );
                break;
            }
        } else if ( event.getSource() == countPopupMenu ) {
            switch ( event.getItem() ) {
            case SORT:
                getModel().sort( COUNT, true );
                break;
            }
        }
    }

    class DefaultColumnHeaderPopUpMenu extends JPopupMenu implements ActionListener {

        private static final long serialVersionUID = 1277364607047268819L;

        private ItemPerformedListener listener;

        private JMenuItem itemSort = new JMenuItem( "Sort" );

        private JMenuItem itemAttribute = new JMenuItem( "Fill Color" );

        private JMenuItem itemReset = new JMenuItem( "Reset Fill Color" );

        public DefaultColumnHeaderPopUpMenu( ItemPerformedListener listener, String sortTitle ) {
            this.listener = listener;
            initMenu( sortTitle, null, null );
        }

        public DefaultColumnHeaderPopUpMenu( ItemPerformedListener listener, String sortTitle, String attributeTitle,
                                             String resetTitle ) {
            this.listener = listener;
            initMenu( sortTitle, attributeTitle, resetTitle );
        }

        private void initMenu( String sortTitle, String attributeTitle, String resetTitle ) {
            itemSort = new JMenuItem( sortTitle );
            itemAttribute = new JMenuItem( attributeTitle );
            itemReset = new JMenuItem( resetTitle );
            itemSort.addActionListener( this );
            itemAttribute.addActionListener( this );
            itemReset.addActionListener( this );

            if ( sortTitle != null ) {
                add( itemSort );
            }
            if ( sortTitle != null && ( attributeTitle != null || resetTitle != null ) ) {
                addSeparator();
            }
            if ( attributeTitle != null ) {
                add( itemAttribute );
            }
            if ( resetTitle != null ) {
                add( itemReset );
            }
        }

        public void actionPerformed( ActionEvent e ) {
            ITEM item = ITEM.SORT;
            if ( e.getSource() == itemReset ) {
                item = ITEM.RESET;
            } else if ( e.getSource() == itemAttribute ) {
                item = ITEM.ATTRIBUTE;
            }
            listener.itemPerformed( new ItemPerformedEvent( this, item ) );
        }

        public void setSortItemEnabled( boolean enabled ) {
            itemSort.setEnabled( enabled );
        }
    }

}
