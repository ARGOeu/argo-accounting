package org.accounting.system.enums;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

public enum Operand {

    EQ("eq"){
        @Override
        public Bson execute(String field, Object value) {
            return Filters.eq(field, value);
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
}
