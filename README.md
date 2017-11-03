# TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm

[![TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm in the Jetrbains Plugin repository](https://img.shields.io/jetbrains/plugin/d/9496-typo3-cms-plugin.svg)](https://plugins.jetbrains.com/plugin/9496-typo3-cms-plugin)
[![Build Status](https://travis-ci.org/cedricziel/idea-php-typo3-plugin.svg?branch=master)](https://travis-ci.org/cedricziel/idea-php-typo3-plugin)
[![Donate to this project using Paypal](https://img.shields.io/badge/paypal-donate-yellow.svg)](https://www.paypal.me/ziel)

[TYPO3 CMS Plugin in Jetbrains plugin repository](https://plugins.jetbrains.com/idea/plugin/9496-typo3-cms-plugin)

## Installation

On PhpStorm or IntelliJ:

* open "File" -> "Settings"
* select "Plugins"
* select "Browse Plugins"
* search "TYPO3 CMS Plugin"
* choose the plugin in the left pane, click the install button in 
  the right pane
* hit apply, close the window
* restart your IDE
* profit!

## Features

* TypeProvider for `GeneralUtility::makeInstance`
* TypeProvider for `GeneralUtility::makeInstanceService`
* TypeProvider for `ObjectManager::get`
* TypeProvider for `$GLOBALS['TYPO3_DB']`, `$GLOBALS['TSFE']` and `$GLOBALS['BE_USER']`
* CompletionContributor for `UriBuilder::buildUriFromRoute` and `BackendUtility::getAjaxUrl`
* CompletionContributor for `IconFactory::getIcon`
* Annotator for both valid and invalid route references to make them distinguishable from normal strings
* Annotator for both valid and invalid icon references to core-defined icons
* LineMarkerProvider to allow quick navigation to the route definition
* Inspection: Extbase `@inject` property injection + QuickFix
* **Experimental:** Generate Fluid Styled Content Element (please report bugs!)

## Development and Contribution

Contributions are very welcome! :tada:

### Requirements

* IntelliJ IDEA works best when developing the plugin (Community Edition should be sufficient),
  but in theory, every Java IDE or even your text-editor should work

### QuickStart (*nix, please adjust to your platform)

* `git clone https://github.com/cedricziel/idea-php-typo3-plugin.git`
* `cd idea-php-typo3-plugin`
* `./gradlew runIde` - `gradlew.bat runIde` for Windows, this starts the IDE with the
  plugin configured and enabled
* to begin development with IntelliJ, please import the `build.gradle` file as `new project from
  existing sources`
* from IntelliJ run the Gradle task `runIde` (in debug mode), set break-points, profit!
* ideally: send your pull request from a feature branch

## Credits

Thank you to <a href="https://github.com/Haehnchen">Daniel Espendiller</a> and <a href="https://github.com/adrienbrault">Adrien Brault</a>
for providing their <a href="https://github.com/Haehnchen/idea-php-symfony2-plugin">Symfony2 Plugin</a> in the first place.
It is a great inspiration for possible solutions and parts of the code.

## Icons

Icons used in this project are provided by:

* Göran Bodenschatz (@coding46)
* [TYPO3.Icons](https://github.com/TYPO3/TYPO3.Icons) (MIT Licensed)

# License

MIT
