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
                'allowed' => '<warning descr="Table 'foo' is not defined">foo</warning>,<warning descr="Table 'bar' is not defined">bar</warning>',
            ]
        ],
    ],
];
