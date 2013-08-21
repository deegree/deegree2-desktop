package org.deegree.desktop.views.swing.digitize.construction;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

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
class JoinAsNewCurvePanel extends JPanel implements JoinCurveParameter {

    private static final long serialVersionUID = 5273101949868505630L;

    private JCheckBox cbConnctionAsNewCurve;

    /**
     * 
     */
    JoinAsNewCurvePanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new Dimension( 299, 205 ) );
            this.setLayout( thisLayout );
            {
                cbConnctionAsNewCurve = new JCheckBox();
                this.add( cbConnctionAsNewCurve, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                cbConnctionAsNewCurve.setText( Messages.getMessage( getLocale(), "$MD11613" ) );
                cbConnctionAsNewCurve.setSelected( true );
            }
            thisLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
            thisLayout.rowHeights = new int[] { 7, 7, 7, 7 };
            thisLayout.columnWeights = new double[] { 0.1, 0.1 };
            thisLayout.columnWidths = new int[] { 7, 7 };
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
        map.put( CONNECTION_PARAMETER.connctionAsNewCurve, cbConnctionAsNewCurve.isSelected() );
        return map;
    }

}
