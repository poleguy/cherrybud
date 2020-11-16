/** \file
 * 
 * Sep 4, 2004
 *
 * Copyright Ian Kaplan 2004, Bear Products International
 *
 * You may use this code for any purpose, without restriction,
 * including in proprietary code for which you charge a fee.
 * In using this code you acknowledge that you understand its
 * function completely and accept all risk in its use.
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package treebuilder;

/**
 * Attribute
 * Sep 4, 2004
 *
<p>
    An XML attribute object.  This object is a subclass of NamedEntity.  The
    fields in the NamedEntity superclass store the attribute name information.
</p> 
<p>
    According to the XmlPullParser documentation, if the parser is non-validating
    the type is always CDATA.  At the time of this writing there were no
    validating XmlPullParsers, so the type field will always be CDATA, making
    this field rather useless.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class Attribute extends NamedEntity {
    /** attribute value */
    private String mValue = null;
    /** attribute type */
    private String mType = null;

    public Attribute(String name ) {
        super( TreeNodeType.ATTRIBUTE, name );
    }

    public Attribute( String name, String prefix, String namespace )
    {
        super(TreeNodeType.ATTRIBUTE, name, prefix, namespace );
    }

    public String getValue() { return mValue; }
    public void setValue( String value ) { mValue = value; }

    public String getAttrType() { return mType; }
    public void setAttrType( String ty ) { mType = ty; }

    public TreeNode getChild() { return null; }
    public void setChild(TreeNode child) {}

    public Attribute getNext() { return (Attribute)this.getSibling(); }
    public void setNext( Attribute attr ) { this.setSibling( attr ); }
}
