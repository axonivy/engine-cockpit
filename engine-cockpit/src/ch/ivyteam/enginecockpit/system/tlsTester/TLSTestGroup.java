package ch.ivyteam.enginecockpit.system.tlsTester;

public enum TLSTestGroup
{
	JAVA_SYSTEM_PROPERTIES("Java System Properties", "Checks all Java system properties relavant for establishing a TLS connection"),
	IVY_SYSTEM_PROPERTIES("Ivy System Properties", "Checks Ivy specific system properties relevant for establishing a TLS connection"),
	CLIENT_SYSTEM_KEYSTORE("Check System Keystore", "Tries to load the system client keystore file, if configured"),
	CLIENT_SYSTEM_TRUSTSTORE("Check System Truststore", "Tries to load the system client truststore file"),
	CLIENT_CUSTOM_KEYSTORE("Check Custom Keystore", "Tries to load the custom client keystore file, if configured"),
	CLIENT_CUSTOM_TRUSTSTORE("Check System Truststore", "Tries to load the custom client trusture file, if configured"),
	CONN_WITH_SYSTEM_TRUSTSTORE("Connect, with System Truststore Only", "Tries to connect to the specified URI with only the system truststore (no client keystore)"),
	CONN_NO_KEYSTORE("Connect, no Client Keystore", "Tries to connect to the specified URI with no client keystore, with system and custom truststore"),
	CONN_WITH_KEYSTORE("Connect, with Client Keystore", "Tries to connect to the specified URI using a client keystore, with system and custom truststore"),
	CONN_UPLOADED_TRUSTSTORE("Connect, with Uploaded Truststore Only", "Tries to connect to the specified URI with truststore uploaded by user (no client keystore)"),
	CONN_IVY_SSL_CONTEXT("Connect, with Ivy SSLContext", "Tries to connect to the specified URI with the Ivy SSL settings.<br/>TLS connections for WebServices, CXF RestServices and secure mail are using these settings."),
	CONN_TLS_SSLV3("SSLv3"),
	CONN_TLS_TLSV10("TLSv1.0"),
	CONN_TLS_TLSV11("TLSv1.1"),
	CONN_TLS_TLSV12("TLSv1.2"),
	CONN_TLS_TLSV13("TLSv1.3");

	private final String name;
	private final String info;

	TLSTestGroup(String protocol)
	{
		this("Connect, with " + protocol, "Tries to connect to the specified URI with TLS protocol version " + protocol);
	}

	TLSTestGroup(String groupName, String groupInfo)
	{
		this.name = groupName;
		this.info = groupInfo;
	}

	public String getName()
	{
		return this.name;
	}

	public String getInfo()
	{
		return this.info;
	}
}
