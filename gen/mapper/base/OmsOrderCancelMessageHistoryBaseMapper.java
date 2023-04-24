package packageMapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import packageModel.OmsOrderCancelMessageHistory;
/**
*  @author guanwu
*/
public interface OmsOrderCancelMessageHistoryBaseMapper {

    int insertOmsOrderCancelMessageHistory(OmsOrderCancelMessageHistory object);

    int updateOmsOrderCancelMessageHistory(OmsOrderCancelMessageHistory object);

    int update(OmsOrderCancelMessageHistory.UpdateBuilder object);

    List<OmsOrderCancelMessageHistory> queryOmsOrderCancelMessageHistory(OmsOrderCancelMessageHistory object);

    OmsOrderCancelMessageHistory queryOmsOrderCancelMessageHistoryLimit1(OmsOrderCancelMessageHistory object);

}