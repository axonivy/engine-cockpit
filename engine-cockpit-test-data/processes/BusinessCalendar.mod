[Ivy]
16AD3F265FFA55DD 7.5.0 #module
>Proto >Proto Collection #zClass
Br0 BusinessCalendar Big #zClass
Br0 B #cInfo
Br0 #process
Br0 @TextInP .type .type #zField
Br0 @TextInP .processKind .processKind #zField
Br0 @AnnotationInP-0n ai ai #zField
Br0 @MessageFlowInP-0n messageIn messageIn #zField
Br0 @MessageFlowOutP-0n messageOut messageOut #zField
Br0 @TextInP .xml .xml #zField
Br0 @TextInP .responsibility .responsibility #zField
Br0 @StartRequest f0 '' #zField
Br0 @EndTask f1 '' #zField
Br0 @GridStep f3 '' #zField
Br0 @PushWFArc f4 '' #zField
Br0 @PushWFArc f2 '' #zField
>Proto Br0 Br0 BusinessCalendar #zField
Br0 f0 outLink start.ivp #txt
Br0 f0 inParamDecl '<> param;' #txt
Br0 f0 requestEnabled true #txt
Br0 f0 triggerEnabled false #txt
Br0 f0 callSignature start() #txt
Br0 f0 caseData businessCase.attach=true #txt
Br0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start.ivp</name>
    </language>
</elementInfo>
' #txt
Br0 f0 @C|.responsibility Everybody #txt
Br0 f0 81 49 30 30 -25 17 #rect
Br0 f0 @|StartRequestIcon #fIcon
Br0 f1 337 49 30 30 0 15 #rect
Br0 f1 @|EndIcon #fIcon
Br0 f3 actionTable 'out=in;
' #txt
Br0 f3 actionCode 'import ch.ivyteam.ivy.application.calendar.FreeDate;
import ch.ivyteam.ivy.application.calendar.FreeEasterRelativeDay;
import ch.ivyteam.ivy.application.calendar.FreeDayOfYear;
import ch.ivyteam.ivy.application.calendar.FreeDayOfWeek;
import ch.ivyteam.util.date.Weekday;
import ch.ivyteam.ivy.application.calendar.WorkingTime;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarSettings;

IApplication app = ivy.wf.getApplication();

IBusinessCalendarSettings settings = app.getBusinessCalendarSettings();

if (settings.findBusinessCalendarConfiguration("Luzern") == null)
{
	IBusinessCalendarConfiguration luzern = settings.createBusinessCalendarConfiguration("Luzern");
	IBusinessCalendarConfiguration zug = settings.createBusinessCalendarConfiguration("Zug");
	
	zug.setParent(luzern);
	
	FreeDayOfYear fdoy1 = new FreeDayOfYear(5, 30);
	fdoy1.setDescription("Auffahrt");
	FreeDayOfYear fdoy2 = new FreeDayOfYear(6, 9);
	fdoy2.setDescription("Pfingsten");
	luzern.getFreeDaysOfYear().add(fdoy1);
	luzern.getFreeDaysOfYear().add(fdoy2);
	
	FreeEasterRelativeDay ferd1 = new FreeEasterRelativeDay(1);
	ferd1.setDescription("Ostermontag");
	luzern.getFreeEasterRelativeDays().add(ferd1);
	
	FreeDate fd1 = new FreeDate(new Date(2019, 8, 15));
	fd1.setDescription("Day off");
	FreeDate fd2 = new FreeDate(new Date(2019, 8, 16));
	fd1.setDescription("Day off");
	
	zug.getFreeDates().add(fd1);
	
	settings.saveBusinessCalendarConfiguration(luzern);
	settings.saveBusinessCalendarConfiguration(zug);
	
	app.findEnvironment("test").setBusinessCalendar(app.getActualEnvironment().getBusinessCalendar().get("Luzern"));
}' #txt
Br0 f3 168 42 112 44 0 -7 #rect
Br0 f3 @|StepIcon #fIcon
Br0 f4 expr out #txt
Br0 f4 111 64 168 64 #arcP
Br0 f2 expr out #txt
Br0 f2 280 64 337 64 #arcP
>Proto Br0 .type engine.cockpit.test.data.Data #txt
>Proto Br0 .processKind NORMAL #txt
>Proto Br0 0 0 32 24 18 0 #rect
>Proto Br0 @|BIcon #fIcon
Br0 f0 mainOut f4 tail #connect
Br0 f4 head f3 mainIn #connect
Br0 f3 mainOut f2 tail #connect
Br0 f2 head f1 mainIn #connect
