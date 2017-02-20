//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 11:27:12 AM BST 
//


package uk.gov.gsi.hmrc.rest.dms.xmlobjects.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dfs_config">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="import_rules">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="target_acl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_acl_domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_folder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_attachment_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_attachment_folder_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="target_mailitem_folder_attr_json" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="meta_data_mappings">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="meta_data" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="meta_data_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="attribute_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="attribute_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="attribute_date_format" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dfsConfig"
})
@XmlRootElement(name = "dfs_configs")
public class DfsConfigs {

    @XmlElement(name = "dfs_config", required = true)
    protected DfsConfigs.DfsConfig dfsConfig;

    /**
     * Gets the value of the dfsConfig property.
     * 
     * @return
     *     possible object is
     *     {@link DfsConfigs.DfsConfig }
     *     
     */
    public DfsConfigs.DfsConfig getDfsConfig() {
        return dfsConfig;
    }

    /**
     * Sets the value of the dfsConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link DfsConfigs.DfsConfig }
     *     
     */
    public void setDfsConfig(DfsConfigs.DfsConfig value) {
        this.dfsConfig = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="import_rules">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="target_acl" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_acl_domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_folder" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_attachment_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_attachment_folder_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="target_mailitem_folder_attr_json" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="meta_data_mappings">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="meta_data" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="meta_data_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="attribute_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="attribute_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="attribute_date_format" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "importRules"
    })
    public static class DfsConfig {

        @XmlElement(name = "import_rules", required = true)
        protected DfsConfigs.DfsConfig.ImportRules importRules;

        /**
         * Gets the value of the importRules property.
         * 
         * @return
         *     possible object is
         *     {@link DfsConfigs.DfsConfig.ImportRules }
         *     
         */
        public DfsConfigs.DfsConfig.ImportRules getImportRules() {
            return importRules;
        }

        /**
         * Sets the value of the importRules property.
         * 
         * @param value
         *     allowed object is
         *     {@link DfsConfigs.DfsConfig.ImportRules }
         *     
         */
        public void setImportRules(DfsConfigs.DfsConfig.ImportRules value) {
            this.importRules = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="target_acl" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_acl_domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_folder" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_attachment_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_attachment_folder_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="target_mailitem_folder_attr_json" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="meta_data_mappings">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="meta_data" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="meta_data_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="attribute_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="attribute_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="attribute_date_format" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "targetAcl",
            "targetAclDomain",
            "targetFolder",
            "targetType",
            "targetAttachmentType",
            "targetAttachmentFolderType",
            "targetMailitemFolderAttrJson",
            "metaDataMappings"
        })
        public static class ImportRules {

            @XmlElement(name = "target_acl", required = true)
            protected String targetAcl;
            @XmlElement(name = "target_acl_domain", required = true)
            protected String targetAclDomain;
            @XmlElement(name = "target_folder", required = true)
            protected String targetFolder;
            @XmlElement(name = "target_type", required = true)
            protected String targetType;
            @XmlElement(name = "target_attachment_type", required = true)
            protected String targetAttachmentType;
            @XmlElement(name = "target_attachment_folder_type", required = true)
            protected String targetAttachmentFolderType;
            @XmlElement(name = "target_mailitem_folder_attr_json", required = true)
            protected String targetMailitemFolderAttrJson;
            @XmlElement(name = "meta_data_mappings", required = true)
            protected DfsConfigs.DfsConfig.ImportRules.MetaDataMappings metaDataMappings;

            /**
             * Gets the value of the targetAcl property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetAcl() {
                return targetAcl;
            }

            /**
             * Sets the value of the targetAcl property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetAcl(String value) {
                this.targetAcl = value;
            }

            /**
             * Gets the value of the targetAclDomain property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetAclDomain() {
                return targetAclDomain;
            }

            /**
             * Sets the value of the targetAclDomain property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetAclDomain(String value) {
                this.targetAclDomain = value;
            }

            /**
             * Gets the value of the targetFolder property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetFolder() {
                return targetFolder;
            }

            /**
             * Sets the value of the targetFolder property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetFolder(String value) {
                this.targetFolder = value;
            }

            /**
             * Gets the value of the targetType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetType() {
                return targetType;
            }

            /**
             * Sets the value of the targetType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetType(String value) {
                this.targetType = value;
            }

            /**
             * Gets the value of the targetAttachmentType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetAttachmentType() {
                return targetAttachmentType;
            }

            /**
             * Sets the value of the targetAttachmentType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetAttachmentType(String value) {
                this.targetAttachmentType = value;
            }

            /**
             * Gets the value of the targetAttachmentFolderType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetAttachmentFolderType() {
                return targetAttachmentFolderType;
            }

            /**
             * Sets the value of the targetAttachmentFolderType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetAttachmentFolderType(String value) {
                this.targetAttachmentFolderType = value;
            }

            /**
             * Gets the value of the targetMailitemFolderAttrJson property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTargetMailitemFolderAttrJson() {
                return targetMailitemFolderAttrJson;
            }

            /**
             * Sets the value of the targetMailitemFolderAttrJson property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTargetMailitemFolderAttrJson(String value) {
                this.targetMailitemFolderAttrJson = value;
            }

            /**
             * Gets the value of the metaDataMappings property.
             * 
             * @return
             *     possible object is
             *     {@link DfsConfigs.DfsConfig.ImportRules.MetaDataMappings }
             *     
             */
            public DfsConfigs.DfsConfig.ImportRules.MetaDataMappings getMetaDataMappings() {
                return metaDataMappings;
            }

            /**
             * Sets the value of the metaDataMappings property.
             * 
             * @param value
             *     allowed object is
             *     {@link DfsConfigs.DfsConfig.ImportRules.MetaDataMappings }
             *     
             */
            public void setMetaDataMappings(DfsConfigs.DfsConfig.ImportRules.MetaDataMappings value) {
                this.metaDataMappings = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="meta_data" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="meta_data_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="attribute_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="attribute_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="attribute_date_format" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "metaData"
            })
            public static class MetaDataMappings {

                @XmlElement(name = "meta_data")
                protected List<DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData> metaData;

                /**
                 * Gets the value of the metaData property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the metaData property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getMetaData().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData }
                 * 
                 * 
                 */
                public List<DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData> getMetaData() {
                    if (metaData == null) {
                        metaData = new ArrayList<DfsConfigs.DfsConfig.ImportRules.MetaDataMappings.MetaData>();
                    }
                    return this.metaData;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="meta_data_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="attribute_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="attribute_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="attribute_date_format" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "metaDataName",
                    "attributeName",
                    "attributeType",
                    "attributeDateFormat"
                })
                public static class MetaData {

                    @XmlElement(name = "meta_data_name", required = true)
                    protected String metaDataName;
                    @XmlElement(name = "attribute_name", required = true)
                    protected String attributeName;
                    @XmlElement(name = "attribute_type", required = true)
                    protected String attributeType;
                    @XmlElement(name = "attribute_date_format", required = true)
                    protected String attributeDateFormat;

                    /**
                     * Gets the value of the metaDataName property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getMetaDataName() {
                        return metaDataName;
                    }

                    /**
                     * Sets the value of the metaDataName property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setMetaDataName(String value) {
                        this.metaDataName = value;
                    }

                    /**
                     * Gets the value of the attributeName property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getAttributeName() {
                        return attributeName;
                    }

                    /**
                     * Sets the value of the attributeName property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setAttributeName(String value) {
                        this.attributeName = value;
                    }

                    /**
                     * Gets the value of the attributeType property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getAttributeType() {
                        return attributeType;
                    }

                    /**
                     * Sets the value of the attributeType property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setAttributeType(String value) {
                        this.attributeType = value;
                    }

                    /**
                     * Gets the value of the attributeDateFormat property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getAttributeDateFormat() {
                        return attributeDateFormat;
                    }

                    /**
                     * Sets the value of the attributeDateFormat property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setAttributeDateFormat(String value) {
                        this.attributeDateFormat = value;
                    }

                }

            }

        }

    }

}
