<?php

namespace MyExt {

    class FieldTypeProvider extends \TYPO3\CMS\Extbase\DomainObject\AbstractEntity
    {
        /**
         * @var \TYPO3\CMS\Extbase\Persistence\ObjectStorage<\My\Extension\Domain\Model\Book>
         */
        protected $book;

        public function getBook()
        {
            foreach ($this-><caret>book as $b) {
                $b->getAuthor();
            }
        }
    }

}
