package com.deepshiftlabs.sf_tests;

public class urlElement extends genericTextElement {

	urlElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_validValue, boolean a_isRequired){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired, 255);
        values.add(new checkValue("value", constants.IT_IS_VALID_VALUE));
        values.add(new checkValue("1probel 2probel  3probel   a", constants.IT_IS_VALID_VALUE, "", "http://1probel 2probel  3probel"));        
        values.add(new checkValue("//value skobka>>bear", constants.IT_IS_VALID_VALUE, "", "http:////value skobkabear"));
        values.add(new checkValue("http://value skobka>>bear", constants.IT_IS_VALID_VALUE, "", "http://value skobkabear"));
        values.add(new checkValue("http:/value skobka>>bear", constants.IT_IS_VALID_VALUE, "", "http://http:/value skobkabear"));
        values.add(new checkValue("1probel 2probel  3probel   a", constants.IT_IS_VALID_VALUE, "", "http://1probel 2probel 3probel a"));
    }
}
