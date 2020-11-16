[Ivy]
175D14A6E0DA8F94 9.2.0 #module
>Proto >Proto Collection #zClass
Ss0 Services Big #zClass
Ss0 WS #cInfo
Ss0 #process
Ss0 @TextInP .webServiceName .webServiceName #zField
Ss0 @TextInP .implementationClassName .implementationClassName #zField
Ss0 @TextInP .authenticationType .authenticationType #zField
Ss0 @TextInP .type .type #zField
Ss0 @TextInP .processKind .processKind #zField
Ss0 @TextInP .xml .xml #zField
Ss0 @TextInP .responsibility .responsibility #zField
Ss0 @StartWS f0 '' #zField
Ss0 @EndWS f1 '' #zField
Ss0 @PushWFArc f2 '' #zField
>Proto Ss0 Ss0 Services #zField
Ss0 f0 inParamDecl '<String name> param;' #txt
Ss0 f0 outParamDecl '<> result;' #txt
Ss0 f0 callSignature hello(String) #txt
Ss0 f0 useUserDefinedException false #txt
Ss0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>hello(String)</name>
    </language>
</elementInfo>
' #txt
Ss0 f0 @C|.responsibility Everybody #txt
Ss0 f0 81 49 30 30 -34 21 #rect
Ss0 f0 @|StartWSIcon #fIcon
Ss0 f1 337 49 30 30 0 15 #rect
Ss0 f1 @|EndWSIcon #fIcon
Ss0 f2 111 64 337 64 #arcP
>Proto Ss0 .webServiceName engine.cockpit.test.data.Services #txt
>Proto Ss0 .type engine.cockpit.test.data.Data #txt
>Proto Ss0 .processKind WEB_SERVICE #txt
>Proto Ss0 -8 -8 16 16 16 26 #rect
>Proto Ss0 '' #fIcon
Ss0 f0 mainOut f2 tail #connect
Ss0 f2 head f1 mainIn #connect
