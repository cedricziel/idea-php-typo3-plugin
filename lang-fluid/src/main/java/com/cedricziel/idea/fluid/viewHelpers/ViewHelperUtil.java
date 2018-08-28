package com.cedricziel.idea.fluid.viewHelpers;

import com.cedricziel.idea.fluid.extensionPoints.NamespaceProvider;
import com.cedricziel.idea.fluid.extensionPoints.ViewHelperProvider;
import com.cedricziel.idea.fluid.tagMode.FluidNamespace;
import com.cedricziel.idea.fluid.viewHelpers.model.ViewHelper;
import com.cedricziel.idea.fluid.viewHelpers.xml.ViewHelperXmlElementDescriptor;
import com.intellij.codeInsight.completion.XmlTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.util.XmlUtil;
import gnu.trove.THashMap;
import icons.FluidIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ViewHelperUtil {
    @NotNull
    public static Collection<LookupElement> tagNameVariantsLookupElementsForCurrentPosition(@NotNull Project project, @NotNull PsiElement tag, String prefix) {
        List<LookupElement> elements = new ArrayList<>();

        findAllViewHelpersInContextByName(project, tag)
            .forEach((name, vh) -> elements.add(
                LookupElementBuilder
                    .create((prefix == null || prefix.isEmpty()) ? name : vh.name)
                    .withIcon(FluidIcons.TYPO3)
                    .withTypeText(vh.getDocumentation())
                    .withPresentableText(name)
                    .withInsertHandler(XmlTagInsertHandler.INSTANCE))
            );

        return elements;
    }

    @Nullable
    public static XmlElementDescriptor xmlElementDescriptorForCurrentTag(@NotNull Project project, @NotNull XmlTag tag) {
        String tagName = tag.getName();

        // Fluid ViewHelpers are always prefixed
        if (tag.getNamespacePrefix().isEmpty()) {
            return null;
        }

        boolean tagDefinedByNamespace = XmlUtil.isTagDefinedByNamespace(tag);
        if (tagDefinedByNamespace) {
            // return null;
        }

        ViewHelper viewHelperInContext = findViewHelperInContext(project, tag, tagName);
        if (viewHelperInContext == null) {
            return null;
        }

        return new ViewHelperXmlElementDescriptor(tagName, tag, viewHelperInContext);
    }

    @Nullable
    private static ViewHelper findViewHelperInContext(@NotNull Project project, @NotNull PsiElement psiElement, @NotNull String name) {

        return findAllViewHelpersInContextByName(project, psiElement).get(name);
    }

    @NotNull
    public static Map<String, ViewHelper> findAllViewHelpersInContextByName(@NotNull Project project, @NotNull PsiElement psiElement) {
        Map<String, ViewHelper> elements = new THashMap<>();

        Map<String, String> namespaces = new THashMap<>();
        Map<String, Map<String, ViewHelper>> collectedViewHelpers = new THashMap<>();
        for (NamespaceProvider nsExt : NamespaceProvider.EP_NAME.getExtensions()) {
            Collection<FluidNamespace> fluidNamespaces = nsExt.provideForElement(psiElement);
            for (FluidNamespace ns : fluidNamespaces) {
                if (!namespaces.containsKey(ns.namespace)) {
                    namespaces.put(ns.namespace, ns.prefix);
                }

                if (!collectedViewHelpers.containsKey(ns.namespace)) {
                    collectedViewHelpers.put(ns.namespace, new THashMap<>());
                }

                for (ViewHelperProvider vhExt : ViewHelperProvider.EP_NAME.getExtensions()) {
                    vhExt
                        .provideForNamespace(project, ns.namespace)
                        .forEach((name, vh) -> collectedViewHelpers.get(ns.namespace).put(name, vh));
                }
            }
        }

        collectedViewHelpers.forEach((ns, vhs) -> vhs.forEach((name, vh) -> {
            if (!namespaces.containsKey(ns)) {
                return;
            }

            String presentableName = namespaces.get(ns) + ":" + name;
            elements.put(presentableName, vh);
        }));

        return elements;
    }

    public static @Nullable
    ViewHelper getViewHelperByName(@NotNull PsiElement context, @NotNull String presentableName) {

        Map<String, ViewHelper> allViewHelpersInContextByName = ViewHelperUtil.findAllViewHelpersInContextByName(context.getProject(), context);
        if (allViewHelpersInContextByName.containsKey(presentableName)) {
            return allViewHelpersInContextByName.get(presentableName);
        }

        return null;
    }
}
