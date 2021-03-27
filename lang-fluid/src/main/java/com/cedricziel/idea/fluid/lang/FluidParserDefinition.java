package com.cedricziel.idea.fluid.lang;

import com.cedricziel.idea.fluid.lang.lexer.FluidLexer;
import com.cedricziel.idea.fluid.lang.parser.FluidParser;
import com.cedricziel.idea.fluid.lang.psi.FluidFile;
import com.cedricziel.idea.fluid.lang.psi.FluidTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class FluidParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(FluidTypes.TEMPLATE_COMMENT);

    public static final IFileElementType FILE = new IFileElementType(FluidLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new FluidLexer();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return FluidTokens.tsSTRINGS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new FluidParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new FluidFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return FluidTypes.Factory.createElement(node);
    }
}
