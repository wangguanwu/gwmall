package packageModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author guanwu
*/
public class OmsOrderCancelMessageHistory implements Serializable {

    private static final long serialVersionUID = 1679486245764L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Long id;

    /**
    * 
    * isNullAble:1
    */
    private Long orderId;

    /**
    * 
    * isNullAble:1
    */
    private Long flashPromotionRelationId;

    /**
    * 
    * isNullAble:1
    */
    private Long productId;

    /**
    * 
    * isNullAble:1
    */
    private Integer status;

    /**
    * 
    * isNullAble:0,defaultVal:CURRENT_TIMESTAMP
    */
    private java.time.LocalDateTime gmtCreate;

    /**
    * 
    * isNullAble:1
    */
    private java.time.LocalDateTime gmtModified;


    public void setId(Long id){this.id = id;}

    public Long getId(){return this.id;}

    public void setOrderId(Long orderId){this.orderId = orderId;}

    public Long getOrderId(){return this.orderId;}

    public void setFlashPromotionRelationId(Long flashPromotionRelationId){this.flashPromotionRelationId = flashPromotionRelationId;}

    public Long getFlashPromotionRelationId(){return this.flashPromotionRelationId;}

    public void setProductId(Long productId){this.productId = productId;}

    public Long getProductId(){return this.productId;}

    public void setStatus(Integer status){this.status = status;}

    public Integer getStatus(){return this.status;}

    public void setGmtCreate(java.time.LocalDateTime gmtCreate){this.gmtCreate = gmtCreate;}

    public java.time.LocalDateTime getGmtCreate(){return this.gmtCreate;}

    public void setGmtModified(java.time.LocalDateTime gmtModified){this.gmtModified = gmtModified;}

    public java.time.LocalDateTime getGmtModified(){return this.gmtModified;}
    @Override
    public String toString() {
        return "OmsOrderCancelMessageHistory{" +
                "id='" + id + '\'' +
                "orderId='" + orderId + '\'' +
                "flashPromotionRelationId='" + flashPromotionRelationId + '\'' +
                "productId='" + productId + '\'' +
                "status='" + status + '\'' +
                "gmtCreate='" + gmtCreate + '\'' +
                "gmtModified='" + gmtModified + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private OmsOrderCancelMessageHistory set;

        private ConditionBuilder where;

        public UpdateBuilder set(OmsOrderCancelMessageHistory set){
            this.set = set;
            return this;
        }

        public OmsOrderCancelMessageHistory getSet(){
            return this.set;
        }

        public UpdateBuilder where(ConditionBuilder where){
            this.where = where;
            return this;
        }

        public ConditionBuilder getWhere(){
            return this.where;
        }

        public UpdateBuilder build(){
            return this;
        }
    }

    public static class QueryBuilder extends OmsOrderCancelMessageHistory{
        /**
        * 需要返回的列
        */
        private Map<String,Object> fetchFields;

        public Map<String,Object> getFetchFields(){return this.fetchFields;}

        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Long> orderIdList;

        public List<Long> getOrderIdList(){return this.orderIdList;}

        private Long orderIdSt;

        private Long orderIdEd;

        public Long getOrderIdSt(){return this.orderIdSt;}

        public Long getOrderIdEd(){return this.orderIdEd;}

        private List<Long> flashPromotionRelationIdList;

        public List<Long> getFlashPromotionRelationIdList(){return this.flashPromotionRelationIdList;}

        private Long flashPromotionRelationIdSt;

        private Long flashPromotionRelationIdEd;

        public Long getFlashPromotionRelationIdSt(){return this.flashPromotionRelationIdSt;}

        public Long getFlashPromotionRelationIdEd(){return this.flashPromotionRelationIdEd;}

        private List<Long> productIdList;

        public List<Long> getProductIdList(){return this.productIdList;}

        private Long productIdSt;

        private Long productIdEd;

        public Long getProductIdSt(){return this.productIdSt;}

        public Long getProductIdEd(){return this.productIdEd;}

        private List<Integer> statusList;

        public List<Integer> getStatusList(){return this.statusList;}

        private Integer statusSt;

        private Integer statusEd;

        public Integer getStatusSt(){return this.statusSt;}

        public Integer getStatusEd(){return this.statusEd;}

        private List<java.time.LocalDateTime> gmtCreateList;

        public List<java.time.LocalDateTime> getGmtCreateList(){return this.gmtCreateList;}

