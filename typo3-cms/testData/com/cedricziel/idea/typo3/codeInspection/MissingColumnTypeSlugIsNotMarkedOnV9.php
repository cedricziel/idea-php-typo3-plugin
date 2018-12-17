<?php

define('TYPO3_branch', '9.5');

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                    'type' => 'slug',
                'size' => 25,
                'max' => 255,
                'eval' => 'required'
            ]
        ],
    ],
];
