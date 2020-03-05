/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;

import com.archimatetool.editor.ui.FontFactory;
import com.archimatetool.editor.ui.IArchiImages;
import com.archimatetool.editor.utils.StringUtils;

/**
 * Fonts Preference Tab
 * 
 * @author Phillip Beauvoir
 */
public class FontsPreferenceTab implements IPreferenceConstants {

    /**
     * Font information for a control
     */
    private static class FontInfo {
        private String text;
        private String prefsKey;
        private FontData fontData;
        
        FontInfo(String text, String prefsKey) {
            this.text = text;
            this.prefsKey = prefsKey;
        }

        FontInfo(String text, FontData fontData) {
            this.text = text;
            this.fontData = fontData;
        }
        
        void performDefault() {
            this.fontData = getDefaultFontData();
        }
        
        FontData getFontData() {
            if(prefsKey != null && fontData == null) {
                String fontDetails = Preferences.STORE.getString(prefsKey);
                if(StringUtils.isSet(fontDetails)) {
                    fontData = new FontData(fontDetails);
                }
                else {
                    fontData = getDefaultFontData();
                }
            }
            
            return fontData;
        }
        
        FontData getDefaultFontData() {
            return JFaceResources.getDefaultFont().getFontData()[0];
        }

        void performOK() {
            if(prefsKey != null) {
                Preferences.STORE.setValue(prefsKey, getFontData().equals(getDefaultFontData()) ? "" : getFontData().toString()); //$NON-NLS-1$
            }
        }
    }
    
    // Table
    private TableViewer fTableViewer;

    private List<FontInfo> fontInfos = new ArrayList<>();

    private Button fEditFontButton;
    private Button fDefaultFontButton;

    private CLabel fFontPreviewLabel;
    
    public Composite createContents(Composite parent) {
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(2, false));
        
        client.addDisposeListener((e) -> {
            disposeLabelFont();
        });
        
        fTableViewer = new TableViewer(client);
        GridData gd = new GridData(GridData.FILL_BOTH);
        fTableViewer.getControl().setLayoutData(gd);
        
