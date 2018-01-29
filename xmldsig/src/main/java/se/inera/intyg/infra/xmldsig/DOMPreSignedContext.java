package se.inera.intyg.infra.xmldsig;

import org.w3c.dom.Node;

import javax.xml.crypto.dom.DOMCryptoContext;

/**
 * Intyg-specific subclass of DOMCryptoContext to use for preparing a XMLDSig signing to be carried out
 * by external EFOS server.
 *
 * I.e. we're responsible for XML canonicalization, digest and the signingValue will be provided by EFOS
 * services.
 *
 * @author eriklupander
 */
public class DOMPreSignedContext extends DOMCryptoContext {

    private Node parent;
    private Node nextSibling;

    public DOMPreSignedContext(Node parent) {
        this.parent = parent;
        this.nextSibling = nextSibling;
    }

    /**
     * Sets the parent node.
     *
     * @param parent the parent node. The marshalled <code>XMLSignature</code>
     *    will be added as a child element of this node.
     * @throws NullPointerException if <code>parent</code> is <code>null</code>
     * @see #getParent
     */
    public void setParent(Node parent) {
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        this.parent = parent;
    }

    /**
     * Sets the next sibling node.
     *
     * @param nextSibling the next sibling node. The marshalled
     *    <code>XMLSignature</code> will be inserted immediately before this
     *    node. Specify <code>null</code> to remove the current setting.
     * @see #getNextSibling
     */
    public void setNextSibling(Node nextSibling) {
        this.nextSibling = nextSibling;
    }

    /**
     * Returns the parent node.
     *
     * @return the parent node (never <code>null</code>)
     * @see #setParent(Node)
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Returns the nextSibling node.
     *
     * @return the nextSibling node, or <code>null</code> if not specified.
     * @see #setNextSibling(Node)
     */
    public Node getNextSibling() {
        return nextSibling;
    }
}
