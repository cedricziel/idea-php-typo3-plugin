define(['jquery', 'TYPO3/CMS/FooBar/Baz'], function($, MyMagicModule) {
    // $ is our jQuery object
    // MyMagicModule is the object, which is returned from our own module
    if(MyMagicModule.foo == 'bar'){
        MyMagicModule.init();
    }
});
