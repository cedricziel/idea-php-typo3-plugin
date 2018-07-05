<?php

class Ding {
    /**
     * @var \My\Extension\Domain\Repository\BookRepository
     */
    protected $barRepository;

    public function foo()
    {
        $this->barRepository->countByAuthor<caret>();
    }
}
