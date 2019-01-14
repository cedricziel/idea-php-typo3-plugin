<?php
defined('TYPO3_MODE') or die();

define('TYPO3_branch', '9.5');

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
                'internal_type' => 'file',
                'rows' => 5,
                'behaviour' => [
                    'allowLanguageSynchronization' => true,
                ],
            ]
        ],
    ]
];
