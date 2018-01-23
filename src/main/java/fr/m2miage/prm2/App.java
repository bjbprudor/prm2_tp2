package fr.m2miage.prm2;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App
{

    public static Tester tester = new Tester();

    public static void main(String[] args)
    {
        try
        {
            TrierMail();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public static void TrierMail() throws Exception
    {
        int count = 0;

        List<String> lstOk = new ArrayList<String>();
        List<String> lstKo = new ArrayList<String>();

        File folder = new File("./atrier");
        File[] list = folder.listFiles();

        for(File f : list)
        {

            MimeMessage message = MailReader.createMessage(f);
            boolean tri = tester.testFile(message);

            if(tri) {lstOk.add(f.getName()); }
            else { lstKo.add(f.getName()); }
            count++;

        }

        System.out.println(count);
        System.out.println(tester.cnt);
        System.out.println("OK " + lstOk.size());
        System.out.println("KO " + lstKo.size());

    }


}
