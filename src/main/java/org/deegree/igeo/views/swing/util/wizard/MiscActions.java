//$HeadURL$
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

package org.deegree.igeo.views.swing.util.wizard;

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openInformationDialog;
import static org.deegree.igeo.views.swing.util.GuiUtils.listen;
import static org.deegree.igeo.views.swing.util.GuiUtils.treeListen;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTree;

import org.deegree.igeo.views.swing.util.wizard.Wizard.Action;

/**
 * <code>MiscActions</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MiscActions {

    /**
     * <code>EmptyAction</code> implements all boolean methods with "true".
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class EmptyAction implements Action {
        public void addListener( ActionListener listener ) {
            // no listeners
        }

        public boolean backward() {
            return true;
        }

        public boolean canForward() {
            return true;
        }

        public boolean forward() {
            return true;
        }
    }

    /**
     * @param list
     * @return a new action
     */
    public static Action monitorJListAction( final JList list ) {
        return new EmptyAction() {

            @Override
            public void addListener( ActionListener listener ) {
                list.addListSelectionListener( listen( listener ) );
            }

            @Override
            public boolean canForward() {
                return list.getSelectedValue() != null;
            }
        };
    }

    /**
     * @param box
     * @return a new action
     */
    public static Action monitorJComboBoxAction( final JComboBox box ) {
        return new EmptyAction() {
            @Override
            public void addListener( ActionListener listener ) {
                box.addActionListener( listener );
            }

            @Override
            public boolean canForward() {
                return box.getSelectedItem() != null;
            }
        };
    }

    /**
     * @param tree
     * @return a new action
     */
    public static Action monitorJTreeAction( final JTree tree ) {
        return new EmptyAction() {
            @Override
            public void addListener( ActionListener listener ) {
                tree.addTreeSelectionListener( treeListen( listener ) );
            }

            @Override
            public boolean canForward() {
                return tree.getSelectionPath() != null;
            }
        };
    }

    /**
     * @param parent
     * @param message
     * @return a new action
     */
    public static Action informationAction( final JComponent parent, final String message ) {
        return new EmptyAction() {
            @Override
            public boolean forward() {
                openInformationDialog( "Application", parent, message, get( "$DI10018" ) );
                return true;
            }
        };
    }

    /**
     * @param one
     * @param two
     * @return a new action that combines the arguments
     */
    public static Action chainAction( final Action one, final Action two ) {
        return new Action() {
            public void addListener( ActionListener listener ) {
                one.addListener( listener );
                two.addListener( listener );
            }

            public boolean backward() {
                return one.backward() && two.backward();
            }

            public boolean canForward() {
                return one.canForward() && two.canForward();
            }

            public boolean forward() {
                return one.forward() && two.forward();
            }
        };
    }

}
