<?php

namespace TYPO3\CMS\Core\Imaging {
    class IconFactory
    {
        public function getIcon($icon)
        {

        }
    }
}

namespace {

    use TYPO3\CMS\Core\Imaging\IconFactory;

    $iconFactory = new IconFactory();
    $iconFactory->getIcon('module-file<caret>');
}
