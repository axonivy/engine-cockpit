[Ivy]
16D80E7AD6FA8FFB 3.28 #module
>Proto >Proto Collection #zClass
Ba0 BusinessData Big #zClass
Ba0 B #cInfo
Ba0 #process
Ba0 @TextInP .type .type #zField
Ba0 @TextInP .processKind .processKind #zField
Ba0 @TextInP .xml .xml #zField
Ba0 @TextInP .responsibility .responsibility #zField
Ba0 @StartRequest f0 '' #zField
Ba0 @GridStep f1 '' #zField
Ba0 @PushWFArc f2 '' #zField
Ba0 @EndTask f3 '' #zField
Ba0 @PushWFArc f4 '' #zField
>Proto Ba0 Ba0 BusinessData #zField
Ba0 f0 outLink create.ivp #txt
Ba0 f0 inParamDecl '<> param;' #txt
Ba0 f0 requestEnabled true #txt
Ba0 f0 triggerEnabled false #txt
Ba0 f0 callSignature create() #txt
Ba0 f0 caseData businessCase.attach=true #txt
Ba0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>create.ivp</name>
    </language>
</elementInfo>
' #txt
Ba0 f0 @C|.responsibility Everybody #txt
Ba0 f0 81 81 30 30 -25 17 #rect
Ba0 f0 @|StartRequestIcon #fIcon
Ba0 f1 actionTable 'out=in;
' #txt
Ba0 f1 actionCode 'import ch.ivyteam.enginecockpit.testdata.businessdata.TestDataCreator;

TestDataCreator.createDemoDataIfNotExist();' #txt
Ba0 f1 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>createBusinessData</name>
    </language>
</elementInfo>
' #txt
Ba0 f1 152 74 144 44 -63 -7 #rect
Ba0 f1 @|StepIcon #fIcon
Ba0 f2 111 96 152 96 #arcP
Ba0 f3 337 81 30 30 0 15 #rect
Ba0 f3 @|EndIcon #fIcon
Ba0 f4 296 96 337 96 #arcP
>Proto Ba0 .type engine.cockpit.test.data.Data #txt
>Proto Ba0 .processKind NORMAL #txt
>Proto Ba0 0 0 32 24 18 0 #rect
>Proto Ba0 @|BIcon #fIcon
Ba0 f0 mainOut f2 tail #connect
Ba0 f2 head f1 mainIn #connect
Ba0 f1 mainOut f4 tail #connect
Ba0 f4 head f3 mainIn #connect
