define([], function() {
    var MyMagicModule = {
        foo: 'bar'
    };

    MyMagicModule.init = function() {
        // do init stuff
    };

    // To let the module be a dependency of another module, we return our object
    return MyMagicModule;
});
