package pojo;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName UserskillrelationExample
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class UserskillrelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UserskillrelationExample() {
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

        public Criteria andSkillidIsNull() {
            addCriterion("skillId is null");
            return (Criteria) this;
        }

        public Criteria andSkillidIsNotNull() {
            addCriterion("skillId is not null");
            return (Criteria) this;
        }

        public Criteria andSkillidEqualTo(Integer value) {
            addCriterion("skillId =", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidNotEqualTo(Integer value) {
            addCriterion("skillId <>", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidGreaterThan(Integer value) {
            addCriterion("skillId >", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidGreaterThanOrEqualTo(Integer value) {
            addCriterion("skillId >=", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidLessThan(Integer value) {
            addCriterion("skillId <", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidLessThanOrEqualTo(Integer value) {
            addCriterion("skillId <=", value, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidIn(List<Integer> values) {
            addCriterion("skillId in", values, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidNotIn(List<Integer> values) {
            addCriterion("skillId not in", values, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidBetween(Integer value1, Integer value2) {
            addCriterion("skillId between", value1, value2, "skillid");
            return (Criteria) this;
        }

        public Criteria andSkillidNotBetween(Integer value1, Integer value2) {
            addCriterion("skillId not between", value1, value2, "skillid");
            return (Criteria) this;
        }

        public Criteria andKeyposIsNull() {
            addCriterion("keypos is null");
            return (Criteria) this;
        }

        public Criteria andKeyposIsNotNull() {
            addCriterion("keypos is not null");
            return (Criteria) this;
        }

        public Criteria andKeyposEqualTo(String value) {
            addCriterion("keypos =", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposNotEqualTo(String value) {
            addCriterion("keypos <>", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposGreaterThan(String value) {
            addCriterion("keypos >", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposGreaterThanOrEqualTo(String value) {
            addCriterion("keypos >=", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposLessThan(String value) {
            addCriterion("keypos <", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposLessThanOrEqualTo(String value) {
            addCriterion("keypos <=", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposLike(String value) {
            addCriterion("keypos like", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposNotLike(String value) {
            addCriterion("keypos not like", value, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposIn(List<String> values) {
            addCriterion("keypos in", values, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposNotIn(List<String> values) {
            addCriterion("keypos not in", values, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposBetween(String value1, String value2) {
            addCriterion("keypos between", value1, value2, "keypos");
            return (Criteria) this;
        }

        public Criteria andKeyposNotBetween(String value1, String value2) {
            addCriterion("keypos not between", value1, value2, "keypos");
            return (Criteria) this;
        }

        public Criteria andSkillcdsIsNull() {
            addCriterion("skillCDS is null");
            return (Criteria) this;
        }

        public Criteria andSkillcdsIsNotNull() {
            addCriterion("skillCDS is not null");
            return (Criteria) this;
        }

        public Criteria andSkillcdsEqualTo(Long value) {
            addCriterion("skillCDS =", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsNotEqualTo(Long value) {
            addCriterion("skillCDS <>", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsGreaterThan(Long value) {
            addCriterion("skillCDS >", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsGreaterThanOrEqualTo(Long value) {
            addCriterion("skillCDS >=", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsLessThan(Long value) {
            addCriterion("skillCDS <", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsLessThanOrEqualTo(Long value) {
            addCriterion("skillCDS <=", value, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsIn(List<Long> values) {
            addCriterion("skillCDS in", values, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsNotIn(List<Long> values) {
            addCriterion("skillCDS not in", values, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsBetween(Long value1, Long value2) {
            addCriterion("skillCDS between", value1, value2, "skillcds");
            return (Criteria) this;
        }

        public Criteria andSkillcdsNotBetween(Long value1, Long value2) {
            addCriterion("skillCDS not between", value1, value2, "skillcds");
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