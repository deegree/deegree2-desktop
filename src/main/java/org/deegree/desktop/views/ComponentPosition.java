//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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
package org.deegree.desktop.views;

import java.util.Locale;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.config.AbsolutePositionType;
import org.deegree.desktop.config.BorderPositionType;
import org.deegree.desktop.config.BorderPositionValueType;
import org.deegree.desktop.config.FooterPositionType;
import org.deegree.desktop.config.GridPositionType;
import org.deegree.desktop.config.HeaderPositionType;
import org.deegree.desktop.config.SplitterPositionType;
import org.deegree.desktop.config.SplitterPositionValueType;
import org.deegree.desktop.config.TabPositionType;
import org.deegree.desktop.config.WindowType;
import org.deegree.desktop.config._ComponentPositionType;

/**
 * 
 * The <code>ComponentPositionAdapter</code> handles the access to the position of the component!
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ComponentPosition extends AbstractComponent {

    public enum SplitterPosition {
        BOTTOM, TOP, LEFT, RIGHT
    };

    public enum BorderPosition {
        CENTER, NORTH, SOUTH, EAST, WEST
    };

    private _ComponentPositionType cp;

    /**
     * 
     * @param identifier
     * @param cp
     */
    public ComponentPosition( _ComponentPositionType cp ) {
        this.cp = cp;
    }

    // SETTERS
    /**
     * 
     * @param width
     *            the width of the component
     * @param height
     *            the height of the componet
     */
    public void setWindowSize( int width, int height ) {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            window.setWidth( width ) ;
            window.setHeight( height  );
        }
    }

    /**
     * @param left
     *            left position (x) of the component
     * @param top
     *            top position (y) of the component
     */
    public void setWindowPosition( int left, int top ) {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            window.setTop( top ) ;
            window.setLeft( left  );
        }
    }

    /**
     * 
     * @param type
     * @see SplitterPositionType for available constants
     */
    public void setSplitterPosition( SplitterPosition type ) {
        if ( this.cp instanceof SplitterPositionType ) {
            SplitterPositionType spt = (SplitterPositionType) this.cp;
            switch ( type ) {
            case BOTTOM:
                spt.setSplitterPosition( SplitterPositionValueType.BOTTOM );
                break;
            case TOP:
                spt.setSplitterPosition( SplitterPositionValueType.TOP );
                break;
            case LEFT:
                spt.setSplitterPosition( SplitterPositionValueType.LEFT );
                break;
            case RIGHT:
                spt.setSplitterPosition( SplitterPositionValueType.RIGHT );
                break;
            default:
                throw new RuntimeException( Messages.getMessage( Locale.getDefault(), "$DG10057", type ) );
            }
        }
    }

    /**
     * 
     * @param type
     * @see BorderPositionType for available constants
     */
    public void setBorderPosition( BorderPosition type ) {
        if ( this.cp instanceof BorderPositionType ) {
            BorderPositionType bpt = (BorderPositionType) this.cp;            
            switch ( type ) {
            case CENTER:
                bpt.setBorderPositionValue( BorderPositionValueType.CENTER );
                break;
            case NORTH:
                bpt.setBorderPositionValue( BorderPositionValueType.NORTH );
                break;
            case SOUTH:
                bpt.setBorderPositionValue(  BorderPositionValueType.SOUTH );
                break;
            case WEST:
                bpt.setBorderPositionValue( BorderPositionValueType.WEST );
                break;
            case EAST:
                bpt.setBorderPositionValue( BorderPositionValueType.EAST );
                break;
            default:
                // TODO
                throw new RuntimeException( "enter a text" );
            }
        }
    }

    /**
     * 
     * @param index
     */
    public void setFooterPosition( int index ) {
        if ( this.cp instanceof FooterPositionType ) {
            ( (FooterPositionType) this.cp ).setFooterPosition( index );
        }
    }

    /**
     * 
     * @return target footer index position
     */
    public int getFooterPosition() {
        if ( this.cp instanceof FooterPositionType ) {
            return ( (FooterPositionType) this.cp ).getFooterPosition();
        }
        return -1;
    }

    /**
     * 
     * @param index
     */
    public void setHeaderPosition( int index ) {
        if ( this.cp instanceof HeaderPositionType ) {
            ( (HeaderPositionType) this.cp ).setHeaderPosition( index );
        }
    }

    /**
     * 
     * @return target Header index position
     */
    public int getHeaderPosition() {
        if ( this.cp instanceof HeaderPositionType ) {
            return ( (HeaderPositionType) this.cp ).getHeaderPosition();
        }
        return -1;
    }

    /**
     * 
     * @param column
     *            desired column index starting at 0
     * @param row
     *            desired row index starting at 0
     */
    public void setGrid( int column, int row ) {
        if ( this.cp instanceof GridPositionType ) {
            GridPositionType gp = (GridPositionType) this.cp;
            gp.setCol( column );
            gp.setRow( row );
        }

    }

    /**
     * 
     * @param tabPosition
     *            desired tab sheet position starting at 0
     */
    public void setTabPosition( int tabPosition ) {
        if ( this.cp instanceof TabPositionType ) {
            ( (TabPositionType) this.cp ).setTabPosition( tabPosition );
        }
    }

    // GETTERS

    /**
     * @return window with if absolute position is defined; otherwise -1 will be returned
     */
    public int getWindowWidth() {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            return window.getWidth();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return window height if absolute position is defined; otherwise -1 will be returned
     */
    public int getWindowHeight() {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            return window.getHeight();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return window left position if absolute position is defined; otherwise -1 will be returned
     */
    public int getWindowLeft() {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            return window.getLeft();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return window right position if absolute position is defined; otherwise -1 will be returned
     */
    public int getWindowTop() {
        if ( this.cp instanceof AbsolutePositionType ) {
            WindowType window = ( (AbsolutePositionType) this.cp ).getWindow();
            return window.getTop();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return splitter position if defined; otherwise -1 will be returned
     */
    public SplitterPosition getSplitterPosition() {
        if ( this.cp instanceof SplitterPositionType ) {
            String tmp = ( (SplitterPositionType) this.cp ).getSplitterPosition().value();
            if ( "bottom".equalsIgnoreCase( tmp ) ) {
                return SplitterPosition.BOTTOM;
            } else if ( "top".equalsIgnoreCase( tmp ) ) {
                return SplitterPosition.TOP;
            } else if ( "left".equalsIgnoreCase( tmp ) ) {
                return SplitterPosition.LEFT;
            } else if ( "right".equalsIgnoreCase( tmp ) ) {
                return SplitterPosition.RIGHT;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 
     * @return border position if defined; otherwise -1 will be returned
     */
    public BorderPosition getBorderPosition() {
        if ( this.cp instanceof BorderPositionType ) {
            String tmp = ( (BorderPositionType) this.cp ).getBorderPositionValue().value();
            if ( "center".equalsIgnoreCase( tmp ) ) {
                return BorderPosition.CENTER;
            } else if ( "north".equalsIgnoreCase( tmp ) ) {
                return BorderPosition.NORTH;
            } else if ( "south".equalsIgnoreCase( tmp ) ) {
                return BorderPosition.SOUTH;
            } else if ( "east".equalsIgnoreCase( tmp ) ) {
                return BorderPosition.EAST;
            } else if ( "west".equalsIgnoreCase( tmp ) ) {
                return BorderPosition.WEST;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 
     * @return target grid column if grid position is defined; othewise -1 will be returned
     */
    public int getGridColumn() {
        if ( this.cp instanceof GridPositionType ) {
            return ( (GridPositionType) this.cp ).getCol();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return target grid row if grid position is defined; othewise -1 will be returned
     */
    public int getGridRow() {
        if ( this.cp instanceof GridPositionType ) {
            return ( (GridPositionType) this.cp ).getRow();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return target tab index if tab position is defined; othewise -1 will be returned
     */
    public int getTabPosition() {
        if ( this.cp instanceof TabPositionType ) {
            return ( (TabPositionType) this.cp ).getTabPosition();
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return true if Absolute Position is defined
     */
    public boolean hasWindow() {
        return this.cp instanceof AbsolutePositionType;
    }
}
