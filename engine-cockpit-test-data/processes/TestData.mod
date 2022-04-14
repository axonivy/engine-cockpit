[Ivy]
16E88DD61E825E70 9.4.7 #module
>Proto >Proto Collection #zClass
Ta0 TestData Big #zClass
Ta0 B #cInfo
Ta0 #process
Ta0 @TextInP .colors .colors #zField
Ta0 @TextInP color color #zField
Ta0 @TextInP .type .type #zField
Ta0 @TextInP .processKind .processKind #zField
Ta0 @TextInP .xml .xml #zField
Ta0 @TextInP .responsibility .responsibility #zField
Ta0 @StartRequest f0 '' #zField
Ta0 @EndTask f1 '' #zField
Ta0 @StartRequest f3 '' #zField
Ta0 @EndTask f4 '' #zField
Ta0 @GridStep f5 '' #zField
Ta0 @PushWFArc f6 '' #zField
Ta0 @PushWFArc f7 '' #zField
Ta0 @GridStep f8 '' #zField
Ta0 @PushWFArc f9 '' #zField
Ta0 @PushWFArc f2 '' #zField
Ta0 @StartRequest f10 '' #zField
Ta0 @GridStep f11 '' #zField
Ta0 @EndTask f12 '' #zField
Ta0 @PushWFArc f13 '' #zField
Ta0 @PushWFArc f14 '' #zField
Ta0 @EndTask f15 '' #zField
Ta0 @DBStep f16 '' #zField
Ta0 @StartRequest f17 '' #zField
Ta0 @PushWFArc f18 '' #zField
Ta0 @PushWFArc f19 '' #zField
Ta0 @ErrorBoundaryEvent f20 '' #zField
Ta0 @DBStep f21 '' #zField
Ta0 @PushWFArc f22 '' #zField
Ta0 @PushWFArc f23 '' #zField
Ta0 @EndTask f24 '' #zField
Ta0 @StartRequest f25 '' #zField
Ta0 @GridStep f26 '' #zField
Ta0 @PushWFArc f27 '' #zField
Ta0 @PushWFArc f28 '' #zField
Ta0 @EndTask f29 '' #zField
Ta0 @StartRequest f30 '' #zField
Ta0 @GridStep f31 '' #zField
Ta0 @PushWFArc f32 '' #zField
Ta0 @PushWFArc f33 '' #zField
Ta0 @StartRequest f34 '' #zField
Ta0 @RestClientCall f35 '' #zField
Ta0 @EndTask f36 '' #zField
Ta0 @PushWFArc f37 '' #zField
Ta0 @PushWFArc f38 '' #zField
Ta0 @StartRequest f39 '' #zField
Ta0 @WSElement f40 '' #zField
Ta0 @PushWFArc f41 '' #zField
Ta0 @EndTask f42 '' #zField
Ta0 @PushWFArc f43 '' #zField
Ta0 @StartRequest f44 '' #zField
Ta0 @GridStep f45 '' #zField
Ta0 @EndTask f46 '' #zField
Ta0 @PushWFArc f47 '' #zField
Ta0 @PushWFArc f48 '' #zField
Ta0 @StartRequest f49 '' #zField
Ta0 @GridStep f50 '' #zField
Ta0 @EndTask f51 '' #zField
Ta0 @StartRequest f52 '' #zField
Ta0 @EndTask f53 '' #zField
Ta0 @GridStep f54 '' #zField
Ta0 @PushWFArc f55 '' #zField
Ta0 @PushWFArc f56 '' #zField
Ta0 @PushWFArc f57 '' #zField
Ta0 @PushWFArc f58 '' #zField
>Proto Ta0 Ta0 TestData #zField
Ta0 f0 outLink addAdministrator.ivp #txt
Ta0 f0 inParamDecl '<> param;' #txt
Ta0 f0 requestEnabled true #txt
Ta0 f0 triggerEnabled false #txt
Ta0 f0 callSignature addAdministrator() #txt
Ta0 f0 caseData businessCase.attach=true #txt
Ta0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>addAdministrator.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f0 @C|.responsibility Everybody #txt
Ta0 f0 81 49 30 30 -63 23 #rect
Ta0 f1 337 49 30 30 0 15 #rect
Ta0 f3 outLink createBusinessCalendar.ivp #txt
Ta0 f3 inParamDecl '<> param;' #txt
Ta0 f3 requestEnabled true #txt
Ta0 f3 triggerEnabled false #txt
Ta0 f3 callSignature createBusinessCalendar() #txt
Ta0 f3 caseData businessCase.attach=true #txt
Ta0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createBusinessCalendar.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f3 @C|.responsibility Everybody #txt
Ta0 f3 81 145 30 30 -75 25 #rect
Ta0 f4 337 145 30 30 0 15 #rect
Ta0 f5 actionTable 'out=in;
' #txt
Ta0 f5 actionCode 'import ch.ivyteam.ivy.application.calendar.FreeDate;
import ch.ivyteam.ivy.application.calendar.FreeEasterRelativeDay;
import ch.ivyteam.ivy.application.calendar.FreeDayOfYear;
import ch.ivyteam.ivy.application.calendar.FreeDayOfWeek;
import ch.ivyteam.util.date.Weekday;
import ch.ivyteam.ivy.application.calendar.WorkingTime;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarSettings;

