/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.diagram.figures.connections;

import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.swt.SWT;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IInfluenceRelationship;



/**
 * Influence Connection Figure class
 * 
 * @author Phillip Beauvoir
 */
public class InfluenceConnectionFigure extends AbstractArchimateConnectionFigure {
	
    /**
     * @return Decoration to use on Target Node
     */
    public static RotatableDecoration createFigureTargetDecoration() {
        return new PolylineDecoration();
    }
    
    public InfluenceConnectionFigure() {
    }

    @Override
    protected void setFigureProperties() {
        setTargetDecoration(createFigureTargetDecoration()); 
        setLineStyle(SWT.LINE_CUSTOM); // We have to explitly set this otherwise dashes/dots don't show
        setLineDash(getLineDashes(1.0));
    }
    
    @Override
    protected void setConnectionText() {
        super.setConnectionText();
        
        // Show Strength after Name if we don't show it already by means of the text expression
        if(getModelConnection().getFeatures().getBoolean(IDiagramModelConnection.FEATURE_NAME_VISIBLE, true)) {
            String text = getConnectionLabel().getText();
            String strength = ((IInfluenceRelationship)getModelConnection().getArchimateRelationship()).getStrength();
            if(StringUtils.isSet(strength) && !text.contains(strength)) {
                text += " " + strength; //$NON-NLS-1$
                getConnectionLabel().setText(text);
            }
        }
    }
    
    @Override
    public void handleZoomChanged(double newZoomValue) {
        setLineDash(getLineDashes(newZoomValue));
    }
    
    private float[] getLineDashes(double zoomLevel) {
        return new float[] { (float)(6 * zoomLevel), (float)(3 * zoomLevel) }; 
    }
}
