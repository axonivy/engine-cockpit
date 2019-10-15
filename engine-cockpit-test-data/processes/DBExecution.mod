[Ivy]
16C6B9ADB931DEF8 7.5.0 #module
>Proto >Proto Collection #zClass
Dn0 DBExecution Big #zClass
Dn0 B #cInfo
Dn0 #process
Dn0 @TextInP .type .type #zField
Dn0 @TextInP .processKind .processKind #zField
Dn0 @AnnotationInP-0n ai ai #zField
Dn0 @MessageFlowInP-0n messageIn messageIn #zField
Dn0 @MessageFlowOutP-0n messageOut messageOut #zField
Dn0 @TextInP .xml .xml #zField
Dn0 @TextInP .responsibility .responsibility #zField
Dn0 @StartRequest f0 '' #zField
Dn0 @EndTask f1 '' #zField
Dn0 @DBStep f3 '' #zField
Dn0 @PushWFArc f4 '' #zField
Dn0 @PushWFArc f2 '' #zField
>Proto Dn0 Dn0 DBExecution #zField
Dn0 f0 outLink start.ivp #txt
Dn0 f0 inParamDecl '<> param;' #txt
Dn0 f0 requestEnabled true #txt
Dn0 f0 triggerEnabled false #txt
Dn0 f0 callSignature start() #txt
Dn0 f0 caseData businessCase.attach=true #txt
Dn0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start.ivp</name>
    </language>
</elementInfo>
' #txt
Dn0 f0 @C|.responsibility Everybody #txt
Dn0 f0 81 49 30 30 -25 17 #rect
Dn0 f0 @|StartRequestIcon #fIcon
Dn0 f1 337 49 30 30 0 15 #rect
Dn0 f1 @|EndIcon #fIcon
Dn0 f3 actionTable 'out=in;
' #txt
Dn0 f3 dbSql '<?xml version=""1.0"" standalone=""no""?>
<!DOCTYPE SELECT SYSTEM  ""sqlStatements.dtd"">
<SELECT><Table name=''test''/></SELECT>' #txt
Dn0 f3 dbUrl realdb #txt
Dn0 f3 lotSize 2147483647 #txt
Dn0 f3 startIdx 0 #txt
Dn0 f3 168 42 112 44 0 -7 #rect
Dn0 f3 @|DBStepIcon #fIcon
Dn0 f4 expr out #txt
Dn0 f4 111 64 168 64 #arcP
Dn0 f2 280 64 337 64 #arcP
>Proto Dn0 .type engine.cockpit.test.data.Data #txt
>Proto Dn0 .processKind NORMAL #txt
>Proto Dn0 0 0 32 24 18 0 #rect
>Proto Dn0 @|BIcon #fIcon
Dn0 f0 mainOut f4 tail #connect
Dn0 f4 head f3 mainIn #connect
Dn0 f3 mainOut f2 tail #connect
Dn0 f2 head f1 mainIn #connect
