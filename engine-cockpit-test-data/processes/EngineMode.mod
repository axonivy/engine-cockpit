[Ivy]
16E600A121BCA31F 9.4.7 #module
>Proto >Proto Collection #zClass
Ee0 EngineMode Big #zClass
Ee0 B #cInfo
Ee0 #process
Ee0 @TextInP .colors .colors #zField
Ee0 @TextInP color color #zField
Ee0 @TextInP .type .type #zField
Ee0 @TextInP .processKind .processKind #zField
Ee0 @TextInP .xml .xml #zField
Ee0 @TextInP .responsibility .responsibility #zField
Ee0 @StartRequest f0 '' #zField
Ee0 @EndTask f1 '' #zField
Ee0 @GridStep f3 '' #zField
Ee0 @PushWFArc f4 '' #zField
Ee0 @PushWFArc f2 '' #zField
Ee0 @StartRequest f5 '' #zField
Ee0 @GridStep f6 '' #zField
Ee0 @EndTask f7 '' #zField
Ee0 @PushWFArc f8 '' #zField
Ee0 @PushWFArc f9 '' #zField
Ee0 @GridStep f10 '' #zField
Ee0 @EndTask f11 '' #zField
Ee0 @StartRequest f12 '' #zField
Ee0 @PushWFArc f13 '' #zField
Ee0 @PushWFArc f14 '' #zField
Ee0 @GridStep f15 '' #zField
Ee0 @EndTask f16 '' #zField
Ee0 @StartRequest f17 '' #zField
Ee0 @PushWFArc f18 '' #zField
Ee0 @PushWFArc f19 '' #zField
Ee0 @EndTask f20 '' #zField
Ee0 @StartRequest f21 '' #zField
Ee0 @GridStep f22 '' #zField
Ee0 @PushWFArc f23 '' #zField
Ee0 @PushWFArc f24 '' #zField
Ee0 @GridStep f25 '' #zField
Ee0 @EndTask f26 '' #zField
Ee0 @StartRequest f27 '' #zField
Ee0 @PushWFArc f28 '' #zField
Ee0 @PushWFArc f29 '' #zField
>Proto Ee0 Ee0 EngineMode #zField
Ee0 f0 outLink demo.ivp #txt
Ee0 f0 inParamDecl '<> param;' #txt
Ee0 f0 requestEnabled true #txt
Ee0 f0 triggerEnabled false #txt
Ee0 f0 callSignature demo() #txt
Ee0 f0 caseData businessCase.attach=true #txt
Ee0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>demo.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f0 @C|.responsibility Everybody #txt
Ee0 f0 81 49 30 30 -21 17 #rect
Ee0 f1 337 49 30 30 0 15 #rect
Ee0 f3 actionTable 'out=in;
' #txt
Ee0 f3 actionCode 'import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.DEMO);' #txt
Ee0 f3 168 42 112 44 0 -8 #rect
Ee0 f4 111 64 168 64 #arcP
Ee0 f2 280 64 337 64 #arcP
Ee0 f5 outLink maintenance_invalidLicence.ivp #txt
Ee0 f5 inParamDecl '<> param;' #txt
Ee0 f5 requestEnabled true #txt
Ee0 f5 triggerEnabled false #txt
Ee0 f5 callSignature maintenance_invalidLicence() #txt
Ee0 f5 caseData businessCase.attach=true #txt
Ee0 f5 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>maintenance_invalidLicence.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f5 @C|.responsibility Everybody #txt
Ee0 f5 81 145 30 30 -77 20 #rect
Ee0 f6 actionTable 'out=in;
' #txt
Ee0 f6 actionCode 'import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.MAINTENANCE);
MaintenanceReason.set(MaintenanceReason.INVALID_LICENCE);' #txt
Ee0 f6 168 138 112 44 0 -8 #rect
Ee0 f7 337 145 30 30 0 15 #rect
Ee0 f8 280 160 337 160 #arcP
Ee0 f9 111 160 168 160 #arcP
Ee0 f10 actionTable 'out=in;
' #txt
Ee0 f10 actionCode 'import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.MAINTENANCE);
MaintenanceReason.set(MaintenanceReason.EXPIRED_LICENCE);' #txt
Ee0 f10 168 234 112 44 0 -8 #rect
Ee0 f11 337 241 30 30 0 15 #rect
Ee0 f12 outLink maintenance_expiredLicence.ivp #txt
Ee0 f12 inParamDecl '<> param;' #txt
Ee0 f12 requestEnabled true #txt
Ee0 f12 triggerEnabled false #txt
Ee0 f12 callSignature maintenance_expiredLicence() #txt
Ee0 f12 caseData businessCase.attach=true #txt
Ee0 f12 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>maintenance_expiredLicence.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f12 @C|.responsibility Everybody #txt
Ee0 f12 81 241 30 30 -77 20 #rect
Ee0 f13 111 256 168 256 #arcP
Ee0 f14 280 256 337 256 #arcP
Ee0 f15 actionTable 'out=in;
' #txt
Ee0 f15 actionCode 'import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.MAINTENANCE);
MaintenanceReason.set(MaintenanceReason.WRONG_SYSTEM_DATABASE_VERSION);' #txt
Ee0 f15 168 330 112 44 0 -8 #rect
Ee0 f16 337 337 30 30 0 15 #rect
Ee0 f17 outLink maintenance_wrongSysDbVersion.ivp #txt
Ee0 f17 inParamDecl '<> param;' #txt
Ee0 f17 requestEnabled true #txt
Ee0 f17 triggerEnabled false #txt
Ee0 f17 callSignature maintenance_wrongSysDbVersion() #txt
Ee0 f17 caseData businessCase.attach=true #txt
Ee0 f17 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>maintenance_wrongSysDbVersion.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f17 @C|.responsibility Everybody #txt
Ee0 f17 81 337 30 30 -77 20 #rect
Ee0 f18 280 352 337 352 #arcP
Ee0 f19 111 352 168 352 #arcP
Ee0 f20 337 433 30 30 0 15 #rect
Ee0 f21 outLink maintenance_invalidSysDbConfig.ivp #txt
Ee0 f21 inParamDecl '<> param;' #txt
Ee0 f21 requestEnabled true #txt
Ee0 f21 triggerEnabled false #txt
Ee0 f21 callSignature maintenance_invalidSysDbConfig() #txt
Ee0 f21 caseData businessCase.attach=true #txt
Ee0 f21 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>maintenance_invalidSysDbConfig.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f21 @C|.responsibility Everybody #txt
Ee0 f21 81 433 30 30 -77 20 #rect
Ee0 f22 actionTable 'out=in;
' #txt
Ee0 f22 actionCode 'import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.MAINTENANCE);
MaintenanceReason.set(MaintenanceReason.INVALID_SYSTEM_DATABASE_CONFIG);' #txt
Ee0 f22 168 426 112 44 0 -8 #rect
Ee0 f23 280 448 337 448 #arcP
Ee0 f24 111 448 168 448 #arcP
Ee0 f25 actionTable 'out=in;
' #txt
Ee0 f25 actionCode 'import ch.ivyteam.ivy.server.restricted.MaintenanceReason;
import ch.ivyteam.ivy.server.restricted.EngineMode;

