package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class CacheBean
{
  private List<Cache> caches;
  private List<Cache> filteredCaches;
  private String filter;

  public CacheBean()
  {
    var server = ManagementFactory.getPlatformMBeanServer();
    try
    {
      caches = server.queryNames(new ObjectName("ivy Engine:type=CacheClassPersistencyService,name=*"), null)
          .stream()
          .flatMap(Cache::toCaches)
          .collect(Collectors.toList());
    }
    catch(MalformedObjectNameException ex)
    {}
  }

  public List<Cache> getCaches()
  {
    return caches;
  }

  public List<Cache> getFilteredCaches()
  {
    return filteredCaches;
  }

  public void setFilteredCaches(List<Cache> filteredCaches)
  {
    this.filteredCaches = filteredCaches;
  }

  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  public static final class Cache
  {
    private final String name;
    private final ValueProvider count;
    private final ValueProvider writes;
    private final ValueProvider readHits;
    private final ValueProvider readMisses;
    private final ValueProvider limit;
    private final ValueProvider info;
    
    private static Stream<Cache> toCaches(ObjectName objectName)
    {
      try
      {
        var name = StringUtils.substringAfterLast(objectName.getKeyProperty("name"), ".");
        name = StringUtils.removeEnd(name, "Data");

        return Stream.of(
             toEntityCache(name, objectName), 
             toAssociationCache(name, objectName), 
             toBinaryCache(name, objectName), 
             toCharacterCache(name, objectName));
      }
      catch(MalformedObjectNameException ex)
      {
        return null;
      }
    }
    
    private static Cache toEntityCache(String name, ObjectName objectName) throws MalformedObjectNameException
    {
      var advisorName = new ObjectName(objectName.toString()+",strategy=CacheAllRemoveUnused");
      ValueProvider limit = null;
      ValueProvider info = null;
      if (ManagementFactory.getPlatformMBeanServer().isRegistered(advisorName))
      {
        limit = ValueProvider.attribute(advisorName, "countLimit", Unit.ONE);
        info = ValueProvider.attribute(advisorName, "usageLimit", Unit.ONE);
      }

      objectName = new ObjectName(objectName.toString()+",cache=ObjectsAndAssociations");
      
      var count = ValueProvider.attribute(objectName, "cachedObjects", Unit.ONE);
      var writes = ValueProvider.attribute(objectName, "objectWrites", Unit.ONE);
      var readHits = ValueProvider.attribute(objectName, "objectReadHits", Unit.ONE);
      var readMisses = ValueProvider.attribute(objectName, "objectReadMisses", Unit.ONE);
      return new Cache(name+" Entities", count, limit, readHits, readMisses, writes, info);
    }

    private static Cache toAssociationCache(String name, ObjectName objectName) throws MalformedObjectNameException
    {
      objectName = new ObjectName(objectName.toString()+",cache=ObjectsAndAssociations");
      
      var count = ValueProvider.attribute(objectName, "cachedAssociations", Unit.ONE);
      var writes = ValueProvider.attribute(objectName, "associationWrites", Unit.ONE);
      var readHits = ValueProvider.attribute(objectName, "assocationReadHits", Unit.ONE);
      var readMisses = ValueProvider.attribute(objectName, "associationReadMisses", Unit.ONE);
      return new Cache(name+" Associations", count, null, readHits, readMisses, writes, null);
    }
    
    private static Cache toBinaryCache(String name, ObjectName objectName) throws MalformedObjectNameException
    {
      ValueProvider info = null;
      var advisorName = getAdvisorName(objectName);
      if (advisorName != null)
      {
        info = ValueProvider.attribute(advisorName, "maxBytesToCache", Unit.BYTES);
      }
      objectName = new ObjectName(objectName.toString()+",cache=LongBinaries");
      var count = ValueProvider.attribute(objectName, "cachedLongValues", Unit.ONE);
      var writes = ValueProvider.attribute(objectName, "writes", Unit.ONE);
      var readHits = ValueProvider.attribute(objectName, "readHits", Unit.ONE);
      var readMisses = ValueProvider.attribute(objectName, "readMisses", Unit.ONE);
      return new Cache(name+" Long Binaries", count, null, readHits, readMisses, writes, info);
    }

    private static Cache toCharacterCache(String name, ObjectName objectName) throws MalformedObjectNameException
    {
      ValueProvider info = null;
      var advisorName = getAdvisorName(objectName);
      if (advisorName != null)
      {
        info = ValueProvider.attribute(advisorName, "maxCharactersToCache", Unit.BYTES);
      }
      objectName = new ObjectName(objectName.toString()+",cache=LongCharacters");
      var count = ValueProvider.attribute(objectName, "cachedLongValues", Unit.ONE);
      var writes = ValueProvider.attribute(objectName, "writes", Unit.ONE);
      var readHits = ValueProvider.attribute(objectName, "readHits", Unit.ONE);
      var readMisses = ValueProvider.attribute(objectName, "readMisses", Unit.ONE);
      return new Cache(name+" Long Characaters", count, null, readHits, readMisses, writes, info);
    }

    private static ObjectName getAdvisorName(ObjectName objectName) throws MalformedObjectNameException
    {
      var advisorName = new ObjectName(objectName.toString()+",strategy=CacheAllRemoveUnused");
      if (ManagementFactory.getPlatformMBeanServer().isRegistered(advisorName))
      {
        return advisorName;
      }
      advisorName = new ObjectName(objectName.toString()+",strategy=CacheAll");
      if (ManagementFactory.getPlatformMBeanServer().isRegistered(advisorName))
      {
        return advisorName;
      }
      return null;
    }

    private Cache(String name, ValueProvider count, ValueProvider limit, ValueProvider readHits, ValueProvider readMisses, ValueProvider writes, ValueProvider info)
    {
      this.name = name;
      this.count = count;
      this.limit = limit;
      this.readHits = readHits;
      this.readMisses = readMisses;
      this.writes = writes;
      this.info = info;
    }
    
    public String getName()
    {
      return name;
    }
    
    public long getCount()
    {
      return count.nextValue().longValue();
    }
    
    public String getCountStyle()
    {
      if (limit == null)
      {
        return "";
      }
      long p = count.nextValue().longValue()*100 / limit.nextValue().longValue();
      if (p > 95L)
      {
        return "critical";
      }
      else if (p > 80L)
      {
        return "warning";
      }
      return "";
    }
    
    public String getLimit()
    {
      if (limit != null)
      {
        return limit.nextValue().toString();
      }
      return "n.a.";
    }

    public long getWrites()
    {
      return writes.nextValue().longValue();
    }
    
    public long getReadHits()
    {
      return readHits.nextValue().longValue();
    }

    public long getReadMisses()
    {
      return readMisses.nextValue().longValue();
    }
    
    public String getReadMissesStyle()
    {
      var misses = readMisses.nextValue().longValue();
      var wrs = writes.nextValue().longValue();
      if (misses <= wrs)
      {
        return "";
      }
      var hits = readHits.nextValue().longValue();
      if (misses <= hits)
      {
        return "";
      }
      if (misses > hits * 10)
      {
        return "critical";
      }
      return "warning";
    }

    public String getInfo()
    {
      if (info != null)
      {
        return info.nextValue().toString();
      }
      return "";
    }
  }
}
