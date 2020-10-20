package org.geoserver.appschema.smart.domain.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geoserver.appschema.smart.domain.DomainModelVisitor;
import org.geoserver.appschema.smart.domain.entities.DomainAttribute;
import org.geoserver.appschema.smart.domain.entities.DomainEntity;
import org.geoserver.appschema.smart.domain.entities.DomainModel;
import org.geoserver.appschema.smart.domain.entities.DomainRelation;
import org.geoserver.appschema.smart.metadata.DataStoreMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DomainModelVisitor implementation that allows to create a AppSchema document definition.
 *
 * @author Jose Macchi - Geosolutions
 */
public class SmartAppSchemaDomainModelVisitor extends DomainModelVisitor {

    private String namespacePrefix = "";
    private String schemaUri = "";
    private DocumentBuilder docBuilder;
    private Document document;
    private Element rootNode;

    private final Map<String, DomainEntity> domainEntitiesIndex = new HashMap<>();

    public SmartAppSchemaDomainModelVisitor(
            String namespacePrefix, String targetNamespace, String schemaUri) {
        docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = docBuilder.newDocument();

            this.namespacePrefix = namespacePrefix;
            this.schemaUri = schemaUri;

            // Root node
            this.rootNode = document.createElement("ns3:AppSchemaDataAccess");
            rootNode.setAttribute("xmlns:ns2", "http://www.opengis.net/ogc");
            rootNode.setAttribute("xmlns:ns3", "http://www.geotools.org/app-schema");
            document.appendChild(rootNode);

            // Namespaces
            Element namespacesNode = document.createElement("namespaces");
            Node gmlNamespace = createNamespaceNode("gml", "http://www.opengis.net/gml/3.2");
            namespacesNode.appendChild(gmlNamespace);
            Node localNamespace = createNamespaceNode(this.namespacePrefix, targetNamespace);
            namespacesNode.appendChild(localNamespace);
            rootNode.appendChild(namespacesNode);

            // Empty includedTypes node
            Element includedTypesNode = document.createElement("includedTypes");
            rootNode.appendChild(includedTypesNode);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(DataStoreMetadata dataStoreMetadata) {
        Element sourceDataStoresNode = document.createElement("sourceDataStores");
        Node dataStoreNode = this.createDataStoreNode(dataStoreMetadata);
        sourceDataStoresNode.appendChild(dataStoreNode);
        rootNode.appendChild(sourceDataStoresNode);
    }

    @Override
    public void visit(DomainModel domainModel) {
        // targetFeatures
        Element targetFeaturesNode = document.createElement("targetTypes");
        // xsd file location is a single featuretype for the case of smart appschema, but
        // it's not included into the domainModel since it's part of appschema visitor
        Node featureTypeNode = createFeatureTypeNode(this.schemaUri);
        targetFeaturesNode.appendChild(featureTypeNode);
        rootNode.appendChild(targetFeaturesNode);

        // typeMappings (empty node, we will add entities here)
        Element typeMappingsNode = document.createElement("typeMappings");
        rootNode.appendChild(typeMappingsNode);
    }

    @Override
    public void visit(DomainEntity domainEntity) {
        DomainEntity candidateDomainEntity = domainEntitiesIndex.get(domainEntity.getName());
        if (candidateDomainEntity != null) {
            // we are done, we already visit this domainEntity
            return;
        }
        Node typeMappingsNode = rootNode.getElementsByTagName("typeMappings").item(0);
        // insert entity into typeMappings node
        String sourceDataStoreValue = getSourceDataStoreId();
        String sourceTypeValue = domainEntity.getName();
        String targetElementValue = namespacePrefix + ":" + domainEntity.getName();
        Node featureTypeMappingNode =
                createFeatureTypeMappingNode(
                        sourceDataStoreValue, sourceTypeValue, targetElementValue);

        typeMappingsNode.appendChild(featureTypeMappingNode);

        domainEntitiesIndex.put(domainEntity.getName(), domainEntity);
        domainEntity.accept(this);
    }

    @Override
    public void visit(DomainAttribute domainAttribute) {
        String targetElementValue = namespacePrefix + ":" + domainAttribute.getEntity().getName();
        // Find the featureTypeMapping with that targetElementValue
        Node featureTypeMapping = getFeatureTypeMapping(targetElementValue);
        if (featureTypeMapping != null) {
            // Append AttributeMapping
            NodeList childs = featureTypeMapping.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                String nodeName = child.getNodeName();
                if (nodeName.equals("attributeMappings")) {
                    String targetAttributeValue = namespacePrefix + ":" + domainAttribute.getName();
                    String OCQLValue = domainAttribute.getName();
                    Node attributeMappingNode =
                            createAttributeMapping(targetAttributeValue, OCQLValue);
                    child.appendChild(attributeMappingNode);
                    return;
                }
            }
        } else {
            throw new RuntimeException(
                    String.format("FeatureTypeMapping '%s' is unknown.", targetElementValue));
        }
    }

