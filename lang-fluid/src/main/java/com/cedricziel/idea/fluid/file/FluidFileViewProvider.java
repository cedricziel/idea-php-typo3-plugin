package com.cedricziel.idea.fluid.file;

import com.cedricziel.idea.fluid.lang.FluidLanguage;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

public class FluidFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {

    public static final IElementType FLUID_FRAGMENT = new IElementType("FluidFragmentElementType", FluidLanguage.INSTANCE);
    public static final IElementType TEMPLATE_DATA = new TemplateDataElementType("FluidTextElementType", FluidLanguage.INSTANCE, FluidTypes.TEXT, FLUID_FRAGMENT);

    private final Language myTemplateDataLanguage;
    private THashSet<Language> languages = null;

    public FluidFileViewProvider(PsiManager manager, VirtualFile file, boolean physical) {
        super(manager, file, physical);

        Language dataLang = TemplateDataLanguageMappings.getInstance(manager.getProject()).getMapping(file);
        if (dataLang == null) {
            dataLang = HtmlFileType.INSTANCE.getLanguage();
        }

        if (dataLang instanceof TemplateLanguage) {
            myTemplateDataLanguage = PlainTextLanguage.INSTANCE;
        } else {
            // The substitutor signals, that a files content should be substituted
            Language mySubstitutedLanguage = LanguageSubstitutors.getInstance().substituteLanguage(dataLang, file, manager.getProject());
            if (mySubstitutedLanguage == FluidLanguage.INSTANCE) {
                this.myTemplateDataLanguage = HtmlFileType.INSTANCE.getLanguage();
            } else {
                this.myTemplateDataLanguage = mySubstitutedLanguage;
            }
        }
    }

    public FluidFileViewProvider(PsiManager psiManager, VirtualFile virtualFile, boolean physical, Language myTemplateDataLanguage) {
        super(psiManager, virtualFile, physical);

        this.myTemplateDataLanguage = myTemplateDataLanguage;
    }


    @NotNull
    @Override
    public Language getBaseLanguage() {

        return FluidLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public Language getTemplateDataLanguage() {

        return myTemplateDataLanguage;
    }

    @NotNull
    @Override
    public Set<Language> getLanguages() {

        if (languages == null) {
            languages = new THashSet<>(Arrays.asList(FluidLanguage.INSTANCE, myTemplateDataLanguage));
        }

        return languages;
    }


    @NotNull
    @Override
    protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile file) {

        return new FluidFileViewProvider(getManager(), file, false, myTemplateDataLanguage);
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Language lang) {
        if (lang == myTemplateDataLanguage) {
            PsiFileImpl file = (PsiFileImpl) LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this);
            file.setContentElementType(TEMPLATE_DATA);

            return file;
        } else if (lang == FluidLanguage.INSTANCE) {

            return LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this);
        } else {

            return null;
        }
    }
}