EngineMode.set(EngineMode.MAINTENANCE);
MaintenanceReason.set(MaintenanceReason.MISSING_SYSTEM_DATABASE);' #txt
Ee0 f25 168 522 112 44 0 -8 #rect
Ee0 f26 337 529 30 30 0 15 #rect
Ee0 f27 outLink maintenance_missingSysDb.ivp #txt
Ee0 f27 inParamDecl '<> param;' #txt
Ee0 f27 requestEnabled true #txt
Ee0 f27 triggerEnabled false #txt
Ee0 f27 callSignature maintenance_missingSysDb() #txt
Ee0 f27 caseData businessCase.attach=true #txt
Ee0 f27 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>maintenance_missingSysDb.ivp</name>
    </language>
</elementInfo>
' #txt
Ee0 f27 @C|.responsibility Everybody #txt
Ee0 f27 81 529 30 30 -77 20 #rect
Ee0 f28 111 544 168 544 #arcP
Ee0 f29 280 544 337 544 #arcP
>Proto Ee0 .type engine.cockpit.test.data.Data #txt
>Proto Ee0 .processKind NORMAL #txt
>Proto Ee0 0 0 32 24 18 0 #rect
>Proto Ee0 @|BIcon #fIcon
Ee0 f0 mainOut f4 tail #connect
Ee0 f4 head f3 mainIn #connect
Ee0 f3 mainOut f2 tail #connect
Ee0 f2 head f1 mainIn #connect
Ee0 f5 mainOut f9 tail #connect
Ee0 f9 head f6 mainIn #connect
Ee0 f6 mainOut f8 tail #connect
Ee0 f8 head f7 mainIn #connect
Ee0 f12 mainOut f13 tail #connect
Ee0 f13 head f10 mainIn #connect
Ee0 f10 mainOut f14 tail #connect
Ee0 f14 head f11 mainIn #connect
Ee0 f17 mainOut f19 tail #connect
Ee0 f19 head f15 mainIn #connect
Ee0 f15 mainOut f18 tail #connect
Ee0 f18 head f16 mainIn #connect
Ee0 f21 mainOut f24 tail #connect
Ee0 f24 head f22 mainIn #connect
Ee0 f22 mainOut f23 tail #connect
Ee0 f23 head f20 mainIn #connect
Ee0 f27 mainOut f28 tail #connect
Ee0 f28 head f25 mainIn #connect
Ee0 f25 mainOut f29 tail #connect
Ee0 f29 head f26 mainIn #connect
