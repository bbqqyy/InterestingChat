package com.bqy.common.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bqy.common.chat.domain.entity.Contact;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.vo.req.ChatMessageReadReq;
import com.bqy.common.chat.mapper.ContactMapper;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    public Contact get(Long roomId, Long receiveUid) {
        return lambdaQuery()
                .eq(Contact::getRoomId,roomId)
                .eq(Contact::getUid,receiveUid)
                .one();
    }

    public CursorPageBaseResp<Contact> getReadPage(Message message, ChatMessageReadReq req) {
        return CursorUtils.getCursorPageByMySql(this,req,wrapper->{
            wrapper.eq(Contact::getRoomId,message.getRoomId());
            wrapper.ne(Contact::getUid,message.getFromUid());
            wrapper.ge(Contact::getReadTime,message.getCreateTime());
        },Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> getUnReadPage(Message message, ChatMessageReadReq req) {
        return CursorUtils.getCursorPageByMySql(this,req,wrapper->{
            wrapper.eq(Contact::getRoomId,message.getRoomId());
            wrapper.ne(Contact::getUid,message.getFromUid());
            wrapper.lt(Contact::getReadTime,message.getCreateTime());
        },Contact::getReadTime);
    }

    public Integer getTotalCount(Long roomId) {
        return lambdaQuery()
                .eq(Contact::getRoomId,roomId)
                .count();
    }

    public Integer getReadCount(Message message) {
        return lambdaQuery()
                .eq(Contact::getRoomId,message.getRoomId())
                .ne(Contact::getUid,message.getFromUid())
                .ge(Contact::getReadTime,message.getCreateTime())
                .count();
    }

    public CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq req) {
        return CursorUtils.getCursorPageByMySql(this,req,wrapper->{
            wrapper.eq(Contact::getUid,uid);
        },Contact::getActiveTime);
    }

    public List<Contact> getByRoomIds(List<Long> roomIds, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId,roomIds)
                .eq(Contact::getUid,uid)
                .list();
    }

    public boolean removeByRoomId(Long roomId, List<Long> uidList) {
        if(CollectionUtil.isNotEmpty(uidList)){
            LambdaQueryWrapper<Contact> lambdaQueryWrapper = Wrappers.lambdaQuery(Contact.class)
                    .eq(Contact::getRoomId,roomId)
                    .in(Contact::getUid,uidList);
            return this.remove(lambdaQueryWrapper);
        }
        return false;
    }
}
