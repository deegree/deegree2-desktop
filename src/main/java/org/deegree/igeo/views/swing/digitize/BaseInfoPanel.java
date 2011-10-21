package org.deegree.igeo.views.swing.digitize;

import java.awt.Font;

import javax.swing.JLabel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.model.feature.Feature;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class BaseInfoPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 4770835014512044512L;

    private static final ILogger LOG = LoggerFactory.getLogger( BaseInfoPanel.class );

    private JLabel layerNameLabel;

    private JLabel namespaceLabel;

    private JLabel nameLabel;

    /**
     * 
     * @param layerName
     * @param featureType
     */
    public BaseInfoPanel( String layerName, Feature feature ) {
        super();
        initGUI( layerName, feature );
    }

    private void initGUI( String layerName, Feature feature ) {
        try {
            this.setPreferredSize( new java.awt.Dimension( 400, 86 ) );
            this.setLayout( null );
            Font font = new Font( "ARIAL", Font.PLAIN, 11 );

            JLabel jLabel1 = new JLabel();
            jLabel1.setFont( font );
            this.add( jLabel1 );
            jLabel1.setText( Messages.getMessage( getLocale(), "$MD10259" ) );
            jLabel1.setBounds( 6, 6, 80, 14 );
            jLabel1.setForeground( new java.awt.Color( 255, 0, 0 ) );

            layerNameLabel = new JLabel();
            layerNameLabel.setFont( font );
            this.add( layerNameLabel );
            layerNameLabel.setText( layerName );
            layerNameLabel.setBounds( 104, 6, 229, 14 );

            JLabel jLabel2 = new JLabel();
            jLabel2.setFont( font );
            this.add( jLabel2 );
            jLabel2.setText( Messages.getMessage( getLocale(), "$MD10260" ) );
            jLabel2.setBounds( 6, 26, 90, 14 );
            jLabel2.setForeground( new java.awt.Color( 255, 0, 0 ) );

            JLabel jLabel3 = new JLabel();
            jLabel3.setFont( font );
            this.add( jLabel3 );
            jLabel3.setText( Messages.getMessage( getLocale(), "$MD10261" ) );
            jLabel3.setBounds( 26, 46, 78, 14 );

            nameLabel = new JLabel();
            nameLabel.setFont( font );
            this.add( nameLabel );
            nameLabel.setText( feature.getFeatureType().getName().getLocalName() );
            nameLabel.setBounds( 123, 46, 257, 14 );

            JLabel jLabel4 = new JLabel();
            jLabel4.setFont( font );
            this.add( jLabel4 );
            jLabel4.setText( Messages.getMessage( getLocale(), "$MD10262" ) );
            jLabel4.setBounds( 26, 66, 82, 14 );

            namespaceLabel = new JLabel();
            namespaceLabel.setFont( font );
            this.add( namespaceLabel );
            namespaceLabel.setText( feature.getFeatureType().getName().getNamespace().toASCIIString() );
            namespaceLabel.setBounds( 120, 66, 249, 14 );

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * sets a new feature to display
     * 
     * @param layerName
     * @param feature
     */
    void setFeature( Layer layer, Feature feature ) {
        layerNameLabel.setText( layer.getTitle() );
        nameLabel.setText( feature.getFeatureType().getName().getLocalName() );
        namespaceLabel.setText( feature.getFeatureType().getName().getNamespace().toASCIIString() );
    }

}
