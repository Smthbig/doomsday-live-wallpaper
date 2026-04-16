package com.devkrishna.doomsday.core.renderer;

import com.devkrishna.doomsday.core.model.RenderResult;

public class DotRenderer {

    public RenderResult compute(int passed, int total) {
        return new RenderResult(total, passed);
    }
}