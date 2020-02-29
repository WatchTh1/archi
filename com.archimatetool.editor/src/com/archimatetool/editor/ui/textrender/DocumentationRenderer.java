/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDocumentable;

/**
 * Documentation renderer
 * 
 * @author Phillip Beauvoir
 */
public class DocumentationRenderer implements ITextRenderer {
    
    public static final String DOCUMENTATION = "${documentation}"; //$NON-NLS-1$

    @Override
    public String render(IArchimateModelObject object, String text) {
        object = getReferencedObject(object);
        
        if(object instanceof IDocumentable) {
            return text.replace(DOCUMENTATION, ((IDocumentable)object).getDocumentation());
        }
        
        return text;
    }
}