package com.bqy.common.chat.service.strategy.msg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.enums.MessageStatusEnum;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.chat.domain.vo.req.TextMsgReq;
import com.bqy.common.chat.domain.vo.resp.TextMsgResp;
import com.bqy.common.chat.service.adapter.MessageAdapter;
import com.bqy.common.chat.service.cache.MsgCache;
import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.common.utils.discover.PrioritizedUrlDiscover;
import com.bqy.common.common.utils.discover.domain.UrlInfo;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.user.service.cache.UserInfoCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TextMsgHandler extends AbstractMessageHandler<TextMsgReq> {
    @Resource
    private MessageDao messageDao;
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private IRoleService iRoleService;
    @Resource
    private MsgCache msgCache;
    @Resource
    private UserCache userCache;
    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        //校验回复消息
        if (ObjectUtil.isNotNull(body.getReplyMsgId())) {
            Message replyMessage = messageDao.getById(body.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMessage, "回复消息不能为空");
            AssertUtil.equal(replyMessage.getRoomId(), roomId, "只能回复相同房间的消息");
        }
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            //去重
            List<Long> atUidList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            Map<Long, User> atUserMap = userInfoCache.getBatch(atUidList);
            long count = atUserMap.values().stream().filter(Objects::isNull).count();
            AssertUtil.equal(count, (long) atUidList.size(), "@的用户不存在");
            boolean isAtAll = atUidList.contains(0L);
            if (isAtAll) {
                AssertUtil.isTrue(iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER), "没有权限");
            }
        }
    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    protected void saveMessage(Message message, TextMsgReq body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(extra);
        if (ObjectUtil.isNotNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(message.getId(), body.getReplyMsgId(), message.getRoomId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());
        }
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            extra.setAtUidList(body.getAtUidList());
        }
        messageDao.updateById(update);

    }

    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlContentMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlContentMap).orElse(null));
        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgCache::getMsg)
                .filter(a->Objects.equals(a.getStatus(),MessageStatusEnum.NORMAL.getStatus()));
        if(reply.isPresent()){
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMsgVO.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userCache.getUserInfo(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount())&&msg.getGapCount()<= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return null;
    }

    @Override
    public String showContactMsg(Message msg) {
        return null;
    }
}
