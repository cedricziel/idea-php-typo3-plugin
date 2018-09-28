<?php

$lang = new \TYPO3\CMS\Core\Localization\LanguageService();
$lang->sL('foo');

$ctrl = new \TYPO3\CMS\Backend\Controller\ContextHelpAjaxController();
$ctrl->getContextHelp('foo', 'bar');
