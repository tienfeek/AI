/**
 * 
 */
package com.tien.ai.net;

import java.util.Comparator;

import com.tien.ai.demain.ContactsMeta;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class ContactComparator implements Comparator<ContactsMeta> {

    @Override
    public int compare(ContactsMeta lhs, ContactsMeta rhs) {
        return lhs.getStatus() - rhs.getStatus();
    }
    
}