    @Override
    public void visit(DomainRelation domainRelation) {
        String sourceTargetElementValue = namespacePrefix + ":" + domainRelation.getSourceEntity().getName();
        String sourceTargetAttributeValue = namespacePrefix + ":" + domainRelation.getSourceAttribute().getName();
        String destinationTargetElementValue = namespacePrefix + ":" + domainRelation.getDestinationEntity().getName();

        domainRelation.accept(this);
        
        if (domainRelation.getRelationType() != null) {
        	
            String number = Integer.toString(countLinkedAttributeMapping(sourceTargetElementValue));
            String linkName = "FEATURE_LINK["+number+"]";
        	
        	Node sourceAttributeMappingNode = getAttributeMapping(sourceTargetElementValue, sourceTargetAttributeValue);
            if (sourceAttributeMappingNode != null) {
            	 NodeList saChilds = sourceAttributeMappingNode.getChildNodes();
                 for (int i = 0; i < saChilds.getLength(); i++) {
                     Node child = saChilds.item(i);
                     String nodeName = child.getNodeName();
                     if (nodeName.equals("targetAttribute")) {
                    	 child.setTextContent(linkName);
                     }
                 }
            }
            Node destinationAttributeMappingNode = getAttributeMapping(destinationTargetElementValue, sourceTargetElementValue);
            if (destinationAttributeMappingNode == null) {
            	Node destinationFeatureTypeMappingNode = getFeatureTypeMapping(destinationTargetElementValue);
            	Node attributeMappingNode = createLinkedAttributeMapping(sourceTargetElementValue, domainRelation.getDestinationAttribute().getName(), sourceTargetElementValue, linkName);
            	NodeList destFtmChilds = destinationFeatureTypeMappingNode.getChildNodes();
                for (int i = 0; i < destFtmChilds.getLength(); i++) {
                    Node child = destFtmChilds.item(i);
                    String childName = child.getNodeName();
                    if (childName.equals("attributeMappings")) {
                        child.appendChild(attributeMappingNode);
                        break;
                    }
                }
            }
        }
    }

    public Document getDocument() {
        return document;
    }

    private Node createNamespaceNode(String prefixValue, String uriValue) {
        Element namespaceNode = document.createElement("Namespace");
        Element prefixNamespaceNode = document.createElement("prefix");
        prefixNamespaceNode.setTextContent(prefixValue);
        Element uriNamespaceNode = document.createElement("uri");
        uriNamespaceNode.setTextContent(uriValue);
        namespaceNode.appendChild(prefixNamespaceNode);
        namespaceNode.appendChild(uriNamespaceNode);
        return namespaceNode;
    }

    private Node createParameterNode(String parameterName, String parameterValue) {
        Element parameterNode = document.createElement("Parameter");
        Element nameParameterNode = document.createElement("name");
        nameParameterNode.setTextContent(parameterName);
        Element valueParameterNode = document.createElement("value");
        valueParameterNode.setTextContent(parameterValue);
        parameterNode.appendChild(nameParameterNode);
        parameterNode.appendChild(valueParameterNode);
        return parameterNode;
    }

