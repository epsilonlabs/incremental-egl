/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.plainxml;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStream extends InputStream {

    protected int    strOffset  = 0;
    protected int    charOffset = 0;
    protected int    available;
    protected String str;

    public StringInputStream(String s) {
        str       = s;
        available = s.length() * 2;
    }

    public int read() throws java.io.IOException {

        if (available == 0) {
            return -1;
        }

        available--;

        char c = str.charAt(strOffset);

        if (charOffset == 0) {
            charOffset = 1;

            return (c & 0x0000ff00) >> 8;
        } else {
            charOffset = 0;

            strOffset++;

            return c & 0x000000ff;
        }
    }

    public int available() throws IOException {
        return available;
    }
}