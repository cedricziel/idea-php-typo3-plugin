<?php

namespace TYPO3\CMS\Core\Page {
    class PageRenderer {
        /**
         * includes an AMD-compatible JS file by resolving the ModuleName, and then requires the file via a requireJS request,
         * additionally allowing to execute JavaScript code afterwards
         *
         * this function only works for AMD-ready JS modules, used like "define('TYPO3/CMS/Backend/FormEngine..."
         * in the JS file
         *
         *	TYPO3/CMS/Backend/FormEngine =>
         * 		"TYPO3": Vendor Name
         * 		"CMS": Product Name
         *		"Backend": Extension Name
         *		"FormEngine": FileName in the Resources/Public/JavaScript folder
         *
         * @param string $mainModuleName Must be in the form of "TYPO3/CMS/PackageName/ModuleName" e.g. "TYPO3/CMS/Backend/FormEngine"
         * @param string $callBackFunction loaded right after the requireJS loading, must be wrapped in function() {}
         */
        public function loadRequireJsModule($mainModuleName, $callBackFunction = null)
        {
        }
    }
}
