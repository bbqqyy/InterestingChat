package com.bqy.common.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.enums.GroupRoleEnum;
import com.bqy.common.chat.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {
    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> groupMemberList = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .select(GroupMember::getUid)
                .list();
        return groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid)
                .one();
    }

    public Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList) {
        List<GroupMember> groupMemberList = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .in(GroupMember::getUid,uidList)
                .eq(GroupMember::getRole, GroupRoleEnum.ADMIN_LIST)
                .select(GroupMember::getGroupId,GroupMember::getUid)
                .list();
        return groupMemberList.stream()
                .collect(Collectors.toMap(GroupMember::getUid,GroupMember::getRole));
    }

    public Boolean isLord(Long groupId, Long removeUid) {
        GroupMember groupMember = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,removeUid)
                .eq(GroupMember::getRole,GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    public boolean isManager(Long groupId, Long removeUid) {
        GroupMember groupMember = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,removeUid)
                .eq(GroupMember::getRole,GroupRoleEnum.MANAGER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }

    public boolean removeByGroupId(Long groupId, List<Long> uidList) {
        if(CollectionUtil.isNotEmpty(uidList)){
            LambdaQueryWrapper<GroupMember> lambdaQueryWrapper = Wrappers.lambdaQuery(GroupMember.class)
                    .eq(GroupMember::getGroupId,groupId)
                    .in(GroupMember::getUid,uidList);
            return this.remove(lambdaQueryWrapper);
        }
        return false;
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid,uid)
                .eq(GroupMember::getRole,GroupRoleEnum.LEADER.getType())
                .list();
    }

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> groupMemberList = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .in(GroupMember::getUid,uidList)
                .select(GroupMember::getUid)
                .list();
        return groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public List<Long> getManageUidList(Long groupId) {
        List<GroupMember> groupMemberList = lambdaQuery()
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getRole,GroupRoleEnum.MANAGER.getType())
                .list();
        return groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public void addAdmin(Long groupId, List<Long> uidList) {
        if(CollectionUtil.isNotEmpty(uidList)){
            LambdaUpdateWrapper<GroupMember> lambdaUpdateWrapper = Wrappers.lambdaUpdate(GroupMember.class)
                    .eq(GroupMember::getGroupId,groupId)
                    .in(GroupMember::getUid,uidList)
                    .set(GroupMember::getRole,GroupRoleEnum.MANAGER.getType());
            this.update(lambdaUpdateWrapper);
        }


    }

    public void revokeAdmin(Long id, List<Long> uidList) {
        if(CollectionUtil.isNotEmpty(uidList)){
            LambdaUpdateWrapper<GroupMember> lambdaUpdateWrapper = Wrappers.lambdaUpdate(GroupMember.class)
                    .eq(GroupMember::getGroupId,id)
                    .in(GroupMember::getUid,uidList)
                    .set(GroupMember::getRole,GroupRoleEnum.MEMBER.getType());
            this.update(lambdaUpdateWrapper);
        }
    }
}
