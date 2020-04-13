<?php
namespace {
    define('TYPO3_ERROR_DLOG', 'foo');

    function debugEnd() {}
}

namespace TYPO3\CMS\Backend\Template {
    class DocumentTemplate {
        const STATUS_ICON_OK = 'foo';
    }
}

namespace TYPO3\CMS\Backend\Controller\Wizard {
    class ColorpickerController {}
}
