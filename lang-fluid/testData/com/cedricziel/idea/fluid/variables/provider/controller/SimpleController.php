<?php

class SimpleController
{
    public function overviewAction()
    {
        $this->assign('fancy_variable', 'value');
        $this->assignMultiple([
            'multi_assign' => 'multi_value',
        ]);
    }
}
