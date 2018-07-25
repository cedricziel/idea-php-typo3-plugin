# TYPO3 Fluid plugin for IntelliJ based IDEs

This plugin is in an early draft stage. The project aims to  provide
a fully OpenSource plugin for the Fluid Template Language.

## Thoughts

In no particular order, these are measures we can implement to make sure,
the language integrates nicely in the context. What's needed, is both
an integration into the HTML Language (which is XML, internally), and
a template language that resembles the inline syntax.

* use TemplateLanguage to be able to embed the fluid inside any outer template
* use XmlAttributeDescriptorsProvider (https://github.com/JetBrains/intellij-plugins/blob/master/AngularJS/src/org/angularjs/codeInsight/attributes/AngularJSAttributeDescriptorsProvider.java)
  to provide valid XML attributes
* use HtmlXmlExtension to fix XML Validation errors (https://github.com/JetBrains/intellij-plugins/blob/master/AngularJS/src/org/angularjs/codeInsight/AngularJSHtmlExtension.java)
  this should be used to implicitly provide namespaces (such as f, which is always available)
* IntentionAndInspectionFilter to filter external inspections on a element
  (https://github.com/JetBrains/intellij-plugins/blob/master/AngularJS/src/org/angularjs/codeInsight/AngularJSInspectionFilter.java)
* provide tags and element descriptors
  https://github.com/JetBrains/intellij-plugins/blob/master/AngularJS/src/org/angularjs/codeInsight/tags/AngularJSTagDescriptorsProvider.java
* Extend XMLLanguage and replace if applicable? https://github.com/JetBrains/intellij-plugins/blob/master/AngularJS/src/org/angularjs/html/Angular2HTMLLanguage.java
* add PostfixTemplateProvider for easier loops
* add LiveTemplateProvider
* allow Injection of other languages through `AbstractLanguageInjectionSupport`

## License

MIT