    private Node createDataStoreNode(DataStoreMetadata dataStoreMetadata) {
        Element dataStoreNode = document.createElement("DataStore");
        Element idNode = document.createElement("id");
        idNode.setTextContent(dataStoreMetadata.getName());
        dataStoreNode.appendChild(idNode);
        Element parametersNode = document.createElement("parameters");
        Map<String, String> params = dataStoreMetadata.getDataStoreMetadataConfig().getParameters();
        Set<String> keys = params.keySet();
        keys.forEach(
                key -> {
                    String value = params.get(key);
                    Node parameterNode = createParameterNode(key, value);
                    parametersNode.appendChild(parameterNode);
                });
        dataStoreNode.appendChild(parametersNode);
        return dataStoreNode;
    }

    private Node createFeatureTypeNode(String schemaUriValue) {
        Element featureTypeNode = document.createElement("FeatureType");
        Element schemaUriNode = document.createElement("schemaUri");
        schemaUriNode.setTextContent(schemaUriValue);
        featureTypeNode.appendChild(schemaUriNode);
        return featureTypeNode;
    }

    private Node createFeatureTypeMappingNode(
            String sourceDataStoreValue, String sourceTypeValue, String targetElementValue) {
        Element featureTypeMappingNode = document.createElement("FeatureTypeMapping");
        Element sourceDataStoreNode = document.createElement("sourceDataStore");
        sourceDataStoreNode.setTextContent(sourceDataStoreValue);
        featureTypeMappingNode.appendChild(sourceDataStoreNode);
        Element sourceTypeNode = document.createElement("sourceType");
        sourceTypeNode.setTextContent(sourceTypeValue);
        featureTypeMappingNode.appendChild(sourceTypeNode);
        Element targetElementNode = document.createElement("targetElement");
        targetElementNode.setTextContent(targetElementValue);
        featureTypeMappingNode.appendChild(targetElementNode);
        Element attributeMappingsNode = document.createElement("attributeMappings");
        featureTypeMappingNode.appendChild(attributeMappingsNode);
        return featureTypeMappingNode;
    }

    private String getSourceDataStoreId() {
        // Empty if there is no definition, first datastore id value (if existing)
        Node sourceDataStoresNode = rootNode.getElementsByTagName("sourceDataStores").item(0);
        NodeList dataStoresList = sourceDataStoresNode.getChildNodes();
        if (dataStoresList.getLength() > 0) {
            NodeList dataStoreChildNodes = dataStoresList.item(0).getChildNodes();
            for (int i = 0; i < dataStoreChildNodes.getLength(); i++) {
                Node child = dataStoreChildNodes.item(i);
                String nodeName = child.getNodeName();
                if (nodeName.equals("id")) {
                    return child.getTextContent();
                }
            }
        }
        return "";
    }

    private Node getFeatureTypeMapping(String targetElementValue) {
        NodeList ftmList = document.getElementsByTagName("FeatureTypeMapping");
        for (int i = 0; i < ftmList.getLength(); i++) {
            Node child = ftmList.item(i);
            NodeList ftmChildsList = child.getChildNodes();
            for (int j = 0; j < ftmChildsList.getLength(); j++) {
                Node ftmChild = ftmChildsList.item(j);
                String nodeName = ftmChild.getNodeName();
                if (nodeName.equals("targetElement")) {
                    if (ftmChild.getTextContent().equals(targetElementValue)) return child;
                }
            }
        }
        return null;
    }
    
