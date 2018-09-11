<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                'type' => 'input',
                'renderType' => 'selectSingleFoo',
                'size' => 25,
                'max' => 255,
                'eval' => 'required',
            ],
        ],
    ],
];
