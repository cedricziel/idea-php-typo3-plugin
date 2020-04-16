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

namespace TYPO3\CMS\Core\Log {
    class Logger {
    }

    class LogManager {
        /**
         * Gets a logger instance for the given name.
         *
         * \TYPO3\CMS\Core\Utility\GeneralUtility::makeInstance(\TYPO3\CMS\Core\Log\LogManager::class)->getLogger('main.sub.subsub');
         *
         * $name can also be submitted as a underscore-separated string, which will
         * be converted to dots. This is useful to call this method with __CLASS__
         * as parameter.
         *
         * @param string $name Logger name, empty to get the global "root" logger.
         * @return \TYPO3\CMS\Core\Log\Logger Logger with name $name
         */
        public function getLogger($name = '') {
        }
    }
}

namespace TYPO3\CMS\Extbase\Mvc {
    interface ResponseInterface {}
    interface RequestInterface {}
}

namespace TYPO3\CMS\Extbase\Mvc\Controller {

    interface ControllerInterface {}

    class ActionController implements ControllerInterface {

    }
}

namespace TYPO3\CMS\Extbase\Persistence {
    interface RepositoryInterface {}

    class Repository implements RepositoryInterface {
        public function __call($methodName, $arguments)
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

    class TwoArgs {
        public function __construct(string $foo)
        {
        }
    }
}
