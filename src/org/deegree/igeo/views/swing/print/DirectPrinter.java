package org.deegree.igeo.views.swing.print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileOutputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.SwingUtilities;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.mapmodel.MapModel;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: admin $
 * 
 * @version $Revision: $, $Date: $
 */
public class DirectPrinter {

    private static final ILogger LOG = LoggerFactory.getLogger( DirectPrinter.class );

    private MapModel mapModel;

    /**
     * 
     * @param mapModel
     */
    public DirectPrinter( MapModel mapModel ) {
        this.mapModel = mapModel;
    }

    /**
     * prints map onto a printer device
     */
    public void print() {
        final String sCrLf = System.getProperty( "line.separator" );
        final String sPrintFile = "PrintFile.ps";
        final String sErrNoPrintService = sCrLf + "Es ist kein passender Print-Service installiert.";

        // Commandline parameter:
        int idxPrintService = -1; // -1 means: no parameter

        // Set DocFlavor and print attributes:
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        final PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        //aset.add( MediaSizeName.ISO_A4 );

        try {

            if ( -2 == idxPrintService ) {
                // Print to Stream (here to PostScript File):
                StreamPrintServiceFactory[] prservFactories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
                                                                                                                           flavor,
                                                                                                                           DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType() );
                if ( null == prservFactories || 0 >= prservFactories.length ) {
                    LOG.logError( sErrNoPrintService );
                    return;
                }
                LOG.logInfo( "Stream-PrintService-Factory:" );
                for ( int i = prservFactories.length - 1; i >= 0; i-- ) {
                    LOG.logInfo( "  " + prservFactories[i] + " (" + prservFactories[i].getOutputFormat() + ")" );
                }
                FileOutputStream fos = new FileOutputStream( sPrintFile );
                StreamPrintService sps = prservFactories[0].getPrintService( fos );
                LOG.logInfo( "Stream-PrintService:" );
                LOG.logInfo( "  " + sps + " (" + sps.getOutputFormat() + ")" );
                DocPrintJob pj = sps.createPrintJob();
                Doc doc = new SimpleDoc( new PrintableMap( mapModel ), flavor, null );
                pj.print( doc, aset );
                fos.close();
                LOG.logInfo(  "Ausgabedatei '" + sPrintFile + "' ist erfolgreich generiert." );
            } else {
                // Print to PrintService (e.g. to Printer):
                PrintService prservDflt = PrintServiceLookup.lookupDefaultPrintService();
                PrintService[] prservices = PrintServiceLookup.lookupPrintServices( flavor, aset );
                if ( null == prservices || 0 >= prservices.length )
                    if ( null != prservDflt ) {
                        LOG.logWarning( "Nur Default-Printer, da lookupPrintServices fehlgeschlagen. " );
                        prservices = new PrintService[] { prservDflt };
                    } else {
                        LOG.logError( sErrNoPrintService );
                        return;
                    }

                if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                    for ( int i = 0; i < prservices.length; i++ ) {
                        LOG.logDebug( "Print-Services:" );
                        LOG.logDebug( "  " + i + ":  " + prservices[i]
                                      + ( ( prservDflt != prservices[i] ) ? "" : " (Default)" ) );
                    }
                }

                final PrinterJob pjob = PrinterJob.getPrinterJob();
                pjob.setPrintService( prservDflt );
                if ( pjob.printDialog() ) {                    
                   // pjob.setPrintable( new PrintableMap( mapModel ), pjob.pageDialog( aset ) );
                    pjob.setPrintable( new PrintableMap( mapModel ) );
                    SwingUtilities.invokeLater( new Runnable() {

                        public void run() {
                            try {
                                pjob.print( aset );
                            } catch ( PrinterException e ) {
                                LOG.logError( e );
                            }
                        }
                    } );
                }
            }

        } catch ( Exception pe ) {
            LOG.logError( pe );
        }
    }

}
