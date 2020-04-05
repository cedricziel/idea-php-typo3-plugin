# TYPO3 Plugins for IntelliJ IDEA / PhpStorm

[![Build Status](https://travis-ci.org/cedricziel/idea-php-typo3-plugin.svg?branch=master)](https://travis-ci.org/cedricziel/idea-php-typo3-plugin)
[![Donate to this project using GitHub Sponsors](https://img.shields.io/badge/Sponsor%20on-Github-green.svg)](https://github.com/sponsors/cedricziel)
[![Donate to this project using Paypal](https://img.shields.io/badge/paypal-donate-yellow.svg)](https://www.paypal.me/ziel)
[![Donate to this project using Patreon](https://img.shields.io/badge/patreon-donate-red.svg)](https://www.patreon.com/cedricziel)

This repository contains IDE plugins for:

* [TYPO3 CMS related functionality](typo3-cms)
* the [TYPO3 Fluid Templating Language](lang-fluid)
* the [TypoScript configuration language](lang-typoscript)

While these 3 plugins work together and may use each others' library functions and extension points, each may be used
independently from the others - with a limited feature set.

The plugins in the JetBrains Plugin repository:

* TYPO3 CMS - [![TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm in the JetBrains Plugin repository](https://img.shields.io/jetbrains/plugin/d/9496-typo3-cms-plugin.svg)](https://plugins.jetbrains.com/plugin/9496-typo3-cms-plugin)
* TYPO3 Fluid - [![TYPO3 CMS Plugin for IntelliJ IDEA / PhpStorm in the JetBrains Plugin repository](https://img.shields.io/jetbrains/plugin/d/10959-typo3-fluid-foss-plugin.svg)](https://plugins.jetbrains.com/plugin/10959-typo3-fluid-foss-plugin)

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

## TYPO3 CMS Plugin - Features

* TypeProvider for `GeneralUtility::makeInstance`
* TypeProvider for `GeneralUtility::makeInstanceService`
* TypeProvider for `ObjectManager::get`
* TypeProvider for `$GLOBALS['TYPO3_DB']`, `$GLOBALS['TSFE']`, `$GLOBALS['BE_USER']` and `$GLOBALS['TYPO3_REQUEST']`
* CompletionContributor for `UriBuilder::buildUriFromRoute` and `BackendUtility::getAjaxUrl`
* CompletionContributor for `IconFactory::getIcon`
* Annotator for both valid and invalid route references to make them distinguishable from normal strings
* Annotator for both valid and invalid icon references to core-defined icons
* LineMarkerProvider to allow quick navigation to the route definition
* Inspection: Extbase `@inject` property injection + QuickFix
* **Experimental:** Generate Fluid Styled Content Element (please report bugs!)
* Generate ViewHelpers
* Generate ActionControllers
* Create TYPO3 Projects from sketch
  * classic layout (through [https://github.com/TYPO3/TYPO3.CMS](https://github.com/TYPO3/TYPO3.CMS))
  * composer based project through [https://github.com/TYPO3/TYPO3.CMS.BaseDistribution](https://github.com/TYPO3/TYPO3.CMS.BaseDistribution)

## Development and Contribution

Contributions are very welcome! :tada:

If you want to sustain further development, you can donate via [PayPal](https://www.paypal.me/ziel), [Patreon](https://www.patreon.com/cedricziel) or Invoice.

### Requirements

* IntelliJ IDEA works best when developing the plugin (Community Edition should be sufficient),
  but in theory, every Java IDE or even your text-editor should work

### QuickStart (*nix, please adjust to your platform)

* `git clone https://github.com/cedricziel/idea-php-typo3-plugin.git`
* `cd idea-php-typo3-plugin`
* start the IDE:
  * TYPO3 CMS Plugin: `./gradlew :typo3-cms:runIde` - `gradlew.bat runIde` for Windows
  * TYPO3 Fluid Plugin `./gradlew :lang-fluid:runIde` - `gradlew.bat runIde` for Windows
  * TypoScript Plugin `./gradlew :lang-typoscript:runIde` - `gradlew.bat runIde` for Windows
  * CTRL + C to terminate the execution
* to begin development with IntelliJ, please import the `build.gradle` file as `new project from
  existing sources`
* from IntelliJ run the Gradle task `runIde` (in debug mode), set break-points, profit!
* run tests:
  * `./gradlew check`
* ideally: send your pull request from a feature branch

## Credits

Thank you to <a href="https://github.com/Haehnchen">Daniel Espendiller</a> and <a href="https://github.com/adrienbrault">Adrien Brault</a>
for providing their <a href="https://github.com/Haehnchen/idea-php-symfony2-plugin">Symfony2 Plugin</a> in the first place.
It is a great inspiration for possible solutions and parts of the code.

## Icons

Icons used in this project are provided by:

* Göran Bodenschatz (@coding46)
* [TYPO3.Icons](https://github.com/TYPO3/TYPO3.Icons) (MIT Licensed)

## YourKit Java Profiler Sponsoring

We're glad, to have YourKit as a sponsor!

<a href="https://www.yourkit.com/java/profiler/" title="YourKit">
<img src="https://www.yourkit.com/images/yklogo.png"/>
</a>

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.

# License

MIT
