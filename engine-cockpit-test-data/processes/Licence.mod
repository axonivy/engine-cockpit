[Ivy]
16E84204B7FE6C91 7.5.0 #module
>Proto >Proto Collection #zClass
Le0 Licence Big #zClass
Le0 B #cInfo
Le0 #process
Le0 @TextInP .type .type #zField
Le0 @TextInP .processKind .processKind #zField
Le0 @TextInP .xml .xml #zField
Le0 @TextInP .responsibility .responsibility #zField
Le0 @StartRequest f0 '' #zField
Le0 @EndTask f1 '' #zField
Le0 @GridStep f3 '' #zField
Le0 @PushWFArc f4 '' #zField
Le0 @PushWFArc f2 '' #zField
Le0 @StartRequest f5 '' #zField
Le0 @EndTask f6 '' #zField
Le0 @GridStep f7 '' #zField
Le0 @PushWFArc f8 '' #zField
Le0 @PushWFArc f9 '' #zField
Le0 @GridStep f10 '' #zField
Le0 @StartRequest f11 '' #zField
Le0 @EndTask f12 '' #zField
Le0 @PushWFArc f13 '' #zField
Le0 @PushWFArc f14 '' #zField
>Proto Le0 Le0 Licence #zField
Le0 f0 outLink resetLicence.ivp #txt
Le0 f0 inParamDecl '<> param;' #txt
Le0 f0 requestEnabled true #txt
Le0 f0 triggerEnabled false #txt
Le0 f0 callSignature resetLicence() #txt
Le0 f0 caseData businessCase.attach=true #txt
Le0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>resetLicence.ivp</name>
    </language>
</elementInfo>
' #txt
Le0 f0 @C|.responsibility Everybody #txt
Le0 f0 81 49 30 30 -42 25 #rect
Le0 f0 @|StartRequestIcon #fIcon
Le0 f1 337 49 30 30 0 15 #rect
Le0 f1 @|EndIcon #fIcon
Le0 f3 actionTable 'out=in;
' #txt
Le0 f3 actionCode 'import ch.ivyteam.licence.SystemLicence;
import java.lang.reflect.Method;

Method method = SystemLicence.class.getDeclaredMethod("reset");
method.setAccessible(true);
method.invoke(null);
' #txt
Le0 f3 security system #txt
Le0 f3 168 42 112 44 0 -7 #rect
Le0 f3 @|StepIcon #fIcon
Le0 f4 111 64 168 64 #arcP
Le0 f2 280 64 337 64 #arcP
Le0 f5 outLink addLicenceEvents.ivp #txt
Le0 f5 inParamDecl '<> param;' #txt
Le0 f5 requestEnabled true #txt
Le0 f5 triggerEnabled false #txt
Le0 f5 callSignature addLicenceEvents() #txt
Le0 f5 caseData businessCase.attach=true #txt
Le0 f5 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>addLicenceEvents.ivp</name>
    </language>
</elementInfo>
' #txt
Le0 f5 @C|.responsibility Everybody #txt
Le0 f5 81 145 30 30 -62 24 #rect
Le0 f5 @|StartRequestIcon #fIcon
Le0 f6 337 145 30 30 0 15 #rect
Le0 f6 @|EndIcon #fIcon
Le0 f7 actionTable 'out=in;
' #txt
Le0 f7 actionCode 'import ch.ivyteam.licence.LicenceEventManager;
import ch.ivyteam.licence.LicenceEvent.Level;

LicenceEventManager.getInstance().reportLicenceEvent(Level.ERROR, "", 
	"Cannot create session because the maximum session that are allowed by your licence has exceeded by a factor of 50%.");
LicenceEventManager.getInstance().reportLicenceEvent(Level.WARN, "", 
	"No administrator email addresses defined. License violation notification is not sent by email.");' #txt
Le0 f7 168 138 112 44 0 -7 #rect
Le0 f7 @|StepIcon #fIcon
Le0 f8 111 160 168 160 #arcP
Le0 f9 280 160 337 160 #arcP
Le0 f10 actionTable 'out=in;
' #txt
Le0 f10 actionCode 'import ch.ivyteam.ivy.config.LicenceInstaller;
LicenceInstaller.install();' #txt
Le0 f10 168 234 112 44 0 -8 #rect
Le0 f10 @|StepIcon #fIcon
Le0 f11 outLink installLicence.ivp #txt
Le0 f11 inParamDecl '<> param;' #txt
Le0 f11 requestEnabled true #txt
Le0 f11 triggerEnabled false #txt
Le0 f11 callSignature installLicence() #txt
Le0 f11 caseData businessCase.attach=true #txt
Le0 f11 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>installLicence.ivp</name>
    </language>
</elementInfo>
' #txt
Le0 f11 @C|.responsibility Everybody #txt
Le0 f11 81 241 30 30 -47 24 #rect
Le0 f11 @|StartRequestIcon #fIcon
Le0 f12 337 241 30 30 0 15 #rect
Le0 f12 @|EndIcon #fIcon
Le0 f13 111 256 168 256 #arcP
Le0 f14 280 256 337 256 #arcP
>Proto Le0 .type engine.cockpit.test.data.Data #txt
>Proto Le0 .processKind NORMAL #txt
>Proto Le0 0 0 32 24 18 0 #rect
>Proto Le0 @|BIcon #fIcon
Le0 f0 mainOut f4 tail #connect
Le0 f4 head f3 mainIn #connect
Le0 f3 mainOut f2 tail #connect
Le0 f2 head f1 mainIn #connect
Le0 f5 mainOut f8 tail #connect
Le0 f8 head f7 mainIn #connect
Le0 f7 mainOut f9 tail #connect
Le0 f9 head f6 mainIn #connect
Le0 f11 mainOut f13 tail #connect
Le0 f13 head f10 mainIn #connect
Le0 f10 mainOut f14 tail #connect
Le0 f14 head f12 mainIn #connect
