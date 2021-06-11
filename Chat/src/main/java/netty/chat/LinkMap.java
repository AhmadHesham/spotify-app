package netty.chat;

import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;

public class LinkMap {
    public static HashMap<String, ChannelGroup> chatToGroupMap = new HashMap<>(); // Will be put in redis caching
    public static HashMap<ChannelGroup, String> groupToChatMap = new HashMap<>(); // Will be put in redis caching
    public static HashMap<String, String> userToRegistrationToken = new HashMap<>();
}
