<?php

namespace TYPO3\CMS\Core\Context {

    /*
     * This file is part of the TYPO3 CMS project.
     *
     * It is free software; you can redistribute it and/or modify it under
     * the terms of the GNU General Public License, either version 2
     * of the License, or any later version.
     *
     * For the full copyright and license information, please read the
     * LICENSE.txt file that was distributed with this source code.
     *
     * The TYPO3 project - inspiring people to share!
     */

    use TYPO3\CMS\Core\Context\Exception\AspectPropertyNotFoundException;

    /**
     * Interface AspectInterface
     * Think of an aspect like a property bag
     */
    interface AspectInterface
    {
        /**
         * Get a property from an aspect
         *
         * @param string $name
         * @return mixed
         * @throws AspectPropertyNotFoundException
         */
        public function get(\string $name);
    }

    class Context implements SingletonInterface
    {
        /**
         * @var AspectInterface[]
         */
        protected $aspects = [];

        /**
         * Sets up the context with pre-defined aspects
         *
         * @param array|null $defaultAspects
         */
        public function __construct(array $defaultAspects = null)
        {
        }

        /**
         * Checks if an aspect exists in the context
         *
         * @param string $name
         * @return bool
         */
        public function hasAspect(string $name): bool
        {
            return isset($this->aspects[$name]);
        }

        /**
         * Sets an aspect, or overrides an existing aspect if an aspect is already set
         *
         * @param string $name
         * @param AspectInterface $aspect
         */
        public function setAspect(string $name, AspectInterface $aspect): void
        {
        }

        /**
         * Returns an aspect, if it is set
         *
         * @param string $name
         * @return AspectInterface
         * @throws AspectNotFoundException
         */
        public function getAspect(string $name): AspectInterface
        {
        }

        /**
         * Returns a propert yfrom the aspect, but only if the property is found.
         *
         * @param string $name
         * @param string $property
         * @param null $default
         * @return mixed|null
         * @throws AspectNotFoundException
         */
        public function getPropertyFromAspect(string $name, string $property, $default = null)
        {
        }
    }

    class UserAspect implements AspectInterface
    {
        /**
         * @var AbstractUserAuthentication
         */
        protected $user;

        /**
         * Alternative list of groups, usually useful for frontend logins with "magic" groups like "-1" and "-2"
         *
         * @var int[]
         */
        protected $groups;

        /**
         * @param AbstractUserAuthentication|null $user
         * @param array|null $alternativeGroups
         */
        public function __construct(AbstractUserAuthentication $user = null, array $alternativeGroups = null)
        {
        }

        /**
         * Fetch common information about the user
         *
         * @param string $name
         * @return int|bool|string|array
         * @throws AspectPropertyNotFoundException
         */
        public function get(string $name)
        {
        }

        /**
         * If a frontend user is checked, he/she also needs to have a group, otherwise it is only
         * checked if the frontend user has a uid > 0
         *
         * @return bool
         */
        public function isLoggedIn(): bool
        {
        }

        /**
         * Return the groups the user is a member of
         *
         * For Frontend Users there are two special groups:
         * "-1" = hide at login
         * "-2" = show at any login
         *
         * @return array
         */
        public function getGroupIds(): array
        {
        }

        /**
         * Get the name of all groups, used in Fluid's IfHasRole ViewHelper
         *
         * @return array
         */
        public function getGroupNames(): array
        {
        }

        /**
         * Checking if a user is logged in or a group constellation different from "0,-1"
         *
         * @return bool TRUE if either a login user is found OR if the group list is set to something else than '0,-1' (could be done even without a user being logged in!)
         */
        public function isUserOrGroupSet(): bool
        {
        }
    }
}
