package ch.ivyteam.testservice.country.client;

import ch.ivyteam.ivy.scripting.objects.DateTime;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, DateTime>
{


    public DateTime unmarshal(String value) {
        return (ch.ivyteam.ivy.scripting.objects.adapters.DateTimeStringJaxbAdapter.parseDateTime(value));
    }

    public String marshal(DateTime value) {
        return (ch.ivyteam.ivy.scripting.objects.adapters.DateTimeStringJaxbAdapter.printDateTime(value));
    }

}
