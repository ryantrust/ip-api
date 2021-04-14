package com.nur.ipapi;

import com.google.gson.JsonObject;
import net.minecraft.util.EnumChatFormatting;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class IPHandler {
    public final static Map<InetAddress, APIResult> ipCache = new HashMap<InetAddress, APIResult>();
    public final static Map<InetAddress, String> infoCache = new HashMap<InetAddress, String>();

    public static String ipInfo(JsonObject j) {
        String ipInfo = "";
        if (j.has("security")) {
            JsonObject security = j.get("security").getAsJsonObject();
            ipInfo += "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " +
                    EnumChatFormatting.GRAY + "VPN: " + (security.has("vpn") ? (security.get("vpn").getAsBoolean() ? EnumChatFormatting.RED + "Yes" : EnumChatFormatting.GREEN + "No") : EnumChatFormatting.GOLD + "unknown") + EnumChatFormatting.GRAY +
                    ", Proxy: " + (security.has("proxy") ? (security.get("proxy").getAsBoolean() ? EnumChatFormatting.RED + "Yes" : EnumChatFormatting.GREEN + "No") : EnumChatFormatting.GOLD + "unknown") + EnumChatFormatting.GRAY +
                    ", Tor: " + (security.has("tor") ? (security.get("tor").getAsBoolean() ? EnumChatFormatting.RED + "Yes" : EnumChatFormatting.GREEN + "No") : EnumChatFormatting.GOLD + "unknown");
        }

        if (j.has("location")) {
            JsonObject location = j.get("location").getAsJsonObject();
            if (location.has("city") && !location.get("city").getAsString().equals("")) {
                if (!ipInfo.isEmpty()) ipInfo += "\n";
                ipInfo += "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "City: " + EnumChatFormatting.RED + location.get("city").getAsString();
            }
            if (location.has("country") && !location.get("country").getAsString().equals("")) {
                if (!ipInfo.isEmpty()) ipInfo += "\n";
                ipInfo += "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Country: " + EnumChatFormatting.RED + location.get("country").getAsString();
            }
        }

        if (j.has("network")) {
            JsonObject network = j.get("network").getAsJsonObject();
            if (network.has("autonomous_system_number") && network.has("autonomous_system_organization")) {
                if (!ipInfo.isEmpty()) ipInfo += "\n";
                ipInfo += "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "ISP: " + EnumChatFormatting.RED + network.get("autonomous_system_number").getAsString() + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.RED + network.get("autonomous_system_organization");
            }
        }

        if (j.has("message")) {
            if (!ipInfo.isEmpty()) ipInfo += "\n";
            ipInfo += "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Message: " + EnumChatFormatting.RED + j.get("message").getAsString();
        }

        return ipInfo;
    }

    public static boolean isCached(String ip) {
        try {
            return ipCache.containsKey(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static APIResult getCached(String ip) {
        try {
            return ipCache.get(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cache(String ip, APIResult result) {
        if (APIResult.ERROR.equals(result)) return;
        try {
            ipCache.put(Inet4Address.getByName(ip), result);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInfoCached(String ip) {
        try {
            return infoCache.containsKey(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCachedInfo(String ip) {
        try {
            return infoCache.get(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cacheInfo(String ip, String info) {
        try {
            infoCache.put(Inet4Address.getByName(ip), info);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
