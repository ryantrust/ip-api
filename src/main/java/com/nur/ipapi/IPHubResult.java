package com.nur.ipapi;

import net.minecraft.util.EnumChatFormatting;

public enum IPHubResult {
    GOOD(0,"Good", EnumChatFormatting.GREEN+"Good"), BAD(1,"Bad",EnumChatFormatting.RED+"Bad"), MIXED(2,"Mixed", EnumChatFormatting.GOLD+"Mixed");

    public int getBlockCode() {
        return blockCode;
    }

    public String getUnformattedString() {
        return unformattedString;
    }

    public String getFormattedString() {
        return formattedString;
    }

    private final int blockCode;
    private final String unformattedString;
    private final String formattedString;

    IPHubResult(int blockCode, String unformattedString, String formattedString) {
        this.blockCode = blockCode;
        this.unformattedString = unformattedString;
        this.formattedString = formattedString;
    }
}
