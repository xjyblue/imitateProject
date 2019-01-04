package pojo;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName UnioninfoExample
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class UnioninfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UnioninfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andUnionidIsNull() {
            addCriterion("unionId is null");
            return (Criteria) this;
        }

        public Criteria andUnionidIsNotNull() {
            addCriterion("unionId is not null");
            return (Criteria) this;
        }

        public Criteria andUnionidEqualTo(String value) {
            addCriterion("unionId =", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidNotEqualTo(String value) {
            addCriterion("unionId <>", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidGreaterThan(String value) {
            addCriterion("unionId >", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidGreaterThanOrEqualTo(String value) {
            addCriterion("unionId >=", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidLessThan(String value) {
            addCriterion("unionId <", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidLessThanOrEqualTo(String value) {
            addCriterion("unionId <=", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidLike(String value) {
            addCriterion("unionId like", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidNotLike(String value) {
            addCriterion("unionId not like", value, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidIn(List<String> values) {
            addCriterion("unionId in", values, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidNotIn(List<String> values) {
            addCriterion("unionId not in", values, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidBetween(String value1, String value2) {
            addCriterion("unionId between", value1, value2, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionidNotBetween(String value1, String value2) {
            addCriterion("unionId not between", value1, value2, "unionid");
            return (Criteria) this;
        }

        public Criteria andUnionnameIsNull() {
            addCriterion("unionName is null");
            return (Criteria) this;
        }

        public Criteria andUnionnameIsNotNull() {
            addCriterion("unionName is not null");
            return (Criteria) this;
        }

        public Criteria andUnionnameEqualTo(String value) {
            addCriterion("unionName =", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameNotEqualTo(String value) {
            addCriterion("unionName <>", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameGreaterThan(String value) {
            addCriterion("unionName >", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameGreaterThanOrEqualTo(String value) {
            addCriterion("unionName >=", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameLessThan(String value) {
            addCriterion("unionName <", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameLessThanOrEqualTo(String value) {
            addCriterion("unionName <=", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameLike(String value) {
            addCriterion("unionName like", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameNotLike(String value) {
            addCriterion("unionName not like", value, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameIn(List<String> values) {
            addCriterion("unionName in", values, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameNotIn(List<String> values) {
            addCriterion("unionName not in", values, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameBetween(String value1, String value2) {
            addCriterion("unionName between", value1, value2, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionnameNotBetween(String value1, String value2) {
            addCriterion("unionName not between", value1, value2, "unionname");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidIsNull() {
            addCriterion("unionWarehourseId is null");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidIsNotNull() {
            addCriterion("unionWarehourseId is not null");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidEqualTo(String value) {
            addCriterion("unionWarehourseId =", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidNotEqualTo(String value) {
            addCriterion("unionWarehourseId <>", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidGreaterThan(String value) {
            addCriterion("unionWarehourseId >", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidGreaterThanOrEqualTo(String value) {
            addCriterion("unionWarehourseId >=", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidLessThan(String value) {
            addCriterion("unionWarehourseId <", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidLessThanOrEqualTo(String value) {
            addCriterion("unionWarehourseId <=", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidLike(String value) {
            addCriterion("unionWarehourseId like", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidNotLike(String value) {
            addCriterion("unionWarehourseId not like", value, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidIn(List<String> values) {
            addCriterion("unionWarehourseId in", values, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidNotIn(List<String> values) {
            addCriterion("unionWarehourseId not in", values, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidBetween(String value1, String value2) {
            addCriterion("unionWarehourseId between", value1, value2, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehourseidNotBetween(String value1, String value2) {
            addCriterion("unionWarehourseId not between", value1, value2, "unionwarehourseid");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyIsNull() {
            addCriterion("unionMoney is null");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyIsNotNull() {
            addCriterion("unionMoney is not null");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyEqualTo(Integer value) {
            addCriterion("unionMoney =", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyNotEqualTo(Integer value) {
            addCriterion("unionMoney <>", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyGreaterThan(Integer value) {
            addCriterion("unionMoney >", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyGreaterThanOrEqualTo(Integer value) {
            addCriterion("unionMoney >=", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyLessThan(Integer value) {
            addCriterion("unionMoney <", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyLessThanOrEqualTo(Integer value) {
            addCriterion("unionMoney <=", value, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyIn(List<Integer> values) {
            addCriterion("unionMoney in", values, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyNotIn(List<Integer> values) {
            addCriterion("unionMoney not in", values, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyBetween(Integer value1, Integer value2) {
            addCriterion("unionMoney between", value1, value2, "unionmoney");
            return (Criteria) this;
        }

        public Criteria andUnionmoneyNotBetween(Integer value1, Integer value2) {
            addCriterion("unionMoney not between", value1, value2, "unionmoney");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}