    private Node createLinkedAttributeMapping(String targetAttributeValue, String OCQLValue, String linkedElement, String linkedField) {
        Element attributeMappingNode = document.createElement("AttributeMapping");
        Element targetAttributeNode = document.createElement("targetAttribute");
        targetAttributeNode.setTextContent(targetAttributeValue);
        Element sourceExpressionNode = document.createElement("sourceExpression");
        Element OCQLNode = document.createElement("OCQL");
        OCQLNode.setTextContent(OCQLValue);
        Element linkElementNode = document.createElement("linkElement");
        linkElementNode.setTextContent(linkedElement);
        Element linkFieldNode = document.createElement("linkField");
        linkFieldNode.setTextContent(linkedField);
        sourceExpressionNode.appendChild(linkFieldNode);
        sourceExpressionNode.appendChild(linkElementNode);
        sourceExpressionNode.appendChild(OCQLNode);
        Element isMultiple = document.createElement("isMutiple");
        isMultiple.setTextContent("true");
        attributeMappingNode.appendChild(isMultiple);
        attributeMappingNode.appendChild(targetAttributeNode);
        attributeMappingNode.appendChild(sourceExpressionNode);
        
        return attributeMappingNode;
    }

    private Node createAttributeMapping(String targetAttributeValue, String OCQLValue) {
        Element attributeMappingNode = document.createElement("AttributeMapping");
        Element targetAttributeNode = document.createElement("targetAttribute");
        targetAttributeNode.setTextContent(targetAttributeValue);
        Element sourceExpressionNode = document.createElement("sourceExpression");
        Element OCQLNode = document.createElement("OCQL");
        OCQLNode.setTextContent(OCQLValue);
        sourceExpressionNode.appendChild(OCQLNode);
        attributeMappingNode.appendChild(targetAttributeNode);
        attributeMappingNode.appendChild(sourceExpressionNode);
        return attributeMappingNode;
    }

    private Node getAttributeMapping(String targetElementValue, String targetAttributeValue) {
        Node featureTypeMappingNode = this.getFeatureTypeMapping(targetElementValue);
        if (featureTypeMappingNode != null) {
            NodeList ftmChilds = featureTypeMappingNode.getChildNodes();
            for (int i = 0; i < ftmChilds.getLength(); i++) {
                Node ftmChild = ftmChilds.item(i);
                String ftmChildNodeName = ftmChild.getNodeName();
                if (ftmChildNodeName.equals("attributeMappings")) {
                    NodeList attributeMappingList = ftmChild.getChildNodes();
                    for (int j = 0; j < attributeMappingList.getLength(); j++) {
                        Node amChild = attributeMappingList.item(j);
                        NodeList amChildChilds = amChild.getChildNodes();
                        for (int k = 0; k < amChildChilds.getLength(); k++) {
                            Node aChild = amChildChilds.item(k);
                            String nodeName = aChild.getNodeName();
                            if (nodeName.equals("targetAttribute")) {
                                if (aChild.getTextContent().equals(targetAttributeValue))
                                    return amChild;
                            }
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException(
                    String.format("FeatureTypeMapping '%s' is unknown.", targetElementValue));
        }
        return null;
    }
    
    private int countLinkedAttributeMapping(String targetElementValue) {
        int ret = 1;
    	Node featureTypeMappingNode = this.getFeatureTypeMapping(targetElementValue);
        if (featureTypeMappingNode != null) {
            NodeList ftmChilds = featureTypeMappingNode.getChildNodes();
            for (int i = 0; i < ftmChilds.getLength(); i++) {
                Node ftmChild = ftmChilds.item(i);
                String ftmChildNodeName = ftmChild.getNodeName();
                if (ftmChildNodeName.equals("attributeMappings")) {
                    NodeList attributeMappingList = ftmChild.getChildNodes();
                    for (int j = 0; j < attributeMappingList.getLength(); j++) {
                        Node amChild = attributeMappingList.item(j);
                        NodeList amChildChilds = amChild.getChildNodes();
                        for (int k = 0; k < amChildChilds.getLength(); k++) {
                            Node aChild = amChildChilds.item(k);
                            String nodeName = aChild.getNodeName();
                            if (nodeName.equals("targetAttribute")) {
                                if (aChild.getTextContent().contains("FEATURE_LINK"))
                                    ret++;
                            }
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException(
                    String.format("FeatureTypeMapping '%s' is unknown.", targetElementValue));
        }
        return ret;
    }
}
