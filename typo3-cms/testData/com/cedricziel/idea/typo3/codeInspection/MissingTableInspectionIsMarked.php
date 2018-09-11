<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                'type' => 'input',
                'foreign_table' => <warning descr="Missing table definition">'footable'</warning>,
                'size' => 25,
                'max' => 255,
                'eval' => 'required'
            ]
        ],
    ],
];
