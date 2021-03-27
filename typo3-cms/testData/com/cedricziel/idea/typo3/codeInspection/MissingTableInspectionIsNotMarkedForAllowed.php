<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                'type' => 'input',
                'size' => 25,
                'max' => 255,
                'eval' => 'required',
                'allowed' => 'tt_content,pages',
            ]
        ],
    ],
];