IApplication app = IApplication.current();

app.findEnvironment("test").setBusinessCalendar(app.getActualEnvironment().getBusinessCalendar().get("Luzern"));' #txt
Ta0 f5 168 138 112 44 0 -7 #rect
Ta0 f6 expr out #txt
Ta0 f6 111 160 168 160 #arcP
Ta0 f7 expr out #txt
Ta0 f7 280 160 337 160 #arcP
Ta0 f8 actionTable 'out=in;
' #txt
Ta0 f8 actionCode 'import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

IConfiguration.instance().set("Administrators.admin.Password", "admin");
IConfiguration.instance().set("Administrators.admin.Email", "admin@example.com");' #txt
Ta0 f8 168 42 112 44 0 -7 #rect
Ta0 f9 111 64 168 64 #arcP
Ta0 f2 280 64 337 64 #arcP
Ta0 f10 outLink createBusinessData.ivp #txt
Ta0 f10 inParamDecl '<> param;' #txt
Ta0 f10 requestEnabled true #txt
Ta0 f10 triggerEnabled false #txt
Ta0 f10 callSignature createBusinessData() #txt
Ta0 f10 caseData businessCase.attach=true #txt
Ta0 f10 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createBusinessData.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f10 @C|.responsibility Everybody #txt
Ta0 f10 81 241 30 30 -68 28 #rect
Ta0 f11 actionTable 'out=in;
' #txt
Ta0 f11 actionCode 'import ch.ivyteam.enginecockpit.testdata.businessdata.TestDataCreator;

TestDataCreator.createDemoDataIfNotExist();' #txt
Ta0 f11 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createBusinessData</name>
    </language>
