package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelperArgument;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import gnu.trove.THashMap;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

public class DefaultViewHelpersProvider implements ViewHelperProvider {
    @NotNull
    @Override
    public Map<String, ViewHelper> provideForNamespace(@NotNull Project project, @NotNull String namespace) {
        if (!namespace.equals("TYPO3/Fluid/ViewHelpers") && !namespace.isEmpty()) {

            return new THashMap<>();
        }

        String schemaLocation = "/schemas/fluid/7.6.xsd";
        String schema = readSchema(schemaLocation);

        XmlFile xmlLanguage = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText(XMLLanguage.INSTANCE, schema);

        ViewHelperSchemaRecursiveElementVisitor visitor = new ViewHelperSchemaRecursiveElementVisitor();
        visitor.visitXmlFile(xmlLanguage);

        return visitor.viewHelpers;
    }

    private String readSchema(String schemaLocation) {
        InputStream resourceAsStream = DefaultViewHelpersProvider.class.getResourceAsStream(schemaLocation);
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(resourceAsStream, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    private class ViewHelperSchemaRecursiveElementVisitor extends XmlRecursiveElementVisitor {
        Map<String, ViewHelper> viewHelpers = new THashMap<>();

        @Override
        public void visitXmlTag(XmlTag tag) {
            if (!tag.getName().equals("xsd:element")) {
                super.visitXmlTag(tag);

                return;
            }

            XmlAttribute nameAttribute = tag.getAttribute("name");
            if (nameAttribute == null || nameAttribute.getValue() == null) {
                super.visitXmlTag(tag);

                return;
            }

            ViewHelper viewHelper = new ViewHelper(nameAttribute.getValue());
            viewHelper.setDocumentation(extractDocumentation(tag));

            XmlTag complexType = tag.findFirstSubTag("xsd:complexType");
            if (complexType != null) {
                XmlTag[] attributeTags = complexType.findSubTags("xsd:attribute");
                for (XmlTag attributeTag : attributeTags) {
                    String argumentName = attributeTag.getAttributeValue("name");
                    if (argumentName == null) {
                        continue;
                    }

                    ViewHelperArgument argument = new ViewHelperArgument(argumentName);

                    argument.setDocumentation(extractDocumentation(attributeTag));

                    String attributeType = attributeTag.getAttributeValue("php:type");
                    if (attributeType == null) {
                        argument.setType("mixed");
                    } else {
                        argument.setType(attributeType);
                    }

                    String requiredAttribute = attributeTag.getAttributeValue("use");
                    if (requiredAttribute != null && requiredAttribute.equals("required")) {
                        argument.setRequired(true);
                    }

                    viewHelper.addArgument(argumentName, argument);
                }
            }

            viewHelpers.put(nameAttribute.getValue(), viewHelper);

            super.visitXmlTag(tag);
        }

        private @NotNull
        String extractDocumentation(XmlTag attributeTag) {
            StringBuilder attributeDocumentation = new StringBuilder();

            XmlTag attributeAnnotation = attributeTag.findFirstSubTag("xsd:annotation");
            if (attributeAnnotation != null) {
                XmlTag attributeDoc = attributeAnnotation.findFirstSubTag("xsd:documentation");
                if (attributeDoc != null) {
                    for (XmlText textElement : attributeDoc.getValue().getTextElements()) {
                        attributeDocumentation.append(textElement.getValue());
                    }
                }
            }

            return attributeDocumentation.toString();
        }
    }
}
