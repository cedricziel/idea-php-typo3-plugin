<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                'type' => 'input',
                'foreign_table' => 'footable',
                'size' => 25,
                'max' => 255,
                'eval' => 'required'
            ]
        ],
    ],
];
