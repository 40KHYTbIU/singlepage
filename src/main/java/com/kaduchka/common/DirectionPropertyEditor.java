package com.kaduchka.common;

import java.beans.PropertyEditorSupport;

public class DirectionPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Direction.valueOf(text.toUpperCase()));
    }
}
