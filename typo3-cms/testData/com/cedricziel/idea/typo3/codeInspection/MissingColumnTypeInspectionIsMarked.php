<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                    'type' => <warning descr = "Missing column type definition" > 'foo'</warning >,
                'size' => 25,
                'max' => 255,
                'eval' => 'required'
            ]
        ],
    ],
];
