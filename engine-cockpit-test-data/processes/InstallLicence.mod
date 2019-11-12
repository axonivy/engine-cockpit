[Ivy]
16E604265B8EB8C3 7.5.0 #module
>Proto >Proto Collection #zClass
Ie0 InstallLicence Big #zClass
Ie0 B #cInfo
Ie0 #process
Ie0 @TextInP .type .type #zField
Ie0 @TextInP .processKind .processKind #zField
Ie0 @TextInP .xml .xml #zField
Ie0 @TextInP .responsibility .responsibility #zField
Ie0 @StartRequest f0 '' #zField
Ie0 @EndTask f1 '' #zField
Ie0 @GridStep f3 '' #zField
Ie0 @PushWFArc f4 '' #zField
Ie0 @PushWFArc f2 '' #zField
>Proto Ie0 Ie0 InstallLicence #zField
Ie0 f0 outLink installLicence.ivp #txt
Ie0 f0 inParamDecl '<> param;' #txt
Ie0 f0 requestEnabled true #txt
Ie0 f0 triggerEnabled false #txt
Ie0 f0 callSignature installLicence() #txt
Ie0 f0 caseData businessCase.attach=true #txt
Ie0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>installLicence.ivp</name>
    </language>
</elementInfo>
' #txt
Ie0 f0 @C|.responsibility Everybody #txt
Ie0 f0 81 49 30 30 -21 17 #rect
Ie0 f0 @|StartRequestIcon #fIcon
Ie0 f1 337 49 30 30 0 15 #rect
Ie0 f1 @|EndIcon #fIcon
Ie0 f3 actionTable 'out=in;
' #txt
Ie0 f3 actionCode 'import ch.ivyteam.ivy.config.LicenceInstaller;
LicenceInstaller.install();' #txt
Ie0 f3 168 42 112 44 0 -8 #rect
Ie0 f3 @|StepIcon #fIcon
Ie0 f4 111 64 168 64 #arcP
Ie0 f2 280 64 337 64 #arcP
>Proto Ie0 .type engine.cockpit.test.data.Data #txt
>Proto Ie0 .processKind NORMAL #txt
>Proto Ie0 0 0 32 24 18 0 #rect
>Proto Ie0 @|BIcon #fIcon
Ie0 f0 mainOut f4 tail #connect
Ie0 f4 head f3 mainIn #connect
Ie0 f3 mainOut f2 tail #connect
Ie0 f2 head f1 mainIn #connect
