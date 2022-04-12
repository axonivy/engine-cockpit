[Ivy]
16E881C7DC458C7D 9.4.7 #module
>Proto >Proto Collection #zClass
Cg0 Config Big #zClass
Cg0 B #cInfo
Cg0 #process
Cg0 @TextInP .colors .colors #zField
Cg0 @TextInP color color #zField
Cg0 @TextInP .type .type #zField
Cg0 @TextInP .processKind .processKind #zField
Cg0 @TextInP .xml .xml #zField
Cg0 @TextInP .responsibility .responsibility #zField
Cg0 @StartRequest f0 '' #zField
Cg0 @EndTask f1 '' #zField
Cg0 @StartRequest f3 '' #zField
Cg0 @EndTask f4 '' #zField
Cg0 @GridStep f6 '' #zField
Cg0 @PushWFArc f7 '' #zField
Cg0 @PushWFArc f2 '' #zField
Cg0 @GridStep f8 '' #zField
Cg0 @PushWFArc f9 '' #zField
Cg0 @PushWFArc f5 '' #zField
Cg0 @EndTask f10 '' #zField
Cg0 @StartRequest f11 '' #zField
Cg0 @GridStep f12 '' #zField
Cg0 @PushWFArc f13 '' #zField
Cg0 @PushWFArc f14 '' #zField
Cg0 @GridStep f15 '' #zField
Cg0 @StartRequest f16 '' #zField
Cg0 @EndTask f17 '' #zField
Cg0 @PushWFArc f18 '' #zField
Cg0 @PushWFArc f19 '' #zField
>Proto Cg0 Cg0 Config #zField
Cg0 f0 outLink cleanupAdmins.ivp #txt
Cg0 f0 inParamDecl '<> param;' #txt
Cg0 f0 requestEnabled true #txt
Cg0 f0 triggerEnabled false #txt
Cg0 f0 callSignature cleanupAdmins() #txt
Cg0 f0 caseData businessCase.attach=true #txt
Cg0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanupAdmins.ivp</name>
    </language>
</elementInfo>
' #txt
Cg0 f0 @C|.responsibility Everybody #txt
Cg0 f0 81 49 30 30 -51 23 #rect
Cg0 f1 337 49 30 30 0 15 #rect
Cg0 f3 outLink cleanupConnectors.ivp #txt
Cg0 f3 inParamDecl '<> param;' #txt
Cg0 f3 requestEnabled true #txt
Cg0 f3 triggerEnabled false #txt
Cg0 f3 callSignature cleanupConnectors() #txt
Cg0 f3 caseData businessCase.attach=true #txt
Cg0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanupConnectors.ivp</name>
    </language>
</elementInfo>
' #txt
Cg0 f3 @C|.responsibility Everybody #txt
Cg0 f3 81 145 30 30 -51 23 #rect
Cg0 f4 337 145 30 30 0 15 #rect
Cg0 f6 actionTable 'out=in;
' #txt
Cg0 f6 actionCode 'import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

IConfiguration.instance().remove("Administrators");
IConfiguration.instance().set("Administrators.admin.Password", "admin");' #txt
Cg0 f6 168 42 112 44 0 -7 #rect
Cg0 f7 111 64 168 64 #arcP
Cg0 f2 280 64 337 64 #arcP
Cg0 f8 actionTable 'out=in;
' #txt
Cg0 f8 actionCode 'import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

IConfiguration.instance().remove("Connector.HTTPS");
IConfiguration.instance().remove("Connector.AJP");
IConfiguration.instance().remove("Connector.HTTP.Enabled");
IConfiguration.instance().remove("Connector.HTTP.Port");' #txt
Cg0 f8 168 138 112 44 0 -7 #rect
Cg0 f9 111 160 168 160 #arcP
Cg0 f5 280 160 337 160 #arcP
Cg0 f10 337 241 30 30 0 15 #rect
Cg0 f11 outLink cleanupSystemDb.ivp #txt
Cg0 f11 inParamDecl '<> param;' #txt
Cg0 f11 requestEnabled true #txt
Cg0 f11 triggerEnabled false #txt
Cg0 f11 callSignature cleanupSystemDb() #txt
Cg0 f11 caseData businessCase.attach=true #txt
Cg0 f11 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanupSystemDb.ivp</name>
    </language>
</elementInfo>
' #txt
Cg0 f11 @C|.responsibility Everybody #txt
Cg0 f11 81 241 30 30 -61 30 #rect
Cg0 f12 actionTable 'out=in;
' #txt
Cg0 f12 actionCode 'import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

IConfiguration.instance().remove("SystemDb");
' #txt
Cg0 f12 168 234 112 44 0 -7 #rect
Cg0 f13 280 256 337 256 #arcP
Cg0 f14 111 256 168 256 #arcP
Cg0 f15 actionTable 'out=in;
' #txt
Cg0 f15 actionCode 'import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

IConfiguration.instance().remove("Data");
' #txt
Cg0 f15 168 330 112 44 0 -7 #rect
Cg0 f16 outLink cleanupDataDirs.ivp #txt
Cg0 f16 inParamDecl '<> param;' #txt
Cg0 f16 requestEnabled true #txt
Cg0 f16 triggerEnabled false #txt
Cg0 f16 callSignature cleanupDataDirs() #txt
Cg0 f16 caseData businessCase.attach=true #txt
Cg0 f16 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cleanupDataDirs.ivp</name>
    </language>
</elementInfo>
' #txt
Cg0 f16 @C|.responsibility Everybody #txt
Cg0 f16 81 337 30 30 -61 30 #rect
Cg0 f17 337 337 30 30 0 15 #rect
Cg0 f18 280 352 337 352 #arcP
Cg0 f19 111 352 168 352 #arcP
>Proto Cg0 .type engine.cockpit.test.data.Data #txt
>Proto Cg0 .processKind NORMAL #txt
>Proto Cg0 0 0 32 24 18 0 #rect
>Proto Cg0 @|BIcon #fIcon
Cg0 f0 mainOut f7 tail #connect
Cg0 f7 head f6 mainIn #connect
Cg0 f6 mainOut f2 tail #connect
Cg0 f2 head f1 mainIn #connect
Cg0 f3 mainOut f9 tail #connect
Cg0 f9 head f8 mainIn #connect
Cg0 f8 mainOut f5 tail #connect
Cg0 f5 head f4 mainIn #connect
Cg0 f11 mainOut f14 tail #connect
Cg0 f14 head f12 mainIn #connect
Cg0 f12 mainOut f13 tail #connect
Cg0 f13 head f10 mainIn #connect
Cg0 f16 mainOut f19 tail #connect
Cg0 f19 head f15 mainIn #connect
Cg0 f15 mainOut f18 tail #connect
Cg0 f18 head f17 mainIn #connect
