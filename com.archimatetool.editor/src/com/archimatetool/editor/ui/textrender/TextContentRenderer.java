/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.ITextContent;

/**
 * TextContent renderer
 * 
 * @author Phillip Beauvoir
 */
public class TextContentRenderer implements ITextRenderer {
    
    public static final String CONTENT = "${content}"; //$NON-NLS-1$

    @Override
    public String render(IArchimateModelObject object, String text) {
        if(object instanceof ITextContent) {
            return text.replace(CONTENT, ((ITextContent)object).getContent());
        }
        
        return text;
    }
}