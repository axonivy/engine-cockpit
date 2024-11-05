package ch.ivyteam.enginecockpit.services.rest;

import java.util.Date;

public record ExecHistory(Date timestamp, long execTime, String response, String elementId ) {}