        private java.time.LocalDateTime gmtCreateSt;

        private java.time.LocalDateTime gmtCreateEd;

        public java.time.LocalDateTime getGmtCreateSt(){return this.gmtCreateSt;}

        public java.time.LocalDateTime getGmtCreateEd(){return this.gmtCreateEd;}

        private List<java.time.LocalDateTime> gmtModifiedList;

        public List<java.time.LocalDateTime> getGmtModifiedList(){return this.gmtModifiedList;}

        private java.time.LocalDateTime gmtModifiedSt;

        private java.time.LocalDateTime gmtModifiedEd;

        public java.time.LocalDateTime getGmtModifiedSt(){return this.gmtModifiedSt;}

        public java.time.LocalDateTime getGmtModifiedEd(){return this.gmtModifiedEd;}

        private QueryBuilder (){
            this.fetchFields = new HashMap<>();
        }

        public QueryBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public QueryBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public QueryBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public QueryBuilder id(Long id){
            setId(id);
            return this;
        }

        public QueryBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public QueryBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public QueryBuilder fetchId(){
            setFetchFields("fetchFields","id");
            return this;
        }

        public QueryBuilder excludeId(){
            setFetchFields("excludeFields","id");
            return this;
        }

        public QueryBuilder orderIdBetWeen(Long orderIdSt,Long orderIdEd){
            this.orderIdSt = orderIdSt;
            this.orderIdEd = orderIdEd;
            return this;
        }

        public QueryBuilder orderIdGreaterEqThan(Long orderIdSt){
            this.orderIdSt = orderIdSt;
            return this;
        }
        public QueryBuilder orderIdLessEqThan(Long orderIdEd){
            this.orderIdEd = orderIdEd;
            return this;
        }


        public QueryBuilder orderId(Long orderId){
            setOrderId(orderId);
            return this;
        }

        public QueryBuilder orderIdList(Long ... orderId){
            this.orderIdList = solveNullList(orderId);
            return this;
        }

        public QueryBuilder orderIdList(List<Long> orderId){
            this.orderIdList = orderId;
            return this;
        }

        public QueryBuilder fetchOrderId(){
            setFetchFields("fetchFields","orderId");
            return this;
        }

        public QueryBuilder excludeOrderId(){
            setFetchFields("excludeFields","orderId");
            return this;
        }

        public QueryBuilder flashPromotionRelationIdBetWeen(Long flashPromotionRelationIdSt,Long flashPromotionRelationIdEd){
            this.flashPromotionRelationIdSt = flashPromotionRelationIdSt;
            this.flashPromotionRelationIdEd = flashPromotionRelationIdEd;
            return this;
        }

        public QueryBuilder flashPromotionRelationIdGreaterEqThan(Long flashPromotionRelationIdSt){
            this.flashPromotionRelationIdSt = flashPromotionRelationIdSt;
            return this;
        }
        public QueryBuilder flashPromotionRelationIdLessEqThan(Long flashPromotionRelationIdEd){
            this.flashPromotionRelationIdEd = flashPromotionRelationIdEd;
            return this;
        }


        public QueryBuilder flashPromotionRelationId(Long flashPromotionRelationId){
            setFlashPromotionRelationId(flashPromotionRelationId);
            return this;
        }

        public QueryBuilder flashPromotionRelationIdList(Long ... flashPromotionRelationId){
            this.flashPromotionRelationIdList = solveNullList(flashPromotionRelationId);
            return this;
        }

        public QueryBuilder flashPromotionRelationIdList(List<Long> flashPromotionRelationId){
            this.flashPromotionRelationIdList = flashPromotionRelationId;
            return this;
        }

        public QueryBuilder fetchFlashPromotionRelationId(){
            setFetchFields("fetchFields","flashPromotionRelationId");
            return this;
        }

        public QueryBuilder excludeFlashPromotionRelationId(){
            setFetchFields("excludeFields","flashPromotionRelationId");
            return this;
        }

        public QueryBuilder productIdBetWeen(Long productIdSt,Long productIdEd){
            this.productIdSt = productIdSt;
            this.productIdEd = productIdEd;
            return this;
        }

        public QueryBuilder productIdGreaterEqThan(Long productIdSt){
            this.productIdSt = productIdSt;
            return this;
        }
        public QueryBuilder productIdLessEqThan(Long productIdEd){
            this.productIdEd = productIdEd;
            return this;
        }


