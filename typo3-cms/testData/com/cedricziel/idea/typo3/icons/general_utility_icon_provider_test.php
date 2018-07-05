<?php

namespace TYPO3\CMS\Core\Imaging {
    class IconFactory
    {

    }
}

namespace {
    use TYPO3\CMS\Core\Utility\GeneralUtility;
    use TYPO3\CMS\Core\Imaging\IconFactory;

    $iconFactory = GeneralUtility::makeInstance(IconFactory::class);

    $iconFactory->getIcon('module-file<caret>');
}