</elementInfo>
' #txt
Ta0 f11 152 234 144 44 -63 -7 #rect
Ta0 f12 337 241 30 30 0 15 #rect
Ta0 f13 111 256 152 256 #arcP
Ta0 f14 296 256 337 256 #arcP
Ta0 f15 337 337 30 30 0 15 #rect
Ta0 f16 actionTable 'out=in;
' #txt
Ta0 f16 dbSql '<?xml version=""1.0"" standalone=""no""?>
<!DOCTYPE ANY_SQL SYSTEM  ""sqlStatements.dtd"">
<ANY_SQL><Verbatim quote=''true''>CREATE TABLE Persons (
    PersonID int,
    LastName varchar(255),
    FirstName varchar(255),
    Address varchar(255),
    City varchar(255)
)</Verbatim></ANY_SQL>' #txt
Ta0 f16 dbUrl realdb #txt
Ta0 f16 dbWizard 'CREATE TABLE Persons (
    PersonID int,
    LastName varchar(255),
    FirstName varchar(255),
    Address varchar(255),
    City varchar(255)
);' #txt
Ta0 f16 lotSize 2147483647 #txt
Ta0 f16 startIdx 0 #txt
Ta0 f16 168 330 112 44 0 -7 #rect
Ta0 f17 outLink runDbExecution.ivp #txt
Ta0 f17 inParamDecl '<> param;' #txt
Ta0 f17 requestEnabled true #txt
Ta0 f17 triggerEnabled false #txt
Ta0 f17 callSignature runDbExecution() #txt
Ta0 f17 caseData businessCase.attach=true #txt
Ta0 f17 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>runDbExecution.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f17 @C|.responsibility Everybody #txt
Ta0 f17 81 337 30 30 -53 22 #rect
Ta0 f18 expr out #txt
Ta0 f18 111 352 168 352 #arcP
Ta0 f19 280 352 337 352 #arcP
Ta0 f20 actionTable 'out=in;
' #txt
Ta0 f20 errorCode ivy:error:database #txt
Ta0 f20 attachedToRef 16E88DD61E825E70-f16 #txt
Ta0 f20 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>ivy:error:database</name>
    </language>
</elementInfo>
' #txt
Ta0 f20 241 369 30 30 0 15 #rect
Ta0 f21 actionTable 'out=in;
' #txt
Ta0 f21 dbSql '<?xml version=""1.0"" standalone=""no""?>
<!DOCTYPE ANY_SQL SYSTEM  ""sqlStatements.dtd"">
<ANY_SQL><Verbatim quote=''true''>SELECT *
FROM Persons</Verbatim></ANY_SQL>' #txt
Ta0 f21 dbUrl realdb #txt
Ta0 f21 dbWizard 'SELECT *
FROM Persons' #txt
Ta0 f21 lotSize 2147483647 #txt
Ta0 f21 startIdx 0 #txt
Ta0 f21 200 458 112 44 0 -7 #rect
Ta0 f22 256 399 256 458 #arcP
Ta0 f23 312 480 352 367 #arcP
Ta0 f23 1 352 480 #addKink
Ta0 f23 1 0.08034838254922853 0 0 #arcLabel
Ta0 f24 337 545 30 30 0 15 #rect
Ta0 f25 outLink createDisabledUser.ivp #txt
Ta0 f25 inParamDecl '<> param;' #txt
Ta0 f25 requestEnabled true #txt
Ta0 f25 triggerEnabled false #txt
Ta0 f25 callSignature createDisabledUser() #txt
Ta0 f25 caseData businessCase.attach=true #txt
Ta0 f25 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createDisabledUser.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f25 @C|.responsibility Everybody #txt
Ta0 f25 81 545 30 30 -63 17 #rect
Ta0 f26 actionTable 'out=in;
' #txt
Ta0 f26 actionCode 'import ch.ivyteam.enginecockpit.testdata.user.TestDataUser;

