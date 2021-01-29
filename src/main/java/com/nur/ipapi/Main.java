package com.nur.ipapi;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "IPAPI";
    public static final String NAME = "IP API";
    public static final String VERSION = "1.0";

    public static String apiKey = "";

    public static void setAPIKey(String arg) {
        apiKey = arg;
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new ChatHandler());
        ClientCommandHandler.instance.registerCommand(new IPInfoCommand());
        ClientCommandHandler.instance.registerCommand(new ToggleIPCheckCommand());
        ClientCommandHandler.instance.registerCommand(new SetIPHubKeyCommand());
        apiKey = ConfigHandler.getString("api","key","");
    }
}
