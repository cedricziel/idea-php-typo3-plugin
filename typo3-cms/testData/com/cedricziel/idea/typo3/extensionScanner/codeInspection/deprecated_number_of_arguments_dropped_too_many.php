<?php

use TYPO3\CMS\Core\DataHandling\DataHandler;

$dh = new DataHandler();
<warning descr="Number of arguments changes with upcoming TYPO3 version, consider refactoring">$dh->extFileFunctions('foo', 'bar', 'baz', 'bongo')</warning>;
