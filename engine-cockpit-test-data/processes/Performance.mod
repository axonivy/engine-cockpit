[Ivy]
17B77E4EAE9AC806 9.4.8 #module
>Proto >Proto Collection #zClass
Pe0 Performance Big #zClass
Pe0 B #cInfo
Pe0 #process
Pe0 @TextInP .colors .colors #zField
Pe0 @TextInP color color #zField
Pe0 @AnnotationInP-0n ai ai #zField
Pe0 @TextInP .type .type #zField
Pe0 @TextInP .processKind .processKind #zField
Pe0 @TextInP .xml .xml #zField
Pe0 @TextInP .responsibility .responsibility #zField
Pe0 @StartRequest f0 '' #zField
Pe0 @EndTask f1 '' #zField
Pe0 @GridStep f3 '' #zField
Pe0 @PushWFArc f4 '' #zField
Pe0 @Alternative f5 '' #zField
Pe0 @PushWFArc f6 '' #zField
Pe0 @PushWFArc f2 '' #zField
Pe0 @PushWFArc f7 '' #zField
>Proto Pe0 Pe0 Performance #zField
Pe0 f0 outLink performance.ivp #txt
Pe0 f0 inParamDecl '<> param;' #txt
Pe0 f0 requestEnabled true #txt
Pe0 f0 triggerEnabled false #txt
Pe0 f0 callSignature performance() #txt
Pe0 f0 caseData businessCase.attach=true #txt
Pe0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>performance.ivp</name>
    </language>
</elementInfo>
' #txt
Pe0 f0 @C|.responsibility Everybody #txt
Pe0 f0 81 113 30 30 -21 17 #rect
Pe0 f1 433 113 30 30 0 15 #rect
Pe0 f3 actionTable 'out=in;
out.count=in.count+1;
' #txt
Pe0 f3 168 106 112 44 0 -8 #rect
Pe0 f4 111 128 168 128 #arcP
Pe0 f5 336 112 32 32 0 16 #rect
Pe0 f6 280 128 336 128 #arcP
Pe0 f2 expr in #txt
Pe0 f2 outCond 'in.count > 100' #txt
Pe0 f2 368 128 433 128 #arcP
Pe0 f7 expr in #txt
Pe0 f7 352 112 224 106 #arcP
Pe0 f7 1 352 48 #addKink
Pe0 f7 2 224 48 #addKink
Pe0 f7 1 0.48878412258473336 0 0 #arcLabel
>Proto Pe0 .type engine.cockpit.test.data.Data #txt
>Proto Pe0 .processKind NORMAL #txt
>Proto Pe0 0 0 32 24 18 0 #rect
>Proto Pe0 @|BIcon #fIcon
Pe0 f0 mainOut f4 tail #connect
Pe0 f4 head f3 mainIn #connect
Pe0 f3 mainOut f6 tail #connect
Pe0 f6 head f5 in #connect
Pe0 f5 out f2 tail #connect
Pe0 f2 head f1 mainIn #connect
Pe0 f5 out f7 tail #connect
Pe0 f7 head f3 mainIn #connect
