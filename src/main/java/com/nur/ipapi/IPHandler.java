package com.nur.ipapi;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class IPHandler {
    public final static Map<InetAddress, IPHubResult> ipCache = new HashMap<InetAddress, IPHubResult>();

    public static boolean isCached(InetAddress ip) {
        return ipCache.containsKey(ip);
    }

    public static boolean isCached(String ip) {
        try {
            return isCached(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IPHubResult getCached(InetAddress ip) {
        return ipCache.get(ip);
    }

    public static IPHubResult getCached(String ip) {
        try {
            return ipCache.get(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cache(InetAddress ip, IPHubResult result) {
        ipCache.put(ip, result);
    }

    public static void cache(String ip, IPHubResult result) {
        try {
            cache(Inet4Address.getByName(ip), result);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
