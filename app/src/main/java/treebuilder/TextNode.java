/** \file
 * 
 * Sep 19, 2004
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
 * TextNode
 * Sep 19, 2004
 * 
<p>
    An XML text node.  "Text" in XML consists of the characters between two
    tags.  This may be white space.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class TextNode extends TreeNode {
    private String mText = null;

    /**
     * @param ty
     */
    public TextNode(String text) {
        super(TreeNodeType.TEXT);
        mText = text;
    }

    /**
       Used for building COMMENT nodes.  Like text nodes, comment nodes
       basicly consist of a String.
     */
    public TextNode(TreeNodeType ty, String text) {
        super(ty);
        mText = text;
    }

    public String getText() { return mText; }

    public String toString() {
        return mText;
    }

}
