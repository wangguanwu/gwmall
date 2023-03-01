package com.gw.gwmall.portal.domain;

import com.gw.gwmall.model.PmsComment;
import com.gw.gwmall.model.PmsCommentReplay;
import lombok.Data;

import java.util.List;

/**
 *
 * @description:
 **/
@Data
public class PmsCommentParam extends PmsComment {
    private List<PmsCommentReplay> pmsCommentReplayList;
}
