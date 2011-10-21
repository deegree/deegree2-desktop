package org.deegree.igeo.views.swing;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;

/**
 * 
 * This class enhances ButtonGroup with clearing function
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ButtonGroup extends javax.swing.ButtonGroup {

    private static final long serialVersionUID = -767098048339098316L;

    /**
     * current selected button model
     */
    private ButtonModel selectedModel = null;

    @Override
    public void add( AbstractButton b ) {
        if ( b == null ) {
            return;
        }
        buttons.addElement( b );

        if ( b.isSelected() ) {
            if ( selectedModel == null ) {
                selectedModel = b.getModel();
            } else {
                b.setSelected( false );
            }
        }

        b.getModel().setGroup( this );
    }

    @Override
    public void remove( AbstractButton b ) {
        if ( b == null ) {
            return;
        }
        buttons.removeElement( b );
        if ( b.getModel() == selectedModel ) {
            selectedModel = null;
        }
        b.getModel().setGroup( null );
    }

    /**
     * removes selected button model from a {@link ButtonGroup}
     */
    public void removeSelection() {
        if ( selectedModel != null ) {
            ButtonModel old = selectedModel;
            selectedModel = null;
            old.setSelected( false );
        }
    }

    @Override
    public void setSelected( ButtonModel model, boolean b ) {
        if ( b && model != null && model != selectedModel ) {
            ButtonModel old = selectedModel;
            selectedModel = model;
            if ( old != null ) {
                old.setSelected( false );
            }
            model.setSelected( true );
        }
    }

    @Override
    public boolean isSelected( ButtonModel model ) {
        return model == selectedModel;
    }

}
