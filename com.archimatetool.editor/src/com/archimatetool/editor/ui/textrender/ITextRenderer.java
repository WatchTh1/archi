/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModelArchimateComponent;

/**
 * Interface for a text control renderer
 * 
 * @author Phillip Beauvoir
 */
public interface ITextRenderer {
    
    /**
     * @param object The object whose text should be rendered
     * @param text The text that should be rendered
     * @return The result of the text rendering
     */
    String render(IArchimateModelObject object, String text);
    
    /**
     * Get the referenced object if there is one
     * In this case, an ArchiMate diagram component references an ArchiMate concept
     * @param object The object
     * @return object itslef or the referenced object
     */
    default IArchimateModelObject getReferencedObject(IArchimateModelObject object) {
        return object instanceof IDiagramModelArchimateComponent ?
                ((IDiagramModelArchimateComponent)object).getArchimateConcept() : object;
    }
}