<?php

namespace TYPO3\CMS\Extbase\Persistence\Generic {
    class Repository {}
    class QueryResultInterface {}
    class ObjectStorage {}

    class Query implements \TYPO3\CMS\Extbase\Persistence\QueryInterface {
        public function equals($tableName, $operand) {}
        public function matching($condition){}
    }
}

namespace TYPO3\CMS\Extbase\Persistence {
    class Repository {}
    class QueryResultInterface {}
    class ObjectStorage {}
    interface QueryInterface {
        public function equals($tableName, $operand);
        public function matching($condition);
    }
}

namespace TYPO3\CMS\Extbase\DomainObject {
    class AbstractDomainObject {
        /**
         * @var int The uid of the record. The uid is only unique in the context of the database table.
         */
        protected $uid;

        /**
         * @var int The uid of the localized record. Holds the uid of the record in default language (the translationOrigin).
         */
        protected $_localizedUid;

        /**
         * @var int The uid of the language of the object. This is the uid of the language record in the table sys_language.
         */
        protected $_languageUid;

        /**
         * @var int The uid of the versioned record.
         */
        protected $_versionedUid;

        /**
         * @var int The id of the page the record is "stored".
         */
        protected $pid;

        /**
         * TRUE if the object is a clone
         *
         * @var bool
         */
        private $_isClone = false;

        /**
         * @var array An array holding the clean property values. Set right after reconstitution of the object
         */
        private $_cleanProperties = [];
    }
    class AbstractEntity extends AbstractDomainObject {
        public function getUid() {}
    }
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

        public function getAuthor() {
            return $this->author;
        }
    }
}

namespace My\Extension\Domain\Repository {
    class BookRepository extends \TYPO3\CMS\Extbase\Persistence\Repository {
        /**
         * @return \TYPO3\CMS\Extbase\Persistence\QueryInterface
         */
        public function createQuery(){}
    }
}
