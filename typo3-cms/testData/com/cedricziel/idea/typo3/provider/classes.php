<?php

namespace TYPO3\CMS\Core\Utility {
    class GeneralUtility
    {
        public static function makeInstance($className, ...$constructorArguments)
        {
        }
    }
}

namespace TYPO3\CMS\Core {
    interface SingletonInterface {}
}

namespace TYPO3\CMS\Extbase\Object {
    interface ObjectManagerInterface extends \TYPO3\CMS\Core\SingletonInterface
    {
        public function isRegistered(string $objectName): bool;

        public function get(string $objectName, ...$constructorArguments): object;

        public function getEmptyObject(string $className): object;

        public function getScope(string $objectName): int;
    }

    class ObjectManager implements ObjectManagerInterface {
        public function isRegistered(string $objectName): bool
        {
        }

        public function get(string $objectName, ...$constructorArguments): object
        {
        }

        public function getEmptyObject(string $className): object
        {
        }

        public function getScope(string $objectName): int
        {
        }
    }
}

namespace App {
    class Baz
    {
    }

    class Foo
    {
        public function foo(): Baz
        {
        }
    }
}
