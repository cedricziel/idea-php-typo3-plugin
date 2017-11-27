<?php

namespace {{ namespace }};

use TYPO3\CMS\Fluid\Core\Rendering\RenderingContextInterface;
use TYPO3\CMS\Fluid\Core\ViewHelper\AbstractViewHelper;
use TYPO3\CMS\Fluid\Core\ViewHelper\Facets\CompilableInterface;

class {{ className }} extends AbstractViewHelper implements CompilableInterface
{
    /**
     * @param string $value A string value
     */
    public function render($value = null)
    {
        return static::renderStatic(
            [
                'value' => $value,
            ],
            $this->buildRenderChildrenClosure(),
            $this->renderingContext
        );
    }

    public static function renderStatic(array $arguments, \Closure $renderChildrenClosure, RenderingContextInterface $renderingContext)
    {
        return $renderChildrenClosure();
    }
}
