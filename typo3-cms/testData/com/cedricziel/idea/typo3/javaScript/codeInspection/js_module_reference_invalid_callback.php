<?php

/** @var \TYPO3\CMS\Core\Page\PageRenderer $pageRenderer */
$pageRenderer->loadRequireJsModule(
    <warning descr="Unknown JavaScript module \"TYPO3/CMS/FooBar/MyMagicModule\"">'TYPO3/CMS/FooBar/MyMagicModule'</warning>,
    'function() { console.log("Loaded own module."); }'
);
