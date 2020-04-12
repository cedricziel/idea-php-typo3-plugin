<?php

namespace TYPO3\CMS\Core\Imaging {
    class IconFactory
    {
        public function getIcon($icon)
        {

        }
    }
}

namespace TYPO3\CMS\Core\Utility {
    class GeneralUtility
    {
        public static function makeInstance()
        {
        }
    }
}

namespace {

    use TYPO3\CMS\Core\Imaging\IconFactory;
    use TYPO3\CMS\Core\Utility\GeneralUtility;

    $iconFactory = GeneralUtility::makeInstance(IconFactory::class);

    $iconFactory->getIcon('module-file<caret>');
}


