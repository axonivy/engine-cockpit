package ch.ivyteam.enginecockpit.services;

import org.apache.commons.lang3.StringUtils;

public class ConnectionTestResult
{
  private String method;
  private int statusCode;
  private TestResult restResult;
  private String message;

  public ConnectionTestResult(String method, int statusCode, TestResult testResult, String message)
  {
    this.method = method;
    this.statusCode = statusCode;
    this.restResult = testResult;
    this.message = message;
  }
  
  public String getMessage()
  {
    if (StringUtils.isNotBlank(method) && statusCode != 0)
    {
      return message + " (Method " + method + " >> Status " + statusCode + ")";
    }
    return message;
  }
  
  public String getTestResult() 
  {
    return restResult.name;
  }
  
  public String getTestResultColor()
  {
    return restResult.color;
  }
  
  public interface IConnectionTestResult
  {
    ConnectionTestResult getResult();
  }
  
  public static enum TestResult
  {
    SUCCESS("Success", "green"),
    WARNING("Warning", "#FFC107"),
    ERROR("Error", "red");
    
    private final String name;
    private final String color;

    private TestResult(String name, String color)
    {
      this.name = name;
      this.color = color;
    }
  }
}

