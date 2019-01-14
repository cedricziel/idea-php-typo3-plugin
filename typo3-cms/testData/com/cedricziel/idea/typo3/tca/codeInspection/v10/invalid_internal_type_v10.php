<?php
defined('TYPO3_MODE') or die();

define('TYPO3_branch', '10.0');

$ll = 'LLL:EXT:news/Resources/Private/Language/locallang_db.xlf:';

return [
    'ctrl' => [
    ],
    'interface' => [
    ],
    'columns' => [
        'description' => [
            'exclude' => true,
            'label' => $ll . 'tx_news_domain_model_link.description',
            'config' => [
                'type' => 'group',
                'internal_type' => <warning descr="Internal type 'file_reference' was removed prior to v10.0">'file_reference'</warning>,
                'rows' => 5,
                'behaviour' => [
                    'allowLanguageSynchronization' => true,
                ],
            ]
        ],
    ]
];
