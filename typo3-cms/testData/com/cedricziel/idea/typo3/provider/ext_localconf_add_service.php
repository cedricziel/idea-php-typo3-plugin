<?php

if (!defined ('TYPO3_MODE')) {
    die ('Access denied.');
}

\TYPO3\CMS\Core\Utility\ExtensionManagementUtility::addService(
    $_EXTKEY,
    // Service type
    'translator',
    // Service key
    'tx_babelfish_translator',
    array(
        'title' => 'Babelfish',
        'description' => 'Guess alien languages by using a babelfish',

        'subtype' => '',

        'available' => true,
        'priority' => 60,
        'quality' => 80,

        'os' => '',
        'exec' => '',

        'className' => \App\Apple::class
    )
);
