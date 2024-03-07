package ch.ivyteam.enginecockpit.setup.migration;

import java.util.List;

import org.apache.commons.lang3.RegExUtils;

import ch.ivyteam.ivy.migration.input.Quest;

@SuppressWarnings("restriction")
public class Question {

  private String title;
  private String more;
  private List<?> options;
  private String answer;
  private String diff;

  public Question(Quest<?> quest, String diff) {
    title = quest.title;
    more = quest.more;
    options = quest.options;
    diff = RegExUtils.replaceAll(diff, "(\\r|\\n|\\r\\n)", "\\\\n");
    this.diff = diff.replace("'", "\\'"); // for javascript
  }

  public String getTitle() {
    return title;
  }

  public String getMore() {
    return more;
  }

  public List<?> getOptions() {
    return options;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getAnswer() {
    return answer;
  }

  public String getDiff() {
    return diff;
  }

}
