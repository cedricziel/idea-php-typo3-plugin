<?php

namespace MyExt {

    use TYPO3\CMS\Extbase\Persistence\ObjectStorage;

    class MethodTypeProvider extends \TYPO3\CMS\Extbase\DomainObject\AbstractEntity
    {
        /**
         * @var \TYPO3\CMS\Extbase\Persistence\ObjectStorage<\My\Extension\Domain\Model\Book>
         */
        protected $book;

        public function getBook() : ObjectStorage
        {
            return $this->book;
        }

        public function foo()
        {
            return $this-><caret>getBook();
        }
    }

}
