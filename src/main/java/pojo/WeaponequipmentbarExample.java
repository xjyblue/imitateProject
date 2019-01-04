package pojo;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName WeaponequipmentbarExample
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class WeaponequipmentbarExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public WeaponequipmentbarExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andWidIsNull() {
            addCriterion("wid is null");
            return (Criteria) this;
        }

        public Criteria andWidIsNotNull() {
            addCriterion("wid is not null");
            return (Criteria) this;
        }

        public Criteria andWidEqualTo(Integer value) {
            addCriterion("wid =", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidNotEqualTo(Integer value) {
            addCriterion("wid <>", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidGreaterThan(Integer value) {
            addCriterion("wid >", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidGreaterThanOrEqualTo(Integer value) {
            addCriterion("wid >=", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidLessThan(Integer value) {
            addCriterion("wid <", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidLessThanOrEqualTo(Integer value) {
            addCriterion("wid <=", value, "wid");
            return (Criteria) this;
        }

        public Criteria andWidIn(List<Integer> values) {
            addCriterion("wid in", values, "wid");
            return (Criteria) this;
        }

        public Criteria andWidNotIn(List<Integer> values) {
            addCriterion("wid not in", values, "wid");
            return (Criteria) this;
        }

        public Criteria andWidBetween(Integer value1, Integer value2) {
            addCriterion("wid between", value1, value2, "wid");
            return (Criteria) this;
        }

        public Criteria andWidNotBetween(Integer value1, Integer value2) {
            addCriterion("wid not between", value1, value2, "wid");
            return (Criteria) this;
        }

        public Criteria andDurabilityIsNull() {
            addCriterion("durability is null");
            return (Criteria) this;
        }

        public Criteria andDurabilityIsNotNull() {
            addCriterion("durability is not null");
            return (Criteria) this;
        }

        public Criteria andDurabilityEqualTo(Integer value) {
            addCriterion("durability =", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityNotEqualTo(Integer value) {
            addCriterion("durability <>", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityGreaterThan(Integer value) {
            addCriterion("durability >", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityGreaterThanOrEqualTo(Integer value) {
            addCriterion("durability >=", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityLessThan(Integer value) {
            addCriterion("durability <", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityLessThanOrEqualTo(Integer value) {
            addCriterion("durability <=", value, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityIn(List<Integer> values) {
            addCriterion("durability in", values, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityNotIn(List<Integer> values) {
            addCriterion("durability not in", values, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityBetween(Integer value1, Integer value2) {
            addCriterion("durability between", value1, value2, "durability");
            return (Criteria) this;
        }

        public Criteria andDurabilityNotBetween(Integer value1, Integer value2) {
            addCriterion("durability not between", value1, value2, "durability");
            return (Criteria) this;
        }

        public Criteria andTypeofIsNull() {
            addCriterion("typeOf is null");
            return (Criteria) this;
        }

        public Criteria andTypeofIsNotNull() {
            addCriterion("typeOf is not null");
            return (Criteria) this;
        }

        public Criteria andTypeofEqualTo(String value) {
            addCriterion("typeOf =", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofNotEqualTo(String value) {
            addCriterion("typeOf <>", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofGreaterThan(String value) {
            addCriterion("typeOf >", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofGreaterThanOrEqualTo(String value) {
            addCriterion("typeOf >=", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofLessThan(String value) {
            addCriterion("typeOf <", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofLessThanOrEqualTo(String value) {
            addCriterion("typeOf <=", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofLike(String value) {
            addCriterion("typeOf like", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofNotLike(String value) {
            addCriterion("typeOf not like", value, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofIn(List<String> values) {
            addCriterion("typeOf in", values, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofNotIn(List<String> values) {
            addCriterion("typeOf not in", values, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofBetween(String value1, String value2) {
            addCriterion("typeOf between", value1, value2, "typeof");
            return (Criteria) this;
        }

        public Criteria andTypeofNotBetween(String value1, String value2) {
            addCriterion("typeOf not between", value1, value2, "typeof");
            return (Criteria) this;
        }

        public Criteria andStartlevelIsNull() {
            addCriterion("startlevel is null");
            return (Criteria) this;
        }

        public Criteria andStartlevelIsNotNull() {
            addCriterion("startlevel is not null");
            return (Criteria) this;
        }

        public Criteria andStartlevelEqualTo(Integer value) {
            addCriterion("startlevel =", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelNotEqualTo(Integer value) {
            addCriterion("startlevel <>", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelGreaterThan(Integer value) {
            addCriterion("startlevel >", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("startlevel >=", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelLessThan(Integer value) {
            addCriterion("startlevel <", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelLessThanOrEqualTo(Integer value) {
            addCriterion("startlevel <=", value, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelIn(List<Integer> values) {
            addCriterion("startlevel in", values, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelNotIn(List<Integer> values) {
            addCriterion("startlevel not in", values, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelBetween(Integer value1, Integer value2) {
            addCriterion("startlevel between", value1, value2, "startlevel");
            return (Criteria) this;
        }

        public Criteria andStartlevelNotBetween(Integer value1, Integer value2) {
            addCriterion("startlevel not between", value1, value2, "startlevel");
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