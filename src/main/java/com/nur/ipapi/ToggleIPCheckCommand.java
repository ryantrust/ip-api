package com.nur.ipapi;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class ToggleIPCheckCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "toggleipcheck";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "toggleipcheck";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<String>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        AutoCheckHandler.toggled = !AutoCheckHandler.toggled;
        sender.addChatMessage(new ChatComponentText("" + EnumChatFormatting.LIGHT_PURPLE + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.LIGHT_PURPLE + "Automatic IP Checking " + (AutoCheckHandler.toggled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled") + EnumChatFormatting.LIGHT_PURPLE + "."));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
