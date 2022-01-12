package org.accounting.system.services;

import io.vavr.control.Try;
import org.accounting.system.parsetypes.Common;
import org.accounting.system.parsetypes.FileToJson;
import org.accounting.system.parsetypes.ParseJson;
import org.accounting.system.parsetypes.UnitType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ReadPredefinedTypesService {

    @ConfigProperty(name = "unit.types.file")
    String unitFile;

    @UnitType
    ParseJson parseJson;

    public String getUnitTypes(){
        return Try
                .of(()-> Common.fileToJson(unitFile, parseJson))
                .map(FileToJson::getFileToString)
                .getOrElseThrow(throwable -> {
                    throw new RuntimeException("Internal Server Error.");});
    }

    public Optional<String> searchForUnit(String unit){
        return Try
                .of(()-> Common.fileToJson(unitFile, parseJson))
                .map(FileToJson::getAvailableTypes)
                .getOrElseThrow(throwable -> {throw new RuntimeException("Internal Server Error.");})
                .stream()
                .filter(type->type.equals(unit))
                .findAny();

    }
}
