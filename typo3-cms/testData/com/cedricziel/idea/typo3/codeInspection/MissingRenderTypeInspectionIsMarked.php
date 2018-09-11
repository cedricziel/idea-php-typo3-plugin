<?php

return [
    'columns' => [
        'title' => [
            'label' => 'title',
            'config' => [
                    'type' => 'input',
                    'renderType' => <warning descr = "Missing renderType definition" > 'selectSingleFoo'</warning >,
                'size' => 25,
                'max' => 255,
                'eval' => 'required'
            ]
        ],
    ],
];
