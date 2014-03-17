package tt.lab.android.ieltspass;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailSender {
	public static void main(String[] args) {
		try {
			new EmailSender().sendEmail("george.tao@aspentech.com", "test t", "test c");
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 邮件发送程序
	 * 
	 * @param to
	 *            接受人
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @throws Exception
	 * @throws MessagingException
	 */
	public static void sendEmail(String to, String subject, String content) throws Exception, MessagingException {
		String host = "smtp.gmail.com";
		String address = "yultao@126.com";
		String from = "yultao@126.com";
		String password = "Aspen2010";
		String port = "25";
		sendEmail(host, address, from, password, to, port, subject, content);
	}

	/**
	 * 邮件发送程序
	 * 
	 * @param host
	 *            邮件服务器 如：smtp.qq.com
	 * @param address
	 *            发送邮件的地址 如：545099227@qq.com
	 * @param from
	 *            来自： wsx2miao@qq.com
	 * @param password
	 *            您的邮箱密码
	 * @param to
	 *            接收人
	 * @param port
	 *            端口（QQ:25）
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @throws Exception
	 */
	public static void sendEmail(String host, String address, String from, String password, String to, String port,
			String subject, String content) throws Exception {
		Multipart multiPart;

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", address);
		props.put("mail.smtp.password", password);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		//Logger.i("Check", "done pops");
		Session session = Session.getDefaultInstance(props, null);
		//DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		//message.setDataHandler(handler);
		//Logger.i("Check", "done sessions");

		multiPart = new MimeMultipart();
		InternetAddress toAddress;
		toAddress = new InternetAddress(to);
		message.addRecipient(Message.RecipientType.TO, toAddress);
		//Logger.i("Check", "added recipient");
		message.setSubject(subject);
		message.setContent(multiPart);
		message.setText(content);

		//Logger.i("check", "transport");
		Transport transport = session.getTransport("smtp");
		//Logger.i("check", "connecting");
		transport.connect(host, address, password);
		//Logger.i("check", "wana send");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		//Logger.i("check", "sent");
	}
}
