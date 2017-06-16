# TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm

![](https://jetbrains-plugins-badges.cedric-ziel.com/plugin/9496/downloads/svg)

[TYPO CMS Plugin in Jetbrains plugin repository](https://plugins.jetbrains.com/idea/plugin/9496-typo3-cms-plugin)

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

## Credits

Thank you to <a href="https://github.com/Haehnchen">Daniel Espendiller</a> and <a href="https://github.com/adrienbrault">Adrien Brault</a>
for providing their <a href="https://github.com/Haehnchen/idea-php-symfony2-plugin">Symfony2 Plugin</a> in the first place.
It is a great inspiration for possible solutions and parts of the code.

## Icons

Icons used in this project are provided by:

* Göran Bodenschatz (@coding46)
* [TYPO3.Icons](https://github.com/TYPO3/TYPO3.Icons) (MIT Licensed)

## Development

Contributions are welcome!

To start developing the plugin, you need either IntelliJ Community Edition or
Ultimate.

* fork the repo
* clone your fork locally
* check out a feature branch
* import the project from existing sources
* run `./gradlew buildPlugin` to build the project locally or `./gradlew buildPlugin` to run the IDE with the plugin enabled

# License

MIT
