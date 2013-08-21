package org.deegree.desktop.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DatasourceCorePanel extends JPanel {

    private static final long serialVersionUID = 2756281625055598313L;

    private JPanel pnName;

    private JPanel pnExtent;

    private JLabel lbMinX;

    private JSpinner spMinX;

    private JSpinner spMax;

    private JLabel lbMax;

    private JSpinner spMin;

    private JLabel lnMinY;

    private JLabel lbMin;

    private JPanel pnScale;

    private JSpinner spMaxY;

    private JLabel lbMaxY;

    private JSpinner spMaxX;

    private JLabel lbMaxX;

    private JSpinner spMinY;

    private JTextField tfName;

    private Datasource datasource;

    public DatasourceCorePanel() {
        initGUI();
    }

    /**
     * 
     * @param datasource
     */
    DatasourceCorePanel( Datasource datasource ) {
        this.datasource = datasource;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 366, 170 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
            thisLayout.rowHeights = new int[] { 43, 69, 52 };
            thisLayout.columnWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
            thisLayout.columnWidths = new int[] { 7, 7, 7, 7 };
            this.setLayout( thisLayout );
            {
                pnName = new JPanel();
                BorderLayout pnNameLayout = new BorderLayout();
                pnName.setLayout( pnNameLayout );
                this.add( pnName, new GridBagConstraints( 0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnName.setBorder( BorderFactory.createTitledBorder( null, Messages.get( "$MD10070" ),
                                                                    TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION ) );
                {
                    tfName = new JTextField( datasource.getName() );
                    pnName.add( tfName, BorderLayout.NORTH );
                }
            }
            {
                pnExtent = new JPanel();
                FormLayout pnExtentLayout = new FormLayout( "40dlu, 55dlu, 49dlu, 56dlu", "19dlu, 18dlu" );
                this.add( pnExtent, new GridBagConstraints( 0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnExtent.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD10069" ) ) );
                pnExtent.setLayout( pnExtentLayout );
                {
                    lbMinX = new JLabel( Messages.get( "$MD10071" ) );
                    pnExtent.add( lbMinX, new CellConstraints( 1, 1, 1, 1, CellConstraints.DEFAULT,
                                                               CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                }
                if ( datasource.getExtent() != null ) {
                    {
                        double d = Math.round( datasource.getExtent().getWidth() / 100 );
                        spMinX = new JSpinner( new SpinnerNumberModel( datasource.getExtent().getMin().getX(), -9E99,
                                                                       9E99, d ) );
                        pnExtent.add( spMinX, new CellConstraints( "2, 1, 1, 1, default, default" ) );
                    }
                    {
                        lnMinY = new JLabel( Messages.get( "$MD10072" ) );
                        pnExtent.add( lnMinY, new CellConstraints( 3, 1, 1, 1, CellConstraints.DEFAULT,
                                                                   CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                    }
                    {
                        double d = Math.round( datasource.getExtent().getHeight() / 100 );
                        spMinY = new JSpinner( new SpinnerNumberModel( datasource.getExtent().getMin().getY(), -9E99,
                                                                       9E99, d ) );
                        pnExtent.add( spMinY, new CellConstraints( "4, 1, 1, 1, default, default" ) );
                    }
                    {
                        lbMaxX = new JLabel( Messages.get( "$MD10073" ) );
                        pnExtent.add( lbMaxX, new CellConstraints( 1, 2, 1, 1, CellConstraints.DEFAULT,
                                                                   CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                    }
                    {
                        double d = Math.round( datasource.getExtent().getWidth() / 100 );
                        spMaxX = new JSpinner( new SpinnerNumberModel( datasource.getExtent().getMax().getX(), -9E99,
                                                                       9E99, d ) );
                        pnExtent.add( spMaxX, new CellConstraints( "2, 2, 1, 1, default, default" ) );
                    }
                    {
                        lbMaxY = new JLabel( Messages.get( "$MD10074" ) );
                        pnExtent.add( lbMaxY, new CellConstraints( 3, 2, 1, 1, CellConstraints.DEFAULT,
                                                                   CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                    }
                    {
                        double d = Math.round( datasource.getExtent().getHeight() / 100 );
                        spMaxY = new JSpinner( new SpinnerNumberModel( datasource.getExtent().getMax().getY(), -9E99,
                                                                       9E99, d ) );
                        pnExtent.add( spMaxY, new CellConstraints( "4, 2, 1, 1, default, default" ) );
                    }
                }
            }
            {
                pnScale = new JPanel();
                FormLayout pnScaleLayout = new FormLayout( "31dlu, 65dlu, 35dlu, 72dlu", "20dlu" );
                this.add( pnScale, new GridBagConstraints( 0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnScale.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD10075" ) ) );
                pnScale.setLayout( pnScaleLayout );
                {
                    lbMin = new JLabel();
                    pnScale.add( lbMin, new CellConstraints( 1, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT,
                                                             new Insets( 0, 10, 0, 0 ) ) );
                    lbMin.setText( Messages.get( "$MD10076" ) );
                }
                {
                    spMin = new JSpinner( new SpinnerNumberModel( datasource.getMinScaleDenominator(), 0, 9E99, 1 ) );
                    pnScale.add( spMin, new CellConstraints( "2, 1, 1, 1, default, default" ) );
                }
                {
                    lbMax = new JLabel();
                    pnScale.add( lbMax, new CellConstraints( 3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT,
                                                             new Insets( 0, 10, 0, 0 ) ) );
                    lbMax.setText( Messages.get( "$MD10077" ) );
                }
                {
                    spMax = new JSpinner(
                                          new SpinnerNumberModel(
                                                                  datasource.getMaxScaleDenominator(),
                                                                  0,
                                                                  9E99,
                                                                  Math.round( datasource.getMaxScaleDenominator() / 100 ) ) );
                    pnScale.add( spMax, new CellConstraints( "4, 1, 1, 1, default, default" ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return min scale denominator read from textfield
     */
    public double getMin() {
        return ( (Number) spMin.getValue() ).doubleValue();
    }

    /**
     * 
     * @return max scale denominator read from textfield
     */
    public double getMax() {
        return ( (Number) spMax.getValue() ).doubleValue();
    }

    /**
     * 
     * @return maximum extent envelope read from text fields
     */
    Envelope getEnvelope() {
        double mnx = ( (Number) spMinX.getValue() ).doubleValue();
        double mny = ( (Number) spMinY.getValue() ).doubleValue();
        double mxx = ( (Number) spMaxX.getValue() ).doubleValue();
        double mxy = ( (Number) spMaxY.getValue() ).doubleValue();
        return GeometryFactory.createEnvelope( mnx, mny, mxx, mxy, datasource.getExtent().getCoordinateSystem() );
    }

    /**
     * 
     * @return data source name
     */
    public String getDSName() {
        return tfName.getText();
    }

}
