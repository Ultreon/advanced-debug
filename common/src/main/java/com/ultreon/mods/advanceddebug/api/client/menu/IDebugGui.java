package com.ultreon.mods.advanceddebug.api.client.menu;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.common.IFormatter;
import org.jetbrains.annotations.Nullable;

public interface IDebugGui extends IFormatter<Object> {
    <T extends DebugPage> T registerPage(Identifier id, T page);

    @Nullable
    DebugPage getDebugPage();

    int getPage();

    void setPage(int page);

    void setPage(DebugPage page);

    void next();

    void prev();

    void format(Object obj, IFormatterContext context);

    Formatter<Object> getDefault();
}
