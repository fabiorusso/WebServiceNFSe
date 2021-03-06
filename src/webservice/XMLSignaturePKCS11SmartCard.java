// Decompiled by DJ v3.12.12.98 Copyright 2014 Atanas Neshkov  Date: 22/03/2016 08:34:18
// Home Page: http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   XMLSignaturePKCS11SmartCard.java

package webservice;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.security.pkcs11.SunPKCS11;

// Referenced classes of package webservice:
//            WebService_recepcionar_lote_rps

public class XMLSignaturePKCS11SmartCard
{

    public XMLSignaturePKCS11SmartCard()
    {
    }

    public static void sign(String pathOrig, String pathDest, String tagName)
    {
        try
        {
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            Reference ref = fac.newReference("", fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", null), Collections.singletonList(fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec)null)), null, null);
            SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec)null), fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null), Collections.singletonList(ref));
            Provider p = new SunPKCS11("c:\\ginfes\\smartCard.cfg");
            Security.addProvider(p);
            char pin[] = "dccprogu".toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS11");
            ks.load(null, pin);
            java.security.KeyStore.PrivateKeyEntry keyEntry = null;
            Enumeration aliasesEnum = ks.aliases();
            PrivateKey privateKey = null;
            do
            {
                if(!aliasesEnum.hasMoreElements())
                    break;
                String alias = (String)aliasesEnum.nextElement();
                System.out.println(alias);
                if(!ks.isKeyEntry(alias))
                    continue;
                keyEntry = (java.security.KeyStore.PrivateKeyEntry)ks.getEntry(alias, new java.security.KeyStore.PasswordProtection("dccprogu".toCharArray()));
                privateKey = keyEntry.getPrivateKey();
                break;
            } while(true);
            X509Certificate cert = (X509Certificate)keyEntry.getCertificate();
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List x509Content = new ArrayList();
            x509Content.add(cert);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(pathOrig));
            NodeList nList = doc.getElementsByTagName(tagName);
            for(int i = 0; i < nList.getLength(); i++)
            {
                org.w3c.dom.Node n = nList.item(i);
                DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), n);
                XMLSignature signature = fac.newXMLSignature(si, ki);
                signature.sign(dsc);
            }

            File arq = new File(pathDest);
            if(arq.exists())
                arq.delete();
            java.io.OutputStream os = new FileOutputStream(pathDest);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));
        }
        catch(TransformerException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(MarshalException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(XMLSignatureException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(ParserConfigurationException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(SAXException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(UnrecoverableEntryException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(CertificateException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(KeyStoreException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(NoSuchAlgorithmException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(InvalidAlgorithmParameterException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void conecta()
    {
        try
        {
            Provider p = new SunPKCS11("c:\\ginfes\\SmartCard.cfg");
            Security.addProvider(p);
            KeyStore ks = KeyStore.getInstance("pkcs11");
            ks.load(null, "dccprogu".toCharArray());
            System.setProperty("javax.net.ssl.keyStore", "NONE");
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS11");
            System.setProperty("javax.net.ssl.keyStorePassword", "dccprogu");
            System.setProperty("javax.net.ssl.keyStoreProvider", "SunPKCS11-SmartCard");
            System.setProperty("javax.net.ssl.trustStore", "C:\\ginfes\\DigiCertSmart.ks");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        }
        catch(IOException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(NoSuchAlgorithmException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(CertificateException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
        catch(KeyStoreException ex)
        {
            Logger.getLogger(getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String getName()
    {
    	return "XMLSignaturePKCS11SmartCard";
    }

    private static final String PIN_NUMBER = "dccprogu";
}