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

        /**
         * Find the best service and check if it works.
         * Returns object of the service class.
         *
         * @param string $serviceType Type of service (service key).
         * @param string $serviceSubType Sub type like file extensions or similar. Defined by the service.
         * @param mixed $excludeServiceKeys List of service keys which should be excluded in the search for a service. Array or comma list.
         * @throws \RuntimeException
         * @return object|string[] The service object or an array with error infos.
         */
        public static function makeInstanceService($serviceType, $serviceSubType = '', $excludeServiceKeys = []) {}
    }
}

namespace TYPO3\CMS\Extbase\Persistence\Generic\Mapper {
    class DataMapper {
        public function map()
        {
        }
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

namespace TYPO3\CMS\Core\Utility {
    class ExtensionManagementUtility {
        public static function addService($extKey, $serviceType, $serviceKey, $info)
        {
        }
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
