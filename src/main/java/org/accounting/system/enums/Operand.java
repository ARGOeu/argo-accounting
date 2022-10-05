package org.accounting.system.enums;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public enum Operand {

    EQ("eq"){
        @Override
        public Bson execute(String field, Object value) {
            if(value instanceof String){
            return eqIgn(field,value);
            }else{
                return Filters.eq(field, value);
            }
        }
    },
    LT("lt"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.lt(field, value);
        }
    },
    LTE("lte"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.lte(field, value);
        }
    },
    GT("gt"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.gt(field, value);
        }
    },
    GTE("gte"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.gte(field, value);
        }
    },
    NEQ("neq"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.ne(field, value);
        }
    };



    public final String label;
    Operand(String value) {

        label = value;
    }
    public String getLabel(){
        return label;

    }
    public static Operand getEnumNameForValue(String value){
        List<Operand> values = Arrays.asList(Operand.values());
        for(Operand op:values){

            if (op.getLabel().equalsIgnoreCase(value)) {
                return op;
            }
        }
        return  null;}


    public abstract Bson execute(String field, Object value);
    public static Bson eqIgn(String fieldName, Object value) {
        String patternString = new StringBuilder("(?i)^").append(value).append("$").toString();
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        return Filters.eq(fieldName, pattern);
    }
}
