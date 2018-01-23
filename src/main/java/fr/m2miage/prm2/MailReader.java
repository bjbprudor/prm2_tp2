package fr.m2miage.prm2;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailReader
{

    private boolean textIsHtml = false;

    public MailReader()
    {

    }

    public static MimeMessage createMessage(File file)
    {
        MimeMessage message = null;
        try
        {

            Properties props = System.getProperties();
            props.put("mail.host", "smtp.dummydomain.com");
            props.put("mail.transport.protocol", "smtp");

            Session mailSession = Session.getDefaultInstance(props, null);
            InputStream source = new FileInputStream(file);
            message = new MimeMessage(mailSession, source);

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return message;
    }


    /**
     * Return the primary text content of the message.
     */
    public String getText(Part p) throws MessagingException, IOException
    {

        if (p.isMimeType("text/*"))
        {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative"))
        {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain"))
                {
                    if (text == null)
                        text = getText(bp);
                    continue;
                }
                else if (bp.isMimeType("text/html"))
                {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                }
                else
                {
                    return getText(bp);
                }
            }
            return text;
        }
        else if (p.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++)
            {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;

    }

}
