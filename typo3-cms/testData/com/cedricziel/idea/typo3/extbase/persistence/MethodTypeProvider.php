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
            $book = $this->getBook()[0];
            return $book->get<caret>Author();
        }
    }

}
