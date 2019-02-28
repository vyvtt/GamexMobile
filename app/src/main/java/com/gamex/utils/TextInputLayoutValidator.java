package com.gamex.utils;

import android.support.design.widget.TextInputLayout;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;

public class TextInputLayoutValidator implements ViewDataAdapter<TextInputLayout, String> {

    @Override
    public String getData(final TextInputLayout til) {
        return getText(til);
    }

    private String getText(TextInputLayout til) {
        return til.getEditText().getText().toString();
    }
}
