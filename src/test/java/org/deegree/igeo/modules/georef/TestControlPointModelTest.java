package org.deegree.igeo.modules.georef;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.deegree.igeo.modules.georef.ControlPointModel.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestControlPointModelTest {

    private File inFile;

    private File saveFile;

    @Before
    public void setup()
                            throws URISyntaxException {
        URL loadUrl = TestControlPointModelTest.class.getResource( "/loadPointsFromFileTest" );
        saveFile = new File( loadUrl.getPath() + "SaveTest" );
        inFile = new File( loadUrl.toURI() );
    }

    @Test
    public void testSavePointsToFile()
                            throws IOException {
        ControlPointModel mdl = new ControlPointModel();
        Point pointToBeSaved = newPoint();

        mdl.getPoints().add( pointToBeSaved );

        mdl.savePointsToFile( saveFile );
        mdl.removeAll();
        mdl.loadPointsFromFile( saveFile );

        Point actualPoint = mdl.getPoints().get( 0 );

        comparePoints( pointToBeSaved, actualPoint );
    }

    @Test
    public void testLoadPointsFromFile()
                            throws IOException {
        ControlPointModel mdl = new ControlPointModel();
        mdl.loadPointsFromFile( inFile );

        Point actualPoint = mdl.getPoints().get( 0 );
        Point expectedPoint = newPoint();

        comparePoints( expectedPoint, actualPoint );
    }

    private static Point newPoint() {
        Point p = new Point();
        p.x0 = 1d;
        p.y0 = 2d;
        p.x1 = 3d;
        p.y1 = 4d;
        return p;
    }

    private static void comparePoints( Point expectedPoint, Point actualPoint ) {
        Assert.assertEquals( expectedPoint.x0, actualPoint.x0, 0.0001 );
        Assert.assertEquals( expectedPoint.y0, actualPoint.y0, 0.0001 );
        Assert.assertEquals( expectedPoint.x1, actualPoint.x1, 0.0001 );
        Assert.assertEquals( expectedPoint.y1, actualPoint.y1, 0.0001 );
    }

}
