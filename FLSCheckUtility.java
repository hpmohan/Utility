public class FLSCheckUtility {
    // FLS check for List of Fields
    // Returns TRUE if user has access to all fields in 'commaDelimitedFields' arguments.
    // No exception handling. (Make sure to pass only valid Fields and sObjectType)
    // 
    public static boolean checkFLS(String sObjectType, String commaDelimitedFields){
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        
        for (String field : commaDelimitedFields.split(',')){
            if (!fields.get(field).getDescribe().isAccessible())
                return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    // 
    // FLS check for SINLGE FIELD.
    // Returns TRUE if user has access to field named 'fieldName'.
    // No exception handling. (Make sure to pass only valid Fields and sObjectType)
    // 
    public static boolean checkFLSforSingleField(String sObjectType, String fieldName){
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        
        DescribeFieldResult fieldDescribe = fields.get(fieldName).getDescribe();
        return fieldDescribe.isAccessible();
    }
    
}