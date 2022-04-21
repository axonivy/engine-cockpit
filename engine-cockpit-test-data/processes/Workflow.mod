[Ivy]
17D57ADFD804B24E 9.4.8 #module
>Proto >Proto Collection #zClass
Ww0 Workflow Big #zClass
Ww0 B #cInfo
Ww0 #process
Ww0 @TextInP .colors .colors #zField
Ww0 @TextInP color color #zField
Ww0 @AnnotationInP-0n ai ai #zField
Ww0 @TextInP .type .type #zField
Ww0 @TextInP .processKind .processKind #zField
Ww0 @TextInP .xml .xml #zField
Ww0 @TextInP .responsibility .responsibility #zField
Ww0 @GridStep f66 '' #zField
Ww0 @EndTask f65 '' #zField
Ww0 @StartRequest f64 '' #zField
Ww0 @EndTask f60 '' #zField
Ww0 @StartRequest f59 '' #zField
Ww0 @TaskSwitchSimple f61 '' #zField
Ww0 @PushWFArc f67 '' #zField
Ww0 @TkArc f62 '' #zField
Ww0 @PushWFArc f68 '' #zField
Ww0 @PushWFArc f63 '' #zField
>Proto Ww0 Ww0 Workflow #zField
Ww0 f66 actionTable 'out=in;
' #txt
Ww0 f66 actionCode 'import ch.ivyteam.ivy.workflow.query.TaskQuery;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.TaskState;

TaskQuery query = TaskQuery.create().where().name().isEqual("openTask")
	.and().state().isEqual(TaskState.SUSPENDED);
ITask task = ivy.wf.getTaskQueryExecutor().getFirstResult(query) as ITask;
task.destroy();' #txt
Ww0 f66 security system #txt
Ww0 f66 168 138 112 44 0 -7 #rect
Ww0 f65 337 145 30 30 0 15 #rect
Ww0 f64 outLink destroyRunningCase.ivp #txt
Ww0 f64 inParamDecl '<> param;' #txt
Ww0 f64 requestEnabled true #txt
Ww0 f64 triggerEnabled false #txt
Ww0 f64 callSignature destroyRunningCase() #txt
Ww0 f64 caseData businessCase.attach=true #txt
Ww0 f64 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>destroyRunningCase.ivp</name>
    </language>
</elementInfo>
' #txt
Ww0 f64 @C|.responsibility Everybody #txt
Ww0 f64 81 145 30 30 -77 22 #rect
Ww0 f60 337 49 30 30 0 15 #rect
Ww0 f59 outLink runningCase.ivp #txt
Ww0 f59 inParamDecl '<> param;' #txt
Ww0 f59 requestEnabled true #txt
Ww0 f59 triggerEnabled false #txt
Ww0 f59 callSignature runningCase() #txt
Ww0 f59 caseData businessCase.attach=true #txt
Ww0 f59 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>runningCase.ivp</name>
    </language>
</elementInfo>
' #txt
Ww0 f59 @C|.responsibility Everybody #txt
Ww0 f59 81 49 30 30 -44 20 #rect
Ww0 f61 actionTable 'out=in1;
' #txt
Ww0 f61 taskData TaskA.NAM=openTask #txt
Ww0 f61 209 49 30 30 0 16 #rect
Ww0 f67 111 160 168 160 #arcP
Ww0 f62 111 64 209 64 #arcP
Ww0 f68 280 160 337 160 #arcP
Ww0 f63 239 64 337 64 #arcP
>Proto Ww0 .type engine.cockpit.test.data.Data #txt
>Proto Ww0 .processKind NORMAL #txt
>Proto Ww0 0 0 32 24 18 0 #rect
>Proto Ww0 @|BIcon #fIcon
Ww0 f59 mainOut f62 tail #connect
Ww0 f62 head f61 in #connect
Ww0 f61 out f63 tail #connect
Ww0 f63 head f60 mainIn #connect
Ww0 f64 mainOut f67 tail #connect
Ww0 f67 head f66 mainIn #connect
Ww0 f66 mainOut f68 tail #connect
Ww0 f68 head f65 mainIn #connect
