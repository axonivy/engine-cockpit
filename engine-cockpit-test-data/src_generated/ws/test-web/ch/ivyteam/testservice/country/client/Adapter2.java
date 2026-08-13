package ch.ivyteam.testservice.country.client;

import ch.ivyteam.ivy.scripting.objects.Time;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, Time>
{


    public Time unmarshal(String value) {
        return (ch.ivyteam.ivy.scripting.objects.adapters.TimeStringJaxbAdapter.parseTime(value));
    }

    public String marshal(Time value) {
        return (ch.ivyteam.ivy.scripting.objects.adapters.TimeStringJaxbAdapter.printTime(value));
    }

}