        public QueryBuilder productId(Long productId){
            setProductId(productId);
            return this;
        }

        public QueryBuilder productIdList(Long ... productId){
            this.productIdList = solveNullList(productId);
            return this;
        }

        public QueryBuilder productIdList(List<Long> productId){
            this.productIdList = productId;
            return this;
        }

        public QueryBuilder fetchProductId(){
            setFetchFields("fetchFields","productId");
            return this;
        }

        public QueryBuilder excludeProductId(){
            setFetchFields("excludeFields","productId");
            return this;
        }

        public QueryBuilder statusBetWeen(Integer statusSt,Integer statusEd){
            this.statusSt = statusSt;
            this.statusEd = statusEd;
            return this;
        }

        public QueryBuilder statusGreaterEqThan(Integer statusSt){
            this.statusSt = statusSt;
            return this;
        }
        public QueryBuilder statusLessEqThan(Integer statusEd){
            this.statusEd = statusEd;
            return this;
        }


        public QueryBuilder status(Integer status){
            setStatus(status);
            return this;
        }

        public QueryBuilder statusList(Integer ... status){
            this.statusList = solveNullList(status);
            return this;
        }

        public QueryBuilder statusList(List<Integer> status){
            this.statusList = status;
            return this;
        }

        public QueryBuilder fetchStatus(){
            setFetchFields("fetchFields","status");
            return this;
        }

        public QueryBuilder excludeStatus(){
            setFetchFields("excludeFields","status");
            return this;
        }

        public QueryBuilder gmtCreateBetWeen(java.time.LocalDateTime gmtCreateSt,java.time.LocalDateTime gmtCreateEd){
            this.gmtCreateSt = gmtCreateSt;
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }

        public QueryBuilder gmtCreateGreaterEqThan(java.time.LocalDateTime gmtCreateSt){
            this.gmtCreateSt = gmtCreateSt;
            return this;
        }
        public QueryBuilder gmtCreateLessEqThan(java.time.LocalDateTime gmtCreateEd){
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }


        public QueryBuilder gmtCreate(java.time.LocalDateTime gmtCreate){
            setGmtCreate(gmtCreate);
            return this;
        }

        public QueryBuilder gmtCreateList(java.time.LocalDateTime ... gmtCreate){
            this.gmtCreateList = solveNullList(gmtCreate);
            return this;
        }

        public QueryBuilder gmtCreateList(List<java.time.LocalDateTime> gmtCreate){
            this.gmtCreateList = gmtCreate;
            return this;
        }

        public QueryBuilder fetchGmtCreate(){
            setFetchFields("fetchFields","gmtCreate");
            return this;
        }

        public QueryBuilder excludeGmtCreate(){
            setFetchFields("excludeFields","gmtCreate");
            return this;
        }

        public QueryBuilder gmtModifiedBetWeen(java.time.LocalDateTime gmtModifiedSt,java.time.LocalDateTime gmtModifiedEd){
            this.gmtModifiedSt = gmtModifiedSt;
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }

        public QueryBuilder gmtModifiedGreaterEqThan(java.time.LocalDateTime gmtModifiedSt){
            this.gmtModifiedSt = gmtModifiedSt;
            return this;
        }
        public QueryBuilder gmtModifiedLessEqThan(java.time.LocalDateTime gmtModifiedEd){
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }


        public QueryBuilder gmtModified(java.time.LocalDateTime gmtModified){
            setGmtModified(gmtModified);
            return this;
        }

        public QueryBuilder gmtModifiedList(java.time.LocalDateTime ... gmtModified){
            this.gmtModifiedList = solveNullList(gmtModified);
            return this;
        }

        public QueryBuilder gmtModifiedList(List<java.time.LocalDateTime> gmtModified){
            this.gmtModifiedList = gmtModified;
            return this;
        }

        public QueryBuilder fetchGmtModified(){
            setFetchFields("fetchFields","gmtModified");
            return this;
        }

        public QueryBuilder excludeGmtModified(){
            setFetchFields("excludeFields","gmtModified");
            return this;
        }
        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public QueryBuilder fetchAll(){
            this.fetchFields.put("AllFields",true);
            return this;
        }

