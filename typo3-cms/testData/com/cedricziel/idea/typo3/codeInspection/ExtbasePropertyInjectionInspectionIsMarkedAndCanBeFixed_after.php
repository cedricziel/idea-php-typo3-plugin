<?php

class MyClass
{
    /**
     * @var Foo
     * <warning descr="Extbase property injection">
     */
    protected $myBar;

    public function injectMyBar(\Foo $myBar)
    {
        $this->myBar = $myBar;
    }
}
