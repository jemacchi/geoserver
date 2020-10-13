package org.geoserver.appschema.smart.domain.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainAttributeType;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.geotools.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GMLDomainModelVisitor extends DomainModelVisitor {
	
	private Map<DomainAttributeType, String> GMLDataTypesMappings = new HashMap<>();

	private String namespaceSuffix = "st";
	private static final Logger LOGGER = Logging.getLogger(GMLDomainModelVisitor.class);
	private DocumentBuilder docBuilder;
	private Document document;
	private Element rootNode;
	
	private final Map<String, DomainEntity> domainEntitiesIndex = new HashMap<>();
	
	public GMLDomainModelVisitor() {
		GMLDataTypesMappings.put(DomainAttributeType.TEXT, "xs:string");
		GMLDataTypesMappings.put(DomainAttributeType.NUMBER, "xs:double");
		GMLDataTypesMappings.put(DomainAttributeType.DATE, "xs:dateTime");
		GMLDataTypesMappings.put(DomainAttributeType.GEOMETRY, "gml:GeometryPropertyType");
		
		docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    document = docBuilder.newDocument();
			
			this.rootNode = document.createElement("xs:schema");
			rootNode.setAttribute("version", "1.0");
			rootNode.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
			rootNode.setAttribute("xmlns:gml", "http://www.opengis.net/gml/3.2");
			rootNode.setAttribute("xmlns:"+this.namespaceSuffix, "");
			rootNode.setAttribute("targetNamespace", "");
			rootNode.setAttribute("elementFormDefault", "qualified");
			rootNode.setAttribute("attributeFormDefault", "unqualified");
			document.appendChild(rootNode);
			
			Element importNode = document.createElement("xs:import");
			importNode.setAttribute("namespace", "http://www.opengis.net/gml/3.2");
			importNode.setAttribute("schemaLocation", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");
			rootNode.appendChild(importNode);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void visit(DataStoreMetadata dataStoreMetadata) {
		// Nothing to map from DataStore in GML mappings
	}

	@Override
	public void visit(DomainModel domainModel) {
		// Nothing to do in GML
	}

	@Override
	public void visit(DomainEntity domainEntity) {	
		DomainEntity candidateDomainEntity = domainEntitiesIndex.get(domainEntity.getName());
        if (candidateDomainEntity != null) {
            // we are done, we already visit this domainEntity
            return ;
        }
		
		Element elementNode = document.createElement("xs:element");
		elementNode.setAttribute("name", domainEntity.getName());
		elementNode.setAttribute("type", this.namespaceSuffix+":"+domainEntity.getName()+"Type");
		elementNode.setAttribute("substitutionGroup", "gml:AbstractFeature");
		rootNode.appendChild(elementNode);

		Node complexTypeContent = createComplexTypeContentNode(domainEntity, domainEntity.getName()+"Type", "gml:AbstractFeatureType");
		rootNode.appendChild(complexTypeContent);
		
		domainEntitiesIndex.put(domainEntity.getName(), domainEntity);
		domainEntity.accept(this);
	}
	
	private Node createComplexTypeSequenceNode(String name, int seqMinOccurs, String seqElementRef, String attributeGroupRef) {
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

	private Node createComplexTypeContentNode(DomainEntity domainEntity, String name, String extensionBaseAttr) {
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
	
	
	@Override
	public void visit(DomainAttribute domainAttribute) {
		NodeList complexTypes = rootNode.getElementsByTagName("xs:complexType");
		for (int i = 0; i < complexTypes.getLength(); i++) {
	            Node nNode = complexTypes.item(i);
	            String complexTypeName = domainAttribute.getEntity().getName()+"Type";
	            String nNodeComplexTypeName = nNode.getAttributes().getNamedItem("name").getNodeValue();
	            if (nNodeComplexTypeName.equals(complexTypeName)) {
	            	Node sequence = nNode.getFirstChild().getFirstChild().getFirstChild();
	        		
	        		Element attribute = document.createElement("xs:element");
	        		attribute.setAttribute("name", domainAttribute.getName());
	        		attribute.setAttribute("type", GMLDataTypesMappings.get(domainAttribute.getType()));
	        		attribute.setAttribute("minOccurs", "1");
	        		attribute.setAttribute("maxOccurs", "1");
	        		
	        		sequence.appendChild(attribute);        	
	            }
		}
	}

	@Override
	public void visit(DomainRelation domainRelation) {
		String propertyType = domainRelation.getDestination().getName()+"PropertyType";
		Node complexTypeSequence = createComplexTypeSequenceNode(propertyType, 0, this.namespaceSuffix+":"+domainRelation.getDestination().getName(), "gml:AssociationAttributeGroup");
		rootNode.appendChild(complexTypeSequence);
		/*
		NodeList complexTypes = rootNode.getElementsByTagName("xs:complexType");
		for (int i = 0; i < complexTypes.getLength(); i++) {
	            Node nNode = complexTypes.item(i);
	            String complexTypeName = domainRelation.getSource().getName()+"Type";
	            String nNodeComplexTypeName = nNode.getAttributes().getNamedItem("name").getNodeValue();
	            if (nNodeComplexTypeName.equals(complexTypeName)) {
	            	Node sequence = nNode.getFirstChild().getFirstChild().getFirstChild();
	            	
	            	// Update types
	            	NodeList elements = sequence.getChildNodes();
	            	for (int j=0; j < elements.getLength(); j++) {
	            		Node element = elements.item(j);
	            		String nNodeElementName = element.getAttributes().getNamedItem("name").getNodeValue();
	            		if (nNodeElementName.equals(domainRelation.getSource().getName())) {
	            			element.getAttributes().getNamedItem("type").setNodeValue(propertyType);
	            			// TODO: based on cardinality we need to rewrite minOccurs, maxOccurs
	            		}
	            	}

	            }
		}
		*/
		domainRelation.accept(this);
	}
	
	public Document getDocument() {
		return document;
	}

}
