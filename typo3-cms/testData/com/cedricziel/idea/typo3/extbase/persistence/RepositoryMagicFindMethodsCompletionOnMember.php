<?php

class Bar {
    /**
     * @var \My\Extension\Domain\Repository\BookRepository
     */
    protected $fooRepository;

    public function foo()
    {
        $this->fooRepository->find<caret>;
    }
}
