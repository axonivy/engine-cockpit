[Ivy]
01682CA38FC4E775 3.23 #module
>Proto >Proto Collection #zClass
Mn0 Main Big #zClass
Mn0 B #cInfo
Mn0 #process
Mn0 @TextInP .resExport .resExport #zField
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
>Proto Mn0 Mn0 Main #zField
Mn0 f0 outLink start.ivp #txt
Mn0 f0 type ch.ivyteam.enginecockpit.Data #txt
Mn0 f0 inParamDecl '<> param;' #txt
Mn0 f0 actionDecl 'ch.ivyteam.enginecockpit.Data out;
' #txt
Mn0 f0 guid 1682CA3901E675F6 #txt
Mn0 f0 requestEnabled true #txt
Mn0 f0 triggerEnabled false #txt
Mn0 f0 callSignature start() #txt
Mn0 f0 caseData businessCase.attach=true #txt
Mn0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start.ivp</name>
    </language>
</elementInfo>
' #txt
Mn0 f0 @C|.responsibility Everybody #txt
Mn0 f0 81 49 30 30 -25 17 #rect
Mn0 f0 @|StartRequestIcon #fIcon
Mn0 f3 type ch.ivyteam.enginecockpit.Data #txt
Mn0 f3 template "index.jsp" #txt
Mn0 f3 337 49 30 30 0 15 #rect
Mn0 f3 @|EndRequestIcon #fIcon
Mn0 f1 expr out #txt
Mn0 f1 111 64 337 64 #arcP
>Proto Mn0 .type ch.ivyteam.enginecockpit.Data #txt
>Proto Mn0 .processKind NORMAL #txt
>Proto Mn0 0 0 32 24 18 0 #rect
>Proto Mn0 @|BIcon #fIcon
Mn0 f0 mainOut f1 tail #connect
Mn0 f1 head f3 mainIn #connect
