[Ivy]
01682CA38FC4E775 9.4.8 #module
>Proto >Proto Collection #zClass
Mn0 Main Big #zClass
Mn0 B #cInfo
Mn0 #process
Mn0 @TextInP .colors .colors #zField
Mn0 @TextInP color color #zField
Mn0 @TextInP .type .type #zField
Mn0 @TextInP .processKind .processKind #zField
Mn0 @AnnotationInP-0n ai ai #zField
Mn0 @MessageFlowInP-0n messageIn messageIn #zField
Mn0 @MessageFlowOutP-0n messageOut messageOut #zField
Mn0 @TextInP .xml .xml #zField
Mn0 @TextInP .responsibility .responsibility #zField
Mn0 @StartRequest f0 '' #zField
Mn0 @EndRequest f3 '' #zField
Mn0 @PushWFArc f1 '' #zField
Mn0 @StartRequest f2 '' #zField
Mn0 @EndRequest f4 '' #zField
Mn0 @PushWFArc f5 '' #zField
>Proto Mn0 Mn0 Main #zField
Mn0 f0 outLink cockpit.ivp #txt
Mn0 f0 inParamDecl '<> param;' #txt
Mn0 f0 requestEnabled true #txt
Mn0 f0 triggerEnabled false #txt
Mn0 f0 callSignature cockpit() #txt
Mn0 f0 startCustomFields embedInFrame=false #txt
Mn0 f0 caseData businessCase.attach=true #txt
Mn0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cockpit.ivp</name>
    </language>
</elementInfo>
' #txt
Mn0 f0 @C|.responsibility Everybody #txt
Mn0 f0 81 49 30 30 -25 17 #rect
Mn0 f3 template "view/dashboard.xhtml" #txt
Mn0 f3 337 49 30 30 0 15 #rect
Mn0 f1 expr out #txt
Mn0 f1 111 64 337 64 #arcP
Mn0 f2 outLink setup.ivp #txt
Mn0 f2 inParamDecl '<> param;' #txt
Mn0 f2 requestEnabled true #txt
Mn0 f2 triggerEnabled false #txt
Mn0 f2 callSignature setup() #txt
Mn0 f2 startCustomFields embedInFrame=false #txt
Mn0 f2 caseData businessCase.attach=true #txt
Mn0 f2 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>setup.ivp</name>
    </language>
</elementInfo>
' #txt
Mn0 f2 @C|.responsibility Everybody #txt
Mn0 f2 81 145 30 30 -29 23 #rect
Mn0 f4 template "view/setup-intro.xhtml" #txt
Mn0 f4 337 145 30 30 0 15 #rect
Mn0 f5 111 160 337 160 #arcP
>Proto Mn0 .type ch.ivyteam.enginecockpit.Data #txt
>Proto Mn0 .processKind NORMAL #txt
>Proto Mn0 0 0 32 24 18 0 #rect
>Proto Mn0 @|BIcon #fIcon
Mn0 f0 mainOut f1 tail #connect
Mn0 f1 head f3 mainIn #connect
Mn0 f2 mainOut f5 tail #connect
Mn0 f5 head f4 mainIn #connect
