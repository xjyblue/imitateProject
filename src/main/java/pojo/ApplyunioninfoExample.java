package pojo;

import java.util.ArrayList;
import java.util.List;

public class ApplyunioninfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ApplyunioninfoExample() {
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

        public Criteria andApplyidIsNull() {
            addCriterion("applyId is null");
            return (Criteria) this;
        }

        public Criteria andApplyidIsNotNull() {
            addCriterion("applyId is not null");
            return (Criteria) this;
        }

        public Criteria andApplyidEqualTo(String value) {
            addCriterion("applyId =", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidNotEqualTo(String value) {
            addCriterion("applyId <>", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidGreaterThan(String value) {
            addCriterion("applyId >", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidGreaterThanOrEqualTo(String value) {
            addCriterion("applyId >=", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidLessThan(String value) {
            addCriterion("applyId <", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidLessThanOrEqualTo(String value) {
            addCriterion("applyId <=", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidLike(String value) {
            addCriterion("applyId like", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidNotLike(String value) {
            addCriterion("applyId not like", value, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidIn(List<String> values) {
            addCriterion("applyId in", values, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidNotIn(List<String> values) {
            addCriterion("applyId not in", values, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidBetween(String value1, String value2) {
            addCriterion("applyId between", value1, value2, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyidNotBetween(String value1, String value2) {
            addCriterion("applyId not between", value1, value2, "applyid");
            return (Criteria) this;
        }

        public Criteria andApplyuserIsNull() {
            addCriterion("applyUser is null");
            return (Criteria) this;
        }

        public Criteria andApplyuserIsNotNull() {
            addCriterion("applyUser is not null");
            return (Criteria) this;
        }

        public Criteria andApplyuserEqualTo(String value) {
            addCriterion("applyUser =", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserNotEqualTo(String value) {
            addCriterion("applyUser <>", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserGreaterThan(String value) {
            addCriterion("applyUser >", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserGreaterThanOrEqualTo(String value) {
            addCriterion("applyUser >=", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserLessThan(String value) {
            addCriterion("applyUser <", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserLessThanOrEqualTo(String value) {
            addCriterion("applyUser <=", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserLike(String value) {
            addCriterion("applyUser like", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserNotLike(String value) {
            addCriterion("applyUser not like", value, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserIn(List<String> values) {
            addCriterion("applyUser in", values, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserNotIn(List<String> values) {
            addCriterion("applyUser not in", values, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserBetween(String value1, String value2) {
            addCriterion("applyUser between", value1, value2, "applyuser");
            return (Criteria) this;
        }

        public Criteria andApplyuserNotBetween(String value1, String value2) {
            addCriterion("applyUser not between", value1, value2, "applyuser");
            return (Criteria) this;
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