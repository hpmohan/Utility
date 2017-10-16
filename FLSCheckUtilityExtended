public with sharing class FLSCheckUtility {
    /***************************************************************************************************************
     * Methods:
     *  1.  isFieldsAccessible(String sObjectType, String commaDelimitedFields)  //for fields accessible check
     *  2.  isFieldsCreateable(String sObjectType, String commaDelimitedFields)  //for fields creatable check
     *  3.  isFieldsUpdateable(String sObjectType, String commaDelimitedFields)  //for fields updatable check
     *  4.  isObjectAccessible(String sObjectType)                               //for object accessible check
     *  5.  isObjectCreateable(String sObjectType)                               //for object creatable check
     *  6.  isObjectUpdateable(String sObjectType)                               //for object updatable check
     *  7.  isObjectUpsertable(String sObjectType)                               //for object Upsertable check
     *  8.  isObjectDeletable(String sObjectType)                                //for object deletable check
     *  9.  checkFLSforSingleField(String sObjectType, String fieldName)         // for CRUD check of single object
     *  10. createLog(String sObjectType, List<String> noAccessfields, string logType) //Log creation
     *          : Multiple Fields Logger :> create a new Record in BatchLog, based on custom setting setting.
     *  11. createLog(String sObjectType, string logType)                        //log creation
     *          : Sinlge Object Logger :> create a new Record in BatchLog, based on custom setting setting.
     *  12. createExceptionLog(String debugString)
     *  13. setToString(Set<String> stringSet)									//to comvert set of field api's to string with comma separated
     *  
     * Return Type : Boolean 
     *  1.  True : Sufficient Access
     *  2.  False: Insufficient Access
     * 
     * Exceptions : None
     * **************************************************************************************************************/
    static final String namespace;
    static final ID PROFILEID;
    
    static {
        namespace = SystemUtils.getNameSpace();
        PROFILEID = UserInfo.getProfileId();
    }
    
    public static boolean isFieldsAccessible(String sObjectType, String commaDelimitedFields){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            field = field.replaceAll(' ', '');
            //adding namespace for custom fields
            if(field.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
                field = namespace+field;
            }
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
    
    public static boolean isFieldsCreateable(String sObjectType, String commaDelimitedFields){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            field = field.replaceAll(' ', '');
            //adding namespace for custom fields
            if(field.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
                field = namespace+field;
            }
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
    
    public static boolean isFieldsUpdateable(String sObjectType, String commaDelimitedFields){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        List<String> noAccessfields = new List<String>();
        for (String field : commaDelimitedFields.split(',')){
            field = field.replaceAll(' ', '');
            //adding namespace for custom fields
            if(field.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
                field = namespace+field;
            }
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
       
    public static boolean isObjectAccessible(String sObjectType){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isAccessible()){
            createLog(sObjectType, 'Missing isAccessible permission on Object.');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isObjectCreateable(String sObjectType){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isCreateable()){
            createLog(sObjectType, 'Missing isCreateable permission on Object.');
            return FALSE; // No Access.
        }
        //Accessible
        return TRUE;
    }
    
    public static boolean isObjectUpdateable(String sObjectType){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isUpdateable()){
            createLog(sObjectType, 'Missing isUpdateable permission on Object.');
            return FALSE; // No Access.
        }
        return TRUE; //Accessible
    }
    public static boolean isObjectUpsertable(String sObjectType){
        if(isObjectCreateable(sObjectType) && isObjectUpdateable(sObjectType)){
        	return TRUE;
        }
        return FALSE;
    }
    
    public static boolean isObjectDeletable(String sObjectType){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        Schema.SObjectType gd = Schema.getGlobalDescribe().get(sObjectType); 
        if(!gd.getDescribe().isDeletable()){
            createLog(sObjectType, 'Missing isDeletable permission.');
            return FALSE; // No Access.
        }
        return TRUE; //Accessible
    }
    
    // 
    // FLS check for SINLGE FIELD.
    // Returns TRUE if user has access to field named 'fieldName'.
    // No exception handling. (Make sure to pass only valid Fields and sObjectType)
    // 
    public static boolean checkFLSforSingleField(String sObjectType, String fieldName){
        //adding namespace for custom Object
        if(sObjectType.endsWith('__c') && !sObjectType.containsIgnoreCase(namespace)){
            sObjectType = namespace+sObjectType;
        }
        fieldName = fieldName.endsWith('__c') ? namespace+fieldName : fieldName;
        SObjectType schemaType = Schema.getGlobalDescribe().get(sObjectType);
        Map<String, SObjectField> fields = schemaType.getDescribe().fields.getMap();
        
        DescribeFieldResult fieldDescribe = fields.get(fieldName).getDescribe();
        return fieldDescribe.isAccessible();
    }
    
    private static void createLog(String sObjectType, List<String> noAccessfields, string logType){
        Boolean enableCrudFLS = FALSE;
        LogManager__c logManager = LogManager__c.getInstance('Logs');
        if(logManager!=null && logManager.Enable_CrudFls_Logs__c){
            enableCrudFLS = TRUE;
        }
        if(Schema.sObjectType.Batch_Log__c.fields.Name.isCreateable()
           && Schema.sObjectType.Batch_Log__c.fields.Process_Type__c.isCreateable() 
           && Schema.sObjectType.Batch_Log__c.fields.Error_Message__c.isCreateable()
           && enableCrudFLS){
            Batch_Log__c log = new Batch_Log__c();
            log.Process_Type__c = 'CRUD FLS missing for query';
            String text = 'CRUD FLS missing for fields : ';
            text += String.join(noAccessfields, ', ');
            text += 'on Object : '+sObjectType + ' for Profile : '+PROFILEID;
            log.Error_Message__c = text;
            log.Name = logType;
            if(Schema.sObjectType.Batch_Log__c.isCreateable())
                insert log;
        }
    }
    private static void createLog(String sObjectType, string logType){
        Boolean enableCrudFLS = FALSE;
        LogManager__c logManager = LogManager__c.getInstance('Logs');
        if(logManager!=null && logManager.Enable_CrudFls_Logs__c){
            enableCrudFLS = TRUE;
        }
        if(Schema.sObjectType.Batch_Log__c.fields.Name.isCreateable()
           && Schema.sObjectType.Batch_Log__c.fields.Process_Type__c.isCreateable() 
           && Schema.sObjectType.Batch_Log__c.fields.Error_Message__c.isCreateable()
           && enableCrudFLS){
            Batch_Log__c log = new Batch_Log__c();
            log.Process_Type__c = 'CRUD missing on Object for Profile : '+PROFILEID;
            String text = 'CRUD missing on Object : '+sObjectType;
            log.Error_Message__c = text;
            log.Name = logType;
            if(Schema.sObjectType.Batch_Log__c.isCreateable())
                insert log;
        }
    }
    
    public static void createExceptionLog(String debugString){
        Boolean enableExceptionLog = FALSE;
        LogManager__c logManager = LogManager__c.getInstance('Logs');
        if(logManager!=null && logManager.Enable_Exception_logs__c){
            enableExceptionLog = TRUE;
        }
        if(Schema.sObjectType.Batch_Log__c.fields.Name.isCreateable()
           && Schema.sObjectType.Batch_Log__c.fields.Process_Type__c.isCreateable() 
           && Schema.sObjectType.Batch_Log__c.fields.Error_Message__c.isCreateable()
           && enableExceptionLog){
            Batch_Log__c log = new Batch_Log__c();
            log.Process_Type__c = 'System debug/ Apex Exception';
            String text = debugString;
            log.Error_Message__c = text;
            log.Name = 'System debug/ Apex Exception';
            if(Schema.sObjectType.Batch_Log__c.isCreateable())
                insert log;
        }
    }
    
    public static string setToString(Set<String> stringSet){
    	List<String> newStringList = new List<String>();
    	
    	newStringList.addAll(stringSet);
		String singleString = '';
		singleString = String.join(newStringList,',');
		
		return singleString; 
    }   
}
