package com.bqy.common.chat.service.impl;

import com.bqy.common.chat.dao.ContactDao;
import com.bqy.common.chat.domain.dto.MsgReadInfoDTO;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.service.ContactService;
import com.bqy.common.common.utils.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {
    @Resource
    private ContactDao contactDao;
    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadCount(List<Message> messages) {
        Map<Long,List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(),1,"只能查看自己房间的消息");
        Long roomId = roomGroup.keySet().iterator().next();
        Integer totalCount = contactDao.getTotalCount(roomId);
        return messages.stream()
                .map(message -> {
                    MsgReadInfoDTO dto = new MsgReadInfoDTO();
                    dto.setMsgId(message.getId());
                    Integer readCount = contactDao.getReadCount(message);
                    dto.setReadCount(readCount);
                    dto.setUnReadCount(totalCount-readCount-1);
                    return dto;
                }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId, Function.identity()));
    }
}
