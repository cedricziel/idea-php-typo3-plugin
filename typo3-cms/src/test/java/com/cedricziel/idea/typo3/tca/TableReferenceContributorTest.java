package com.cedricziel.idea.typo3.tca;

import com.cedricziel.idea.typo3.AbstractTestCase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpFileType;

public class TableReferenceContributorTest extends AbstractTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/cedricziel/idea/typo3/tca";
    }

    public void testCanContributeTableReferencesOnTCADefinitions() {
        PsiFile psiFile = myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "return [\n" +
            "    'ctrl' => [\n" +
            "        'title' => 'LLL:EXT:blog_example/Resources/Private/Language/locallang_db.xml:tx_blogexample_domain_model_post',\n" +
            "        'label' => 'title',\n" +
            "        'label_alt' => 'author',\n" +
            "        'label_alt_force' => true,\n" +
            "        'tstamp'   => 'tstamp',\n" +
            "        'crdate'   => 'crdate',\n" +
            "        'versioningWS' => true,\n" +
            "        'origUid' => 't3_origuid',\n" +
            "        'languageField' => 'sys_language_uid',\n" +
            "        'transOrigPointerField' => 'l18n_parent',\n" +
            "        'transOrigDiffSourceField' => 'l18n_diffsource',\n" +
            "        'delete'   => 'deleted',\n" +
            "        'sortby' => 'sorting',\n" +
            "        'enablecolumns'  => [\n" +
            "            'disabled' => 'hidden'\n" +
            "        ],\n" +
            "        'iconfile' => 'EXT:blog_example/Resources/Public/Icons/icon_tx_blogexample_domain_model_post.gif'\n" +
            "    ],\n" +
            "    'interface' => [\n" +
            "        'showRecordFieldList' => 'title, date, author',\n" +
            "        'maxDBListItems' => 100,\n" +
            "        'maxSingleDBListItems' => 500\n" +
            "    ],\n" +
            "    'types' => [\n" +
            "        '1' => ['showitem' => 'sys_language_uid, hidden, blog, title, date, author, content, tags, comments, related_posts']\n" +
            "    ],\n" +
            "    'columns' => [\n" +
            "        'sys_language_uid' => [\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:core/Resources/Private/Language/locallang_general.xlf:LGL.language',\n" +
            "            'config' => [\n" +
            "                'type' => 'select',\n" +
            "                'renderType' => 'selectSingle',\n" +
            "                'foreign_table' => 'sys_language',\n" +
            "                'foreign_table_where' => 'ORDER BY sys_language.title',\n" +
            "                'items' => [\n" +
            "                    ['LLL:EXT:core/Resources/Private/Language/locallang_general.xlf:LGL.allLanguages', -1],\n" +
            "                    ['LLL:EXT:core/Resources/Private/Language/locallang_general.xlf:LGL.default_value', 0]\n" +
            "                ],\n" +
            "                'default' => 0\n" +
            "            ]\n" +
            "        ],\n" +
            "        'l18n_parent' => [\n" +
            "            'displayCond' => 'FIELD:sys_language_uid:>:0',\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:core/Resources/Private/Language/locallang_general.xlf:LGL.l18n_parent',\n" +
            "            'config' => [\n" +
            "                'type' => 'select',\n" +
            "                'renderType' => 'selectSingle',\n" +
            "                'items' => [\n" +
            "                    ['', 0],\n" +
            "                ],\n" +
            "                'foreign_table' => 'tx_blogexample_domain_model_post',\n" +
            "                'foreign_table_where' => 'AND tx_blogexample_domain_model_post.uid=###REC_FIELD_l18n_parent### AND tx_blogexample_domain_model_post.sys_language_uid IN (-1,0)',\n" +
            "            ]\n" +
            "        ],\n" +
            "        'l18n_diffsource' => [\n" +
            "            'config'=>[\n" +
            "                'type' => 'passthrough',\n" +
            "                'default' => ''\n" +
            "            ]\n" +
            "        ],\n" +
            "        'hidden' => [\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:core/Resources/Private/Language/locallang_general.xlf:LGL.hidden',\n" +
            "            'config' => [\n" +
            "                'type' => 'check'\n" +
            "            ]\n" +
            "        ],\n" +
            "        'blog' => [\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:blog_example/Resources/Private/Language/locallang_db.xml:tx_blogexample_domain_model_post.blog',\n" +
            "            'config' => [\n" +
            "                'type' => 'select',\n" +
            "                'renderType' => 'selectSingle',\n" +
            "                'foreign_table' => 'tx_blogexample_domain_model_blog',\n" +
            "                'maxitems' => 1,\n" +
            "            ]\n" +
            "        ],\n" +
            "        'title' => [\n" +
            "            'label' => 'LLL:EXT:blog_example/Resources/Private/Language/locallang_db.xml:tx_blogexample_domain_model_post.title',\n" +
            "            'config' => [\n" +
            "                'type' => 'input',\n" +
            "                'size' => 20,\n" +
            "                'eval' => 'trim, required',\n" +
            "                'max' => 256\n" +
            "            ]\n" +
            "        ],\n" +
            "        'date' => [\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:blog_example/Resources/Private/Language/locallang_db.xml:tx_blogexample_domain_model_post.date',\n" +
            "            'config' => [\n" +
            "                'type' => 'input',\n" +
            "                'renderType' => 'inputDateTime',\n" +
            "                'size' => 12,\n" +
            "                'eval' => 'datetime, required',\n" +
            "                'default' => time()\n" +
            "            ]\n" +
            "        ],\n" +
            "        'author' => [\n" +
            "            'exclude' => true,\n" +
            "            'label' => 'LLL:EXT:blog_example/Resources/Private/Language/locallang_db.xml:tx_blogexample_domain_model_post.author',\n" +
            "            'config' => [\n" +
            "                'type' => 'select',\n" +
            "                'renderType' => 'selectSingle',\n" +
            "                'foreign_table' => 'tx_blog<caret>example_domain_model_person',\n" +
            "                'fieldControl' => [\n" +
            "                    'editPopup' => [\n" +
            "                        'disabled' => false,\n" +
            "                    ],\n" +
            "                    'addRecord' => [\n" +
            "                        'disabled' => false,\n" +
            "                        'options' => [\n" +
            "                            'setValue' => 'prepend',\n" +
            "                        ],\n" +
            "                    ],\n" +
            "                ],\n" +
            "            ],\n" +
            "        ],\n" +
            "    ],\n" +
            "];"
        );

        PsiElement elementAt = getPsiElementAtCaretOffset(psiFile);
        assertContainsReference(TableReference.class, elementAt.getParent());
    }

    private PsiElement getPsiElementAtCaretOffset(PsiFile psiFile) {
        int caretOffset = myFixture.getCaretOffset();
        return psiFile.findElementAt(caretOffset);
    }
}
