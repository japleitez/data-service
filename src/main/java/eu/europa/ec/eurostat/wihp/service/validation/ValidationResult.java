package eu.europa.ec.eurostat.wihp.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult<T> {

    private List<T> validList =  new ArrayList<>();
    private List<T> invalidList =  new ArrayList<>();

    public ValidationResult(){}

    public ValidationResult(List<T> inValidList, List<T> validList){
        this.validList = validList;
        this.invalidList = inValidList;
    }
    public List<T> getValidList(){return validList;}
    public List<T> getInvalidList(){return invalidList;}

}