        public QueryBuilder addField(String ... fields){
            List<String> list = new ArrayList<>();
            if (fields != null){
                for (String field : fields){
                    list.add(field);
                }
            }
            this.fetchFields.put("otherFields",list);
            return this;
        }
        @SuppressWarnings("unchecked")
        private void setFetchFields(String key,String val){
            Map<String,Boolean> fields= (Map<String, Boolean>) this.fetchFields.get(key);
            if (fields == null){
                fields = new HashMap<>();
            }
            fields.put(val,true);
            this.fetchFields.put(key,fields);
        }

        public OmsOrderCancelMessageHistory build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Long> orderIdList;

        public List<Long> getOrderIdList(){return this.orderIdList;}

        private Long orderIdSt;

        private Long orderIdEd;

        public Long getOrderIdSt(){return this.orderIdSt;}

        public Long getOrderIdEd(){return this.orderIdEd;}

        private List<Long> flashPromotionRelationIdList;

        public List<Long> getFlashPromotionRelationIdList(){return this.flashPromotionRelationIdList;}

        private Long flashPromotionRelationIdSt;

        private Long flashPromotionRelationIdEd;

        public Long getFlashPromotionRelationIdSt(){return this.flashPromotionRelationIdSt;}

        public Long getFlashPromotionRelationIdEd(){return this.flashPromotionRelationIdEd;}

        private List<Long> productIdList;

        public List<Long> getProductIdList(){return this.productIdList;}

        private Long productIdSt;

        private Long productIdEd;

        public Long getProductIdSt(){return this.productIdSt;}

        public Long getProductIdEd(){return this.productIdEd;}

        private List<Integer> statusList;

        public List<Integer> getStatusList(){return this.statusList;}

        private Integer statusSt;

        private Integer statusEd;

        public Integer getStatusSt(){return this.statusSt;}

        public Integer getStatusEd(){return this.statusEd;}

        private List<java.time.LocalDateTime> gmtCreateList;

        public List<java.time.LocalDateTime> getGmtCreateList(){return this.gmtCreateList;}

        private java.time.LocalDateTime gmtCreateSt;

        private java.time.LocalDateTime gmtCreateEd;

        public java.time.LocalDateTime getGmtCreateSt(){return this.gmtCreateSt;}

        public java.time.LocalDateTime getGmtCreateEd(){return this.gmtCreateEd;}

        private List<java.time.LocalDateTime> gmtModifiedList;

        public List<java.time.LocalDateTime> getGmtModifiedList(){return this.gmtModifiedList;}

        private java.time.LocalDateTime gmtModifiedSt;

        private java.time.LocalDateTime gmtModifiedEd;

        public java.time.LocalDateTime getGmtModifiedSt(){return this.gmtModifiedSt;}

        public java.time.LocalDateTime getGmtModifiedEd(){return this.gmtModifiedEd;}


        public ConditionBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public ConditionBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public ConditionBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public ConditionBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public ConditionBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public ConditionBuilder orderIdBetWeen(Long orderIdSt,Long orderIdEd){
            this.orderIdSt = orderIdSt;
            this.orderIdEd = orderIdEd;
            return this;
        }

        public ConditionBuilder orderIdGreaterEqThan(Long orderIdSt){
            this.orderIdSt = orderIdSt;
            return this;
        }
        public ConditionBuilder orderIdLessEqThan(Long orderIdEd){
            this.orderIdEd = orderIdEd;
            return this;
        }


        public ConditionBuilder orderIdList(Long ... orderId){
            this.orderIdList = solveNullList(orderId);
            return this;
        }

        public ConditionBuilder orderIdList(List<Long> orderId){
            this.orderIdList = orderId;
            return this;
        }

        public ConditionBuilder flashPromotionRelationIdBetWeen(Long flashPromotionRelationIdSt,Long flashPromotionRelationIdEd){
            this.flashPromotionRelationIdSt = flashPromotionRelationIdSt;
            this.flashPromotionRelationIdEd = flashPromotionRelationIdEd;
            return this;
        }

        public ConditionBuilder flashPromotionRelationIdGreaterEqThan(Long flashPromotionRelationIdSt){
            this.flashPromotionRelationIdSt = flashPromotionRelationIdSt;
            return this;
        }
        public ConditionBuilder flashPromotionRelationIdLessEqThan(Long flashPromotionRelationIdEd){
            this.flashPromotionRelationIdEd = flashPromotionRelationIdEd;
            return this;
        }


        public ConditionBuilder flashPromotionRelationIdList(Long ... flashPromotionRelationId){
            this.flashPromotionRelationIdList = solveNullList(flashPromotionRelationId);
            return this;
        }