TestDataUser.createDisabledUser();
' #txt
Ta0 f26 168 538 112 44 0 -7 #rect
Ta0 f27 280 560 337 560 #arcP
Ta0 f28 111 560 168 560 #arcP
Ta0 f29 337 657 30 30 0 15 #rect
Ta0 f30 outLink createManyDynamicRoles.ivp #txt
Ta0 f30 inParamDecl '<> param;' #txt
Ta0 f30 requestEnabled true #txt
Ta0 f30 triggerEnabled false #txt
Ta0 f30 callSignature createManyDynamicRoles() #txt
Ta0 f30 caseData businessCase.attach=true #txt
Ta0 f30 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createManyDynamicRoles.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f30 @C|.responsibility Everybody #txt
Ta0 f30 81 657 30 30 -85 27 #rect
Ta0 f31 actionTable 'out=in;
' #txt
Ta0 f31 actionCode 'import ch.ivyteam.enginecockpit.testdata.security.DynamicRoles;
DynamicRoles.createRoles();' #txt
Ta0 f31 168 650 112 44 0 -7 #rect
Ta0 f32 111 672 168 672 #arcP
Ta0 f33 280 672 337 672 #arcP
Ta0 f34 outLink executeRest.ivp #txt
Ta0 f34 inParamDecl '<> param;' #txt
Ta0 f34 requestEnabled true #txt
Ta0 f34 triggerEnabled false #txt
Ta0 f34 callSignature executeRest() #txt
Ta0 f34 caseData businessCase.attach=true #txt
Ta0 f34 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>executeRest.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f34 @C|.responsibility Everybody #txt
Ta0 f34 81 849 30 30 -49 18 #rect
Ta0 f35 clientId 2fb1152a-d43a-4840-b798-81ce34cef104 #txt
Ta0 f35 path /store/inventory #txt
Ta0 f35 168 842 112 44 0 -7 #rect
Ta0 f36 337 849 30 30 0 15 #rect
Ta0 f37 111 864 168 864 #arcP
Ta0 f38 280 864 337 864 #arcP
Ta0 f39 outLink executeWebService.ivp #txt
Ta0 f39 inParamDecl '<> param;' #txt
Ta0 f39 requestEnabled true #txt
Ta0 f39 triggerEnabled false #txt
Ta0 f39 callSignature executeWebService() #txt
Ta0 f39 caseData businessCase.attach=true #txt
Ta0 f39 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>executeWebService.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f39 @C|.responsibility Everybody #txt
Ta0 f39 81 945 30 30 -71 30 #rect
Ta0 f40 actionTable 'out=in;
' #txt
Ta0 f40 clientId 16AB1778C1FA54E5 #txt
Ta0 f40 port SampleWebServiceSoap #txt
Ta0 f40 operation HelloWorld #txt
Ta0 f40 168 938 112 44 0 -7 #rect
Ta0 f41 111 960 168 960 #arcP
Ta0 f42 337 945 30 30 0 15 #rect
Ta0 f43 280 960 337 960 #arcP
Ta0 f44 outLink cleanupDynamicRoles.ivp #txt
Ta0 f44 inParamDecl '<> param;' #txt
Ta0 f44 requestEnabled true #txt
Ta0 f44 triggerEnabled false #txt
Ta0 f44 callSignature cleanupDynamicRoles() #txt
Ta0 f44 caseData businessCase.attach=true #txt
Ta0 f44 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanupDynamicRoles.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f44 @C|.responsibility Everybody #txt
Ta0 f44 433 657 30 30 -64 29 #rect
Ta0 f45 actionTable 'out=in;
' #txt
Ta0 f45 actionCode 'import ch.ivyteam.enginecockpit.testdata.security.DynamicRoles;
DynamicRoles.cleanupRoles();' #txt
Ta0 f45 520 650 112 44 0 -7 #rect
Ta0 f46 689 657 30 30 0 15 #rect
Ta0 f47 463 672 520 672 #arcP
Ta0 f48 632 672 689 672 #arcP
Ta0 f49 outLink create200kUsers.ivp #txt
Ta0 f49 inParamDecl '<> param;' #txt
Ta0 f49 requestEnabled true #txt
Ta0 f49 triggerEnabled false #txt
Ta0 f49 callSignature create200kUsers() #txt
Ta0 f49 startName create200kUsers #txt
Ta0 f49 caseData businessCase.attach=true #txt
Ta0 f49 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>create200kUsers.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f49 @C|.responsibility Everybody #txt
Ta0 f49 81 753 30 30 -65 26 #rect
Ta0 f50 actionTable 'out=in;
' #txt
Ta0 f50 actionCode 'import ch.ivyteam.enginecockpit.testdata.user.TestDataUser;
TestDataUser.create200kUsers();' #txt
Ta0 f50 168 746 112 44 0 -7 #rect
Ta0 f51 689 753 30 30 0 15 #rect
Ta0 f52 outLink cleanup200kUsers.ivp #txt
Ta0 f52 inParamDecl '<> param;' #txt
Ta0 f52 requestEnabled true #txt
Ta0 f52 triggerEnabled false #txt
Ta0 f52 callSignature cleanup200kUsers() #txt
Ta0 f52 startName cleanup200kUsers #txt
Ta0 f52 caseData businessCase.attach=true #txt
Ta0 f52 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanup200kUsers.ivp</name>
    </language>
