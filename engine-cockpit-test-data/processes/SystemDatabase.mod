[Ivy]
16E8EAD7CC77A0A3 7.5.0 #module
>Proto >Proto Collection #zClass
Se0 SystemDatabase Big #zClass
Se0 B #cInfo
Se0 #process
Se0 @TextInP .type .type #zField
Se0 @TextInP .processKind .processKind #zField
Se0 @TextInP .xml .xml #zField
Se0 @TextInP .responsibility .responsibility #zField
Se0 @StartRequest f0 '' #zField
Se0 @EndTask f1 '' #zField
Se0 @GridStep f3 '' #zField
Se0 @PushWFArc f4 '' #zField
Se0 @PushWFArc f2 '' #zField
Se0 @EndTask f5 '' #zField
Se0 @StartRequest f6 '' #zField
Se0 @GridStep f7 '' #zField
Se0 @PushWFArc f8 '' #zField
Se0 @PushWFArc f9 '' #zField
>Proto Se0 Se0 SystemDatabase #zField
Se0 f0 outLink createOldDatabase.ivp #txt
Se0 f0 inParamDecl '<> param;' #txt
Se0 f0 requestEnabled true #txt
Se0 f0 triggerEnabled false #txt
Se0 f0 callSignature createOldDatabase() #txt
Se0 f0 caseData businessCase.attach=true #txt
Se0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createOldDatabase.ivp</name>
    </language>
</elementInfo>
' #txt
Se0 f0 @C|.responsibility Everybody #txt
Se0 f0 81 49 30 30 -61 30 #rect
Se0 f0 @|StartRequestIcon #fIcon
Se0 f1 337 49 30 30 0 15 #rect
Se0 f1 @|EndIcon #fIcon
Se0 f3 actionTable 'out=in;
' #txt
Se0 f3 actionCode 'import ch.ivyteam.enginecockpit.system.database.SystemDatabaseCreator;
SystemDatabaseCreator.createOldDatabase();
' #txt
Se0 f3 168 42 112 44 0 -7 #rect
Se0 f3 @|StepIcon #fIcon
Se0 f4 111 64 168 64 #arcP
Se0 f2 280 64 337 64 #arcP
Se0 f5 337 145 30 30 0 15 #rect
Se0 f5 @|EndIcon #fIcon
Se0 f6 outLink deleteOldDatabase.ivp #txt
Se0 f6 inParamDecl '<> param;' #txt
Se0 f6 requestEnabled true #txt
Se0 f6 triggerEnabled false #txt
Se0 f6 callSignature deleteOldDatabase() #txt
Se0 f6 caseData businessCase.attach=true #txt
Se0 f6 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>deleteOldDatabase.ivp</name>
    </language>
</elementInfo>
' #txt
Se0 f6 @C|.responsibility Everybody #txt
Se0 f6 81 145 30 30 -61 30 #rect
Se0 f6 @|StartRequestIcon #fIcon
Se0 f7 actionTable 'out=in;
' #txt
Se0 f7 actionCode 'import ch.ivyteam.enginecockpit.system.database.SystemDatabaseCreator;
SystemDatabaseCreator.deleteOldDatabase();
' #txt
Se0 f7 168 138 112 44 0 -7 #rect
Se0 f7 @|StepIcon #fIcon
Se0 f8 280 160 337 160 #arcP
Se0 f9 111 160 168 160 #arcP
>Proto Se0 .type engine.cockpit.test.data.Data #txt
>Proto Se0 .processKind NORMAL #txt
>Proto Se0 0 0 32 24 18 0 #rect
>Proto Se0 @|BIcon #fIcon
Se0 f0 mainOut f4 tail #connect
Se0 f4 head f3 mainIn #connect
Se0 f3 mainOut f2 tail #connect
Se0 f2 head f1 mainIn #connect
Se0 f6 mainOut f9 tail #connect
Se0 f9 head f7 mainIn #connect
Se0 f7 mainOut f8 tail #connect
Se0 f8 head f5 mainIn #connect
