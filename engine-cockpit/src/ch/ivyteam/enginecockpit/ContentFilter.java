package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.model.SelectItem;

public interface ContentFilter
{

  List<SelectItem> getContentFilters();

  List<String> getSelectedContentFilters();

  void setSelectedContentFilters(List<String> selectedContentFilters);

  void resetSelectedContentFilters();

  String getContentFilterText();

}