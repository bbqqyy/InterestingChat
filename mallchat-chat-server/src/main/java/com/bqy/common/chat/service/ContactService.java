package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.dto.MsgReadInfoDTO;
import com.bqy.common.chat.domain.entity.Message;

import java.util.List;
import java.util.Map;

public interface ContactService {
    Map<Long, MsgReadInfoDTO> getMsgReadCount(List<Message> messages);
}
