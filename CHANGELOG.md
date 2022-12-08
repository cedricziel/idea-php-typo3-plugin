# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## 0.9.0 - 2022-12-08

### Changed
- Bump version to 0.8.1
- Bump platform to 223
- Update plugin ids for 223
- Migrate Icons for 223
- Update JS ModuleProvider
- Bump versions in GitHub workflows
- Migrate to postStartupActivity from project listeners

## 0.8.0 - 2022-12-08

### Changed
- Upgrade grammar-kit-plugin to 2021.2.2
- Use current way to reference html file type
- Upgrade platform to 221.5080.169
- Upgrade platform to 2022.1
- Upgrade platform to 2022.2.3
- Upgrade platform to 2022.2.4

### Removed
- Removed travis build indicator

## 0.7.3

### Added
- Upgrade to platform 213

### Changed
- migrate checkHighlight to testHighlight where possible
- migrate away from using FileTypeIndex.NAME directly
- register inspections individually and remove InspectionTool
- migrate `CreateInjectorQuickFix` away from removed API to use writeCommandAction
- migrate `ExtbaseControllerActionAction` away from removed API to use writeCommandAction
- migrate `createNotification` usage to distinct NotificationGroup
- Remove unused, deprecated API usage related to icons
- Use diamond operator where applicable

## 0.7.2

### Changed
- change main target for changelog patching to master
- use nightly channel when publishing Fluid or TypoScript plugins
- upgrade gradle-intellij plugin to 1.1.6

### Removed
- only support 2021.2.2
- remove MissingTableInspectionTest as it's not compatible

## 0.7.1

### Changed
- Improve publishing through GitHub actions
- Grab version number from changelog correctly
- Use "nightly" channel for TypoScript and Fluid projects
- Fix syntax of release task

## 0.7.0

### Added
- Use GitHub actions to build plugin instead of travis
- c2b9eca [T3CMS] allowed TCA field can contain CSV string of tables (#351) (Cedric Ziel)
- f7e3bb4 [T3CMS] Create convenience method to get locale (#350) (Cedric Ziel)
- d3499ef [All] Update Platform version to 2021.1 (#349) (Cedric Ziel)
- 95a22b1 Update issue templates (Cedric Ziel)
- 8b4849f [T3CMS] Rename settings property for translation folding (#344) (Cedric Ziel)
- 66d717b Prepare v0.6.1 (Cedric Ziel)
