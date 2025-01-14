package com.bqy.common.common.event;

import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
@Getter
public class GroupMemberAddEvent extends ApplicationEvent {
    private final RoomGroup roomGroup;
    private final List<GroupMember> groupMemberList;
    private final Long inviteUid;
    public GroupMemberAddEvent(Object source,RoomGroup roomGroup,List<GroupMember> groupMemberList,Long uid) {
        super(source);
        this.roomGroup = roomGroup;
        this.groupMemberList = groupMemberList;
        this.inviteUid = uid;
    }
}
