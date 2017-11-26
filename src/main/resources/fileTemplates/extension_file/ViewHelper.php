<?php

namespace {{ namespace }};

use TYPO3\CMS\Fluid\Core\Rendering\RenderingContextInterface;
use TYPO3\CMS\Fluid\Core\ViewHelper\AbstractViewHelper;

class {{ className }} extends AbstractViewHelper
{
    /**
     * @param string $value The value to output
     * @return string
     */
    public function render($value = null)
    {
        return static::renderStatic(
            [
                'value' => $value
            ],
            $this->buildRenderChildrenClosure(),
            $this->renderingContext
        );
    }

    /**
     * @param array $arguments
     * @param \Closure $renderChildrenClosure
     * @param RenderingContextInterface $renderingContext
     * @return string
     */
    public static function renderStatic(array $arguments, \Closure $renderChildrenClosure, RenderingContextInterface $renderingContext)
    {
        $value = $arguments['value'];
        if ($value === null) {
            return $renderChildrenClosure();
        } else {
            return $value;
        }
    }
}
