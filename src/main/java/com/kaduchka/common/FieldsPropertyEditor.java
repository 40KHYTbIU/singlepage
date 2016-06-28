package com.kaduchka.common;

import java.beans.PropertyEditorSupport;

public class FieldsPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Fields.valueOf(text.toUpperCase()));
    }
}