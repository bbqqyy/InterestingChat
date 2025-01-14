package com.bqy.common.user.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendCheckResp {
    private List<FriendCheck> checkedList;

    @Data
    public static class FriendCheck{
        private Long uid;
        private Boolean isFriend;
    }

}
