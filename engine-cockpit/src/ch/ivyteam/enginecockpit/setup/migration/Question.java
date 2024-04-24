package ch.ivyteam.enginecockpit.setup.migration;

import java.util.List;

import org.apache.commons.lang3.RegExUtils;

import ch.ivyteam.ivy.engine.migration.input.Quest;

public class Question {

  private final String title;
  private final String more;
  private final List<?> options;
  private String answer;
  private final String diff;

  public Question(Quest<?> quest, String diff) {
    this.title = quest.title;
    this.more = quest.more;
    this.options = quest.options;
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
