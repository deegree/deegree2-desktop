package org.deegree.igeo.modules;

import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.state.mapstate.MapStateChangedEvent;

public class CopyCoordinatesModule<T> extends DefaultModule<T> implements ChangeListener{

    private static final ILogger LOG = getLogger( CopyCoordinatesModule.class );

    private DefaultMapModule<?> mapModule;
    
    static {
        ActionDescription ad1 = new ActionDescription( "copyCoordinates", "copy coordinates into clip board", null,
                                                       "copy coordinates clip board", ACTIONTYPE.ToggleButton, null,
                                                       null );

        moduleCapabilities = new ModuleCapabilities( ad1 );

    }
    
    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );

        this.mapModule = appContainer.getActiveMapModule();
        if ( this.mapModule == null ) {
            LOG.logError( "no map module found " );
            return;
        }

        this.mapModule.getMapTool().addChangeListener( this );
    }

    /**
     * method assigned to action
     */
    public void copyCoordinates() {
        this.mapModule.getMapTool().setCopyCoordinatesState( );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof MapStateChangedEvent ) {
            this.mapModule.getMapTool().removeChangeListener( this );
            this.mapModule = appContainer.getActiveMapModule();
            this.mapModule.getMapTool().addChangeListener( this );
        }
    }
}
