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

package org.deegree.igeo.style.classification;

import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.ANCHORPOINT;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.DISPLACEMENT;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTCOLOR;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTFAMILY;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTSIZE;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTSTYLE;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTTRANSPARENCY;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.FONTWEIGHT;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.HALOCOLOR;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.HALORADIUS;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.LINEWIDTH;
import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.ROTATION;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ExternalGraphic;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.Halo;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointPlacement;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.FillPattern;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.WellKnownMark;
import org.deegree.igeo.style.model.classification.ClassificationTableRow;
import org.deegree.igeo.style.model.classification.IllegalClassificationException;
import org.deegree.igeo.style.model.classification.Intervallable;
import org.deegree.igeo.style.model.classification.Intervallables.DateIntervallable;
import org.deegree.igeo.style.model.classification.Intervallables.DoubleIntervallable;
import org.deegree.igeo.style.model.classification.Intervallables.StringIntervallable;
import org.deegree.igeo.style.model.classification.ThematicGroupingInformation;
import org.deegree.igeo.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE;
import org.deegree.igeo.style.model.classification.ValueRange;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.igeo.views.swing.style.component.classification.AbstractClassificationPanel.SYMBOLIZERTYPE;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.ExpressionDefines;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;

/**
 * <code>ClassificationFromSld</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class ClassificationFromSld {

    private static final ILogger LOG = LoggerFactory.getLogger( ClassificationFromSld.class );

    private static final String IllegalPTMsg = "PropertyType is not supported!";

    private static final String IllegalPNMsg = "PropertyNames are not all the same!";

    private static final String IllegalMiscMsg = "Operation differs - must be all PROPERTYISEQUALTO, or not!";

    private static enum ISUNIQUE {
        UNKNOWN, TRUE, FALSE
    };

    /**
     * creates an unique values classification of the given rules, which property names has type VARCHAR
     * 
     * @param rules
     * @param propertyType
     * @return
     * @throws IllegalClassificationException
     * @throws FilterEvaluationException
     */
    public static ThematicGroupingInformation<String> createStringClassification( List<Rule> rules,
                                                                                  List<Intervallable<String>> values,
                                                                                  int propertyType, Settings settings )
                            throws IllegalClassificationException, FilterEvaluationException {
        List<ClassificationTableRow<String>> classification = new ArrayList<ClassificationTableRow<String>>();

        for ( Rule rule : rules ) {
            ValueRange<String> vr = null;
            if ( rule.getFilter() instanceof ComplexFilter ) {
                Operation filterOperation = cleanFilter( (ComplexFilter) rule.getFilter() );
                if ( filterOperation.getOperatorId() == OperationDefines.PROPERTYISEQUALTO ) {
                    if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                        PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                        Expression e1 = compOp.getFirstExpression();
                        Expression e2 = compOp.getSecondExpression();
                        Literal literal = null;
                        if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                            literal = (Literal) e2;
                        } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                            literal = (Literal) e1;
                        }
                        if ( literal != null ) {
                            vr = getStringValueRange( values, literal );
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                } else {
                    throw new IllegalClassificationException( "FOr Strings, only UNIQUE classification is supported!" );
                }
            }
            if ( vr != null ) {
                ClassificationTableRow<String> row = new ClassificationTableRow<String>( vr );
                setRowStyles( row, rule, settings );
                classification.add( row );

            } else {
                throw new IllegalClassificationException( "Could not create list of value ranges" );
            }
        }

        GROUPINGTYPE gt = null;
        ClassificationCalculator<String> classCalculator = new ClassificationCalculator<String>();

        for ( ValueRange<String> origClass : classCalculator.calculateQualityClassification( values ) ) {
            boolean isInOrig = false;
            for ( ClassificationTableRow<String> classificationTableRow : classification ) {
                if ( origClass.equals( classificationTableRow.getValue() ) ) {
                    isInOrig = true;
                    break;
                }
            }
            if ( !isInOrig ) {
                gt = GROUPINGTYPE.MANUAL;
                break;
            }
        }

        for ( ValueRange<String> origClass : classCalculator.calculateUniqueValues( values ) ) {
            boolean isInOrig = false;
            for ( ClassificationTableRow<String> classificationTableRow : classification ) {
                if ( origClass.equals( classificationTableRow.getValue() ) ) {
                    isInOrig = true;
                    break;
                }
            }
            if ( !isInOrig ) {
                gt = GROUPINGTYPE.MANUAL;
                break;
            }
        }

        if ( gt == null ) {
            gt = GROUPINGTYPE.UNIQUE;
        }

        return new ThematicGroupingInformation<String>( GROUPINGTYPE.UNIQUE, classification );
    }

    public static boolean isTypeCorrect( List<Rule> rules, SYMBOLIZERTYPE type ) {
        for ( Rule rule : rules ) {
            if ( rule.getSymbolizers() != null && rule.getSymbolizers().length > 0 ) {
                Symbolizer s = rule.getSymbolizers()[0];
                if ( SYMBOLIZERTYPE.POINT.equals( type ) && !( s instanceof PointSymbolizer ) ) {
                    return false;
                } else if ( SYMBOLIZERTYPE.POLYGON.equals( type ) && !( s instanceof PolygonSymbolizer ) ) {
                    return false;
                } else if ( SYMBOLIZERTYPE.LINE.equals( type ) && !( s instanceof LineSymbolizer ) ) {
                    return false;
                } else if ( SYMBOLIZERTYPE.LABEL.equals( type ) && !( s instanceof TextSymbolizer ) ) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * creates a classification of the given rules, which property names has type DOUBLE or INTEGER
     * 
     * @param rules
     * @param values
     * @param propertyType
     * @param decimalPattern
     * @param settings
     * @return
     * @throws IllegalClassificationException
     * @throws FilterEvaluationException
     */
    public static ThematicGroupingInformation<Double> createDoubleClassification( List<Rule> rules,
                                                                                  List<Intervallable<Double>> values,
                                                                                  int propertyType,
                                                                                  String decimalPattern,
                                                                                  Settings settings )
                            throws IllegalClassificationException, FilterEvaluationException {
        List<ClassificationTableRow<Double>> classification = new ArrayList<ClassificationTableRow<Double>>();

        ISUNIQUE isUnique = ISUNIQUE.UNKNOWN;
        boolean foundFirst = false;
        boolean foundLast = false;
        for ( Rule rule : rules ) {
            ValueRange<Double> vr = null;
            if ( rule.getFilter() instanceof ComplexFilter ) {
                Operation filterOperation = cleanFilter( (ComplexFilter) rule.getFilter() );
                switch ( filterOperation.getOperatorId() ) {
                case OperationDefines.AND:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE ) {
                        if ( filterOperation instanceof LogicalOperation ) {
                            LogicalOperation lo = (LogicalOperation) filterOperation;
                            List<Operation> arguments = lo.getArguments();
                            if ( arguments.size() > 1 ) {
                                Intervallable<Double> int1;
                                // first argument
                                switch ( arguments.get( 0 ).getOperatorId() ) {
                                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                                case OperationDefines.PROPERTYISLESSTHAN:
                                    int1 = createDoubleIntervallable( arguments.get( 0 ), decimalPattern );
                                    break;
                                default:
                                    throw new IllegalClassificationException(
                                                                              "First operation of the AND operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo), but is "
                                                                                                      + ExpressionDefines.getNameById( arguments.get( 0 ).getOperatorId() ) );
                                }
                                Intervallable<Double> int2;
                                // second argument
                                switch ( arguments.get( 1 ).getOperatorId() ) {
                                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                                case OperationDefines.PROPERTYISLESSTHAN:
                                    int2 = createDoubleIntervallable( arguments.get( 1 ), decimalPattern );
                                    break;
                                default:
                                    throw new IllegalClassificationException(
                                                                              "Second operation of the AND operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo), but is "
                                                                                                      + ExpressionDefines.getNameById( arguments.get( 1 ).getOperatorId() ) );
                                }
                                if ( int1 != null && int2 != null ) {
                                    vr = new ValueRange<Double>( int1, int2, 0 );
                                    updateCountDouble( values, vr );
                                }
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISEQUALTO:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.TRUE;
                    }
                    if ( isUnique == ISUNIQUE.TRUE ) {
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDoubleValueRange( values, literal, literal, decimalPattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE && !foundLast ) {
                        foundLast = true;
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDoubleValueRange( values, literal, null, decimalPattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISLESSTHAN:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE && !foundFirst ) {
                        foundFirst = true;
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDoubleValueRange( values, null, literal, decimalPattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                default:
                    throw new IllegalClassificationException(
                                                              " First operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo, And), but is "
                                                                                      + ExpressionDefines.getNameById( filterOperation.getOperatorId() ) );
                }
            }
            if ( vr != null ) {
                ClassificationTableRow<Double> row = new ClassificationTableRow<Double>( vr );
                setRowStyles( row, rule, settings );
                classification.add( row );

            } else {
                throw new IllegalClassificationException( "Could not create list of value ranges" );
            }
        }
        GROUPINGTYPE gt = GROUPINGTYPE.MANUAL;
        ClassificationCalculator<Double> classCalculator = new ClassificationCalculator<Double>();
        if ( isDoubleClassificationEqual( classification,
                                          classCalculator.calculateEqualInterval( values, classification.size() ) ) ) {
            gt = GROUPINGTYPE.EQUAL;
        } else if ( isDoubleClassificationEqual( classification,
                                                 classCalculator.calculateQuantileClassification( values,
                                                                                                  classification.size() ) ) ) {
            gt = GROUPINGTYPE.QUANTILE;
        } else if ( isDoubleClassificationEqual( classification,
                                                 classCalculator.calculateQualityClassification( values ) ) ) {
            gt = GROUPINGTYPE.QUALITY;

        } else if ( isDoubleClassificationEqual( classification, classCalculator.calculateUniqueValues( values ) ) ) {
            gt = GROUPINGTYPE.UNIQUE;
        }
        return new ThematicGroupingInformation<Double>( gt, classification );
    }

    private static boolean isDoubleClassificationEqual( List<ClassificationTableRow<Double>> classification,
                                                        List<ValueRange<Double>> origClassification ) {
        for ( ValueRange<Double> origClass : origClassification ) {
            boolean isInOrig = false;
            for ( ClassificationTableRow<Double> classificationTableRow : classification ) {
                if ( origClass.equals( classificationTableRow.getValue() ) ) {
                    isInOrig = true;
                }
            }
            if ( !isInOrig ) {
                return false;
            }
        }
        return true;
    }

    /**
     * creates a classification of the given rules, which property names has type DATE
     * 
     * @param rules
     * @param propertyType
     * @param datePattern
     * @return
     * @throws IllegalClassificationException
     * @throws FilterEvaluationException
     */
    public static ThematicGroupingInformation<Date> createDateClassification( List<Rule> rules,
                                                                              List<Intervallable<Date>> values,
                                                                              int propertyType, String datePattern,
                                                                              Settings settings )
                            throws IllegalClassificationException, FilterEvaluationException {
        List<ClassificationTableRow<Date>> classification = new ArrayList<ClassificationTableRow<Date>>();

        ISUNIQUE isUnique = ISUNIQUE.UNKNOWN;
        boolean foundFirst = false;
        boolean foundLast = false;
        for ( Rule rule : rules ) {
            ValueRange<Date> vr = null;
            if ( rule.getFilter() instanceof ComplexFilter ) {
                Operation filterOperation = cleanFilter( (ComplexFilter) rule.getFilter() );
                switch ( filterOperation.getOperatorId() ) {
                case OperationDefines.AND:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE ) {
                        if ( filterOperation instanceof LogicalOperation ) {
                            LogicalOperation lo = (LogicalOperation) filterOperation;
                            List<Operation> arguments = lo.getArguments();
                            if ( arguments.size() > 1 ) {

                                Intervallable<Date> int1;
                                // first argument
                                switch ( arguments.get( 0 ).getOperatorId() ) {
                                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                                case OperationDefines.PROPERTYISLESSTHAN:
                                    int1 = createDateIntervallable( arguments.get( 0 ), datePattern );
                                    break;
                                default:
                                    throw new IllegalClassificationException(
                                                                              "First operation of the AND operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo), but is "
                                                                                                      + ExpressionDefines.getNameById( arguments.get( 0 ).getOperatorId() ) );
                                }
                                Intervallable<Date> int2;
                                // second argument
                                switch ( arguments.get( 1 ).getOperatorId() ) {
                                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                                case OperationDefines.PROPERTYISLESSTHAN:
                                    int2 = createDateIntervallable( arguments.get( 1 ), datePattern );
                                    break;
                                default:
                                    throw new IllegalClassificationException(
                                                                              "Second operation of the AND operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo), but is "
                                                                                                      + ExpressionDefines.getNameById( arguments.get( 1 ).getOperatorId() ) );
                                }
                                if ( int1 != null && int2 != null ) {
                                    vr = new ValueRange<Date>( int1, int2, 0 );
                                    updateCountDate( values, vr );
                                }
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISEQUALTO:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.TRUE;
                    }
                    if ( isUnique == ISUNIQUE.TRUE ) {
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDateValueRange( values, literal, literal, datePattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE && !foundLast ) {
                        foundLast = true;
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDateValueRange( values, literal, null, datePattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                case OperationDefines.PROPERTYISLESSTHAN:
                    if ( isUnique == ISUNIQUE.UNKNOWN ) {
                        isUnique = ISUNIQUE.FALSE;
                    }
                    if ( isUnique == ISUNIQUE.FALSE && !foundFirst ) {
                        foundFirst = true;
                        if ( filterOperation instanceof PropertyIsCOMPOperation ) {
                            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) filterOperation;
                            Expression e1 = compOp.getFirstExpression();
                            Expression e2 = compOp.getSecondExpression();
                            Literal literal = null;
                            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e2;
                            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                                literal = (Literal) e1;
                            }
                            if ( literal != null ) {
                                vr = getDateValueRange( values, null, literal, datePattern );
                            }
                        }
                    } else {
                        throw new IllegalClassificationException( IllegalMiscMsg );
                    }
                    break;
                default:
                    throw new IllegalClassificationException(
                                                              " First operation is not supported (must be one of PropertyIsLessThan, PropertyIsGreaterThanOrEqualTo, And), but is "
                                                                                      + ExpressionDefines.getNameById( filterOperation.getOperatorId() ) );
                }
            }
            if ( vr != null ) {
                ClassificationTableRow<Date> row = new ClassificationTableRow<Date>( vr );
                setRowStyles( row, rule, settings );
                classification.add( row );

            } else {
                throw new IllegalClassificationException( "Could not create list of value ranges" );
            }
        }
        GROUPINGTYPE gt = GROUPINGTYPE.MANUAL;
        ClassificationCalculator<Date> classCalculator = new ClassificationCalculator<Date>();
        if ( isDateClassificationEqual( classification,
                                        classCalculator.calculateEqualInterval( values, classification.size() ) ) ) {
            gt = GROUPINGTYPE.EQUAL;
        } else if ( isDateClassificationEqual( classification,
                                               classCalculator.calculateQuantileClassification( values,
                                                                                                classification.size() ) ) ) {
            gt = GROUPINGTYPE.QUANTILE;
        } else if ( isDateClassificationEqual( classification, classCalculator.calculateQualityClassification( values ) ) ) {
            gt = GROUPINGTYPE.QUALITY;
        } else if ( isDateClassificationEqual( classification, classCalculator.calculateUniqueValues( values ) ) ) {
            gt = GROUPINGTYPE.UNIQUE;
        }

        return new ThematicGroupingInformation<Date>( gt, classification );
    }

    private static boolean isDateClassificationEqual( List<ClassificationTableRow<Date>> classification,
                                                      List<ValueRange<Date>> origClassification ) {
        for ( ValueRange<Date> origClass : origClassification ) {
            boolean isInOrig = false;
            for ( ClassificationTableRow<Date> classificationTableRow : classification ) {
                if ( origClass.equals( classificationTableRow.getValue() ) ) {
                    isInOrig = true;
                }
            }
            if ( !isInOrig ) {
                return false;
            }
        }
        return true;
    }

    private static void setRowStyles( ClassificationTableRow<?> row, Rule rule, Settings settings )
                            throws FilterEvaluationException {
        row.setLabel( rule.getTitle() );
        if ( rule.getSymbolizers()[0] instanceof PolygonSymbolizer ) {
            PolygonSymbolizer ps = (PolygonSymbolizer) rule.getSymbolizers()[0];
            Fill fill = ps.getFill();
            Stroke s = ps.getStroke();

            row.setFillColor( fill.getFill( null ) );
            if ( fill.getGraphicFill() != null && fill.getGraphicFill().getGraphic() != null
                 && fill.getGraphicFill().getGraphic().getMarksAndExtGraphics() != null ) {
                Object[] marksAndExtGrapics = fill.getGraphicFill().getGraphic().getMarksAndExtGraphics();
                if ( marksAndExtGrapics.length > 0 && marksAndExtGrapics[0] instanceof ExternalGraphic ) {
                    ExternalGraphic eg = (ExternalGraphic) marksAndExtGrapics[0];
                    GraphicSymbol gs;
                    if ( SldValues.isFillPattern( eg.getOnlineResource() ) ) {
                        gs = new FillPattern( eg.getOnlineResource().getFile(), eg.getOnlineResource(),
                                              fill.getFill( null ) );
                    } else {
                        gs = new GraphicSymbol( eg.getOnlineResource().getFile(), eg.getOnlineResource() );
                    }
                    gs.setSize( getSize( fill.getGraphicFill().getGraphic() ) );
                    row.setFillColor( gs );
                }
            }

            row.setFillTransparency( SldValues.getOpacityInPercent( fill.getOpacity( null ) ) );
            row.setLineColor( s.getStroke( null ) );
            row.setLineTransparency( SldValues.getOpacityInPercent( s.getOpacity( null ) ) );
            row.setLineWidth( s.getWidth( null ) );
            setDashArray( s, row, settings );
        } else if ( rule.getSymbolizers()[0] instanceof PointSymbolizer ) {
            Graphic g = ( (PointSymbolizer) rule.getSymbolizers()[0] ).getGraphic();
            Object[] marksAndExtGrapics = g.getMarksAndExtGraphics();
            if ( marksAndExtGrapics != null && marksAndExtGrapics.length > 0 ) {
                if ( marksAndExtGrapics[0] instanceof Mark ) {
                    Mark m = (Mark) marksAndExtGrapics[0];
                    WellKnownMark wkm = SldValues.getDefaultWKM();
                    for ( WellKnownMark mark : SldValues.getWellKnownMarks() ) {
                        if ( mark.getSldName().equals( m.getWellKnownName() ) ) {
                            wkm = mark;
                        }
                    }
                    row.setSymbol( wkm );
                    Fill fill = m.getFill();
                    if ( fill != null ) {
                        row.setFillColor( fill.getFill( null ) );
                        row.setFillTransparency( SldValues.getOpacityInPercent( fill.getOpacity( null ) ) );
                    }
                    Stroke stroke = m.getStroke();
                    if ( stroke != null ) {
                        row.setLineColor( stroke.getStroke( null ) );
                    }
                } else if ( marksAndExtGrapics[0] instanceof ExternalGraphic ) {
                    ExternalGraphic eg = (ExternalGraphic) marksAndExtGrapics[0];
                    GraphicSymbol gs = null;
                    try {
                        Map<String, GraphicSymbol> symbols = settings.getGraphicOptions().getSymbolDefinitions();
                        for ( String symbolName : symbols.keySet() ) {
                            GraphicSymbol graphicSymbol = symbols.get( symbolName );
                            if ( graphicSymbol.getUrl().equals( eg.getOnlineResource() ) ) {
                                gs = graphicSymbol;
                                break;
                            }
                        }
                    } catch ( MalformedURLException e ) {
                        LOG.logDebug( "Could not find the symbol with URL " + eg.getOnlineResource() );
                    }
                    if ( gs == null ) {
                        gs = new GraphicSymbol( eg.getOnlineResource().getFile(), eg.getOnlineResource() );
                    }
                    row.setSymbol( gs );
                }
            }
            row.setSize( getSize( g ) );
        } else if ( rule.getSymbolizers()[0] instanceof LineSymbolizer ) {
            Stroke stroke = ( (LineSymbolizer) rule.getSymbolizers()[0] ).getStroke();
            row.setLineColor( stroke.getStroke( null ) );
            row.setLineTransparency( SldValues.getOpacityInPercent( stroke.getOpacity( null ) ) );

            // TODO
            CssParameter strokeWidthParam = (CssParameter) stroke.getCssParameters().get( "stroke-width" );
            if ( strokeWidthParam != null && strokeWidthParam.getValue() != null ) {
                ParameterValueType pvt = (ParameterValueType) strokeWidthParam.getValue();
                PropertyName propertyNameFromPvt = SldCreatorUtils.getPropertyNameFromPvt( pvt );

                if ( propertyNameFromPvt != null ) {
                    row.setValue( LINEWIDTH, propertyNameFromPvt );
                } else {
                    double defaultValue;
                    try {
                        defaultValue = stroke.getWidth( null );
                    } catch ( Exception e ) {
                        defaultValue = SldValues.getDefaultFontSize();
                    }
                    row.setValue( LINEWIDTH, UnitsValue.readFromParameterValueType( pvt, defaultValue ).getValue() );
                }
            }

            setLineCap( stroke, row );
            setDashArray( stroke, row, settings );
        } else if ( rule.getSymbolizers()[0] instanceof TextSymbolizer ) {
            TextSymbolizer ts = (TextSymbolizer) rule.getSymbolizers()[0];
            Font font = ts.getFont();
            if ( font != null ) {
                // font-color
                CssParameter fontColorParam = (CssParameter) font.getCssParameters().get( "font-color" );
                if ( fontColorParam != null && fontColorParam.getValueAsPropertyName() != null ) {
                    row.setValue( FONTCOLOR, fontColorParam.getValueAsPropertyName() );
                } else {
                    row.setValue( FONTCOLOR, font.getColor( null ) );
                }

                // font-family
                CssParameter fontFamilyParam = (CssParameter) font.getCssParameters().get( "font-family" );
                if ( fontFamilyParam != null && fontFamilyParam.getValueAsPropertyName() != null ) {
                    row.setValue( FONTFAMILY, fontFamilyParam.getValueAsPropertyName() );
                } else {
                    row.setValue( FONTFAMILY, font.getFamily( null ) );
                }

                // font-size
                CssParameter fontSizeParam = (CssParameter) font.getCssParameters().get( "font-size" );
                if ( fontSizeParam != null ) {
                    ParameterValueType pvt = (ParameterValueType) fontSizeParam.getValue();
                    PropertyName propertyNameFromPvt = SldCreatorUtils.getPropertyNameFromPvt( pvt );
                    if ( propertyNameFromPvt != null ) {
                        row.setValue( FONTSIZE, propertyNameFromPvt );
                    } else {
                        double defaultValue;
                        try {
                            defaultValue = font.getSize( null );
                        } catch ( Exception e ) {
                            defaultValue = SldValues.getDefaultFontSize();
                        }
                        row.setValue( FONTSIZE, UnitsValue.readFromParameterValueType( pvt, defaultValue ).getValue() );
                    }
                }

                // font-style
                CssParameter fontStyleParam = (CssParameter) font.getCssParameters().get( "font-style" );
                if ( fontStyleParam != null && fontStyleParam.getValueAsPropertyName() != null ) {
                    row.setValue( FONTSTYLE, fontStyleParam.getValueAsPropertyName() );
                } else {
                    row.setValue( FONTSTYLE, SldValues.getFontStyle( font.getStyle( null ) ) );
                }

                // font-weight
                CssParameter fontWeightParam = (CssParameter) font.getCssParameters().get( "font-weight" );
                if ( fontWeightParam != null && fontWeightParam.getValueAsPropertyName() != null ) {
                    row.setValue( FONTWEIGHT, fontWeightParam.getValueAsPropertyName() );
                } else {
                    row.setValue( FONTWEIGHT, SldValues.getFontWeight( font.getWeight( null ) ) );
                }
            }

            if ( ts.getLabelPlacement() != null && ts.getLabelPlacement().getPointPlacement() != null ) {
                PointPlacement pp = ts.getLabelPlacement().getPointPlacement();

                Point2d anchorAsPoint = new Point2d( pp.getAnchorPoint( null ) );
                ParameterValueType[] anchorPoint = pp.getAnchorPoint();
                if ( anchorPoint != null && anchorPoint.length > 1 && anchorPoint[0] != null && anchorPoint[1] != null ) {
                    row.setValue( ANCHORPOINT,
                                  new Pair<PropertyName, PropertyName>( anchorPoint[0].getValueAsPropertyName(),
                                                                        anchorPoint[1].getValueAsPropertyName() ) );
                } else if ( anchorAsPoint != null ) {
                    row.setValue( ANCHORPOINT, anchorAsPoint );
                }

                Point2d displacementAsPoint = null;
                try {
                    displacementAsPoint = new Point2d( pp.getDisplacement( null ) );
                } catch ( Exception e ) {
                    // displacement is not a simple point
                }
                ParameterValueType[] displacement = pp.getDisplacement();
                if ( displacementAsPoint != null ) {
                    row.setValue( DISPLACEMENT, displacementAsPoint );
                } else if ( displacement != null && displacement.length > 1 ) {
                    PropertyName pn1 = SldCreatorUtils.getPropertyNameFromPvt( displacement[0] );
                    PropertyName pn2 = SldCreatorUtils.getPropertyNameFromPvt( displacement[1] );
                    if ( pn1 != null && pn2 != null ) {
                        row.setValue( DISPLACEMENT, new Pair<PropertyName, PropertyName>( pn1, pn2 ) );
                    }
                }

                ParameterValueType rotation = pp.getRotation();
                if ( rotation != null ) {
                    PropertyName pointRotationPropName = pp.getRotationPropertyName();
                    if ( pointRotationPropName != null ) {
                        row.setValue( ROTATION, pointRotationPropName );
                    } else {
                        row.setValue( ROTATION, pp.getRotation( null ) );
                    }
                }
            }
            // halo
            Halo halo = ts.getHalo();
            if ( halo != null ) {
                ParameterValueType haloRadius = halo.getRadius();
                if ( haloRadius != null ) {
                    PropertyName haloRadiusPropName = haloRadius.getValueAsPropertyName();
                    if ( haloRadiusPropName != null ) {
                        row.setValue( HALORADIUS, haloRadiusPropName );
                    } else {
                        row.setValue( HALORADIUS, halo.getRadius( null ) );
                    }
                }
                if ( halo.getFill() != null && halo.getFill().getCssParameters().get( "fill" ) != null ) {
                    CssParameter haloFillColor = (CssParameter) halo.getFill().getCssParameters().get( "fill" );
                    if ( haloFillColor != null && haloFillColor.getValueAsPropertyName() != null ) {
                        row.setValue( HALOCOLOR, haloFillColor.getValueAsPropertyName() );
                    } else {
                        row.setValue( HALOCOLOR, halo.getFill().getFill( null ) );
                    }
                }
            }

            // fill
            Fill fill = ts.getFill();
            if ( fill != null ) {
                CssParameter fillOpacityParam = (CssParameter) fill.getCssParameters().get( "fill-opacity" );
                if ( fillOpacityParam != null && fillOpacityParam.getValueAsPropertyName() != null ) {
                    row.setValue( FONTTRANSPARENCY, fillOpacityParam.getValueAsPropertyName() );
                } else {
                    row.setValue( FONTTRANSPARENCY, SldValues.getOpacityInPercent( fill.getOpacity( null ) ) );
                }
            }

        }

    }

    private static double getSize( Graphic g ) {
        ParameterValueType pvt = g.getSize();
        double defaultValue;
        try {
            defaultValue = g.getSize( null );
        } catch ( Exception e ) {
            defaultValue = SldValues.getDefaultLineWidth();
        }
        UnitsValue sizeFromParameterValueType = UnitsValue.readFromParameterValueType( pvt, defaultValue );
        return sizeFromParameterValueType.getValue();
    }

    private static void setDashArray( Stroke stroke, ClassificationTableRow<?> row, Settings settings )
                            throws FilterEvaluationException {
        float[] daOrig = stroke.getDashArray( null );
        DashArray daToSelect = null;
        for ( DashArray da : SldValues.getDashArrays() ) {
            if ( Arrays.equals( daOrig, da.getDashArray() ) ) {
                daToSelect = da;
            }
        }
        Map<String, DashArray> dashArrays = settings.getGraphicOptions().getDashArrays();
        for ( DashArray da : dashArrays.values() ) {
            if ( Arrays.equals( daOrig, da.getDashArray() ) ) {
                daToSelect = da;
            }
        }
        if ( daToSelect == null && daOrig != null ) {
            StringBuffer sb = new StringBuffer( daOrig.length * 3 );
            for ( int i = 0; i < daOrig.length; i++ ) {
                sb.append( daOrig[i] );
                if ( i != daOrig.length - 1 ) {
                    sb.append( ", " );
                }
            }
            DashArray dashArray = new DashArray( sb.toString(), daOrig );
            settings.getGraphicOptions().addDashArray( sb.toString(), dashArray );
            daToSelect = dashArray;
        }

        if ( daToSelect == null ) {
            daToSelect = SldValues.getDefaultLineStyle();
        }
        row.setLineStyle( daToSelect );
    }

    private static void setLineCap( Stroke stroke, ClassificationTableRow<?> row )
                            throws FilterEvaluationException {
        SldProperty lineCapToSelect = SldValues.getDefaultLineCapAsProperty();
        for ( SldProperty lineCap : SldValues.getLineCaps() ) {
            if ( lineCap.getTypeCode() == stroke.getLineCap( null ) ) {
                lineCapToSelect = lineCap;
            }
        }
        row.setLineCap( lineCapToSelect );
    }

    private static Intervallable<Double> createDoubleIntervallable( Operation op, String decimalPattern ) {
        Intervallable<Double> intervallable = null;
        Literal literal = null;
        if ( op instanceof PropertyIsCOMPOperation ) {
            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) op;
            Expression e1 = compOp.getFirstExpression();
            Expression e2 = compOp.getSecondExpression();
            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                literal = (Literal) e2;
            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                literal = (Literal) e1;
            }
        }
        if ( literal != null ) {
            intervallable = new DoubleIntervallable( Double.parseDouble( literal.getValue() ), decimalPattern );
        }
        return intervallable;
    }

    private static Intervallable<Date> createDateIntervallable( Operation op, String datePattern )
                            throws IllegalClassificationException {
        Intervallable<Date> intervallable = null;
        Literal literal = null;
        if ( op instanceof PropertyIsCOMPOperation ) {
            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) op;
            Expression e1 = compOp.getFirstExpression();
            Expression e2 = compOp.getSecondExpression();
            if ( e2.getExpressionId() == ExpressionDefines.LITERAL ) {
                literal = (Literal) e2;
            } else if ( e1.getExpressionId() == ExpressionDefines.LITERAL ) {
                literal = (Literal) e1;
            }
        }
        if ( literal != null ) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-mm-dd" );
                intervallable = new DateIntervallable( sdf.parse( literal.getValue() ), datePattern );
            } catch ( ParseException e ) {
                throw new IllegalClassificationException( "Could not create a valid date of the given literal value "
                                                          + literal.getValue(), e );
            }
        }
        return intervallable;
    }

    private static ValueRange<Double> getDoubleValueRange( List<Intervallable<Double>> values, Literal literal1,
                                                           Literal literal2, String decimalPattern )
                            throws IllegalClassificationException {
        ValueRange<Double> vr = null;
        String litValue1 = null;
        if ( literal1 != null ) {
            litValue1 = literal1.getValue();
        }
        String litValue2 = null;
        if ( literal2 != null ) {
            litValue2 = literal2.getValue();
        }
        try {
            Intervallable<Double> di1 = null;
            if ( litValue1 != null ) {
                di1 = new DoubleIntervallable( Double.parseDouble( litValue1 ), decimalPattern );
            }
            Intervallable<Double> di2 = null;
            if ( litValue2 != null ) {
                di2 = new DoubleIntervallable( Double.parseDouble( litValue2 ), decimalPattern );
            }
            vr = new ValueRange<Double>( di1, di2, 0 );
            updateCountDouble( values, vr );
        } catch ( Exception e ) {
            throw new IllegalClassificationException( "Could not create a valid value of the given literal values "
                                                      + litValue1 + " and " + litValue2, e );
        }
        return vr;
    }

    private static void updateCountDouble( List<Intervallable<Double>> values, ValueRange<Double> vr ) {
        int count = 0;
        for ( Intervallable<Double> value : values ) {
            if ( vr.isInThisValueRange( value ) ) {
                count++;
            }
        }
        vr.setCount( count );
    }

    private static void updateCountDate( List<Intervallable<Date>> values, ValueRange<Date> vr ) {
        int count = 0;
        for ( Intervallable<Date> value : values ) {
            if ( vr.isInThisValueRange( value ) ) {
                count++;
            }
        }
        vr.setCount( count );
    }

    private static ValueRange<String> getStringValueRange( List<Intervallable<String>> values, Literal literal )
                            throws IllegalClassificationException {
        String litValue = null;
        if ( literal != null ) {
            litValue = literal.getValue();
        }
        int count = 0;
        for ( Intervallable<String> value : values ) {
            if ( value.getValue().equals( litValue ) ) {
                count++;
            }
        }
        return new ValueRange<String>( new StringIntervallable( litValue ), new StringIntervallable( litValue ), count );
    }

    private static ValueRange<Date> getDateValueRange( List<Intervallable<Date>> values, Literal literal1,
                                                       Literal literal2, String datePattern )
                            throws IllegalClassificationException {
        ValueRange<Date> vr = null;
        String litValue1 = null;
        if ( literal1 != null ) {
            litValue1 = literal1.getValue();
        }
        String litValue2 = null;
        if ( literal2 != null ) {
            litValue2 = literal2.getValue();
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-mm-dd" );
            Intervallable<Date> di1 = null;
            if ( litValue1 != null ) {

                di1 = new DateIntervallable( sdf.parse( litValue1 ), datePattern );
            }
            Intervallable<Date> di2 = null;
            if ( litValue2 != null ) {
                di2 = new DateIntervallable( sdf.parse( litValue2 ), datePattern );
            }
            vr = new ValueRange<Date>( di1, di2, 0 );
            updateCountDate( values, vr );
        } catch ( Exception e ) {
            throw new IllegalClassificationException( "Could not create a valid value of the given literal values "
                                                      + litValue1 + " and " + litValue2, e );
        }
        return vr;
    }

    /**
     * detect the property name of the rules (all of them must be same!)
     * 
     * @param rules
     * @return
     * @throws IllegalClassificationException
     */
    public static PropertyName detectPropertyName( List<Rule> rules )
                            throws IllegalClassificationException {
        PropertyName propName = null;
        for ( Rule rule : rules ) {
            if ( rule.getFilter() instanceof ComplexFilter ) {
                Operation filterOperation = cleanFilter( (ComplexFilter) rule.getFilter() );
                switch ( filterOperation.getOperatorId() ) {
                case OperationDefines.AND:
                    if ( filterOperation instanceof LogicalOperation ) {
                        LogicalOperation lo = (LogicalOperation) filterOperation;
                        List<Operation> arguments = lo.getArguments();
                        if ( arguments.size() > 2 ) {
                            // first argument
                            switch ( arguments.get( 0 ).getOperatorId() ) {
                            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                            case OperationDefines.PROPERTYISLESSTHAN:
                                PropertyName pn = evaluatePropertyName( arguments.get( 0 ), propName );
                                if ( pn == null ) {
                                    throw new IllegalClassificationException( IllegalPNMsg );
                                } else {
                                    propName = pn;
                                }
                                break;
                            default:
                                throw new IllegalClassificationException( IllegalPTMsg );
                            }

                            // second argument
                            switch ( arguments.get( 1 ).getOperatorId() ) {
                            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                            case OperationDefines.PROPERTYISLESSTHAN:
                                PropertyName pn = evaluatePropertyName( arguments.get( 1 ), propName );
                                if ( pn == null ) {
                                    throw new IllegalClassificationException( IllegalPNMsg );
                                } else {
                                    propName = pn;
                                }
                                break;
                            default:
                                throw new IllegalClassificationException( IllegalPTMsg );
                            }
                        }
                    }
                    break;
                case OperationDefines.PROPERTYISEQUALTO:
                case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                case OperationDefines.PROPERTYISLESSTHAN:
                    PropertyName pn = evaluatePropertyName( filterOperation, propName );
                    if ( pn == null ) {
                        throw new IllegalClassificationException( IllegalPNMsg );
                    } else {
                        propName = pn;
                    }
                    break;
                default:
                    throw new IllegalClassificationException( IllegalPTMsg );
                }
            }
        }
        return propName;
    }

    /**
     * @param op
     *            the operation to evaluate
     * @param propName
     *            the given propertyName
     * @return the unchanged propertyName or null, if the operation contains property names which are not equal to the
     *         given propertyName
     */
    private static PropertyName evaluatePropertyName( Operation op, PropertyName propName ) {
        if ( op instanceof PropertyIsCOMPOperation ) {
            PropertyIsCOMPOperation compOp = (PropertyIsCOMPOperation) op;
            if ( propName == null && compOp.getFirstExpression().getExpressionId() == ExpressionDefines.PROPERTYNAME ) {
                return (PropertyName) compOp.getFirstExpression();
            } else if ( propName != null
                        && compOp.getFirstExpression().getExpressionId() == ExpressionDefines.PROPERTYNAME
                        && propName.equals( (PropertyName) compOp.getFirstExpression() ) ) {
                return propName;
            }
        }
        return null;
    }

    /**
     * detects the property name of the rules (they must be all the same))
     * 
     * @param propertyName
     * @param ft
     * @return
     * @throws IllegalClassificationException
     */
    public static int detectPropertyType( PropertyName propertyName, FeatureType ft )
                            throws IllegalClassificationException {
        PropertyType[] propertyTypes = ft.getProperties();
        for ( PropertyType pt : propertyTypes ) {
            PropertyPath typeAsPath = PropertyPathFactory.createPropertyPath( pt.getName() );
            if ( equalsPropertyNameWithotNS( propertyName.getValue(), typeAsPath ) ) {
                return pt.getType();
            }
        }
        throw new IllegalClassificationException( "Can not detect PropertyType of the propertyName "
                                                  + propertyName.toString() + " in the featureTyp " + ft.getName() );
    }

    /**
     * Compares two property pathes, does not consider the namespace of the qualified name of each property path step
     * 
     * @param pp1
     * @param pp2
     * @return
     */
    public static boolean equalsPropertyNameWithotNS( PropertyPath pp1, PropertyPath pp2 ) {
        if ( pp1.getSteps() != pp2.getSteps() ) {
            return false;
        }
        for ( int i = 0; i < pp1.getSteps(); i++ ) {
            if ( pp1.getStep( i ) == null || pp2.getStep( i ) == null ) {
                return false;
            }
            if ( pp1.getStep( i ).getPropertyName() == null || pp2.getStep( i ).getPropertyName() == null ) {
                return false;
            }
            if ( pp1.getStep( i ).getPropertyName().getLocalName() == null
                 || pp2.getStep( i ).getPropertyName().getLocalName() == null ) {
                return false;
            }
            if ( !pp1.getStep( i ).getPropertyName().getLocalName().equals( pp2.getStep( i ).getPropertyName().getLocalName() ) ) {
                return false;
            }
        }
        return true;
    }

    // removes PropertyIsInstanceOf filter, defined if geomType is specified
    private static Operation cleanFilter( ComplexFilter filter ) {
        if ( filter.getOperation().getOperatorId() == OperationDefines.AND ) {
            LogicalOperation lo = (LogicalOperation) filter.getOperation();
            Operation op1 = lo.getArguments().get( 0 );
            Operation op2 = lo.getArguments().get( 1 );
            if ( op2.getOperatorId() == OperationDefines.PROPERTYISINSTANCEOF ) {
                return op1;
            } else if ( op1.getOperatorId() == OperationDefines.PROPERTYISINSTANCEOF ) {
                return op2;
            }
        }
        return filter.getOperation();
    }

}
