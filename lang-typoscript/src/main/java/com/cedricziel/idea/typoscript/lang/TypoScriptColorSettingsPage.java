package com.cedricziel.idea.typoscript.lang;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import icons.TypoScriptIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class TypoScriptColorSettingsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
      new AttributesDescriptor("Key", TypoScriptSyntaxHighlighter.KEY),
      new AttributesDescriptor("Separator", TypoScriptSyntaxHighlighter.SEPARATOR),
      new AttributesDescriptor("Value", TypoScriptSyntaxHighlighter.VALUE),
  };

  @Nullable
  @Override
  public Icon getIcon() {
    return TypoScriptIcons.FILE;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new TypoScriptSyntaxHighlighter();
  }

  @NotNull
  @Override
  public String getDemoText() {
    return "plugin.tx_form {\n" +
        "    view {\n" +
        "        # Note that this configuration only affects the EXT:Form plugin\n" +
        "        # template (form/Resources/Private/Frontend/Templates/Render.html)\n" +
        "        # The fluid paths for the formelements reside within the YAML settings.\n" +
        "        # @see TYPO3.CMS.Form.<prototypeName>.standard.formElementsDefinition.Form.renderingOptions\n" +
        "        #\n" +
        "        # That means: If you want to override the formelement templates\n" +
        "        # then change them within the YAML settings, not here.\n" +
        "        templateRootPaths.0 = EXT:form/Resources/Private/Frontend/Templates/\n" +
        "        partialRootPaths.0 = EXT:form/Resources/Private/Frontend/Partials/\n" +
        "        layoutRootPaths.0 = EXT:form/Resources/Private/Frontend/Layouts/\n" +
        "    }\n" +
        "\n" +
        "    mvc {\n" +
        "        callDefaultActionIfActionCantBeResolved = 1\n" +
        "    }\n" +
        "\n" +
        "    settings {\n" +
        "        yamlConfigurations {\n" +
        "            10 = EXT:form/Configuration/Yaml/BaseSetup.yaml\n" +
        "            20 = EXT:form/Configuration/Yaml/FormEngineSetup.yaml\n" +
        "        }\n" +
        "    }\n" +
        "}\n" +
        "\n" +
        "# Rendering of content elements\n" +
        "lib.tx_form.contentElementRendering = RECORDS\n" +
        "lib.tx_form.contentElementRendering {\n" +
        "    tables = tt_content\n" +
        "    source.current = 1\n" +
        "    dontCheckPid = 1\n" +
        "}";
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor @NotNull [] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "TypoScript";
  }
}
