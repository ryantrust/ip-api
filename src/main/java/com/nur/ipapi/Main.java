package com.nur.ipapi;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, acceptedMinecraftVersions = "[1.8.8,1.8.9]")
public class Main {
    public static final String MODID = "ipapi";
    public static final String NAME = "IP API";
    public static final String VERSION = "1.1";

    public static String apiKey = "";
    public static boolean apiKeyNotSetWarningSent = false;

    public static void setAPIKey(String arg) {
        apiKey = arg;
        ConfigHandler.setString("api", "vpnapiio", arg);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new AutoCheckHandler());
        ClientCommandHandler.instance.registerCommand(new IPInfoCommand());
        ClientCommandHandler.instance.registerCommand(new ToggleIPCheckCommand());
        ClientCommandHandler.instance.registerCommand(new SetVPNAPIKeyCommand());
        apiKey = ConfigHandler.getString("api", "vpnapiio", "");
    }
}
