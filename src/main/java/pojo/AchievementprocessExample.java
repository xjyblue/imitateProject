package pojo;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName AchievementprocessExample
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class AchievementprocessExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AchievementprocessExample() {
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

        public Criteria andIffinishIsNull() {
            addCriterion("ifFinish is null");
            return (Criteria) this;
        }

        public Criteria andIffinishIsNotNull() {
            addCriterion("ifFinish is not null");
            return (Criteria) this;
        }

        public Criteria andIffinishEqualTo(Boolean value) {
            addCriterion("ifFinish =", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishNotEqualTo(Boolean value) {
            addCriterion("ifFinish <>", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishGreaterThan(Boolean value) {
            addCriterion("ifFinish >", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishGreaterThanOrEqualTo(Boolean value) {
            addCriterion("ifFinish >=", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishLessThan(Boolean value) {
            addCriterion("ifFinish <", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishLessThanOrEqualTo(Boolean value) {
            addCriterion("ifFinish <=", value, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishIn(List<Boolean> values) {
            addCriterion("ifFinish in", values, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishNotIn(List<Boolean> values) {
            addCriterion("ifFinish not in", values, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishBetween(Boolean value1, Boolean value2) {
            addCriterion("ifFinish between", value1, value2, "iffinish");
            return (Criteria) this;
        }

        public Criteria andIffinishNotBetween(Boolean value1, Boolean value2) {
            addCriterion("ifFinish not between", value1, value2, "iffinish");
            return (Criteria) this;
        }

        public Criteria andAchievementidIsNull() {
            addCriterion("achievementId is null");
            return (Criteria) this;
        }

        public Criteria andAchievementidIsNotNull() {
            addCriterion("achievementId is not null");
            return (Criteria) this;
        }

        public Criteria andAchievementidEqualTo(Integer value) {
            addCriterion("achievementId =", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidNotEqualTo(Integer value) {
            addCriterion("achievementId <>", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidGreaterThan(Integer value) {
            addCriterion("achievementId >", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidGreaterThanOrEqualTo(Integer value) {
            addCriterion("achievementId >=", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidLessThan(Integer value) {
            addCriterion("achievementId <", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidLessThanOrEqualTo(Integer value) {
            addCriterion("achievementId <=", value, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidIn(List<Integer> values) {
            addCriterion("achievementId in", values, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidNotIn(List<Integer> values) {
            addCriterion("achievementId not in", values, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidBetween(Integer value1, Integer value2) {
            addCriterion("achievementId between", value1, value2, "achievementid");
            return (Criteria) this;
        }

        public Criteria andAchievementidNotBetween(Integer value1, Integer value2) {
            addCriterion("achievementId not between", value1, value2, "achievementid");
            return (Criteria) this;
        }

        public Criteria andProcesssIsNull() {
            addCriterion("processs is null");
            return (Criteria) this;
        }

        public Criteria andProcesssIsNotNull() {
            addCriterion("processs is not null");
            return (Criteria) this;
        }

        public Criteria andProcesssEqualTo(String value) {
            addCriterion("processs =", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssNotEqualTo(String value) {
            addCriterion("processs <>", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssGreaterThan(String value) {
            addCriterion("processs >", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssGreaterThanOrEqualTo(String value) {
            addCriterion("processs >=", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssLessThan(String value) {
            addCriterion("processs <", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssLessThanOrEqualTo(String value) {
            addCriterion("processs <=", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssLike(String value) {
            addCriterion("processs like", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssNotLike(String value) {
            addCriterion("processs not like", value, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssIn(List<String> values) {
            addCriterion("processs in", values, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssNotIn(List<String> values) {
            addCriterion("processs not in", values, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssBetween(String value1, String value2) {
            addCriterion("processs between", value1, value2, "processs");
            return (Criteria) this;
        }

        public Criteria andProcesssNotBetween(String value1, String value2) {
            addCriterion("processs not between", value1, value2, "processs");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("type not between", value1, value2, "type");
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