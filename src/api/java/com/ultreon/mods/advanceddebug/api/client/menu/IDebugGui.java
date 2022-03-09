package com.ultreon.mods.advanceddebug.api.client.menu;

import com.ultreon.mods.advanceddebug.api.common.IFormatter;

import javax.annotation.Nullable;

public interface IDebugGui extends IFormatter {
    <T extends DebugPage> T registerPage(T page);

    @Nullable
    DebugPage getDebugPage();

    int getPage();

    void setPage(int page);

    void setPage(DebugPage page);

    void next();

    void prev();

    String format(Object obj);

    Formatter<Object> getDefault();
}
