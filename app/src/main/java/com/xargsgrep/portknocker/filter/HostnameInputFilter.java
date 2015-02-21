package com.xargsgrep.portknocker.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class HostnameInputFilter implements InputFilter
{
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        for (int i = start; i < end; i++)
        {
            char c = source.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '.' && c != '-') return "";
        }
        return null;
    }
}
