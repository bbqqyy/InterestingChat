package com.bqy.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.chat.domain.vo.req.ChatMessageReq;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.adapter.MessageAdapter;
import com.bqy.common.common.utils.AssertUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;

public abstract class AbstractMessageHandler<Req> {
    @Resource
    private MessageDao messageDao;
    private Class<Req> bodyClass;

    @PostConstruct
    public void init(){
        ParameterizedType genericSuperClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperClass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(),this);
    }
    abstract MessageTypeEnum getMsgTypeEnum();

    protected void checkMsg(Req body, Long roomId, Long uid){

    }
    public Long checkAndSaveMsg(ChatMessageReq req,Long uid){
        Req body = this.toBean(req.getBody());
        AssertUtil.allCheckValidateThrow(body);
        checkMsg(body,req.getRoomId(),uid);
        Message message = MessageAdapter.buildMsgSave(req,uid);
        messageDao.save(message);
        saveMessage(message,body);
        return message.getId();

    }

    protected abstract void saveMessage(Message message, Req body);

    private Req toBean(Object body){
        if(bodyClass.isAssignableFrom(body.getClass())){
            return (Req) body;
        }
        return BeanUtil.toBean(body,bodyClass);
    }
    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
