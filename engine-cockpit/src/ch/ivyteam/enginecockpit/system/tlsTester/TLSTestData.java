package ch.ivyteam.enginecockpit.system.tlsTester;

import java.util.ArrayList;
import java.util.List;

public final class TLSTestData
{
  private TLSTestGroup testGroup;
  private List<String> entryList;
  private int result = 1;

  public TLSTestData(TLSTestGroup testGroup)
  {
    this.testGroup = testGroup;
    this.entryList = new ArrayList<>();
  }

  public String getGroup()
  {
    return testGroup.getName();
  }

  public String getGroupInfo()
  {
    return testGroup.getInfo();
  }

  public void addEntry(String newEntry)
  {
    this.entryList.add(newEntry);
  }

  public void addEntry(String newEntry, Object... args)
  {
    this.entryList.add(String.format(newEntry, args));
  }

  public List<String> getEntryList()
  {
    return entryList;
  }

  public void setEntryList(List<String> newEntryList)
  {
    this.entryList = newEntryList;
  }

  /**
   * Returns result of test. Default is 1.
   * @return 0 for fail, 1 for success, 2 for infoOnly.
   */
  public int getResult() {
	return result;
  }

  public void setResult(int result) {
	this.result = result;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("Entry for group " + this.testGroup.getName() + ":\n");
    builder.append("  result: " + this.result + ":\n");
    for (String entry : this.entryList)
    {
      builder.append("  - " + entry + "\n");
    }
    return builder.toString();
  }
}
