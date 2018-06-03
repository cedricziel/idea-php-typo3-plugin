<?php

namespace TYPO3\CMS\Extbase\Persistence {
    class Repository {}
    class QueryResultInterface {}
    class ObjectStorage {}
}

namespace TYPO3\CMS\Extbase\DomainObject {
    class AbstractEntity {}
}

namespace My\Extension\Domain\Model {
    class Book extends \TYPO3\CMS\Extbase\DomainObject\AbstractEntity {
        /**
         * @var string
         */
        protected $title;

        /**
         * @var string
         */
        protected $author;

        /**
         * @var int
         */
        protected $downloads;

        /**
         * @var \TYPO3\CMS\Extbase\Persistence\ObjectStorage<\My\Extension\Domain\Model>
         */
        protected $publishers;
    }
}

namespace My\Extension\Domain\Repository {
    class BookRepository extends \TYPO3\CMS\Extbase\Persistence\Repository {}
}
