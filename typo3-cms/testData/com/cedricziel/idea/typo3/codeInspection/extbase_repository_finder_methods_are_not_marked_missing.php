<?php

use TYPO3\CMS\Extbase\Persistence\Repository;

class Book {
    /** @var string */
    private $title;
}

class NoRepository {
}

class BookRepository extends Repository {
    public function findByUid($uid)
    {
    }
}

/** @var $bookRepository BookRepository */
$bookRepository->findByUid(123);

$bookRepository->countByTitle('foo');

$bookRepository->findByTitle('foo');
$bookRepository->findOneByTitle('foo');

$bookRepository->findByUndefined('');
$bookRepository->findOneByUndefined('');

/** @var $noRepository NoRepository */
$noRepository-><warning descr="Method 'findByTitle' not found in NoRepository">findByTitle</warning>('');
