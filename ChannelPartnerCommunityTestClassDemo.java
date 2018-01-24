public class AccountTraversalwithATM_Test{
    static testMethod void createAccount(){
        UserRole portalRole = [Select Id From UserRole Where PortalType = 'None' Limit 1];
        Profile profile1 = [Select Id from Profile where name = 'System Administrator' LIMIT 1];      
        User portalAccountOwner1 = new User(
            UserRoleId = portalRole.Id,
            ProfileId = profile1.Id,
            Username = System.now().millisecond() + 'test2@test.com',
            Alias = 'batman',
            Email='bruce.wayne@wayneenterprises.com',
            EmailEncodingKey='UTF-8',
            Firstname='Bruce',
            Lastname='Wayne',
            LanguageLocaleKey='en_US',
            LocaleSidKey='en_US',
            TimeZoneSidKey='America/Chicago'
           
        );
        Database.insert(portalAccountOwner1);

        Account a = new Account();
        Contact c = new Contact();
       
       System.runAs (portalAccountOwner1 ) { 
        a = new Account( Name ='TestAccount',OwnerId = portalAccountOwner1.Id);
        insert a;
        
        c = New Contact(LastName = 'Test', AccountID = a.id);
        insert c;
       }
       
        User newUser = createPartnerUser(c.id);        
        system.RunAs(newUser){
            a = [Select isPartner From Account where ID = :a.id];
            //system.assert(a.isPartner,'Is Partner flag was not set to true');
            List<Account> accListChild = new List<Account>();
            for(Integer i =0; i<3; i++){
                Account childAcc = new Account(Name='ChildAccount' +i, parentId= a.Id);
                accListChild.add(childAcc);
            }
            test.startTest();
            insert accListChild;
            test.stopTest();
            
            
            Contact c1 = new Contact(LastName = 'TestContact', AccountID = a.id);
            insert c1;
           }
          /*  newUser = createPartnerUser(c1.id);
            }
            AccountTeamMember atm = new AccountTeamMember(AccountId = accListChild[0].id,TeamMemberRole = 'Partner User' , UserId=newUser.id );
            insert atm;
            
            accListChild[1].ParentId = accListChild[0].Id;
            update accListChild[1];
        }*/
        
    }
    
    static user createPartnerUser(ID cId){    
         
            user u = new User();                            
            Profile p = [Select ID, Name from Profile Where Name = 'Partner Community User' LIMIT 1];
            u = New User(
                UserName = 'test_' + math.random() + '@test.com',
                FirstName = 'Test-First',
                LastName = 'Test-Last',
                Alias = 'test',
                email = 'test' + math.random() + '@test.com',
                CommunityNickName = string.valueOf(math.random()).substring(0,6),
                ProfileID = p.id,
                TimeZoneSidKey = 'America/New_York', 
                LocaleSidKey = 'en_US', 
                EmailEncodingKey = 'UTF-8', 
                LanguageLocaleKey = 'en_US',
                ContactID = cId,
                PortalRole = 'Manager'               
                );
        insert u;     
   //  }
     return u;
     }
}
