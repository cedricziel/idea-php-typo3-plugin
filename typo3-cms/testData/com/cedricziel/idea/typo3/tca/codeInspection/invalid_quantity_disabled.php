<?php
defined('TYPO3_MODE') or die();

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
                'type' => 'text',
                'cols' => '30',
                'rows' => 5,
                'behaviour' => [
                    'allowLanguageSynchronization' => true,
                ],
            ]
        ],
    ]
];
