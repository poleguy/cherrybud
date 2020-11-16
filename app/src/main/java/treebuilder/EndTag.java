/** \file
 * 
 * Sep 13, 2004
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
 * EndTag
 * Sep 13, 2004
 * 
<p>
    An XML end tag.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class EndTag extends NamedEntity {

    /**
     * @param ty
     * @param name
     */
    public EndTag(String name) {
        super(TreeNodeType.END_TAG, name);
    }
}
