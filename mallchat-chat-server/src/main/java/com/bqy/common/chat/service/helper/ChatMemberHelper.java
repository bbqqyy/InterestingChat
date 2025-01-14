package com.bqy.common.chat.service.helper;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.bqy.common.chat.domain.enums.ChatActiveStatusEnum;

public class ChatMemberHelper {
    private static final String SEPARATOR = "_";

    public static Pair<ChatActiveStatusEnum, String> getCursorPair(String cursor) {
        ChatActiveStatusEnum onlineActiveStatusEnum = ChatActiveStatusEnum.ONLINE;
        String timeCursor = null;
        if (StrUtil.isNotBlank(cursor)) {
            String activeStr = cursor.split(SEPARATOR)[0];
            String timeStr = cursor.split(SEPARATOR)[1];
            onlineActiveStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeStr));
            timeCursor = timeStr;
        }
        return Pair.of(onlineActiveStatusEnum, timeCursor);
    }

    public static String generateCursor(ChatActiveStatusEnum chatActiveStatusEnum, String timeCursor) {
        return chatActiveStatusEnum.getStatus() + SEPARATOR + timeCursor;
    }
}
