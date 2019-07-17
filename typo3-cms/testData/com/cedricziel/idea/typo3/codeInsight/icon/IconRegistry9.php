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
use TYPO3\CMS\Core\Utility\GeneralUtility;
use TYPO3\CMS\Core\Utility\StringUtility;

/**
 * Class IconRegistry, which makes it possible to register custom icons
 * from within an extension.
 */
class IconRegistry implements SingletonInterface
{
    /**
     * @var bool
     */
    protected $fullInitialized = false;

    /**
     * @var bool
     */
    protected $tcaInitialized = false;

    /**
     * @var bool
     */
    protected $flagsInitialized = false;

    /**
     * @var bool
     */
    protected $moduleIconsInitialized = false;

    /**
     * @var bool
     */
    protected $backendIconsInitialized = false;

    /**
     * Registered icons
     *
     * @var array
     */
    protected $icons = [];

    /**
     * Paths to backend icon folders for automatic registration
     *
     * @var string[]
     */
    protected $backendIconPaths = [
        'EXT:backend/Resources/Public/Icons/',
        'EXT:core/Resources/Public/Icons/T3Icons/',
        'EXT:impexp/Resources/Public/Icons/',
        'EXT:install/Resources/Public/Icons'
    ];

    /**
     * List of allowed icon file extensions with their Provider class
     *
     * @var string[]
     */
    protected $backendIconAllowedExtensionsWithProvider = [
        'png' => BitmapIconProvider::class,
        'svg' => SvgIconProvider::class
    ];

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
        'actions-wizard-rte' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'arrows-alt'
            ]
        ],

        // Apps
        'apps-pagetree-category-toggle-hide-checked' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'check-square'
            ]
        ],

        // Module
        'module-web' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'file-o'
            ]
        ],
        'module-file' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'image'
            ]
        ],
        'module-tools' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'rocket'
            ]
        ],
        'module-system' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'plug'
            ]
        ],
        'module-help' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'question-circle'
            ]
        ],

        // Status
        'status-dialog-information' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'exclamation-circle'
            ]
        ],
        'status-dialog-ok' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'check-circle',
            ]
        ],
        'status-dialog-notification' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'exclamation-circle'
            ]
        ],
        'status-dialog-warning' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'exclamation-triangle'
            ]
        ],
        'status-dialog-error' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'exclamation-circle'
            ]
        ],
        'status-status-checked' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'check',
            ]
        ],
        'status-status-current' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'caret-right',
            ]
        ],
        'status-status-sorting-asc' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'caret-up',
            ]
        ],
        'status-status-sorting-desc' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'caret-down',
            ]
        ],
        'status-status-sorting-light-asc' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'caret-up',
            ]
        ],
        'status-status-sorting-light-desc' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'caret-down',
            ]
        ],
        'status-status-permission-granted' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'check',
            ]
        ],
        'status-status-permission-denied' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'times',
            ]
        ],

        // Extensions
        'extensions-extensionmanager-update-script' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'refresh',
            ]
        ],
        'extensions-scheduler-run-task' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'play-circle',
            ]
        ],
        'extensions-scheduler-run-task-cron' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'clock-o',
            ]
        ],
        'generate-ws-preview-link' => [
            'provider' => BitmapIconProvider::class,
            'options' => [
                'source' => 'EXT:workspaces/Resources/Public/Images/generate-ws-preview-link.png'
            ]
        ],

        // Empty
        'empty-empty' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'empty-empty',
            ]
        ],

        // System Information
        'sysinfo-php-version' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'code'
            ]
        ],
        'sysinfo-database' =>  [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'database'
            ]
        ],
        'sysinfo-application-context' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'tasks'
            ]
        ],
        'sysinfo-composer-mode' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'music'
            ]
        ],
        'sysinfo-git' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'git'
            ]
        ],
        'sysinfo-webserver' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'server'
            ]
        ],
        'sysinfo-os-linux' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'linux'
            ]
        ],
        'sysinfo-os-apple' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'apple'
            ]
        ],
        'sysinfo-os-windows' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'windows'
            ]
        ],

        // Sysnote
        'sysnote-type-0' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'sticky-note-o'
            ]
        ],
        'sysnote-type-1' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'cog'
            ]
        ],
        'sysnote-type-2' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'code'
            ]
        ],
        'sysnote-type-3' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'thumb-tack'
            ]
        ],
        'sysnote-type-4' => [
            'provider' => FontawesomeIconProvider::class,
            'options' => [
                'name' => 'check-square'
            ]
        ],

        // Flags will be auto-registered after we have the SVG files
        'flags-multiple' => [
            'provider' => BitmapIconProvider::class,
            'options' => [
                'source' => 'EXT:core/Resources/Public/Icons/Flags/multiple.png'
            ]
        ],
        'flags-catalonia' => [
            'provider' => BitmapIconProvider::class,
            'options' => [
                'source' => 'EXT:core/Resources/Public/Icons/Flags/catalonia.png'
            ]
        ],
        'flags-en-us-gb' => [
            'provider' => BitmapIconProvider::class,
            'options' => [
                'source' => 'EXT:core/Resources/Public/Icons/Flags/en_us-gb.png'
            ]
        ],
    ];

    /**
     * Mapping of file extensions to mimetypes
     *
     * @var string[]
     */
    protected $fileExtensionMapping = [
        'htm' => 'mimetypes-text-html',
        'html' => 'mimetypes-text-html',
        'css' => 'mimetypes-text-css',
        'js' => 'mimetypes-text-js',
        'csv' => 'mimetypes-text-csv',
        'php' => 'mimetypes-text-php',
        'php6' => 'mimetypes-text-php',
        'php5' => 'mimetypes-text-php',
        'php4' => 'mimetypes-text-php',
        'php3' => 'mimetypes-text-php',
        'inc' => 'mimetypes-text-php',
        'ts' => 'mimetypes-text-ts',
        'typoscript' => 'mimetypes-text-typoscript',
        'txt' => 'mimetypes-text-text',
        'class' => 'mimetypes-text-text',
        'tmpl' => 'mimetypes-text-text',
        'jpg' => 'mimetypes-media-image',
        'jpeg' => 'mimetypes-media-image',
        'gif' => 'mimetypes-media-image',
        'png' => 'mimetypes-media-image',
        'bmp' => 'mimetypes-media-image',
        'tif' => 'mimetypes-media-image',
        'tiff' => 'mimetypes-media-image',
        'tga' => 'mimetypes-media-image',
        'psd' => 'mimetypes-media-image',
        'eps' => 'mimetypes-media-image',
        'ai' => 'mimetypes-media-image',
        'svg' => 'mimetypes-media-image',
        'pcx' => 'mimetypes-media-image',
        'avi' => 'mimetypes-media-video',
        'mpg' => 'mimetypes-media-video',
        'mpeg' => 'mimetypes-media-video',
        'mov' => 'mimetypes-media-video',
        'vimeo' => 'mimetypes-media-video-vimeo',
        'youtube' => 'mimetypes-media-video-youtube',
        'wav' => 'mimetypes-media-audio',
        'mp3' => 'mimetypes-media-audio',
        'ogg' => 'mimetypes-media-audio',
        'flac' => 'mimetypes-media-audio',
        'opus' => 'mimetypes-media-audio',
        'mid' => 'mimetypes-media-audio',
        'swf' => 'mimetypes-media-flash',
        'swa' => 'mimetypes-media-flash',
        'exe' => 'mimetypes-application',
        'com' => 'mimetypes-application',
        't3x' => 'mimetypes-compressed',
        't3d' => 'mimetypes-compressed',
        'zip' => 'mimetypes-compressed',
        'tgz' => 'mimetypes-compressed',
        'gz' => 'mimetypes-compressed',
        'pdf' => 'mimetypes-pdf',
        'doc' => 'mimetypes-word',
        'dot' => 'mimetypes-word',
        'docm' => 'mimetypes-word',
        'docx' => 'mimetypes-word',
        'dotm' => 'mimetypes-word',
        'dotx' => 'mimetypes-word',
        'sxw' => 'mimetypes-word',
        'rtf' => 'mimetypes-word',
        'xls' => 'mimetypes-excel',
        'xlsm' => 'mimetypes-excel',
        'xlsx' => 'mimetypes-excel',
        'xltm' => 'mimetypes-excel',
        'xltx' => 'mimetypes-excel',
        'sxc' => 'mimetypes-excel',
        'pps' => 'mimetypes-powerpoint',
        'ppsx' => 'mimetypes-powerpoint',
        'ppt' => 'mimetypes-powerpoint',
        'pptm' => 'mimetypes-powerpoint',
        'pptx' => 'mimetypes-powerpoint',
        'potm' => 'mimetypes-powerpoint',
        'potx' => 'mimetypes-powerpoint',
        'mount' => 'apps-filetree-mount',
        'folder' => 'apps-filetree-folder-default',
        'default' => 'mimetypes-other-other',
    ];

    /**
     * Mapping of mime types to icons
     *
     * @var string[]
     */
    protected $mimeTypeMapping = [
        'video/*' => 'mimetypes-media-video',
        'audio/*' => 'mimetypes-media-audio',
        'image/*' => 'mimetypes-media-image',
        'text/*' => 'mimetypes-text-text',
    ];

    /**
     * Array of deprecated icons, add deprecated icons to this array and remove it from registry
     * - Index of this array contains the deprecated icon
     * - Value of each entry may contain a possible new identifier
     *
     * Example:
     * [
     *   'deprecated-icon-identifier' => 'new-icon-identifier',
     *   'another-deprecated-identifier' => null,
     * ]
     *
     * @var array
     */
    protected $deprecatedIcons = [
        'status-warning-lock' => 'warning-lock',
        'status-warning-in-use' => 'warning-in-use',
        'status-status-reference-hard' => 'status-reference-hard',
        'status-status-reference-soft' => 'status-reference-soft',
        'status-status-edit-read-only' => 'status-edit-read-only',
        'extensions-workspaces-generatepreviewlink' => 'generate-ws-preview-link',
    ];

    /**
     * @var string
     */
    protected $defaultIconIdentifier = 'default-not-found';

    /**
     * The constructor
     */
    public function __construct()
    {
        $this->initialize();
    }

    /**
     * Initialize the registry
     * This method can be called multiple times, depending on initialization status.
     * In some cases e.g. TCA is not available, the method must be called multiple times.
     */
    protected function initialize()
    {
        if (!$this->backendIconsInitialized) {
            $this->getCachedBackendIcons();
        }
        if (!$this->tcaInitialized && !empty($GLOBALS['TCA'])) {
            $this->registerTCAIcons();
        }
        if (!$this->moduleIconsInitialized && !empty($GLOBALS['TBE_MODULES'])) {
            $this->registerModuleIcons();
        }
        if (!$this->flagsInitialized) {
            $this->registerFlags();
        }
        if ($this->backendIconsInitialized
            && $this->tcaInitialized
            && $this->moduleIconsInitialized
            && $this->flagsInitialized) {
            $this->fullInitialized = true;
        }
    }

    /**
     * Retrieve the icons from cache render them when not cached yet
     */
    protected function getCachedBackendIcons()
    {
        $cacheIdentifier = 'BackendIcons_' . sha1((TYPO3_version . PATH_site . 'BackendIcons'));
        /** @var \TYPO3\CMS\Core\Cache\Frontend\VariableFrontend $assetsCache */
        $assetsCache = GeneralUtility::makeInstance(CacheManager::class)->getCache('assets');
        $cacheEntry = $assetsCache->get($cacheIdentifier);

        if ($cacheEntry !== false) {
            $this->icons = $cacheEntry;
        } else {
            $this->registerBackendIcons();
            // all found icons should now be present, for historic reasons now merge w/ the statically declared icons
            $this->icons = array_merge($this->icons, $this->staticIcons);
            $assetsCache->set($cacheIdentifier, $this->icons);
        }
        // if there's now at least one icon registered, consider it successful
        if (is_array($this->icons) && (count($this->icons) >= count($this->staticIcons))) {
            $this->backendIconsInitialized = true;
        }
    }

    /**
     * Automatically find and register the core backend icons
     */
    protected function registerBackendIcons()
    {
        foreach ($this->backendIconPaths as $iconPath) {
            $absoluteIconFolderPath = GeneralUtility::getFileAbsFileName($iconPath);
            if ($absoluteIconFolderPath === '' || !is_readable($absoluteIconFolderPath)) {
                // maybe EXT:path could not be resolved, then ignore
                continue;
            }

            $finder = new Finder();
            $finder
                ->files()
                ->in($absoluteIconFolderPath)
                ->name('/\.(' . implode('|', array_keys($this->backendIconAllowedExtensionsWithProvider)) . ')$/');

            foreach ($finder as $iconFile) {
                $iconOptions = [
                    'source' => $iconPath . GeneralUtility::fixWindowsFilePath($iconFile->getRelativePathname())
                ];
                // kind of hotfix for now, needs a nicer concept later
                if (strpos($iconFile->getFilename(), 'spinner') === 0) {
                    $iconOptions['spinning'] = true;
                }

                $this->registerIcon(
                    $iconFile->getBasename('.' . $iconFile->getExtension()),
                    $this->backendIconAllowedExtensionsWithProvider[$iconFile->getExtension()],
                    $iconOptions
                );
            }
        }
    }

    /**
     * @param string $identifier
     * @return bool
     */
    public function isRegistered($identifier)
    {
        if (!$this->fullInitialized) {
            $this->initialize();
        }
        return isset($this->icons[$identifier]);
    }

    /**
     * @param string $identifier
     * @return bool
     */
    public function isDeprecated($identifier)
    {
        return isset($this->deprecatedIcons[$identifier]);
    }

    /**
     * @return string
     */
    public function getDefaultIconIdentifier()
    {
        return $this->defaultIconIdentifier;
    }

    /**
     * Registers an icon to be available inside the Icon Factory
     *
     * @param string $identifier
     * @param string $iconProviderClassName
     * @param array $options
     *
     * @throws \InvalidArgumentException
     */
    public function registerIcon($identifier, $iconProviderClassName, array $options = [])
    {
        if (!in_array(IconProviderInterface::class, class_implements($iconProviderClassName), true)) {
            throw new \InvalidArgumentException('An IconProvider must implement '
                . IconProviderInterface::class, 1437425803);
        }
        $this->icons[$identifier] = [
            'provider' => $iconProviderClassName,
            'options' => $options
        ];
    }

    /**
     * Register an icon for a file extension
     *
     * @param string $fileExtension
     * @param string $iconIdentifier
     */
    public function registerFileExtension($fileExtension, $iconIdentifier)
    {
        $this->fileExtensionMapping[$fileExtension] = $iconIdentifier;
    }

    /**
     * Register an icon for a mime-type
     *
     * @param string $mimeType
     * @param string $iconIdentifier
     */
    public function registerMimeTypeIcon($mimeType, $iconIdentifier)
    {
        $this->mimeTypeMapping[$mimeType] = $iconIdentifier;
    }

    /**
     * Fetches the configuration provided by registerIcon()
     *
     * @param string $identifier the icon identifier
     * @return mixed
     * @throws Exception
     */
    public function getIconConfigurationByIdentifier($identifier)
    {
        if (!$this->fullInitialized) {
            $this->initialize();
        }
        if (!$this->isRegistered($identifier)) {
            throw new Exception('Icon with identifier "' . $identifier . '" is not registered"', 1437425804);
        }
        if ($this->isDeprecated($identifier)) {
            $replacement = $this->deprecatedIcons[$identifier];
            if (!empty($replacement)) {
                $message = 'The icon "%s" is deprecated since TYPO3 v9 and will be removed in TYPO3 v10. Please use "%s" instead.';
                $arguments = [$replacement];
            } else {
                $message = 'The icon "%s" is deprecated since TYPO3 v9 and will be removed in TYPO3 v10.';
                $arguments = [];
            }
            trigger_error(vsprintf($message, $arguments), E_USER_DEPRECATED);
        }
        return $this->icons[$identifier];
    }

    /**
     * @return array
     */
    public function getAllRegisteredIconIdentifiers()
    {
        if (!$this->fullInitialized) {
            $this->initialize();
        }
        return array_keys($this->icons);
    }

    /**
     * @param string $fileExtension
     * @return string
     */
    public function getIconIdentifierForFileExtension($fileExtension)
    {
        // If the file extension is not valid use the default one
        if (!isset($this->fileExtensionMapping[$fileExtension])) {
            $fileExtension = 'default';
        }
        return $this->fileExtensionMapping[$fileExtension];
    }

    /**
     * Get iconIdentifier for given mimeType
     *
     * @param string $mimeType
     * @return string|null Returns null if no icon is registered for the mimeType
     */
    public function getIconIdentifierForMimeType($mimeType)
    {
        if (!isset($this->mimeTypeMapping[$mimeType])) {
            return null;
        }
        return $this->mimeTypeMapping[$mimeType];
    }

    /**
     * Load icons from TCA for each table and add them as "tcarecords-XX" to $this->icons
     */
    protected function registerTCAIcons()
    {
        $resultArray = [];

        $tcaTables = array_keys($GLOBALS['TCA']);
        // check every table in the TCA, if an icon is needed
        foreach ($tcaTables as $tableName) {
            // This method is only needed for TCA tables where typeicon_classes are not configured
            if (is_array($GLOBALS['TCA'][$tableName])) {
                $tcaCtrl = $GLOBALS['TCA'][$tableName]['ctrl'];
                $iconIdentifier = 'tcarecords-' . $tableName . '-default';
                if (isset($this->icons[$iconIdentifier])) {
                    continue;
                }
                if (isset($tcaCtrl['iconfile'])) {
                    $resultArray[$iconIdentifier] = $tcaCtrl['iconfile'];
                }
            }
        }

        foreach ($resultArray as $iconIdentifier => $iconFilePath) {
            $iconProviderClass = $this->detectIconProvider($iconFilePath);
            $this->icons[$iconIdentifier] = [
                'provider' => $iconProviderClass,
                'options' => [
                    'source' => $iconFilePath
                ]
            ];
        }
        $this->tcaInitialized = true;
    }

    /**
     * Register module icons
     */
    protected function registerModuleIcons()
    {
        $moduleConfiguration = $GLOBALS['TBE_MODULES']['_configuration'];
        foreach ($moduleConfiguration as $moduleKey => $singleModuleConfiguration) {
            $iconIdentifier = !empty($singleModuleConfiguration['iconIdentifier'])
                ? $singleModuleConfiguration['iconIdentifier']
                : null;

            if ($iconIdentifier !== null) {
                // iconIdentifier found, icon is registered, continue
                continue;
            }

            $iconPath = !empty($singleModuleConfiguration['icon'])
                ? $singleModuleConfiguration['icon']
                : null;
            $iconProviderClass = $this->detectIconProvider($iconPath);
            $iconIdentifier = 'module-icon-' . $moduleKey;

            $this->icons[$iconIdentifier] = [
                'provider' => $iconProviderClass,
                'options' => [
                    'source' => $iconPath
                ]
            ];
        }
        $this->moduleIconsInitialized = true;
    }

    /**
     * Register flags
     */
    protected function registerFlags()
    {
        $iconFolder = 'EXT:core/Resources/Public/Icons/Flags/PNG/';
        $files = [
            'AC', 'AD', 'AE', 'AF', 'AG', 'AI', 'AL', 'AM', 'AN', 'AO', 'AQ', 'AR', 'AS', 'AT', 'AU', 'AW', 'AX', 'AZ',
            'BA', 'BB', 'BD', 'BE', 'BF', 'BG', 'BH', 'BI', 'BJ', 'BL', 'BM', 'BN', 'BO', 'BQ', 'BR', 'BS', 'BT', 'BV', 'BW', 'BY', 'BZ',
            'CA', 'CC', 'CD', 'CF', 'CG', 'CH', 'CI', 'CK', 'CL', 'CM', 'CN', 'CO', 'CP', 'CR', 'CS', 'CU', 'CV', 'CW', 'CX', 'CY', 'CZ',
            'DE', 'DG', 'DJ', 'DK', 'DM', 'DO', 'DZ',
            'EA', 'EC', 'EE', 'EG', 'EH', 'ER', 'ES', 'ET', 'EU',
            'FI', 'FJ', 'FK', 'FM', 'FO', 'FR',
            'GA', 'GB-ENG', 'GB-NIR', 'GB-SCT', 'GB-WLS', 'GB', 'GD', 'GE', 'GF', 'GG', 'GH', 'GI', 'GL', 'GM', 'GN', 'GP', 'GQ', 'GR', 'GS', 'GT', 'GU', 'GW', 'GY',
            'HK', 'HM', 'HN', 'HR', 'HT', 'HU',
            'IC', 'ID', 'IE', 'IL', 'IM', 'IN', 'IO', 'IQ', 'IR', 'IS', 'IT',
            'JE', 'JM', 'JO', 'JP',
            'KE', 'KG', 'KH', 'KI', 'KM', 'KN', 'KP', 'KR', 'KW', 'KY', 'KZ',
            'LA', 'LB', 'LC', 'LI', 'LK', 'LR', 'LS', 'LT', 'LU', 'LV', 'LY',
            'MA', 'MC', 'MD', 'ME', 'MF', 'MG', 'MH', 'MK', 'ML', 'MM', 'MN', 'MO', 'MP', 'MQ', 'MR', 'MS', 'MT', 'MU', 'MV', 'MW', 'MX', 'MY', 'MZ',
            'NA', 'NC', 'NE', 'NF', 'NG', 'NI', 'NL', 'NO', 'NP', 'NR', 'NU', 'NZ',
            'OM',
            'PA', 'PE', 'PF', 'PG', 'PH', 'PK', 'PL', 'PM', 'PN', 'PR', 'PS', 'PT', 'PW', 'PY',
            'QA', 'QC',
            'RE', 'RO', 'RS', 'RU', 'RW',
            'SA', 'SB', 'SC', 'SD', 'SE', 'SG', 'SH', 'SI', 'SJ', 'SK', 'SL', 'SM', 'SN', 'SO', 'SR', 'SS', 'ST', 'SV', 'SX', 'SY', 'SZ',
            'TA', 'TC', 'TD', 'TF', 'TG', 'TH', 'TJ', 'TK', 'TL', 'TM', 'TN', 'TO', 'TR', 'TT', 'TV', 'TW', 'TZ',
            'UA', 'UG', 'UM', 'US', 'UY', 'UZ',
            'VA', 'VC', 'VE', 'VG', 'VI', 'VN', 'VU',
            'WF', 'WS',
            'XK',
            'YE', 'YT',
            'ZA', 'ZM', 'ZW'
        ];
        foreach ($files as $file) {
            $identifier = strtolower($file);
            $this->icons['flags-' . $identifier] = [
                'provider' => BitmapIconProvider::class,
                'options' => [
                    'source' => $iconFolder . $file . '.png'
                ]
            ];
        }
        $this->flagsInitialized = true;
    }

    /**
     * Detect the IconProvider of an icon
     *
     * @param string $iconReference
     * @return string
     */
    public function detectIconProvider($iconReference)
    {
        if (StringUtility::endsWith(strtolower($iconReference), 'svg')) {
            return SvgIconProvider::class;
        }
        return BitmapIconProvider::class;
    }
}
