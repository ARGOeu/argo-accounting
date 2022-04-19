package org.accounting.system.enums;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Operator {
    AND("and"){
        @Override
        public Bson execute(ArrayList<Bson> bsons) {
            return Filters.and(bsons);
        }
    },
    OR("or"){
        @Override
        public Bson execute(ArrayList<Bson> bsons) {
            return Filters.or(bsons);
        }
    };

    public final String label;
    Operator(String value) {

        label = value;
    }
    public String getLabel(){
        return label;

    }
    public static Operator getEnumNameForValue(String value){
        List<Operator> values = Arrays.asList(Operator.values());
        for(Operator op:values){

            if (op.getLabel().equalsIgnoreCase(value)) {
                return op;
            }
        }
        return  null;}


    public abstract Bson execute(ArrayList<Bson> bsons);
}
