<?php

namespace MyExt {

    use TYPO3\CMS\Extbase\Persistence\ObjectStorage;

    class MethodReferenceTypeProvider extends \TYPO3\CMS\Extbase\DomainObject\AbstractEntity
    {
        /**
         * @var \TYPO3\CMS\Extbase\Persistence\ObjectStorage<\My\Extension\Domain\Model\Book>
         */
        protected $book;

        public function getBook() : ObjectStorage
        {
            return $this->book;
        }
    }
}

namespace Bar {

    use MyExt\MethodReferenceTypeProvider;

    class Foo {
        public function getFoo()
        {
            $methodTypeProvider = new MethodReferenceTypeProvider();
            $methodTypeProvider->get<caret>Book();
        }
    }
}
