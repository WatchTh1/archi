/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import java.util.HashSet;
import java.util.Set;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimateModelObject;

/**
 * Render Text for display in Text controls in diagrams
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class TextRenderer {
    
    public static final boolean MULTI_LINE = true;
    
    public static final String FEATURE_NAME = "formatExpression";

    private Set<ITextRenderer> renderers = new HashSet<>();
    
    private static TextRenderer defaultTextRenderer = new TextRenderer();
    
    public static TextRenderer getDefault() {
        return defaultTextRenderer;
    }
    
    private TextRenderer() {
        // Register internal renderers
        registerRenderer(new NameRenderer());
        registerRenderer(new DocumentationRenderer());
        registerRenderer(new PropertiesRenderer());
        
        registerRenderer(new TextContentRenderer());
        registerRenderer(new RelationshipRenderer());
        registerRenderer(new LinkedNoteRenderer());
    }
    
    /**
     * @param object The object that has the format expression string and will be rendered 
     * @return The rendered text, or the empty string "" if no rendering is performed
     */
    public String render(IArchimateModelObject object) {
        // Get the format string from the object's feature
        String result = getFormatExpression(object);
        
        if(!StringUtils.isSet(result)) {
            return "";
        }
        
        // Remove escapement of newline chars
        result = renderNewLines(result);
        
        // Iterate through all registered renderers
        for(ITextRenderer r : renderers) {
            result = r.render(object, result);
        }

        return result;
    }
    
    /**
     * @return the object's text expression or the empty string if not present
     */
    public String getFormatExpression(IArchimateModelObject object) {
        return object.getFeatures().getString(FEATURE_NAME, "");
    }
    
    /**
     * Register a ITextRenderer
     * @param renderer
     */
    public void registerRenderer(ITextRenderer renderer) {
        renderers.add(renderer);
    }
    
    private String renderNewLines(String result) {
        if(MULTI_LINE) {
            result = result.replace("\\n", "");
        }
        else {
            result = result.replace("\\n", "\n");
        }
        
        return result;
    }
}
