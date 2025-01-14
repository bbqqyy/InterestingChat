package com.bqy.common.user.service;

import com.bqy.common.common.constant.MQConstant;
import com.bqy.common.common.domain.dto.PushMessageDTO;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.mallchat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PushService {
    @Resource
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList){
        mqProducer.sendMessage(MQConstant.PUSH_TOPIC,new PushMessageDTO(uidList,msg));
    }
    public void sendPushMsg(WSBaseResp<?> msg,Long uid){
        mqProducer.sendMessage(MQConstant.PUSH_TOPIC,new PushMessageDTO(uid,msg));
    }
    public void sendPushMsg(WSBaseResp<?> msg){
        mqProducer.sendMessage(MQConstant.PUSH_TOPIC,new PushMessageDTO(msg));
    }

}
