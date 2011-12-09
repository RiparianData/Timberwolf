package com.softartisans.timberwolf;

import java.util.Date;

public interface Email {
    String getBody();
    String getSender();
    String[] getRecipients();
    String[] getCcRecipients();
    String[] getBccRecipients();
    String getSubject();
    Date getTimeSent();
    // TODO: What other common headers do we want?

    String[] getHeaderKeys();
    String getHeader(String header);
}