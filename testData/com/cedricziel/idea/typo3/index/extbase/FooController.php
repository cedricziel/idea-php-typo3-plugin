<?php

namespace Foo;

use TYPO3\CMS\Extbase\Mvc\Controller\ActionController;

class BarController extends ActionController {
    public function bazAction()
    {
        $this->forward('saveStopwordsKeywords', 'Administration', 'indexed_search');

        $this->redirect('form', 'Search', 'indexed_search');
    }

    public function fooAction()
    {

    }
}