        public ConditionBuilder flashPromotionRelationIdList(List<Long> flashPromotionRelationId){
            this.flashPromotionRelationIdList = flashPromotionRelationId;
            return this;
        }

        public ConditionBuilder productIdBetWeen(Long productIdSt,Long productIdEd){
            this.productIdSt = productIdSt;
            this.productIdEd = productIdEd;
            return this;
        }

        public ConditionBuilder productIdGreaterEqThan(Long productIdSt){
            this.productIdSt = productIdSt;
            return this;
        }
        public ConditionBuilder productIdLessEqThan(Long productIdEd){
            this.productIdEd = productIdEd;
            return this;
        }


        public ConditionBuilder productIdList(Long ... productId){
            this.productIdList = solveNullList(productId);
            return this;
        }

        public ConditionBuilder productIdList(List<Long> productId){
            this.productIdList = productId;
            return this;
        }

        public ConditionBuilder statusBetWeen(Integer statusSt,Integer statusEd){
            this.statusSt = statusSt;
            this.statusEd = statusEd;
            return this;
        }

        public ConditionBuilder statusGreaterEqThan(Integer statusSt){
            this.statusSt = statusSt;
            return this;
        }
        public ConditionBuilder statusLessEqThan(Integer statusEd){
            this.statusEd = statusEd;
            return this;
        }


        public ConditionBuilder statusList(Integer ... status){
            this.statusList = solveNullList(status);
            return this;
        }

        public ConditionBuilder statusList(List<Integer> status){
            this.statusList = status;
            return this;
        }

        public ConditionBuilder gmtCreateBetWeen(java.time.LocalDateTime gmtCreateSt,java.time.LocalDateTime gmtCreateEd){
            this.gmtCreateSt = gmtCreateSt;
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }

        public ConditionBuilder gmtCreateGreaterEqThan(java.time.LocalDateTime gmtCreateSt){
            this.gmtCreateSt = gmtCreateSt;
            return this;
        }
        public ConditionBuilder gmtCreateLessEqThan(java.time.LocalDateTime gmtCreateEd){
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }


        public ConditionBuilder gmtCreateList(java.time.LocalDateTime ... gmtCreate){
            this.gmtCreateList = solveNullList(gmtCreate);
            return this;
        }

        public ConditionBuilder gmtCreateList(List<java.time.LocalDateTime> gmtCreate){
            this.gmtCreateList = gmtCreate;
            return this;
        }

        public ConditionBuilder gmtModifiedBetWeen(java.time.LocalDateTime gmtModifiedSt,java.time.LocalDateTime gmtModifiedEd){
            this.gmtModifiedSt = gmtModifiedSt;
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }

        public ConditionBuilder gmtModifiedGreaterEqThan(java.time.LocalDateTime gmtModifiedSt){
            this.gmtModifiedSt = gmtModifiedSt;
            return this;
        }
        public ConditionBuilder gmtModifiedLessEqThan(java.time.LocalDateTime gmtModifiedEd){
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }


        public ConditionBuilder gmtModifiedList(java.time.LocalDateTime ... gmtModified){
            this.gmtModifiedList = solveNullList(gmtModified);
            return this;
        }

        public ConditionBuilder gmtModifiedList(List<java.time.LocalDateTime> gmtModified){
            this.gmtModifiedList = gmtModified;
            return this;
        }

        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public ConditionBuilder build(){return this;}
    }

    public static class Builder {

        private OmsOrderCancelMessageHistory obj;

        public Builder(){
            this.obj = new OmsOrderCancelMessageHistory();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder orderId(Long orderId){
            this.obj.setOrderId(orderId);
            return this;
        }
        public Builder flashPromotionRelationId(Long flashPromotionRelationId){
            this.obj.setFlashPromotionRelationId(flashPromotionRelationId);
            return this;
        }
        public Builder productId(Long productId){
            this.obj.setProductId(productId);
            return this;
        }
        public Builder status(Integer status){
            this.obj.setStatus(status);
            return this;
        }
        public Builder gmtCreate(java.time.LocalDateTime gmtCreate){
            this.obj.setGmtCreate(gmtCreate);
            return this;
        }
        public Builder gmtModified(java.time.LocalDateTime gmtModified){
            this.obj.setGmtModified(gmtModified);
            return this;
        }
        public OmsOrderCancelMessageHistory build(){return obj;}
    }

}
