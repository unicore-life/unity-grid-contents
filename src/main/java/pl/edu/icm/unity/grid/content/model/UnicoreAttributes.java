package pl.edu.icm.unity.grid.content.model;

/**
 * All UNICORE attributes with names.
 *
 * @author R.Kluszczynski
 */
public enum UnicoreAttributes {
    XLOGIN("urn:unicore:attrType:xlogin"),
    DEFAULT_XLOGIN("urn:unicore:attrType:defaultXlogin"),
    ROLE("urn:unicore:attrType:role"),
    DEFAULT_ROLE("urn:unicore:attrType:defaultRole"),
    GROUP("urn:unicore:attrType:primaryGid"),
    DEFAULT_GROUP("urn:unicore:attrType:defaultPrimaryGid"),
    SUPPLEMENTARY_GROUPS("urn:unicore:attrType:supplementaryGids"),
    DEFAULT_SUPPLEMENTARY_GROUPS("urn:unicore:attrType:defaultSupplementaryGids"),
    ADD_DEFAULT_GROUPS("urn:unicore:attrType:addDefaultGroups"),
    QUEUE("urn:unicore:attrType:queue"),
    DEFAULT_QUEUE("urn:unicore:attrType:defaultQueue"),
    PROJECT("urn:unicore:attrType:project"),
    DEFAULT_PROJECT("urn:unicore:attrType:defaultProject"),
    VIRTUAL_ORGANISATIONS("urn:SAML:voprofile:group");

    private final String attributeName;

    UnicoreAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }
}
