package org.geoserver.appschema.smart.domain.generator;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geoserver.appschema.smart.domain.DomainModelVisitorImpl;
import org.geoserver.appschema.smart.domain.entities.DomainEntityAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainAttributeType;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.domain.entities.DomainRelationType;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DomainModelVisitor implementation that allows to create a GML document definition.
 *
 * @author Jose Macchi - Geosolutions
 */
public class GMLDomainModelVisitor extends DomainModelVisitorImpl {

    private static final Map<DomainAttributeType, String> GMLDataTypesMappings = new HashMap<>();

    static {
        GMLDataTypesMappings.put(DomainAttributeType.TEXT, "xs:string");
        GMLDataTypesMappings.put(DomainAttributeType.INT, "xs:int");
        GMLDataTypesMappings.put(DomainAttributeType.NUMBER, "xs:double");
        GMLDataTypesMappings.put(DomainAttributeType.DATE, "xs:dateTime");
        GMLDataTypesMappings.put(DomainAttributeType.GEOMETRY, "gml:GeometryPropertyType");
    }

    private String namespacePrefix = "";
    private DocumentBuilder docBuilder;
    private Document document;
    private Element rootNode;

    private final Map<String, DomainEntity> domainEntitiesIndex = new HashMap<>();
    private final Map<String, DomainRelation> domainRelationsIndex = new HashMap<>();

