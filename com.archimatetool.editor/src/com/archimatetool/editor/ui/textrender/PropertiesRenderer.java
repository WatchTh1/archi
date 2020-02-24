/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui.textrender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.archimatetool.model.IArchimateModelObject;
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
        if(object instanceof IProperties) {
            text = renderPropertyValue((IProperties)object, text);
            text = renderPropertiesList((IProperties)object, text);
            text = renderPropertiesValues((IProperties)object, text);
        }
        
        return text;
    }
    
    private String renderPropertyValue(IProperties object, String text) {
        // Get Property Value from its key
        Matcher matcher = PROPERTY_VALUE.matcher(text);
        
        while(matcher.find()) {
            String key = matcher.group(1);
            String propertyValue = getPropertyValue(object, key);
            text = text.replace(matcher.group(), propertyValue);
        }
        
        return text;
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