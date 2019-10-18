[Ivy]
16DD9CFD7CAC310B 7.5.0 #module
>Proto >Proto Collection #zClass
Lt0 LicenceEvent Big #zClass
Lt0 B #cInfo
Lt0 #process
Lt0 @TextInP .type .type #zField
Lt0 @TextInP .processKind .processKind #zField
Lt0 @TextInP .xml .xml #zField
Lt0 @TextInP .responsibility .responsibility #zField
Lt0 @StartRequest f0 '' #zField
Lt0 @EndTask f1 '' #zField
Lt0 @GridStep f3 '' #zField
Lt0 @PushWFArc f4 '' #zField
Lt0 @PushWFArc f2 '' #zField
>Proto Lt0 Lt0 LicenceEvent #zField
Lt0 f0 outLink start.ivp #txt
Lt0 f0 inParamDecl '<> param;' #txt
Lt0 f0 requestEnabled true #txt
Lt0 f0 triggerEnabled false #txt
Lt0 f0 callSignature start() #txt
Lt0 f0 caseData businessCase.attach=true #txt
Lt0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start.ivp</name>
    </language>
</elementInfo>
' #txt
Lt0 f0 @C|.responsibility Everybody #txt
Lt0 f0 81 49 30 30 -25 17 #rect
Lt0 f0 @|StartRequestIcon #fIcon
Lt0 f1 337 49 30 30 0 15 #rect
Lt0 f1 @|EndIcon #fIcon
Lt0 f3 actionTable 'out=in;
' #txt
Lt0 f3 actionCode 'import ch.ivyteam.licence.LicenceEventManager;
import ch.ivyteam.licence.LicenceEvent.Level;

LicenceEventManager.getInstance().reportLicenceEvent(Level.ERROR, "", 
	"Cannot create session because the maximum session that are allowed by your licence has exceeded by a factor of 50%.");
LicenceEventManager.getInstance().reportLicenceEvent(Level.WARN, "", 
	"No administrator email addresses defined. License violation notification is not sent by email.");' #txt
Lt0 f3 168 42 112 44 0 -7 #rect
Lt0 f3 @|StepIcon #fIcon
Lt0 f4 111 64 168 64 #arcP
Lt0 f2 280 64 337 64 #arcP
>Proto Lt0 .type engine.cockpit.test.data.Data #txt
>Proto Lt0 .processKind NORMAL #txt
>Proto Lt0 0 0 32 24 18 0 #rect
>Proto Lt0 @|BIcon #fIcon
Lt0 f0 mainOut f4 tail #connect
Lt0 f4 head f3 mainIn #connect
Lt0 f3 mainOut f2 tail #connect
Lt0 f2 head f1 mainIn #connect
