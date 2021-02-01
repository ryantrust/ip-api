package com.nur.ipapi;

import net.minecraft.util.EnumChatFormatting;

public enum IPHubResult {
    GOOD(0,"Good", EnumChatFormatting.GREEN+"Good", EnumChatFormatting.GREEN+"(Good)"), BAD(1,"Bad",EnumChatFormatting.RED+"Bad", EnumChatFormatting.RED+"(Bad)"), MIXED(2,"Mixed", EnumChatFormatting.GOLD+"Mixed", EnumChatFormatting.GOLD+"(Mixed)");

    public static IPHubResult fromBlock(int blockInt) {
        for(IPHubResult result : values()){
            if(result.getBlockCode() == blockInt) return result;
        }
        return null;
    }

    public int getBlockCode() {
        return blockCode;
    }

    public String getUnformattedString() {
        return unformattedString;
    }

    public String getFormattedString() {
        return formattedString;
    }

    public String getFormattedStringWithParentheses() {
        return formattedStringWithParentheses;
    }

    private final int blockCode;
    private final String unformattedString;
    private final String formattedString;
    private final String formattedStringWithParentheses;

    IPHubResult(int blockCode, String unformattedString, String formattedString, String formattedStringWithParentheses) {
        this.blockCode = blockCode;
        this.unformattedString = unformattedString;
        this.formattedString = formattedString;
        this.formattedStringWithParentheses = formattedStringWithParentheses;
    }
}
