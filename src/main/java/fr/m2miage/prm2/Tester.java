package fr.m2miage.prm2;

import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tester
{

    public int cnt = 0;
    public HashMap<String,Double> grammar;

    public Tester()
    {
        load();
    }

    public void load()
    {
        try
        {
            List<String> lines = Files.readAllLines(Paths.get("./grammar.txt"));
            grammar = new HashMap<String, Double>();

            for (String line : lines)
            {
                String[] values = line.split(";");
                Double val = Double.parseDouble(values[1]);
                grammar.put(values[0],val);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public boolean testFile(MimeMessage content) throws Exception
    {

        //System.out.println("Subject : " + content.getSubject());
        // vecteur de reference a 0.5 si rare (pas obligé d'avoir la ref a 1 ou entier)

        MailReader reader = new MailReader();

        String body = "";
        if(content.getContent().getClass() == MimeMultipart.class)
        {
            MimeMultipart multipart = (MimeMultipart)content.getContent();
            body = reader.getText(multipart.getBodyPart(0));
        }
        else
        {
            cnt++;
        }

        List<Double> ref = new ArrayList<Double>();
        List<Double> vec = new ArrayList<Double>();

        for (String gm : grammar.keySet())
        {
            Double cnt = (double)StringUtils.countMatches(body, gm);
            ref.add(grammar.get(gm));
            vec.add(cnt);
        }
        double res = vecteurScalaire(ref,vec);

        //System.out.println(content.getSubject() + " cos :" + res + " vecteur : " + vec);
        return res >= 0.80;
    }

    public double vecteurScalaire(List<Double> ref, List<Double> v0)
    {

        double normeRef = 0;
        for (Double valRef : ref) { normeRef += valRef*valRef; }
        normeRef = Math.sqrt(normeRef);

        double normeMes = 0;
        for (Double valMes : v0) { normeMes += valMes*valMes; }
        normeMes = Math.sqrt(normeMes);

        double norme = normeRef * normeMes;
        int val = 0;
        for (int i = 0; i < ref.size();i++) { val += ref.get(i) * v0.get(i); }

        double cos = val / (normeMes * normeRef);
        return cos;

    }

}
