/** \file
 * 
 * Sep 12, 2004
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

import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * TreeBuilder
 * Sep 12, 2004
 * 
<p>
    This class supports the construction of a light weight in memory
    tree from an XML file.  Like the W3C DOM Document object, the
    structure of this tree reflects the structure of the XML.
</p>
<p>
    The tree that is constructed is designed for rapid traversal 
    and in memory modification.  It also has the advantage of using
    less memory than the java.sun.com DOM Document implementation.
</p>
<p>
    This code demonstrates how little source code is required to
    parse XML using the XmlPullParser.
</p>
<p>
    A few notes about attributes and namespaces.  In general I think
    that the XmlPullParser rocks.  The API is will designed and
    the calls mostly make sense.  But...  the XmlPullParser does
    not treat all attributes the same way.  In particular it does
    not treat name space definitions like other attributes.  This
    can be seen in XML designed to be processed via a schema.  For
    example:
</p>
<pre>
          &lt;ex:EXPRESSION 
              xmlns:ex="http://www.bearcave.com/expression" 
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
              xsi:schemaLocation="http://www.bearcave.com/expression xmlexpr/expression.xsd"&gt;

</pre>
<p>
    Here there are two name space definitions.  One defining the namespace
    associated with the "ex" prefix and one associated with XML schemas
    (for the schema location attribute).
</p>
<p>
     The getAttributeCount() method will return 1 when processing the EXPRESSION tag.
     This is because the name space definitions (the attributes with the xmlns
     prefix) are not treated as normal attributes.  And sometimes this is good,
     because these attributes are not necessarily of interest.  In this case,
     however, the intent is to exactly mirror the XML in an in-memory tree.
     So that if the tree is serialized the original XML will be recovered 
     (with the exception of white space TEXT, since this is not included).
</p>
<p>
     The attributes are only available when the END_TAG element for the document
     tag is processed.  The getDepth() method tells the current XML nesting
     depth, so it can be determined that an END_TAG is the document end tag.
     The attributes are then fetched and prepended to the attribute list.
     Sort of awkward.  This is the one place where I would differ in the
     design of the XmlPullParser.  I'd treat all attributes the same way,
     including those with the "xmlns" prefix.  Then the user can simply
     ignore operands with the namespace prefix.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class TreeBuilder {
    private XmlPullParser mParser = null;
    private TreeNode mDocumentTag = null;

    private Attribute buildAttr( int index )
    {
        String name = mParser.getAttributeName( index );
        String prefix = mParser.getAttributePrefix( index );
        String namespace = mParser.getAttributeNamespace( index );

        Attribute attr = new Attribute( name, prefix, namespace );

        String attrType = mParser.getAttributeType( index );
        String attrVal = mParser.getAttributeValue( index );

        attr.setAttrType( attrType );
        attr.setValue( attrVal );
        return attr;
    } // buildAttr


    private Attribute buildNS( int index ) throws XmlPullParserException
    {
        String nsName = mParser.getNamespacePrefix( index );
        String uri = mParser.getNamespaceUri( index );
        Attribute attr = new Attribute( nsName, "xmlns", uri );
        attr.setValue( uri );
        return attr;
    }


    /**
       This method is called for the end tag of the root document tag.
       If there are name spaces, they would have been defined in this
       tag.  Insert them in the front of the attribute list.
     * @throws XmlPullParserException
     */
    private void addNamespaces( int depth ) throws XmlPullParserException
    {
        TagNode tag = (TagNode)mDocumentTag;
        AttributeList attrList = tag.getAttrList();

        int nsStart = mParser.getNamespaceCount( depth-1 );
        int nsEnd = mParser.getNamespaceCount( depth );
        for (int i = nsEnd-1; i >= nsStart; i--) {
            Attribute attr = buildNS( i );
            attrList.insert( attr );
        }
    } // addNamespaces


    /**
       Build a tag node.  Note that a tag node always has an
       AttributeList object, even if there is no attribute list.  This
       wastes some memory, but in theory should make the tree
       processing more regular, since it can always be assumed that
       this object exits.
     */
    private TagNode buildTagNode()
    {
        String name = mParser.getName();
        String prefix = mParser.getPrefix();
        String namespace = mParser.getNamespace();
        TagNode tag = new TagNode( name, prefix, namespace );

        AttributeList attrList = new AttributeList();
        int numAttr = mParser.getAttributeCount();
        for (int i = 0; i < numAttr; i++) {
            Attribute attr = buildAttr( i );
            attrList.append( attr );
        } // for
        tag.setAttrList( attrList );
        return tag;
    } // buildTagNode




    /**
       Recursively parse an XML file into an in-memory tree data structure.
<p>
       Currently this code only handles the COMMENT, TEXT and START_TAG
       elements returned by the XmlPullParser.  Other XML elements are 
       ignored.
</p>
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TreeNode buildTree() 
    	throws XmlPullParserException, IOException
    {
        TreeNode root = null;
        TreeNode child = null;
        TreeNode curSib = null;
        boolean done = false;
        do {
            int event = mParser.nextToken();
            if (event == XmlPullParser.START_TAG) {
                root = buildTagNode();
                if (mParser.getDepth() == 1) {
                    mDocumentTag = root;
                }
                for (TreeNode t = buildTree(); !(t instanceof EndTag); t = buildTree()) {
                    if (child == null) {
                        child = t;
                        curSib = child;
                    }
                    else {
                        curSib.setSibling( t );
                        curSib = t;
                    }
                } // for
                root.setChild( child );
                done = true;;
            }
            else if (event == XmlPullParser.COMMENT) {
                String comment = mParser.getText();
                root = new TextNode( TreeNodeType.COMMENT, comment );
                done = true;
            }
            else if (event == XmlPullParser.TEXT) {
                String text = mParser.getText();
                root = new TextNode( text );
                done = true;
            }
            else if (event == XmlPullParser.END_TAG) { 
                String name = mParser.getName();
                root = new EndTag( name );
                int depth = mParser.getDepth();
                if (depth == 1) {
                    // add the namespace attribtues to the document tag, if they exist
                    addNamespaces( depth );
                }
                done = true;
            }
            else if (event == XmlPullParser.END_DOCUMENT) {
                root = new EndTag( "END DOCUMENT" );
                done = true;
            }
        } while (! done);
        return root;
    }  // buildTree


    /**
       Allocate and initial an XmlPullParser
<p>
       At the time this code was written the XmlPullParser
       did not support validation, so the call to setValidating()
       is passed "false".
</p>
     */
    private XmlPullParser getParser()
        throws XmlPullParserException
    {
        XmlPullParserFactory factory;
        factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware( true );
        factory.setValidating( false );
        XmlPullParser parser = factory.newPullParser();
        return parser;
    } // getParser


    /**
       This is the public entry point for the TreeBuilder.  It is
       passed a FileReader, which has been opened for an XML file.
     */
    public TreeNode parseXML( FileReader reader )
        throws XmlPullParserException, IOException
    {
        TreeNode root = null;
        mParser = getParser();
        if (mParser != null) {
            mParser.setInput( reader );
            root = buildTree();
        }
        return root;
    } // parseXML

}