        // Table Double-click listener
        fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = ((IStructuredSelection)event.getSelection());
                if(!selection.isEmpty()) {
                    FontInfo fontInfo = (FontInfo)selection.getFirstElement();
                    FontData fd = openFontDialog(fontInfo);
                    if(fd != null) {
                        fontInfo.fontData = fd;
                        fTableViewer.update(fontInfo, null);
                        updatePreviewLabel(fontInfo);
                    }
                }
            }
        });

        // Table Selection Changed Listener
        fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() { 
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = event.getStructuredSelection();
                fEditFontButton.setEnabled(!selection.isEmpty());
                fDefaultFontButton.setEnabled(!selection.isEmpty());
                if(!selection.isEmpty()) {
                    updatePreviewLabel((FontInfo)selection.getFirstElement());
                }
            }
        });
        
        // Table Content Provider
        fTableViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                return fontInfos.toArray();
            }
        });
        
        // Table Label Provider
        fTableViewer.setLabelProvider(new CellLabelProvider() {
            private float tableHeight = JFaceResources.getDefaultFont().getFontData()[0].height;
            private Map<FontInfo, Font> fontCache = new HashMap<>();
            
            @Override
            public void update(ViewerCell cell) {
                FontInfo fontInfo = (FontInfo)cell.getElement();
                cell.setText(fontInfo.text);
                cell.setImage(IArchiImages.ImageFactory.getImage(IArchiImages.ICON_FONT));
                cell.setFont(getFont(fontInfo));
            }

            private Font getFont(FontInfo fontInfo) {
                Font font = fontCache.get(fontInfo);
                if(font != null) {
                    font.dispose();
                }
                
                FontData fd = new FontData(fontInfo.getFontData().toString());
                fd.height = tableHeight;
                font = new Font(null, fd);
                fontCache.put(fontInfo, font);
                
                return font;
            }
            
            @Override
            public void dispose() {
                for(Entry<FontInfo, Font> entry : fontCache.entrySet()) {
                    entry.getValue().dispose();
                }
                super.dispose();
            }
        });
        
        // Buttons
        Composite buttonClient = new Composite(client, SWT.NULL);
        gd = new GridData(SWT.TOP, SWT.TOP, false, false);
        buttonClient.setLayoutData(gd);
        buttonClient.setLayout(new GridLayout());

        // Edit...
        fEditFontButton = new Button(buttonClient, SWT.PUSH);
        fEditFontButton.setText(Messages.FontsPreferenceTab_2);
        fEditFontButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fEditFontButton.setEnabled(false);
        fEditFontButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = fTableViewer.getStructuredSelection();
                if(!selection.isEmpty()) {
                    FontData fd = openFontDialog((FontInfo)selection.getFirstElement());
                    if(fd != null) {
                        for(Object object : selection.toList()) {
                            ((FontInfo)object).fontData = fd;
                        }
                        updatePreviewLabel((FontInfo)selection.getFirstElement());
                        fTableViewer.refresh();
                    }
                }
            }
        });

        // Default
        fDefaultFontButton = new Button(buttonClient, SWT.PUSH);
        fDefaultFontButton.setText(Messages.FontsPreferenceTab_7);
        fDefaultFontButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fDefaultFontButton.setEnabled(false);
        fDefaultFontButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = fTableViewer.getStructuredSelection();
                if(!selection.isEmpty()) {
                    for(Object object :  selection.toList()) {
                        ((FontInfo)object).performDefault();
                    }
                    updatePreviewLabel((FontInfo)selection.getFirstElement());
                    fTableViewer.refresh();
                }
            }
        });
        
        // Preview
        Group fontPreviewGroup = new Group(client, SWT.NULL);
        fontPreviewGroup.setText(Messages.FontsPreferenceTab_8);
        fontPreviewGroup.setLayout(new GridLayout());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 80;
        gd.horizontalSpan = 2;
        fontPreviewGroup.setLayoutData(gd);
        
        fFontPreviewLabel = new CLabel(fontPreviewGroup, SWT.NONE);
        fFontPreviewLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // View object default font
        fontInfos.add(new FontInfo(Messages.FontsPreferenceTab_1, FontFactory.getDefaultUserViewFontData()) {
            @Override
            void performOK() {
                FontFactory.setDefaultUserViewFont(getFontData());
            }
            
            @Override
            FontData getDefaultFontData() {
                return FontFactory.getDefaultViewOSFontData();
            }
        });
        
        // Multiline text control font
        fontInfos.add(new FontInfo(Messages.FontsPreferenceTab_4, MULTI_LINE_TEXT_FONT));
        
        // Model Tree font
        fontInfos.add(new FontInfo(Messages.FontsPreferenceTab_0, MODEL_TREE_FONT));
        
        // Navigator Tree font
        fontInfos.add(new FontInfo(Messages.FontsPreferenceTab_9, NAVIGATOR_TREE_FONT));

        fTableViewer.setInput(""); //$NON-NLS-1$
        
        return client;
    }
    
    private FontData openFontDialog(FontInfo fontInfo) {
        FontDialog dialog = new FontDialog(fTableViewer.getControl().getShell());
        dialog.setText(Messages.FontsPreferenceTab_3);
        dialog.setFontList(new FontData[] { fontInfo.getFontData() });
        return dialog.open();
    }
    
    private void updatePreviewLabel(FontInfo fontInfo) {
        FontData fd = fontInfo.getFontData();
        
        fFontPreviewLabel.setText(fd.getName() + " " + //$NON-NLS-1$
                fd.getHeight() + " " + //$NON-NLS-1$
                ((fd.getStyle() & SWT.BOLD) == SWT.BOLD ? Messages.FontsPreferenceTab_5 : "") + " " +  //$NON-NLS-1$//$NON-NLS-2$
                ((fd.getStyle() & SWT.ITALIC) == SWT.ITALIC ? Messages.FontsPreferenceTab_6 : ""));  //$NON-NLS-1$
        
        Font font = new Font(null, fd);
        fFontPreviewLabel.setFont(font);
        disposeLabelFont();
        fFontPreviewLabel.setData(font);
        
        fFontPreviewLabel.getParent().getParent().layout();
        fFontPreviewLabel.getParent().getParent().redraw();
    }

    public void performDefaults() {
        for(FontInfo info : fontInfos) {
            info.performDefault();
        }
        
        fTableViewer.refresh();
        
        FontInfo fontInfo = (FontInfo)fTableViewer.getStructuredSelection().getFirstElement();
        if(fontInfo != null) {
            updatePreviewLabel((FontInfo)fTableViewer.getStructuredSelection().getFirstElement());
        }
    }
    
    public void performOK() {
        for(FontInfo info : fontInfos) {
            info.performOK();
        }
    }

    private void disposeLabelFont() {
        Font labelFont = (Font)fFontPreviewLabel.getData();
        if(labelFont != null) {
            labelFont.dispose();
        }
    }
}
