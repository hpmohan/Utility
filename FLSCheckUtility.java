public without sharing class FLSCheckUtility {
	/***************************************************************************************************************
	 * Methods:
	 *  1. isAccessible(String sObjectType, String commaDelimitedFields)	// for CRUD and FLS check multiple fields
	 *  2. isAccessible(String sObjectType) 								// for CRUD check of single object
	 *  3. isCreateable(String sObjectType, String commaDelimitedFields)	// for 
	 *  4. isCreateable(String sObjectType)
	 *  5. isUpdateable(String sObjectType, String commaDelimitedFields)
	 *  6. isDeletable(String sObjectType)
	 *  7. isDeletable(String sObjectType)
	 * **************************************************************************************************************/
    
    public static boolean isAccessible(String sObjectType, String commaDelimitedFields){
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            if (!fields.get(field).getDescribe().isAccessible()){
                noAccessfields.add(field);
            }
        }
        if(!noAccessfields.isEmpty()){
            createLog(sObjectType, noAccessfields, 'FLS Check isAccessible');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isCreateable(String sObjectType, String commaDelimitedFields){
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            if (!fields.get(field).getDescribe().isCreateable()){
                noAccessfields.add(field);
            }
        }
        if(!noAccessfields.isEmpty()){
            createLog(sObjectType, noAccessfields, 'FLS Check isCreateable');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isUpdateable(String sObjectType, String commaDelimitedFields){
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            if (!fields.get(field).getDescribe().isUpdateable()){
                noAccessfields.add(field);
            }
        }
        if(!noAccessfields.isEmpty()){
            createLog(sObjectType, noAccessfields, 'FLS Check isUpdateable');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
       
    public static boolean isAccessible(String sObjectType){
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isAccessible()){
            createLog(sObjectType, 'Missing isAccessible permission on Object.');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isCreateable(String sObjectType){
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isCreateable()){
            createLog(sObjectType, 'Missing isCreateable permission on Object.');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isUpdateable(String sObjectType){
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isUpdateable()){
            createLog(sObjectType, 'Missing isUpdateable permission on Object.');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isDeletable(String sObjectType){
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isDeletable()){
            createLog(sObjectType, 'Missing isDeletable permission.');
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
    
    private static void createLog(String sObjectType, List<String> noAccessfields, string logType){
        Batch_Log__c log = new Batch_Log__c();
        log.Process_Type__c = 'CRUD FLS missing for query';
        String text = 'CRUD FLS missing for fields : ';
        text += String.join(noAccessfields, ', ');
        text += 'on Object : '+sObjectType;
        log.Error_Message__c = text;
        log.Name = logType;
        insert log;
    }
    private static void createLog(String sObjectType, string logType){
        Batch_Log__c log = new Batch_Log__c();
        log.Process_Type__c = 'CRUD missing on Object';
        String text = 'CRUD missing on Object : '+sObjectType;
        log.Error_Message__c = text;
        log.Name = logType;
        insert log;
    }
    
}
