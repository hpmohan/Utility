/*
* Class Name : AccessCheck
* Author : Persistent Systems
* Desc : This Utility method is used to check the Object level CRUD permission and FLS before performing any DML Operations
*    The code dynamically parses all the involved fields in the sObject instance(s) passed and throws Exception/returns true based on CRUD and FLS 
*    permissions.
*    
*    1. Insert Operation 
*		[It will throw Exception/return true after validating Object Level Create and Field Level Creatable and Accessible values ]
*    	Example:
*    
*   	 Contact c = new contact(FirestName==’ABC’, LastName=’xyz’,CustomField__c=’Some Data’ );
*   	 or
*   	 Contact c = new contact();
*   	 FirestName==’ABC’;
*   	 LastName=’xyz’;
*   	 CustomField__c=’Some Data’ ; 
*   	 
*   	 Use of Utility AccessCheck.isCreateable(c);
*    
*    2.  Update Operation [It will throw Exception/return true after validating Object Level Update and Field Level Up-datable and Accessible values  ]
*    3.  Delete Operation [throw Exception/return true on object level Delete permission, no need of FLS check]
*
*/

public  class AccessCheck {
    //Static Variable to store the result across complete transaction
    public static map<Schema.sObjectType,ObjectDetails> accessDetails = new map<Schema.sObjectType,ObjectDetails>();
    
    
    /* 
       @Name : isCreateable
       @return type : boolean
       @Parameters : sObject obj, Map<String,Object> fieldsMap
       @Desc : after validating Object Level create and Field Level create and Accessible values, returns true if access is proper
       			else throws a custom exception
	   @Note : User should send Map<String,Object> using obj.getPopulatedFieldsAsMap() while sending a single sObject
	*/
    public static boolean isCreateable(sObject obj,Map<String,Object> fieldsMap){
    	system.debug('1. SObject '+obj);
    	system.debug('fieldsMap '+ fieldsMap);
        Schema.sObjectType objType= initObjectAccess(obj);
        system.debug('9. Crud --> '+ objType.getDescribe().isCreateable());
        system.debug('10. Crud2--> '+ AccessCheck.accessDetails.get(objType).objectAccess.isCreateable());
        system.debug('11 objectDetail '+AccessCheck.accessDetails.get(objType));
        //Proceed only if object level Create is available
        if(AccessCheck.accessDetails.get(objType).objectAccess.isCreateable()){
            //getting all the related fields FLS
            for(String fieldAPI : fieldsMap.keySet()){
            	System.debug('12. No Access On Field '+ fieldAPI);
            	System.debug('13. No Access On Field '+ AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI));
            	//This logic in if is implemented to prevent null pointer exception because fieldsMap can contain standard fields which
            	//are not createable or updatable. This fields are not present in FieldDetailsMap as we are excluding these fields while 
            	//creating FieldDetails map.
            	Schema.DescribeFieldResult fieldDescribeResult =  AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI);
                if(fieldDescribeResult !=null && (!AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI).isAccessible() ||
                   !AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI).isCreateable())){
                   	System.debug('14. No Access On Field '+ fieldAPI);
                   	//Throw exception if FLS permission not present
                    throw new FLSException(objType,fieldAPI);
                   }else{
                       System.debug('15. Have Access On Field '+ fieldAPI);	
                   }
            }
        }else{
        	System.debug('16. No Access Object '+ objType);
        	//Throw exception if object permission not present
            throw new CRUDException(objType);
        }
        System.debug('17. Before return  '+ fieldsMap);
        return true;
    }
    
    /* 
       @Name : isCreateable
       @return type : boolean
       @Parameters : List<sObject> objList
       @Desc : Method to validate List<sObject> instead of a single sObject, internally uses isCreateable(sObject obj,Map<String,Object> fieldsMap)
       @Note : This method should be used while inserting a list of sObject of single or mixed type
    */
    public static boolean isCreateable(List<sObject> objList){
    	Set<String> types = new Set<String>();	    	//This set is used to store the type of sObjects(when user enters multiple types of sObjects)
    	Map<String,Map<String,Object>> objectTypeToFieldsMap = new Map<String,Map<String,Object>>();	//this map stores objectType to map<fields, Sobject> as key value pair Ex : {contact(type)=>{Name(field),contact(object)}}
    																									//internal map<String,Sobject> stores all the fields populated in sobject List and a sobject to represent it.
    	List<sObject> newObjectList = new List<sObject>();				    	//stores only one sObject of its type	
    	for(sObject obj: objList){
    		String objType = String.valueOf(obj.getSObjectType());
    		//loops for each object and adds an object to the new list for each new type
    		if(!types.contains(objType)){
    			types.add(objType);
    			newObjectList.add(obj);
    			//Initialise the field map for each new object type
    			objectTypeToFieldsMap.put(objType,new Map<String,Object>());
    		}
    		//Iterate on each field of each object and add it to the corresponding map
    		for(String field : (obj.getPopulatedFieldsAsMap()).keySet()){
	    		if(!objectTypeToFieldsMap.get(objType).containsKey(field)){
	    			objectTypeToFieldsMap.get(objType).put(field,obj);
	    		}
    		}
    	}
        boolean result =  true;
    	for(sObject obj: newObjectList){
    		//check CRUD and FLS for each sObjectType
    		String typeObj = String.valueOf(obj.getSObjectType());
            if(!AccessCheck.isCreateable(obj,objectTypeToFieldsMap.get(typeObj))){
    			result =  false;
    			break;
    		}    		
    	}
    	return result;
    }    
     
    
    /* @Name : isUpdateable
       @return type : boolean
       @Parameters : sObject obj,Map<String,Object> fieldsMap
       @Desc : after validating Object Level Update and Field Level Up-datable and Accessible values  
    			throws exception if access is not available
       @Note : User should send Map<String,Object> using obj.getPopulatedFieldsAsMap() while sending a single sObject
    	*/
    public static boolean isUpdateable(sObject obj,Map<String,Object> fieldsMap){
        Schema.sObjectType objType= initObjectAccess(obj);
        /*Proceed only if object level Update is available
        For Create and Update we do not need to check parent/relationship fields*/
        if( AccessCheck.accessDetails.get(objType).objectAccess.isUpdateable()){
            for(String fieldAPI : fieldsMap.keySet()){
                Schema.DescribeFieldResult fieldDescribeResult =  AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI);
                if(fieldDescribeResult !=null && (!AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI).isAccessible() ||
                   !AccessCheck.accessDetails.get(objType).fieldDetails.get(fieldAPI).isCreateable())){
                    throw new FLSException(objType,fieldAPI);
                   }
            }
        }else{
            throw new CRUDException(objType);
        }
        return true;
    }
    
    /* @Name : isUpdateable
       @return type : boolean
       @Parameters : List<sObject> objList
       @Desc : method to validate List<sObject> instead of a single sObject
       @Note : This method should be used while inserting a list of sObject of single or mixed type
       */
    public static boolean isUpdateable(List<sObject> objList){
        Set<String> types = new Set<String>();
    	Map<String,Map<String,Object>> objectTypeToFieldsMap = new Map<String,Map<String,Object>>();
    	List<sObject> newObjectList = new List<sObject>();
    	for(sObject obj: objList){
    		String objType = String.valueOf(obj.getSObjectType());
    		if(!types.contains(objType)){
    			types.add(objType);
    			newObjectList.add(obj);
    			objectTypeToFieldsMap.put(objType,new Map<String,Object>());
    		}
    		for(String field : (obj.getPopulatedFieldsAsMap()).keySet()){
	    		if(!objectTypeToFieldsMap.get(objType).containsKey(field)){
	    			objectTypeToFieldsMap.get(objType).put(field,obj);
	    		}
    		}
    	}
        boolean result =  true;
    	for(sObject obj: objList){
    	    String typeObj = String.valueOf(obj.getSObjectType());
    		if(!AccessCheck.isUpdateable(obj,objectTypeToFieldsMap.get(typeObj))){
    			result =  false;
    			break;
    		}
    	}
    	return result;
    } 
       
    /* @Name : isAccessible
       @return type : boolean
       @Parameters : sObject obj
       @Desc : method to check accessibility of sObject*/
    public static boolean isAccessible(sObject obj){
        Schema.sObjectType objType= initObjectAccess(obj);
        return AccessCheck.accessDetails.get(objType).objectAccess.isAccessible();
    }
    
    /* @Name : isDeletable
       @return type : boolean
       @Parameters : sObject obj
       @Desc :method to check object level Delete permission, no need of FLS check for delete
       		throws exception if permission not found, else returns true
       */
    public static boolean isDeletable(sObject obj){
        Schema.sObjectType objType= initObjectAccess(obj);
        return AccessCheck.accessDetails.get(objType).objectAccess.isDeletable();
    }
    public static boolean isDeletable(List<sObject> objList){
    	Set<Schema.sObjectType> objTypeList =  new Set<Schema.sObjectType>();
    	for(sObject obj: objList){
    		if(!objTypeList.contains(initObjectAccess(obj))){
        		objTypeList.add(initObjectAccess(obj));
    		}
    	}
    	for(Schema.sObjectType objtype: objTypeList){
    		if(!AccessCheck.accessDetails.get(objType).objectAccess.isDeletable()){
    			throw new CRUDException(objType);
    		}
    	}
        return true;
    }
    
    /*@Name : initObjectAccess
       @return type : Schema.sObjectType
       @Parameters : sObject obj
       @Desc : This method will create object and its field definition */
    public static Schema.sObjectType initObjectAccess(sObject obj){
    	system.debug('2. Inside initObjectAccess');
        Schema.sObjectType objType= obj.getSObjectType();
        	system.debug('3. outside initObjectAccess if '+AccessCheck.accessDetails.containsKey(objType));
        if(!AccessCheck.accessDetails.containsKey(objType)){
        	system.debug('4. Inside initObjectAccess if ');
            AccessCheck.accessDetails.put(objType,new ObjectDetails(objType));
        }
        system.debug('8. returning objType ');
        return objType;
    }
    
    
    /* @Name : ObjectDetails
       @ type : wrapper class
       @Desc :Inner class to store Object and field describe result
       */
    public class ObjectDetails{
        Schema.DescribeSObjectResult  objectAccess;
        Map <String, Schema.DescribeFieldResult> fieldDetails=new Map <String, Schema.DescribeFieldResult>();
        public ObjectDetails(Schema.sObjectType objType){
        	system.debug('5. Inside Constructor ObjectDetails');
            try{
                objectAccess = objType.getDescribe();
                system.debug('6. Inside Constructor ObjectDetails '+objectAccess);
                for(Schema.SObjectField tempFieldToken : objectAccess.fields.getMap().values()){
                	//Exclude fields which are standard or formula fields.
                	if(tempFieldToken.getDescribe().isCustom() &&(!tempFieldToken.getDescribe().isCalculated())) {
                		// system.debug('fieldToken '+tempFieldToken);
                       	fieldDetails.put(tempFieldToken+'',tempFieldToken.getDescribe()) ;
                	}
                }
                system.debug('7. Adding Fields '+fieldDetails);
           }catch(Exception e){
           	     system.debug('error  '+e);
                throw new AccessCheckException('Unable to get describe result');
            }
        }
    }
    /* @Name : AccessCheckException
       @ type : Exception class
       @Desc :Parent Exception class to represent access Security Exceptions
       */
    virtual class AccessCheckException extends Exception{

    }
    /* @Name : CRUDException
       @ type : Exception class
       @Desc : Exception class to represent CRUD related Security Exceptions
       */
    public class CRUDException extends AccessCheckException{
        public CRUDException(SObjectType objType){
			this('Access Denied: on ' + objType);
		}
    }
    /* @Name : FLSException
       @ type : Exception class
       @Desc : Exception class to represent FLS related Security Exceptions
       */
    public class FLSException extends AccessCheckException{
        public FLSException(SObjectType objType, String field){
			this('Access Denied: on ' + objType + '.' + field);
		}
    }
    
    
    
}

