[Ivy]
16E88DD61E825E70 7.5.0 #module
>Proto >Proto Collection #zClass
Ta0 TestData Big #zClass
Ta0 B #cInfo
Ta0 #process
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
Ta0 f0 @|StartRequestIcon #fIcon
Ta0 f1 337 49 30 30 0 15 #rect
Ta0 f1 @|EndIcon #fIcon
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
Ta0 f3 @|StartRequestIcon #fIcon
Ta0 f4 337 145 30 30 0 15 #rect
Ta0 f4 @|EndIcon #fIcon
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

IApplication app = ivy.wf.getApplication();

IBusinessCalendarSettings settings = app.getBusinessCalendarSettings();

if (settings.findBusinessCalendarConfiguration("Luzern") == null)
{
	IBusinessCalendarConfiguration luzern = settings.createBusinessCalendarConfiguration("Luzern");
	IBusinessCalendarConfiguration zug = settings.createBusinessCalendarConfiguration("Zug");
	
	zug.setParent(luzern);
	
	FreeDayOfYear fdoy1 = new FreeDayOfYear(5, 30);
	fdoy1.setDescription("Auffahrt");
	FreeDayOfYear fdoy2 = new FreeDayOfYear(6, 9);
	fdoy2.setDescription("Pfingsten");
	luzern.getFreeDaysOfYear().add(fdoy1);
	luzern.getFreeDaysOfYear().add(fdoy2);
	
	FreeEasterRelativeDay ferd1 = new FreeEasterRelativeDay(1);
	ferd1.setDescription("Ostermontag");
	luzern.getFreeEasterRelativeDays().add(ferd1);
	
	FreeDate fd1 = new FreeDate(new Date(2019, 8, 15));
	fd1.setDescription("Day off");
	FreeDate fd2 = new FreeDate(new Date(2019, 8, 16));
	fd1.setDescription("Day off");
	
	zug.getFreeDates().add(fd1);
	
	settings.saveBusinessCalendarConfiguration(luzern);
	settings.saveBusinessCalendarConfiguration(zug);
	
	app.findEnvironment("test").setBusinessCalendar(app.getActualEnvironment().getBusinessCalendar().get("Luzern"));
}' #txt
Ta0 f5 168 138 112 44 0 -7 #rect
Ta0 f5 @|StepIcon #fIcon
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
Ta0 f8 @|StepIcon #fIcon
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
Ta0 f10 @|StartRequestIcon #fIcon
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
Ta0 f11 @|StepIcon #fIcon
Ta0 f12 337 241 30 30 0 15 #rect
Ta0 f12 @|EndIcon #fIcon
Ta0 f13 111 256 152 256 #arcP
Ta0 f14 296 256 337 256 #arcP
Ta0 f15 337 337 30 30 0 15 #rect
Ta0 f15 @|EndIcon #fIcon
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
Ta0 f16 @|DBStepIcon #fIcon
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
Ta0 f17 @|StartRequestIcon #fIcon
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
Ta0 f20 @|ErrorBoundaryEventIcon #fIcon
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
Ta0 f21 @|DBStepIcon #fIcon
Ta0 f22 256 399 256 458 #arcP
Ta0 f23 312 480 352 367 #arcP
Ta0 f23 1 352 480 #addKink
Ta0 f23 1 0.08034838254922853 0 0 #arcLabel
Ta0 f24 329 545 30 30 0 15 #rect
Ta0 f24 @|EndIcon #fIcon
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
Ta0 f25 73 545 30 30 -63 17 #rect
Ta0 f25 @|StartRequestIcon #fIcon
Ta0 f26 actionTable 'out=in;
' #txt
Ta0 f26 actionCode 'import ch.ivyteam.enginecockpit.testdata.user.TestDataUser;

TestDataUser.createDisabledUser();
' #txt
Ta0 f26 160 538 112 44 0 -7 #rect
Ta0 f26 @|StepIcon #fIcon
Ta0 f27 272 560 329 560 #arcP
Ta0 f28 103 560 160 560 #arcP
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
