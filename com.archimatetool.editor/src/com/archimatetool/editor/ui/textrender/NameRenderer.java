/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import com.archimatetool.model.IArchimateModelObject;

/**
 * Name renderer
 * 
 * @author Phillip Beauvoir
 */
public class NameRenderer implements ITextRenderer {
    
    public static final String NAME = "${name}"; //$NON-NLS-1$

    @Override
    public String render(IArchimateModelObject object, String text) {
        return text.replace(NAME, object.getName());
    }
}