</elementInfo>
' #txt
Ta0 f52 @C|.responsibility Everybody #txt
Ta0 f52 433 753 30 30 -59 26 #rect
Ta0 f53 337 753 30 30 0 15 #rect
Ta0 f54 actionTable 'out=in;
' #txt
Ta0 f54 actionCode 'import ch.ivyteam.enginecockpit.testdata.user.TestDataUser;
TestDataUser.cleanup200kUsers();' #txt
Ta0 f54 520 746 112 44 0 -7 #rect
Ta0 f55 280 768 337 768 #arcP
Ta0 f56 111 768 168 768 #arcP
Ta0 f57 632 768 689 768 #arcP
Ta0 f58 463 768 520 768 #arcP
>Proto Ta0 .type engine.cockpit.test.data.Data #txt
>Proto Ta0 .processKind NORMAL #txt
>Proto Ta0 0 0 32 24 18 0 #rect
>Proto Ta0 @|BIcon #fIcon
Ta0 f3 mainOut f6 tail #connect
Ta0 f6 head f5 mainIn #connect
Ta0 f5 mainOut f7 tail #connect
Ta0 f7 head f4 mainIn #connect
Ta0 f0 mainOut f9 tail #connect
Ta0 f9 head f8 mainIn #connect
Ta0 f8 mainOut f2 tail #connect
Ta0 f2 head f1 mainIn #connect
Ta0 f10 mainOut f13 tail #connect
Ta0 f13 head f11 mainIn #connect
Ta0 f11 mainOut f14 tail #connect
Ta0 f14 head f12 mainIn #connect
Ta0 f17 mainOut f18 tail #connect
Ta0 f18 head f16 mainIn #connect
Ta0 f16 mainOut f19 tail #connect
Ta0 f19 head f15 mainIn #connect
Ta0 f20 mainOut f22 tail #connect
Ta0 f22 head f21 mainIn #connect
Ta0 f21 mainOut f23 tail #connect
Ta0 f23 head f15 mainIn #connect
Ta0 f25 mainOut f28 tail #connect
Ta0 f28 head f26 mainIn #connect
Ta0 f26 mainOut f27 tail #connect
Ta0 f27 head f24 mainIn #connect
Ta0 f30 mainOut f32 tail #connect
Ta0 f32 head f31 mainIn #connect
Ta0 f31 mainOut f33 tail #connect
Ta0 f33 head f29 mainIn #connect
Ta0 f34 mainOut f37 tail #connect
Ta0 f37 head f35 mainIn #connect
Ta0 f35 mainOut f38 tail #connect
Ta0 f38 head f36 mainIn #connect
Ta0 f39 mainOut f41 tail #connect
Ta0 f41 head f40 mainIn #connect
Ta0 f40 mainOut f43 tail #connect
Ta0 f43 head f42 mainIn #connect
Ta0 f44 mainOut f47 tail #connect
Ta0 f47 head f45 mainIn #connect
Ta0 f45 mainOut f48 tail #connect
Ta0 f48 head f46 mainIn #connect
Ta0 f49 mainOut f56 tail #connect
Ta0 f56 head f50 mainIn #connect
Ta0 f50 mainOut f55 tail #connect
Ta0 f55 head f53 mainIn #connect
Ta0 f52 mainOut f58 tail #connect
Ta0 f58 head f54 mainIn #connect
Ta0 f54 mainOut f57 tail #connect
Ta0 f57 head f51 mainIn #connect
