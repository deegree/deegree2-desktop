package org.deegree.desktop.views.swing.digitize.construction;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.deegree.desktop.i18n.Messages;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class TangentJoinPanel extends JPanel implements JoinCurveParameter {

    private static final long serialVersionUID = -8522712096338055526L;

    private JCheckBox cbConnectionAsNewCurve;

    private JLabel lbNoOfSegments;

    private JSpinner spNoOfSegments;

    /**
     * 
     */
    TangentJoinPanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 320, 223 ) );
            thisLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
            thisLayout.rowHeights = new int[] { 7, 7, 7, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 152, 7 };
            this.setLayout( thisLayout );
            {
                cbConnectionAsNewCurve = new JCheckBox( Messages.getMessage( getLocale(), "$MD11509" ) );
                this.add( cbConnectionAsNewCurve, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                cbConnectionAsNewCurve.setSelected( true );
            }
            {
                lbNoOfSegments = new JLabel( Messages.getMessage( getLocale(), "$MD11510" ) );
                this.add( lbNoOfSegments, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                spNoOfSegments = new JSpinner( new SpinnerNumberModel( 12, 0, 2000, 1 ) );
                this.add( spNoOfSegments, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 0, 0, 9 ), 0, 0 ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.construction.CurveConnectionParameter#getParameter()
     */
    public Map<CONNECTION_PARAMETER, Object> getParameter() {
        Map<CONNECTION_PARAMETER, Object> map = new HashMap<CONNECTION_PARAMETER, Object>();
        map.put( CONNECTION_PARAMETER.connctionAsNewCurve, cbConnectionAsNewCurve.isSelected() );
        map.put( CONNECTION_PARAMETER.noOfSegments, ( (Number) spNoOfSegments.getValue() ).intValue() );
        return map;
    }
}
