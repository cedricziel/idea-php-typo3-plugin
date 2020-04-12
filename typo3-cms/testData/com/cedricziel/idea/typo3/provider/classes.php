<?php

namespace TYPO3\CMS\Core\Utility {
    interface SingletonInterface {}

    class GeneralUtility {
        /**
         * @param string $className name of the class to instantiate, must not be empty and not start with a backslash
         * @param array<int,mixed> $constructorArguments Arguments for the constructor
         * @return object the created instance
         * @throws \InvalidArgumentException if $className is empty or starts with a backslash
         */
        public static function makeInstance($className, ...$constructorArguments) {}
    }
}

namespace TYPO3\CMS\Extbase\Object {
    interface ObjectManagerInterface extends \TYPO3\CMS\Core\SingletonInterface
    {
        /**
         * Returns a fresh or existing instance of the object specified by $objectName.
         *
         * @param string $objectName The name of the object to return an instance of
         * @param array ...$constructorArguments
         * @return object The object instance
         */
        public function get(string $objectName, ...$constructorArguments): object;

        /**
         * Create an instance of $className without calling its constructor
         *
         * @param string $className
         * @return object
         */
        public function getEmptyObject(string $className): object;
    }
}

namespace App {
    class Apple {
        public function tree() : ?Tree
        {
        }
    }
    class Tree {

    }
}
