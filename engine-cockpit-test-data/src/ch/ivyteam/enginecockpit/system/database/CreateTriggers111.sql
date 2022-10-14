CREATE TRIGGER IWA_ApplicationDeleteTrigger
AFTER DELETE ON IWA_Application
FOR EACH ROW
BEGIN
  DELETE FROM IWA_ProcessModel
  WHERE IWA_ProcessModel.ApplicationId = OLD.ApplicationId;

END;

CREATE TRIGGER IWA_ProcessModelDeleteTrigger
AFTER DELETE ON IWA_ProcessModel
FOR EACH ROW
BEGIN
  DELETE FROM IWA_ProcessModelVersion
  WHERE IWA_ProcessModelVersion.ProcessModelId = OLD.ProcessModelId;

  DELETE FROM IWA_Case
  WHERE IWA_Case.ProcessModelId = OLD.ProcessModelId;

END;

CREATE TRIGGER IWA_ProcessModelVersionDeleteTrigger
AFTER DELETE ON IWA_ProcessModelVersion
FOR EACH ROW
BEGIN
  DELETE FROM IWA_TaskElement
  WHERE IWA_TaskElement.ProcessModelVersionId = OLD.ProcessModelVersionId;

END;

CREATE TRIGGER IWA_LibraryDeleteTrigger
AFTER DELETE ON IWA_Library
FOR EACH ROW
BEGIN
  UPDATE IWA_LibrarySpecification
  SET ResolvedLibraryId=NULL
  WHERE IWA_LibrarySpecification.ResolvedLibraryId=OLD.LibraryId;

END;

CREATE TRIGGER IWA_TaskElementDeleteTrigger
AFTER DELETE ON IWA_TaskElement
FOR EACH ROW
BEGIN
  DELETE FROM IWA_TaskSwitchEvent
  WHERE IWA_TaskSwitchEvent.TaskElementId = OLD.TaskElementId;

  DELETE FROM IWA_TaskStart
  WHERE IWA_TaskStart.TaskElementId = OLD.TaskElementId;

  DELETE FROM IWA_TaskEnd
  WHERE IWA_TaskEnd.TaskElementId = OLD.TaskElementId;

END;

CREATE TRIGGER IWA_TaskStartDeleteTrigger
AFTER DELETE ON IWA_TaskStart
FOR EACH ROW
BEGIN
  DELETE FROM IWA_IntermediateEvent
  WHERE IWA_IntermediateEvent.TaskStartId = OLD.TaskStartId;

END;

CREATE TRIGGER IWA_TaskSwitchEventDeleteTrigger
AFTER DELETE ON IWA_TaskSwitchEvent
FOR EACH ROW
BEGIN
  DELETE FROM IWA_StartTaskData
  WHERE IWA_StartTaskData.TaskSwitchEventId = OLD.TaskSwitchEventId;

END;

CREATE TRIGGER IWA_CaseDeleteTrigger
AFTER DELETE ON IWA_Case
FOR EACH ROW
BEGIN
  DELETE FROM IWA_TaskSwitchEvent
  WHERE IWA_TaskSwitchEvent.CaseId = OLD.CaseId;

  DELETE FROM IWA_Task
  WHERE IWA_Task.CaseId = OLD.CaseId;

  DELETE FROM IWA_Task
  WHERE IWA_Task.BusinessCaseId = OLD.CaseId;

  DELETE FROM IWA_CaseNote
  WHERE IWA_CaseNote.CaseId = OLD.CaseId;

  DELETE FROM IWA_WorkflowEvent
  WHERE IWA_WorkflowEvent.CaseId = OLD.CaseId;

END;

CREATE TRIGGER IWA_TaskDeleteTrigger
AFTER DELETE ON IWA_Task
FOR EACH ROW
BEGIN
  UPDATE IWA_SignalEvent
  SET SentByTaskId=NULL
  WHERE IWA_SignalEvent.SentByTaskId=OLD.TaskId;

  DELETE FROM IWA_IntermediateEvent
  WHERE IWA_IntermediateEvent.TaskId = OLD.TaskId;

  DELETE FROM IWA_SignaledTask
  WHERE IWA_SignaledTask.SignaledTaskId = OLD.TaskId;

  DELETE FROM IWA_TaskLocation
  WHERE IWA_TaskLocation.TaskId = OLD.TaskId;

  DELETE FROM IWA_TaskNote
  WHERE IWA_TaskNote.TaskId = OLD.TaskId;

  DELETE FROM IWA_TaskSignalEventReceiver
  WHERE IWA_TaskSignalEventReceiver.WaitingTaskId = OLD.TaskId;

  DELETE FROM IWA_TaskData
  WHERE IWA_TaskData.TaskId = OLD.TaskId;

  DELETE FROM IWA_WorkflowEvent
  WHERE IWA_WorkflowEvent.TaskId = OLD.TaskId;

END;

CREATE TRIGGER IWA_IntermediateEventTrigger
AFTER DELETE ON IWA_IntermediateEvent
FOR EACH ROW
BEGIN
  DELETE FROM IWA_IntermediateEventData
  WHERE IWA_IntermediateEventData.IntermediateEventId = OLD.IntermediateEventId;

END;

CREATE TRIGGER IWA_TaskNoteDeleteTrigger
AFTER DELETE ON IWA_TaskNote
FOR EACH ROW
BEGIN
  DELETE FROM IWA_Note
  WHERE IWA_Note.NoteId = OLD.NoteId;

END;

CREATE TRIGGER IWA_CaseNoteDeleteTrigger
AFTER DELETE ON IWA_CaseNote
FOR EACH ROW
BEGIN
  DELETE FROM IWA_Note
  WHERE IWA_Note.NoteId = OLD.NoteId;

END;

CREATE TRIGGER IWA_SignaledTaskDeleteTrigger
AFTER DELETE ON IWA_SignaledTask
FOR EACH ROW
BEGIN
  UPDATE IWA_TaskSignalEventReceiver
  SET StartedSignaledTaskId=NULL
  WHERE IWA_TaskSignalEventReceiver.StartedSignaledTaskId=OLD.SignaledTaskId;

END;

CREATE TRIGGER IWA_SignalEventDeleteTrigger
AFTER DELETE ON IWA_SignalEvent
FOR EACH ROW
BEGIN
  UPDATE IWA_SignaledTask
  SET ReceivedSignalEventId=NULL
  WHERE IWA_SignaledTask.ReceivedSignalEventId=OLD.SignalEventId;

  DELETE FROM IWA_SignalEventData
  WHERE IWA_SignalEventData.SignalEventId = OLD.SignalEventId;

END;

CREATE TRIGGER IWA_SecurityDescriptorDeleteTrigger
AFTER DELETE ON IWA_SecurityDescriptor
FOR EACH ROW
BEGIN
  DELETE FROM IWA_AccessControl
  WHERE IWA_AccessControl.SecurityDescriptorId = OLD.SecurityDescriptorId;

END;
