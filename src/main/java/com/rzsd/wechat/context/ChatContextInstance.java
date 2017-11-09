package com.rzsd.wechat.context;

import java.util.HashMap;
import java.util.Map;

import com.rzsd.wechat.util.DateUtil;

public class ChatContextInstance {

    private static Long VALIDATE_TIME_MS = 5 * 60 * 1000L;

    private static Map<String, ChatContextDto> instance = new HashMap<>();

    private ChatContextInstance() {
    }

    public static void newInstance(String openId) {
        if (instance.containsKey(openId)) {
            instance.remove(openId);
        }
        ChatContextDto chatContextDto = new ChatContextDto();
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
        instance.put(openId, chatContextDto);
    }

    public static void removeInstance(String openId) {
        if (instance.containsKey(openId)) {
            instance.remove(openId);
        }
    }

    public static ChatContextDto getInstance(String openId) {
        if (!instance.containsKey(openId)) {
            return null;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        if (DateUtil.getCurrentTimestamp().getTime() - chatContextDto.getLastUpdTms().getTime() > VALIDATE_TIME_MS) {
            instance.remove(openId);
            return null;
        }
        return chatContextDto;
    }

    public static void setType(String openId, String type) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setType(type);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }

    public static void setWord1(String openId, String word1) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setWord1(word1);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }

    public static void setWord2(String openId, String word2) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setWord2(word2);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }

    public static void setWord3(String openId, String word3) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setWord3(word3);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }

    public static void setWord4(String openId, String word4) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setWord4(word4);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }

    public static void setWord5(String openId, String word5) {
        if (!instance.containsKey(openId)) {
            return;
        }
        ChatContextDto chatContextDto = instance.get(openId);
        chatContextDto.setWord5(word5);
        chatContextDto.setLastUpdTms(DateUtil.getCurrentTimestamp());
    }
}
