package ch.ivyteam.testservice.country.client;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.ivyteam.testservice.country.client package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.9", date = "2026-08-13T13:20:51+02:00")
public class ObjectFactory {

    private static final QName _SimpleType_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
    private static final QName _ComplexType_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
    private static final QName _Group_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "group");
    private static final QName _AttributeGroup_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
    private static final QName _Element_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "element");
    private static final QName _Attribute_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
    private static final QName _AnyAttribute_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
    private static final QName _All_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "all");
    private static final QName _Choice_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "choice");
    private static final QName _Sequence_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
    private static final QName _Unique_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "unique");
    private static final QName _Key_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "key");
    private static final QName _MinExclusive_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "minExclusive");
    private static final QName _MinInclusive_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "minInclusive");
    private static final QName _MaxExclusive_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "maxExclusive");
    private static final QName _MaxInclusive_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "maxInclusive");
    private static final QName _FractionDigits_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "fractionDigits");
    private static final QName _Length_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "length");
    private static final QName _MinLength_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "minLength");
    private static final QName _MaxLength_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "maxLength");
    private static final QName _Enumeration_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "enumeration");
    private static final QName _GetCountryByShortName_QNAME = new QName("http://country.testservice.ivyteam.ch/", "getCountryByShortName");
    private static final QName _GetCountryByShortNameResponse_QNAME = new QName("http://country.testservice.ivyteam.ch/", "getCountryByShortNameResponse");
    private static final QName _LongRunningOperation_QNAME = new QName("http://country.testservice.ivyteam.ch/", "longRunningOperation");
    private static final QName _LongRunningOperationResponse_QNAME = new QName("http://country.testservice.ivyteam.ch/", "longRunningOperationResponse");
    private static final QName _ReturnSoapFault_QNAME = new QName("http://country.testservice.ivyteam.ch/", "returnSoapFault");
    private static final QName _ReturnSoapFaultResponse_QNAME = new QName("http://country.testservice.ivyteam.ch/", "returnSoapFaultResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.ivyteam.testservice.country.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Schema }
     * 
     * @return
     *     the new instance of {@link Schema }
     */
    public Schema createSchema() {
        return new Schema();
    }

    /**
     * Create an instance of {@link OpenAttrs }
     * 
     * @return
     *     the new instance of {@link OpenAttrs }
     */
    public OpenAttrs createOpenAttrs() {
        return new OpenAttrs();
    }

    /**
     * Create an instance of {@link Include }
     * 
     * @return
     *     the new instance of {@link Include }
     */
    public Include createInclude() {
        return new Include();
    }

    /**
     * Create an instance of {@link Annotated }
     * 
     * @return
     *     the new instance of {@link Annotated }
     */
    public Annotated createAnnotated() {
        return new Annotated();
    }

    /**
     * Create an instance of {@link Annotation }
     * 
     * @return
     *     the new instance of {@link Annotation }
     */
    public Annotation createAnnotation() {
        return new Annotation();
    }

    /**
     * Create an instance of {@link Appinfo }
     * 
     * @return
     *     the new instance of {@link Appinfo }
     */
    public Appinfo createAppinfo() {
        return new Appinfo();
    }

    /**
     * Create an instance of {@link Documentation }
     * 
     * @return
     *     the new instance of {@link Documentation }
     */
    public Documentation createDocumentation() {
        return new Documentation();
    }

    /**
     * Create an instance of {@link Import }
     * 
     * @return
     *     the new instance of {@link Import }
     */
    public Import createImport() {
        return new Import();
    }

    /**
     * Create an instance of {@link Redefine }
     * 
     * @return
     *     the new instance of {@link Redefine }
     */
    public Redefine createRedefine() {
        return new Redefine();
    }

    /**
     * Create an instance of {@link TopLevelSimpleType }
     * 
     * @return
     *     the new instance of {@link TopLevelSimpleType }
     */
    public TopLevelSimpleType createTopLevelSimpleType() {
        return new TopLevelSimpleType();
    }

    /**
     * Create an instance of {@link TopLevelComplexType }
     * 
     * @return
     *     the new instance of {@link TopLevelComplexType }
     */
    public TopLevelComplexType createTopLevelComplexType() {
        return new TopLevelComplexType();
    }

    /**
     * Create an instance of {@link NamedGroup }
     * 
     * @return
     *     the new instance of {@link NamedGroup }
     */
    public NamedGroup createNamedGroup() {
        return new NamedGroup();
    }

    /**
     * Create an instance of {@link NamedAttributeGroup }
     * 
     * @return
     *     the new instance of {@link NamedAttributeGroup }
     */
    public NamedAttributeGroup createNamedAttributeGroup() {
        return new NamedAttributeGroup();
    }

    /**
     * Create an instance of {@link TopLevelElement }
     * 
     * @return
     *     the new instance of {@link TopLevelElement }
     */
    public TopLevelElement createTopLevelElement() {
        return new TopLevelElement();
    }

    /**
     * Create an instance of {@link TopLevelAttribute }
     * 
     * @return
     *     the new instance of {@link TopLevelAttribute }
     */
    public TopLevelAttribute createTopLevelAttribute() {
        return new TopLevelAttribute();
    }

    /**
     * Create an instance of {@link Notation }
     * 
     * @return
     *     the new instance of {@link Notation }
     */
    public Notation createNotation() {
        return new Notation();
    }

    /**
     * Create an instance of {@link Wildcard }
     * 
     * @return
     *     the new instance of {@link Wildcard }
     */
    public Wildcard createWildcard() {
        return new Wildcard();
    }

    /**
     * Create an instance of {@link ComplexContent }
     * 
     * @return
     *     the new instance of {@link ComplexContent }
     */
    public ComplexContent createComplexContent() {
        return new ComplexContent();
    }

    /**
     * Create an instance of {@link ComplexRestrictionType }
     * 
     * @return
     *     the new instance of {@link ComplexRestrictionType }
     */
    public ComplexRestrictionType createComplexRestrictionType() {
        return new ComplexRestrictionType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     * @return
     *     the new instance of {@link ExtensionType }
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link SimpleContent }
     * 
     * @return
     *     the new instance of {@link SimpleContent }
     */
    public SimpleContent createSimpleContent() {
        return new SimpleContent();
    }

    /**
     * Create an instance of {@link SimpleRestrictionType }
     * 
     * @return
     *     the new instance of {@link SimpleRestrictionType }
     */
    public SimpleRestrictionType createSimpleRestrictionType() {
        return new SimpleRestrictionType();
    }

    /**
     * Create an instance of {@link SimpleExtensionType }
     * 
     * @return
     *     the new instance of {@link SimpleExtensionType }
     */
    public SimpleExtensionType createSimpleExtensionType() {
        return new SimpleExtensionType();
    }

    /**
     * Create an instance of {@link All }
     * 
     * @return
     *     the new instance of {@link All }
     */
    public All createAll() {
        return new All();
    }

    /**
     * Create an instance of {@link ExplicitGroup }
     * 
     * @return
     *     the new instance of {@link ExplicitGroup }
     */
    public ExplicitGroup createExplicitGroup() {
        return new ExplicitGroup();
    }

    /**
     * Create an instance of {@link Any }
     * 
     * @return
     *     the new instance of {@link Any }
     */
    public Any createAny() {
        return new Any();
    }

    /**
     * Create an instance of {@link Selector }
     * 
     * @return
     *     the new instance of {@link Selector }
     */
    public Selector createSelector() {
        return new Selector();
    }

    /**
     * Create an instance of {@link Field }
     * 
     * @return
     *     the new instance of {@link Field }
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link Keybase }
     * 
     * @return
     *     the new instance of {@link Keybase }
     */
    public Keybase createKeybase() {
        return new Keybase();
    }

    /**
     * Create an instance of {@link Keyref }
     * 
     * @return
     *     the new instance of {@link Keyref }
     */
    public Keyref createKeyref() {
        return new Keyref();
    }

    /**
     * Create an instance of {@link Restriction }
     * 
     * @return
     *     the new instance of {@link Restriction }
     */
    public Restriction createRestriction() {
        return new Restriction();
    }

    /**
     * Create an instance of {@link LocalSimpleType }
     * 
     * @return
     *     the new instance of {@link LocalSimpleType }
     */
    public LocalSimpleType createLocalSimpleType() {
        return new LocalSimpleType();
    }

    /**
     * Create an instance of {@link Facet }
     * 
     * @return
     *     the new instance of {@link Facet }
     */
    public Facet createFacet() {
        return new Facet();
    }

    /**
     * Create an instance of {@link TotalDigits }
     * 
     * @return
     *     the new instance of {@link TotalDigits }
     */
    public TotalDigits createTotalDigits() {
        return new TotalDigits();
    }

    /**
     * Create an instance of {@link NumFacet }
     * 
     * @return
     *     the new instance of {@link NumFacet }
     */
    public NumFacet createNumFacet() {
        return new NumFacet();
    }

    /**
     * Create an instance of {@link NoFixedFacet }
     * 
     * @return
     *     the new instance of {@link NoFixedFacet }
     */
    public NoFixedFacet createNoFixedFacet() {
        return new NoFixedFacet();
    }

    /**
     * Create an instance of {@link WhiteSpace }
     * 
     * @return
     *     the new instance of {@link WhiteSpace }
     */
    public WhiteSpace createWhiteSpace() {
        return new WhiteSpace();
    }

    /**
     * Create an instance of {@link Pattern }
     * 
     * @return
     *     the new instance of {@link Pattern }
     */
    public Pattern createPattern() {
        return new Pattern();
    }

    /**
     * Create an instance of {@link List }
     * 
     * @return
     *     the new instance of {@link List }
     */
    public List createList() {
        return new List();
    }

    /**
     * Create an instance of {@link Union }
     * 
     * @return
     *     the new instance of {@link Union }
     */
    public Union createUnion() {
        return new Union();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     * @return
     *     the new instance of {@link Attribute }
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Create an instance of {@link LocalComplexType }
     * 
     * @return
     *     the new instance of {@link LocalComplexType }
     */
    public LocalComplexType createLocalComplexType() {
        return new LocalComplexType();
    }

    /**
     * Create an instance of {@link RestrictionType }
     * 
     * @return
     *     the new instance of {@link RestrictionType }
     */
    public RestrictionType createRestrictionType() {
        return new RestrictionType();
    }

    /**
     * Create an instance of {@link LocalElement }
     * 
     * @return
     *     the new instance of {@link LocalElement }
     */
    public LocalElement createLocalElement() {
        return new LocalElement();
    }

    /**
     * Create an instance of {@link RealGroup }
     * 
     * @return
     *     the new instance of {@link RealGroup }
     */
    public RealGroup createRealGroup() {
        return new RealGroup();
    }

    /**
     * Create an instance of {@link GroupRef }
     * 
     * @return
     *     the new instance of {@link GroupRef }
     */
    public GroupRef createGroupRef() {
        return new GroupRef();
    }

    /**
     * Create an instance of {@link SimpleExplicitGroup }
     * 
     * @return
     *     the new instance of {@link SimpleExplicitGroup }
     */
    public SimpleExplicitGroup createSimpleExplicitGroup() {
        return new SimpleExplicitGroup();
    }

    /**
     * Create an instance of {@link AttributeGroupRef }
     * 
     * @return
     *     the new instance of {@link AttributeGroupRef }
     */
    public AttributeGroupRef createAttributeGroupRef() {
        return new AttributeGroupRef();
    }

    /**
     * Create an instance of {@link GetCountryByShortName }
     * 
     * @return
     *     the new instance of {@link GetCountryByShortName }
     */
    public GetCountryByShortName createGetCountryByShortName() {
        return new GetCountryByShortName();
    }

    /**
     * Create an instance of {@link GetCountryByShortNameResponse }
     * 
     * @return
     *     the new instance of {@link GetCountryByShortNameResponse }
     */
    public GetCountryByShortNameResponse createGetCountryByShortNameResponse() {
        return new GetCountryByShortNameResponse();
    }

    /**
     * Create an instance of {@link LongRunningOperation }
     * 
     * @return
     *     the new instance of {@link LongRunningOperation }
     */
    public LongRunningOperation createLongRunningOperation() {
        return new LongRunningOperation();
    }

    /**
     * Create an instance of {@link LongRunningOperationResponse }
     * 
     * @return
     *     the new instance of {@link LongRunningOperationResponse }
     */
    public LongRunningOperationResponse createLongRunningOperationResponse() {
        return new LongRunningOperationResponse();
    }

    /**
     * Create an instance of {@link ReturnSoapFault }
     * 
     * @return
     *     the new instance of {@link ReturnSoapFault }
     */
    public ReturnSoapFault createReturnSoapFault() {
        return new ReturnSoapFault();
    }

    /**
     * Create an instance of {@link ReturnSoapFaultResponse }
     * 
     * @return
     *     the new instance of {@link ReturnSoapFaultResponse }
     */
    public ReturnSoapFaultResponse createReturnSoapFaultResponse() {
        return new ReturnSoapFaultResponse();
    }

    /**
     * Create an instance of {@link Country }
     * 
     * @return
     *     the new instance of {@link Country }
     */
    public Country createCountry() {
        return new Country();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TopLevelSimpleType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TopLevelSimpleType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "simpleType")
    public JAXBElement<TopLevelSimpleType> createSimpleType(TopLevelSimpleType value) {
        return new JAXBElement<>(_SimpleType_QNAME, TopLevelSimpleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TopLevelComplexType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TopLevelComplexType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "complexType")
    public JAXBElement<TopLevelComplexType> createComplexType(TopLevelComplexType value) {
        return new JAXBElement<>(_ComplexType_QNAME, TopLevelComplexType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedGroup }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NamedGroup }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "group")
    public JAXBElement<NamedGroup> createGroup(NamedGroup value) {
        return new JAXBElement<>(_Group_QNAME, NamedGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedAttributeGroup }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NamedAttributeGroup }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "attributeGroup")
    public JAXBElement<NamedAttributeGroup> createAttributeGroup(NamedAttributeGroup value) {
        return new JAXBElement<>(_AttributeGroup_QNAME, NamedAttributeGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TopLevelElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TopLevelElement }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "element")
    public JAXBElement<TopLevelElement> createElement(TopLevelElement value) {
        return new JAXBElement<>(_Element_QNAME, TopLevelElement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TopLevelAttribute }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TopLevelAttribute }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "attribute")
    public JAXBElement<TopLevelAttribute> createAttribute(TopLevelAttribute value) {
        return new JAXBElement<>(_Attribute_QNAME, TopLevelAttribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Wildcard }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Wildcard }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "anyAttribute")
    public JAXBElement<Wildcard> createAnyAttribute(Wildcard value) {
        return new JAXBElement<>(_AnyAttribute_QNAME, Wildcard.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link All }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link All }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "all")
    public JAXBElement<All> createAll(All value) {
        return new JAXBElement<>(_All_QNAME, All.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "choice")
    public JAXBElement<ExplicitGroup> createChoice(ExplicitGroup value) {
        return new JAXBElement<>(_Choice_QNAME, ExplicitGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExplicitGroup }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "sequence")
    public JAXBElement<ExplicitGroup> createSequence(ExplicitGroup value) {
        return new JAXBElement<>(_Sequence_QNAME, ExplicitGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Keybase }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Keybase }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "unique")
    public JAXBElement<Keybase> createUnique(Keybase value) {
        return new JAXBElement<>(_Unique_QNAME, Keybase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Keybase }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Keybase }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "key")
    public JAXBElement<Keybase> createKey(Keybase value) {
        return new JAXBElement<>(_Key_QNAME, Keybase.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "minExclusive")
    public JAXBElement<Facet> createMinExclusive(Facet value) {
        return new JAXBElement<>(_MinExclusive_QNAME, Facet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "minInclusive")
    public JAXBElement<Facet> createMinInclusive(Facet value) {
        return new JAXBElement<>(_MinInclusive_QNAME, Facet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "maxExclusive")
    public JAXBElement<Facet> createMaxExclusive(Facet value) {
        return new JAXBElement<>(_MaxExclusive_QNAME, Facet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Facet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "maxInclusive")
    public JAXBElement<Facet> createMaxInclusive(Facet value) {
        return new JAXBElement<>(_MaxInclusive_QNAME, Facet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "fractionDigits")
    public JAXBElement<NumFacet> createFractionDigits(NumFacet value) {
        return new JAXBElement<>(_FractionDigits_QNAME, NumFacet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "length")
    public JAXBElement<NumFacet> createLength(NumFacet value) {
        return new JAXBElement<>(_Length_QNAME, NumFacet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "minLength")
    public JAXBElement<NumFacet> createMinLength(NumFacet value) {
        return new JAXBElement<>(_MinLength_QNAME, NumFacet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NumFacet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "maxLength")
    public JAXBElement<NumFacet> createMaxLength(NumFacet value) {
        return new JAXBElement<>(_MaxLength_QNAME, NumFacet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoFixedFacet }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NoFixedFacet }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "enumeration")
    public JAXBElement<NoFixedFacet> createEnumeration(NoFixedFacet value) {
        return new JAXBElement<>(_Enumeration_QNAME, NoFixedFacet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountryByShortName }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetCountryByShortName }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "getCountryByShortName")
    public JAXBElement<GetCountryByShortName> createGetCountryByShortName(GetCountryByShortName value) {
        return new JAXBElement<>(_GetCountryByShortName_QNAME, GetCountryByShortName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountryByShortNameResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetCountryByShortNameResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "getCountryByShortNameResponse")
    public JAXBElement<GetCountryByShortNameResponse> createGetCountryByShortNameResponse(GetCountryByShortNameResponse value) {
        return new JAXBElement<>(_GetCountryByShortNameResponse_QNAME, GetCountryByShortNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LongRunningOperation }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LongRunningOperation }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "longRunningOperation")
    public JAXBElement<LongRunningOperation> createLongRunningOperation(LongRunningOperation value) {
        return new JAXBElement<>(_LongRunningOperation_QNAME, LongRunningOperation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LongRunningOperationResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LongRunningOperationResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "longRunningOperationResponse")
    public JAXBElement<LongRunningOperationResponse> createLongRunningOperationResponse(LongRunningOperationResponse value) {
        return new JAXBElement<>(_LongRunningOperationResponse_QNAME, LongRunningOperationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReturnSoapFault }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ReturnSoapFault }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "returnSoapFault")
    public JAXBElement<ReturnSoapFault> createReturnSoapFault(ReturnSoapFault value) {
        return new JAXBElement<>(_ReturnSoapFault_QNAME, ReturnSoapFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReturnSoapFaultResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ReturnSoapFaultResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://country.testservice.ivyteam.ch/", name = "returnSoapFaultResponse")
    public JAXBElement<ReturnSoapFaultResponse> createReturnSoapFaultResponse(ReturnSoapFaultResponse value) {
        return new JAXBElement<>(_ReturnSoapFaultResponse_QNAME, ReturnSoapFaultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LocalElement }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "element", scope = Group.class)
    public JAXBElement<LocalElement> createGroupElement(LocalElement value) {
        return new JAXBElement<>(_Element_QNAME, LocalElement.class, Group.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroupRef }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GroupRef }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/XMLSchema", name = "group", scope = Group.class)
    public JAXBElement<GroupRef> createGroupGroup(GroupRef value) {
        return new JAXBElement<>(_Group_QNAME, GroupRef.class, Group.class, value);
    }

}
