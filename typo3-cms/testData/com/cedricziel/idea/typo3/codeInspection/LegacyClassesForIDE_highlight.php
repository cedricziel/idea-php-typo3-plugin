<?php

$lang = new <warning descr="Legacy class usage">\TYPO3\CMS\Lang\LanguageService</warning>();
$lang->sL('foo');

$ctrl = new <warning descr="Legacy class usage">\TYPO3\CMS\ContextHelp\Controller\ContextHelpAjaxController</warning>();
$ctrl->getContextHelp('foo', 'bar');
