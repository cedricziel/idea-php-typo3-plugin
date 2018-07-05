<?php
defined('TYPO3_MODE') || die();

/*
 * This element was generated with the "TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm".
 *
 * https://plugins.jetbrains.com/plugin/9496-typo3-cms-plugin
 *
 * If you spot any problems, please report them at https://github.com/cedricziel/idea-php-typo3-plugin/issues
 */

/*
 * Add Content Element
 */
if (!is_array($GLOBALS['TCA']['tt_content']['types']['{{ elementName }}'])) {
    $GLOBALS['TCA']['tt_content']['types']['{{ elementName }}'] = [];
}


/*
 * Add content element to selector list
 */
\TYPO3\CMS\Core\Utility\ExtensionManagementUtility::addTcaSelectItem(
    'tt_content',
    'CType',
    [
        '{{ elementTitle }}',
        '{{ elementName }}',
        '{{ icon }}'
    ]
);

/*
 * Assign Icon
 */
$GLOBALS['TCA']['tt_content']['ctrl']['typeicon_classes']['{{ elementName }}'] = '{{ icon }}';

// Configure the default backend fields for the content element
$GLOBALS['TCA']['tt_content']['types']['{{ elementName }}'] = array_replace_recursive(
    $GLOBALS['TCA']['tt_content']['types']['{{ elementName }}'],
    [
        'showitem' => '
             --palette--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:palette.general;general,
             --palette--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:palette.header;header,
          --div--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:tabs.appearance,
             --palette--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:palette.frames;frames,
          --div--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:tabs.access,
             --palette--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:palette.visibility;visibility,
             --palette--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:palette.access;access,
          --div--;LLL:EXT:frontend/Resources/Private/Language/locallang_ttc.xml:tabs.extended
        '
    ]
);

/*
 * Uncomment the following, if you want to attach a FlexForm to the element
 */
///*
// * Add flexForms for content element configuration
// */
// \TYPO3\CMS\Core\Utility\ExtensionManagementUtility::addPiFlexFormValue(
//     '*',
//    'FILE:EXT:{{ extensionName }}/Configuration/FlexForms/{{ elementName }}.xml',
//    '{{ elementName }}'
//);
