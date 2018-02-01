/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public DOMPreSignedContext(Node parent, Node nextSibling) {
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