    public GMLDomainModelVisitor(String namespacePrefix, String targetNamespace) {
        docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = docBuilder.newDocument();

            this.namespacePrefix = namespacePrefix;

            this.rootNode = document.createElement("xs:schema");
            rootNode.setAttribute("version", "1.0");
            rootNode.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
            rootNode.setAttribute("xmlns:gml", "http://www.opengis.net/gml/3.2");
            rootNode.setAttribute("xmlns:" + this.namespacePrefix, targetNamespace);
            rootNode.setAttribute("targetNamespace", targetNamespace);
            rootNode.setAttribute("elementFormDefault", "qualified");
            rootNode.setAttribute("attributeFormDefault", "unqualified");
            document.appendChild(rootNode);

            Element importNode = document.createElement("xs:import");
            importNode.setAttribute("namespace", "http://www.opengis.net/gml/3.2");
            importNode.setAttribute(
                    "schemaLocation", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");
            rootNode.appendChild(importNode);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(DomainEntity domainEntity) {
        DomainEntity candidateDomainEntity = domainEntitiesIndex.get(domainEntity.getName());
        if (candidateDomainEntity != null) {
            // we are done, we already visit this domainEntity
            return;
        }

        Element elementNode = document.createElement("xs:element");
        elementNode.setAttribute("name", domainEntity.getName());
        elementNode.setAttribute(
                "type", this.namespacePrefix + ":" + domainEntity.getName() + "Type");
        elementNode.setAttribute("substitutionGroup", "gml:AbstractFeature");
        rootNode.appendChild(elementNode);

        Node complexTypeContent =
                createComplexTypeContentNode(
                        domainEntity, domainEntity.getName() + "Type", "gml:AbstractFeatureType");
        rootNode.appendChild(complexTypeContent);

        domainEntitiesIndex.put(domainEntity.getName(), domainEntity);
        domainEntity.accept(this);
    }

    @Override
    public void visit(DomainEntityAttribute domainAttribute) {
        NodeList complexTypes = rootNode.getElementsByTagName("xs:complexType");
        for (int i = 0; i < complexTypes.getLength(); i++) {
            Node nNode = complexTypes.item(i);
            String complexTypeName = domainAttribute.getEntity().getName() + "Type";
            String nNodeComplexTypeName = nNode.getAttributes().getNamedItem("name").getNodeValue();
            if (nNodeComplexTypeName.equals(complexTypeName)) {
                Node sequence = nNode.getFirstChild().getFirstChild().getFirstChild();

                Element attribute = document.createElement("xs:element");
                attribute.setAttribute("name", domainAttribute.getName());
                attribute.setAttribute("type", GMLDataTypesMappings.get(domainAttribute.getType()));
                attribute.setAttribute("minOccurs", getMinOccurs(domainAttribute.getType()));
                attribute.setAttribute("maxOccurs", getMaxOccurs(domainAttribute.getType()));

                sequence.appendChild(attribute);
            }
        }
    }
   
    private String getDomainRelationKey(DomainRelation dm) {
    	String retVal = dm.getSourceEntity().getName()+"."+dm.getSourceAttribute()+"-"+dm.getRelationType()+"-"+dm.getDestinationEntity()+"."+dm.getDestinationAttribute();
    	return retVal;
    }
    
    private String getInvertedDomainRelationKey(DomainRelation dm) {
    	DomainRelationType type = dm.getRelationType();
    	if (dm.getRelationType().equals(DomainRelationType.ONEMANY))
    		type = DomainRelationType.MANYONE;
    	String retVal = dm.getDestinationEntity()+"."+dm.getDestinationAttribute()+"-"+type+"-"+dm.getSourceEntity().getName()+"."+dm.getSourceAttribute();
    	return retVal;
    }
    
    @Override
    public void visit(DomainRelation domainRelation) {
    	String dmKey = getDomainRelationKey(domainRelation);
    	DomainRelation candidateDomainRelation = domainRelationsIndex.get(dmKey);
        if (candidateDomainRelation != null) {
            // we are done, we already visit this domainRelation
            return;
        }
    	
        domainRelationsIndex.put(dmKey, domainRelation);
    	domainRelation.accept(this);        
        
        if (domainRelation.getRelationType() != null) {

        	String propertyType = domainRelation.getSourceEntity().getName() + "PropertyType";
            Node complexPropertyType = this.getComplexTypeByName(propertyType);
            if (complexPropertyType == null) {
                Node complexTypeSequence =
                        createComplexTypeSequenceNode(
                                propertyType,
                                0,
                                this.namespacePrefix
                                        + ":"
                                        + domainRelation.getSourceEntity().getName(),
                                "gml:AssociationAttributeGroup");
                rootNode.appendChild(complexTypeSequence);
            }        	
        	
        	Node sourceComplexType = this.getComplexTypeByName(domainRelation.getSourceEntity().getName() + "Type");
	        Node destinationComplexType = this.getComplexTypeByName(domainRelation.getDestinationEntity().getName() + "Type");
	        
	        if (domainRelation.getRelationType().equals(DomainRelationType.ONEMANY)) {
	            Node srcAttribSeq = sourceComplexType.getFirstChild().getFirstChild().getFirstChild();
	            if (srcAttribSeq != null) {
		            Element attribute = document.createElement("xs:element");
		            attribute.setAttribute("name", domainRelation.getDestinationEntity().getName());
		            attribute.setAttribute("type", this.namespacePrefix+":"+domainRelation.getDestinationEntity().getName() + "PropertyType");
		            attribute.setAttribute("minOccurs", "0");
		            attribute.setAttribute("maxOccurs", "unbounded");
		            srcAttribSeq.appendChild(attribute);
	            }
	            
	            Node destAttribute = this.getAttributeElement(destinationComplexType,domainRelation.getDestinationAttribute().getName());
	            destAttribute.getParentNode().removeChild(destAttribute);
	        }
	        
	        if (domainRelation.getRelationType().equals(DomainRelationType.MANYONE)) {
	        	// if the opposite relation was already visited then not to add to source
	        	if (domainRelationsIndex.containsKey(this.getInvertedDomainRelationKey(domainRelation))) {
		        	// add to source
		        	Node srcAttribSeq = sourceComplexType.getFirstChild().getFirstChild().getFirstChild();
		            if (srcAttribSeq != null) {
				            Element attribute = document.createElement("xs:element");
				            attribute.setAttribute("name", domainRelation.getDestinationEntity().getName());
				            attribute.setAttribute("type", this.namespacePrefix+":"+domainRelation.getDestinationEntity().getName() + "PropertyType");
				            attribute.setAttribute("minOccurs", "1");
				            attribute.setAttribute("maxOccurs", "1");
				            srcAttribSeq.appendChild(attribute);
		            }
	        	}
	        }
	        
	        if (domainRelation.getRelationType().equals(DomainRelationType.ONEONE)) {
	            Node destSequence = destinationComplexType.getFirstChild().getFirstChild().getFirstChild();
	            NodeList elements = destSequence.getChildNodes();
	            for (int j = 0; j < elements.getLength(); j++) {
	            	Node element = elements.item(j);
	                String nNodeElementName = element.getAttributes().getNamedItem("name").getNodeValue();
	                if (nNodeElementName.equals(domainRelation.getDestinationAttribute().getName())) {
	                    element.getAttributes()
	                            .getNamedItem("type")
	                            .setNodeValue(this.namespacePrefix + ":" + domainRelation.getSourceEntity().getName() + "PropertyType");
	                    element.getAttributes()
	                            .getNamedItem("minOccurs")
	                            .setNodeValue("1");
	                    element.getAttributes()
	                            .getNamedItem("maxOccurs")
	                            .setNodeValue("1");
	                }
	            }
	        }
	        
        }
	            
    }

    public Document getDocument() {
        return document;
    }

    private Node createComplexTypeSequenceNode(
            String name, int seqMinOccurs, String seqElementRef, String attributeGroupRef) {
        Element node = document.createElement("xs:complexType");
        node.setAttribute("name", name);
        Element sequence = document.createElement("xs:sequence");
        sequence.setAttribute("minOccurs", Integer.toString(seqMinOccurs));
        Element element = document.createElement("xs:element");
        element.setAttribute("ref", seqElementRef);
        sequence.appendChild(element);
        node.appendChild(sequence);
        Element attributeGroup = document.createElement("xs:attributeGroup");
        attributeGroup.setAttribute("ref", attributeGroupRef);
        node.appendChild(attributeGroup);
        return node;
    }

    private Node createComplexTypeContentNode(
            DomainEntity domainEntity, String name, String extensionBaseAttr) {
        Element node = document.createElement("xs:complexType");
        node.setAttribute("name", name);
        Element content = document.createElement("xs:complexContent");
        Element extension = document.createElement("xs:extension");
        extension.setAttribute("base", "gml:AbstractFeatureType");
        Node sequence = document.createElement("xs:sequence");
        extension.appendChild(sequence);
        content.appendChild(extension);
        node.appendChild(content);
        return node;
    }

    private String getMinOccurs(DomainAttributeType type) {
        if (type.equals(DomainAttributeType.GEOMETRY)) return "0";
        return "1";
    }

    private String getMaxOccurs(DomainAttributeType type) {
        return "1";
    }
    
    private Node getComplexTypeByName(String complexTypeName) {
        NodeList complexTypes = rootNode.getElementsByTagName("xs:complexType");
        for (int i = 0; i < complexTypes.getLength(); i++) {
            Node nNode = complexTypes.item(i);
	            String nNodeComplexTypeName = nNode.getAttributes().getNamedItem("name").getNodeValue();
	            if (nNodeComplexTypeName.equals(complexTypeName)) {
	                return nNode;
	            }
        }
    	return null;
    }

    private Node getAttributeElement(Node complexType, String name) {
    	Node sequence = complexType.getFirstChild().getFirstChild().getFirstChild();
    	NodeList elements = sequence.getChildNodes();
        for (int j = 0; j < elements.getLength(); j++) {
        	Node element = elements.item(j);
            String nNodeElementName = element.getAttributes().getNamedItem("name").getNodeValue();
            if (nNodeElementName.equals(name)) {
            	return element;
            }
        }
    	return null;
    }
    
}
