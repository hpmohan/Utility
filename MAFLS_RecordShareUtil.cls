public class MAFLS_RecordShareUtil {
    Static String opDivisionFieldApiName;
    Static String opRegionFieldApiName;
    Static Schema.SObjectType sObjType;
    Static Schema.SObjectType shareObjType;
    Static Boolean isSharingForStandardObject; 
    Static Boolean isCreateMissingGroupsEnabled = true;
    
    public static void shareRecords(List<sObject> recordsToShare, 
                                    Map<Id,sObject> oldMap, 
                                    String operationRegionFieldApiName,
                                    String operationDivisionFieldApiName)
    {
        if(recordsToShare.isEmpty()) return;
        opDivisionFieldApiName = operationDivisionFieldApiName;
        opRegionFieldApiName = operationRegionFieldApiName;
        
        setObjectAndShareObjectTypes(recordsToShare[0]);
        if(trigger.isAfter && Trigger.isInsert){
            shareToGroups(recordsToShare);
        } else if(trigger.isAfter && Trigger.isUpdate){
            List<SObject> validRecords = deleteExistingSharing(recordsToShare, oldMap);
            if(!validRecords.isEmpty()){
                shareToGroups(validRecords);
            }
        }
    } 
    
    private static void setObjectAndShareObjectTypes(Sobject rec){
        sObjType = rec.Id.getSobjectType();
        String shareObjApiName;
        String objApiName = String.valueOf(sObjType);
        
        if(objApiName.endsWith('__c')){
            isSharingForStandardObject = false;
            shareObjApiName = objApiName.removeEnd('__c')+'__Share';
        } else{
            isSharingForStandardObject = true;
            shareObjApiName = objApiName+'Share';
        }
        System.debug('--shareObjApiName--'+shareObjApiName);
        shareObjType = Schema.getGlobalDescribe().get(shareObjApiName);
    }
    
    private static List<SObject> deleteExistingSharing(List<sObject> records, Map<Id,sObject> oldMap){
        Map<Id, SObject> validRecords = new Map<Id, sObject>();
        Map<String, Set<Id>> groupNameToRecodIdsMap = new Map<String, Set<Id>>();
        Set<Id> recordIdSet = new Set<Id>();
        for(sObject rec : records){
            sObject oldRec = oldMap.get(rec.Id);
            String oldDivisions = (String)oldRec.get(opDivisionFieldApiName);
            String oldRegions = (String)oldRec.get(opRegionFieldApiName);
            String newDivisions = (String)rec.get(opDivisionFieldApiName);
            String newRegions = (String)rec.get(opRegionFieldApiName);
            
            if((newDivisions != oldDivisions) || (newRegions != oldRegions)){
                //--there is a change in one of the fields.
                //--Delete old sharing for these records ONLY
                //--Recreate new sharing for these records ONLY.
                //--Get previous groups
                for(String division : oldDivisions.split(';')){
                    for(String region : oldRegions.split(';')){
                        String groupName = region +'-'+division;
                        validRecords.put(rec.Id, rec);
                        if(groupNameToRecodIdsMap.containsKey(groupName)){
                            groupNameToRecodIdsMap.get(groupName).add(rec.Id);
                        } else {
                            groupNameToRecodIdsMap.put(groupName, new Set<Id>{ rec.Id });
                        }
                    }
                }
            } else if(isSharingForStandardObject && (rec.get('ownerId') != oldRec.get('ownerId'))){
                //--We are shaing for standard objects, and record owner was changed
                //--Already shared records will be deleted
                //--Create new sharings
                validRecords.put(rec.Id, rec);
            }
        }
        System.debug('--DELETE:groupNameToRecodIdsMap--'+groupNameToRecodIdsMap);
        if(!groupNameToRecodIdsMap.isEmpty()){
            Set<String> groupNames = groupNameToRecodIdsMap.keySet();
            String query = 'SELECT Id, UserOrGroupId, UserOrGroup.Name, RowCause, ParentId ';
            query += ' FROM '+  String.valueOf(shareObjType) + '';
            query += ' WHERE ParentId IN :recordIdSet';
            query += ' AND UserOrGroup.Name IN :groupNames';
            System.debug('--DELETE:query--'+query);
            List<sObject> oldRecords = Database.query(query);
            System.debug('--DELETE:oldRecords--'+oldRecords);
            Database.delete(oldRecords, false);
        }
        System.debug('--validRecords.values()--'+validRecords.values());
        return validRecords.values();
    }
    
    private static void shareToGroups(List<sObject> records){
        Map<String, Set<Id>> groupNameToRecordsToShareMap = new Map<String, Set<Id>>();
        List<sObject> sharedRecords = new List<sObject>();
        Map<String, Id> groupNameToIdMap = new Map<String, Id>();
        List<Group> newGroups = new List<Group>();
        
        for(SObject rec : records){
            if(rec.get(opDivisionFieldApiName) != null && rec.get(opRegionFieldApiName) != null){
                for(String opDivision : ((String)rec.get(opDivisionFieldApiName)).split(';')){
                    for(String opRegion : ((String)rec.get(opRegionFieldApiName)).split(';')){
                        String groupName = opRegion + '-' +opDivision;
                        if(groupNameToRecordsToShareMap.containsKey(groupName)){
                            groupNameToRecordsToShareMap.get(groupName).add(rec.Id);
                        } else {
                            groupNameToRecordsToShareMap.put(groupName, new Set<Id>{ rec.Id });
                        }
                    }
                }
            }
        }
        System.debug('--groupNameToRecordsToShareMap--'+groupNameToRecordsToShareMap);
        for(Group gp :[SELECT Id, Name FROM Group WHERE NAME IN :groupNameToRecordsToShareMap.keySet()]){
            groupNameToIdMap.put(gp.Name, gp.Id);
        }
        
        if(isCreateMissingGroupsEnabled){
            //--To dynamically create new group
            for(String newGrpName : groupNameToRecordsToShareMap.keySet()){
                if(!groupNameToIdMap.containsKey(newGrpName)){
                    Group newGroup = new Group(Name = newGrpName, Type='Regular');
                    newGroups.add(newGroup);
                }
            }
            if(!newGroups.isEmpty()){
                System.debug('--Creating new group while sharing records--'+newGroups);
                INSERT newGroups;
                for(Group gp: newGroups){
                    groupNameToIdMap.put(gp.Name, gp.Id);
                }
            }
        }
        
        System.debug('--groupNameToIdMap--'+groupNameToIdMap);
        for(String grpName : groupNameToRecordsToShareMap.keySet()){
            if(groupNameToIdMap.containsKey(grpName)){
                for(Id recId : groupNameToRecordsToShareMap.get(grpName)){
                    sObject shareRec = shareObjType.newSObject();
                    shareRec.put('AccessLevel','Edit');
                    //shareRec.put('rowCause','Apex__c');
                    shareRec.put('ParentID', recId);
                    shareRec.put('UserOrGroupId', groupNameToIdMap.get(grpName));
                    sharedRecords.add(shareRec);
                }
            } else if(trigger.isExecuting && !isCreateMissingGroupsEnabled){
                //--TODO
                //--Can create a group with the same name and add the record. why to bother the user?
                records[0].addError('A group with name \''+ grpName.trim()+ '\' is not found. Please create it with same name to proceed further.');
            }
        }
        System.debug('--sharedRecords--'+sharedRecords);
        if(!sharedRecords.isEmpty()){
            Database.SaveResult[] srList = Database.insert(sharedRecords,false);
            for (Database.SaveResult sr : srList) {
                if (!sr.isSuccess()) {
                    //--Operation failed,get all errors                
                    for(Database.Error err : sr.getErrors()) {
                        System.debug('--The following error has occurred while sharing the record.');                    
                        System.debug('--'+err.getStatusCode() + ': ' + err.getMessage());
                        System.debug('--Record fields that affected this error: ' + err.getFields());
                    }
                }
            }
        }
    }
}
