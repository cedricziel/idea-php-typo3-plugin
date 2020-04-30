define(['jquery', <warning descr="Unknown JavaScript module \"TYPO3/CMS/FooBar/Baz\"">'TYPO3/CMS/FooBar/Baz'</warning>], function($, MyMagicModule) {
    // $ is our jQuery object
    // MyMagicModule is the object, which is returned from our own module
    if(MyMagicModule.foo == 'bar'){
        MyMagicModule.init();
    }
});
