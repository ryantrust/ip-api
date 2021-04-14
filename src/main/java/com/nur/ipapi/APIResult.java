package com.nur.ipapi;

import net.minecraft.util.EnumChatFormatting;

public enum APIResult {
    GOOD(EnumChatFormatting.GREEN + "", "Home"), BAD(EnumChatFormatting.RED + "", "VPN"), ERROR(EnumChatFormatting.DARK_RED + "", "Error");

    public String getDisplayNameWithParentheses() {
        return prefix + "(" + displayName + ")";
    }

    private final String prefix;

    private final String displayName;

    APIResult(String prefix, String displayName) {
        this.prefix = prefix;
        this.displayName = displayName;
    }
}
