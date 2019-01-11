package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IRole;

public class Role {
	private String name;
	private String description;
	private String displayName;
	private String externalName;
	
	public Role(IRole role) {
		this(role.getName(), role.getDisplayDescription(), role.getDisplayName(), role.getExternalSecurityName());
	}

	public Role(String name) {
		this(name, "", "", "");
	}
	
	public Role(String name, String description, String displayName, String externalName) {
		this.name = name;
		this.description = description;
		this.displayName = displayName;
		this.externalName = externalName;
	}
	
	public Role() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((externalName == null) ? 0 : externalName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (externalName == null) {
			if (other.externalName != null)
				return false;
		} else if (!externalName.equals(other.externalName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Role [name=" + name + "]";
	}
	
}
