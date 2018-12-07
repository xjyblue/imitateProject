package pojo;

import java.util.ArrayList;
import java.util.List;

public class UnionwarehouseExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UnionwarehouseExample() {
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

        public Criteria andUnionwarehouseidIsNull() {
            addCriterion("unionWarehouseId is null");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidIsNotNull() {
            addCriterion("unionWarehouseId is not null");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidEqualTo(String value) {
            addCriterion("unionWarehouseId =", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidNotEqualTo(String value) {
            addCriterion("unionWarehouseId <>", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidGreaterThan(String value) {
            addCriterion("unionWarehouseId >", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidGreaterThanOrEqualTo(String value) {
            addCriterion("unionWarehouseId >=", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidLessThan(String value) {
            addCriterion("unionWarehouseId <", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidLessThanOrEqualTo(String value) {
            addCriterion("unionWarehouseId <=", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidLike(String value) {
            addCriterion("unionWarehouseId like", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidNotLike(String value) {
            addCriterion("unionWarehouseId not like", value, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidIn(List<String> values) {
            addCriterion("unionWarehouseId in", values, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidNotIn(List<String> values) {
            addCriterion("unionWarehouseId not in", values, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidBetween(String value1, String value2) {
            addCriterion("unionWarehouseId between", value1, value2, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUnionwarehouseidNotBetween(String value1, String value2) {
            addCriterion("unionWarehouseId not between", value1, value2, "unionwarehouseid");
            return (Criteria) this;
        }

        public Criteria andUserbagidIsNull() {
            addCriterion("userbagId is null");
            return (Criteria) this;
        }

        public Criteria andUserbagidIsNotNull() {
            addCriterion("userbagId is not null");
            return (Criteria) this;
        }

        public Criteria andUserbagidEqualTo(String value) {
            addCriterion("userbagId =", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidNotEqualTo(String value) {
            addCriterion("userbagId <>", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidGreaterThan(String value) {
            addCriterion("userbagId >", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidGreaterThanOrEqualTo(String value) {
            addCriterion("userbagId >=", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidLessThan(String value) {
            addCriterion("userbagId <", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidLessThanOrEqualTo(String value) {
            addCriterion("userbagId <=", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidLike(String value) {
            addCriterion("userbagId like", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidNotLike(String value) {
            addCriterion("userbagId not like", value, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidIn(List<String> values) {
            addCriterion("userbagId in", values, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidNotIn(List<String> values) {
            addCriterion("userbagId not in", values, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidBetween(String value1, String value2) {
            addCriterion("userbagId between", value1, value2, "userbagid");
            return (Criteria) this;
        }

        public Criteria andUserbagidNotBetween(String value1, String value2) {
            addCriterion("userbagId not between", value1, value2, "userbagid");
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