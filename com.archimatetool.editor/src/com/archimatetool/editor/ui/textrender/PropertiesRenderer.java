/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * Properties renderer
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class PropertiesRenderer implements ITextRenderer {
    
    public static final String PROPERTIES = "${properties}";
    public static final String PROPERTIES_VALUES = "${propertiesvalues}";
    
    private Pattern PROPERTY_VALUE = Pattern.compile("\\$\\{property:([^\\}]+)\\}");

    @Override
    public String render(IArchimateModelObject object, String text) {
        IArchimateModelObject refObject = getReferencedObject(object);
        if(refObject instanceof IProperties) {
            text = renderPropertyValue(object, text);
            text = renderPropertiesList((IProperties)refObject, text);
            text = renderPropertiesValues((IProperties)refObject, text);
        }
        
        return text;
    }
    
    private String renderPropertyValue(IArchimateModelObject object, String text) {
        Matcher matcher = PROPERTY_VALUE.matcher(text);
        
        while(matcher.find()) {
            String key = matcher.group(1);
            
            String propertyValue = "";

            // Get property value from model
            if(key.startsWith("model:")) {
                propertyValue = getReferencedPropertyValue(object.getArchimateModel(), key, "model:");
            }
            // Get property value from parent view
            else if(key.startsWith("view:") && object instanceof IDiagramModelComponent) {
                propertyValue = getReferencedPropertyValue(((IDiagramModelComponent)object).getDiagramModel(), key, "view:");
            }
            // Get property value from this object
            else {
                propertyValue = getPropertyValue((IProperties)getReferencedObject(object), key);
            }
            
            text = text.replace(matcher.group(), propertyValue);
        }
        
        return text;
    }
    
    /**
     * @param refPropertiesObject The Referenced Properties Object, such as the model or parent view
     * @param key The Properties Key
     * @param prefix The prefix reference like "model:" or "view:"
     * @return The referenced property value
     * 
     * Examples:
     * 
     * Get property value from model
     * ${property:model:lang} Get the value of the property key "lang" in the model
     * 
     * Get property from parent view
     * ${property:model:view} Get the value of the property key "lang" in the view
     */
    private String getReferencedPropertyValue(IProperties refPropertiesObject, String key, String prefix) {
        // Strip the prefix and get the actual key
        key = key.substring(prefix.length());
        // The properties object is the referenced properties object
        return getPropertyValue(refPropertiesObject, key);
    }
    
    // List all properties like key: value
    private String renderPropertiesList(IProperties object, String text) {
        return text.replace(PROPERTIES, getAllProperties(object, true));
    }
    
    // List all properties' values
    private String renderPropertiesValues(IProperties object, String text) {
        return text.replace(PROPERTIES_VALUES, getAllProperties(object, false));
    }

    private String getAllProperties(IProperties object, boolean full) {
        String s = "";
        
        for(int i = 0; i < object.getProperties().size(); i++) {
            IProperty property = object.getProperties().get(i);
            
            if(full) {
                s += property.getKey() + ": ";
            }

            s += property.getValue();
            
            if(i < object.getProperties().size() - 1) {
                s += "\n";
            }
        }
        
        return s;
    }
    
    private String getPropertyValue(IProperties object, String key) {
        for(IProperty property : object.getProperties()) {
            if(property.getKey().equals(key)) {
                return property.getValue();
            }
        }
        
        return "";
    }
}