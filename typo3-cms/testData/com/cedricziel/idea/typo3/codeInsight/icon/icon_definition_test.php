<?php
namespace TYPO3\CMS\Core\Imaging;

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

use Symfony\Component\Finder\Finder;
use TYPO3\CMS\Core\Cache\CacheManager;
use TYPO3\CMS\Core\Exception;
use TYPO3\CMS\Core\Imaging\IconProvider\BitmapIconProvider;
use TYPO3\CMS\Core\Imaging\IconProvider\FontawesomeIconProvider;
use TYPO3\CMS\Core\Imaging\IconProvider\SvgIconProvider;
use TYPO3\CMS\Core\SingletonInterface;
use TYPO3\CMS\Core\Utility\StringUtility;

/**
 * Class IconRegistry, which makes it possible to register custom icons
 * from within an extension.
 */
class IconRegistry implements SingletonInterface
{
    /**
     * manually registered icons
     * hopefully obsolete one day
     *
     * @var array
     */
    protected $staticIcons = [

        /**
         * Important Information:
         *
         * Icons are maintained in an external repository, if new icons are needed
         * please request them at: https://github.com/wmdbsystems/T3.Icons/issues
         */

        // Actions
        'actions-wizard-link' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'link'
            ]
        ],
    ];
}
