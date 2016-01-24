package tt.lab.android.ieltspass;/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.util.*;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * @author Administrator
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class MailSender {

	private static String TAG = MailSender.class.getName();
	private String fromDisplayName;
	private String fromEmailAddress;// email
	private String password;
	private String smtpServer;
	private String bcc;
	private String to;// email
	private String toDisplayName;// email

	private String type;
	private String subject;
	private String content;
	private ArrayList fileList = new ArrayList();
	private ArrayList urlList = new ArrayList();
	private ArrayList txtList = new ArrayList();

	public MailSender() {
	}

	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}

	public void setFromPassword(String password) {
		this.password = password;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addFile(String file) {
		this.fileList.add(file);
	}

	public void addUrl(String url) {
		this.urlList.add(url);
	}

	public void addTxt(String txt) {
		this.txtList.add(txt);
	}
	public String getSender() {
		return fromDisplayName;
	}

	public String getToDisplayName() {
		return toDisplayName;
	}

	public void setToDisplayName(String toDisplayName) {
		this.toDisplayName = toDisplayName;
	}

	public String getFromDisplayName() {
		return fromDisplayName;
	}

	public void setFromDisplayName(String fromDisplayName) {
		this.fromDisplayName = fromDisplayName;
	}
	/**
	 * You can call this directly, or use a thread to call it
	 * 
	 * @return
	 */
	public boolean send() {
		//tt.lab.android.ieltspass.Logger.i("send... " , "I");
		boolean b = false;
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", this.smtpServer);
			props.put("mail.smtp.auth", "true");
//			Session s = Session.getInstance(props,new Authenticator() {
//				@Override
//				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication(fromEmailAddress, password); 
//				}
//			});
			Session s = Session.getInstance(props);
			s.setDebug(true);

			MimeMessage message = new MimeMessage(s);
			// FROM
			InternetAddress fromAddress = new InternetAddress(fromEmailAddress, fromDisplayName);
			message.setFrom(fromAddress);

			// TO
			InternetAddress toAddress[] = new InternetAddress[1];
			toAddress[0] = new InternetAddress(to, toDisplayName);
			message.setRecipients(Message.RecipientType.TO, toAddress);

			// BCC
			if (this.bcc != null) {

				ArrayList addressList = new ArrayList();
				StringTokenizer st = new StringTokenizer(this.bcc, ",");
				while (st.hasMoreElements()) {
					addressList.add(st.nextToken().trim());
				}

				InternetAddress ccAddress[] = new InternetAddress[addressList.size()];
				int errorAddressCount = 0;
				for (int i = 0; i < ccAddress.length; i++) {
					try {
						ccAddress[i] = new InternetAddress((String) addressList.get(i));
					} catch (Exception e) {
						errorAddressCount++;
						System.out.println(e.getMessage());
					}
				}
				System.out.println("errorAddressCount:" + errorAddressCount);

				message.setRecipients(Message.RecipientType.BCC, ccAddress);
			}
			/* single */
			// InternetAddress toAddress=new InternetAddress(this.to);
			// message.setRecipient(Message.RecipientType.TO,toAddress);
			
			//SUBJECT
			message.setSubject(this.subject);
			
			//SEND DATE
			message.setSentDate(new Date());
			
			//CONTENT
			Multipart mimeMultipart = new MimeMultipart();
			
			//MIME body
			BodyPart mdp = new MimeBodyPart();
			//DataHandler dh = new DataHandler(this.content, this.type + ";charset=UTF-8");
			//mdp.setDataHandler(dh);
			mdp.setContent(this.content, this.type + ";charset=UTF-8");
			mimeMultipart.addBodyPart(mdp);
			/*
			
			for (int i = 0; i < this.txtList.size(); i++) {
				mdp = new MimeBodyPart();
				String txt = (String) txtList.get(i);
				dh = new DataHandler(txt, "text/plain;charset=GBK");

				mdp.setFileName("txt" + (i + 1));
				mdp.setDataHandler(dh);
				mm.addBodyPart(mdp);
			}
			for (int i = 0; i < this.fileList.size(); i++) {

				mdp = new MimeBodyPart();
				String fileName = (String) this.fileList.get(i);
				// FileDataSource fds = new FileDataSource(fileName);
				// dh = new DataHandler(fds);
				mdp.setFileName(fileName);
				// mdp.setDataHandler(dh);
				mm.addBodyPart(mdp);
			}
			for (int i = 0; i < urlList.size(); i++) {
				mdp = new MimeBodyPart();
				String url = (String) this.urlList.get(i);
				// URLDataSource ur = new URLDataSource(new URL(url));

				// dh = new DataHandler(ur);
				mdp.setFileName(url);
				// mdp.setDataHandler(dh);
				mm.addBodyPart(mdp);
			}
*/
			message.setContent(mimeMultipart);
			
			//Text
			//message.setText("plan text ");
			message.saveChanges();
			
			Transport transport = s.getTransport("smtp");
			
			//tt.lab.android.ieltspass.Logger.i("Sending... " , "("+this.fromEmailAddress + "/" + this.password + ")");

			transport.connect(this.smtpServer, this.fromEmailAddress, this.password);
			
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			b = true;

		} catch (Throwable e) {
			Logger.e(TAG , "send: e: "+e);
			e.printStackTrace();
		} finally {
			System.out.println("Sent.");
		}
		//tt.lab.android.ieltspass.Logger.i(TAG , "send: b: "+b);
		return b;
	}



	public  boolean prepareAndSend(String content) {
		//tt.lab.android.ieltspass.Logger.i(TAG , "prepareAndSendAsync: "+content);
		/* Basic Settings */
		MailSender mailSender = new MailSender();
		mailSender.setFromDisplayName("IELTSPASS Android 用户");
		mailSender.setFromEmailAddress("ieltspassuser@126.com");
		mailSender.setFromPassword("ieltspass123");
		mailSender.setSmtpServer("smtp.126.com");
		mailSender.setTo("ieltspassandroid@126.com");
		mailSender.setToDisplayName("IELTSPASS Android 意见反馈邮箱");
		mailSender.setSubject("IELTSPASS Android V0.1 意见反馈");
		mailSender.setContent(content);
		mailSender.setType("text/plain");//"text/html";// "text/plain"

		/* Attachment Settings */
		// mailSender.addTxt("testxt1");
		// mailSender.addTxt("testxt2");

		// mailSender.addFile("d:/userList.jsp");
		// mailSender.addFile("d:/updateRate.inc");

		// mailSender.addUrl("http://www.navitone.com.cn/index.jsp");
		// mailSender.addUrl("http://www.navitone.com.cn/dropin.jsp");

		// System.out.println(mailSender.send());
		//tt.lab.android.ieltspass.Logger.i(TAG , "prepareAndSendAsync: 1");
		boolean send = mailSender.send();
		
		Logger.i(TAG , "prepareAndSendAsync O: "+send);
		return send;
	}


}
