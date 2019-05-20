package ch.ivyteam.enginecockpit.fileupload;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.value.QualifiedVersion;
import ch.ivyteam.ivy.application.value.VersionBound;
import ch.ivyteam.ivy.application.value.VersionRange;

//TODO: make ch.ivyteam.ivy.deployment.internal.auto.VersionRangeParser public
public class VersionRangeParser
{
  private static final String DELIMITER = ",";
  private static final String MIN_BOUND_EXCLUSIVE = "(";
  private static final String MIN_BOUND_INCLUSIVE = "[";
  private static final String MAX_BOUND_EXCLUSIVE = ")";
  private static final String MAX_BOUND_INCLUSIVE = "]";

  private String versionRange;
  
  public VersionRangeParser(String versionRange)
  {
    this.versionRange = versionRange;
  }
  
  public VersionRange parse()
  {
    if (StringUtils.isBlank(versionRange))
    {
      return VersionRange.UNSPECIFIED;
    }
    versionRange = versionRange.trim();
    if (!StringUtils.contains(versionRange, DELIMITER))
    {
      QualifiedVersion minBound = new QualifiedVersion(versionRange);
      return new VersionRange(new VersionBound(minBound, true), VersionBound.UNSPECIFIED);
    }
    else
    {
      String minBoundStr = StringUtils.substringBefore(versionRange, DELIMITER);
      String maxBoundStr = StringUtils.substringAfter(versionRange, DELIMITER);
      if (minBoundStr.isEmpty() && maxBoundStr.isEmpty())
      {
        return VersionRange.UNSPECIFIED;
      }
      VersionBound minBound = parseMinBound(minBoundStr);
      VersionBound maxBound = parseMaxBound(maxBoundStr);
      return new VersionRange(minBound, maxBound);
    }
  }

  private VersionBound parseMinBound(String minBound)
  {
    if (StringUtils.isBlank(minBound))
    {
      return VersionBound.UNSPECIFIED;
    }
    minBound = minBound.trim();
    boolean inclusive = true;
    if (minBound.startsWith(MIN_BOUND_INCLUSIVE))
    {
      minBound = StringUtils.substringAfter(minBound, MIN_BOUND_INCLUSIVE);
    }
    else if (minBound.startsWith(MIN_BOUND_EXCLUSIVE))
    {
      inclusive = false;
      minBound = StringUtils.substringAfter(minBound, MIN_BOUND_EXCLUSIVE);
    }
    minBound = minBound.trim();
    if (StringUtils.isBlank(minBound))
    {
      return VersionBound.UNSPECIFIED;
    }
    QualifiedVersion minBoundVersion = new QualifiedVersion(minBound);
    return new VersionBound(minBoundVersion, inclusive);
  }

  private VersionBound parseMaxBound(String maxBound)
  {
    if (StringUtils.isBlank(maxBound))
    {
      return VersionBound.UNSPECIFIED;
    }
    maxBound = maxBound.trim();
    boolean inclusive = true;
    if (maxBound.endsWith(MAX_BOUND_INCLUSIVE))
    {
      maxBound = StringUtils.substringBefore(maxBound, MAX_BOUND_INCLUSIVE);
    }
    else if (maxBound.endsWith(MAX_BOUND_EXCLUSIVE))
    {
      inclusive = false;
      maxBound = StringUtils.substringBefore(maxBound, MAX_BOUND_EXCLUSIVE);
    }
    maxBound = maxBound.trim();
    if (StringUtils.isBlank(maxBound))
    {
      return VersionBound.UNSPECIFIED;
    }
    QualifiedVersion minBoundVersion = new QualifiedVersion(maxBound);
    return new VersionBound(minBoundVersion, inclusive);
  }
}
