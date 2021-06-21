package com.sistema.macroex.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JavaMailApp {

    @Value("${macroex.email}")
    private String emailProvedor;

    @Value("${macroex.senha}")
    private String senhaprovedor;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //metodo para enviar email notificando o fornecedor
    public void enviaremail(String email, String nomeContraRotulo) {
        Properties props = new Properties();

        /** Parâmetros de conexão com servidor Gmail */
        props.put("mail.smtp.auth", "true"); /** Autorização */
        props.put("mail.smtp.starttls", "true"); /** Autenticação */
        props.put("mail.smtp.host", "smtp.gmail.com"); /** Servidor do Gmail */
        props.put("mail.smtp.port", "465"); /** Porta */
        props.put("mail.smtp.socketFactory.port", "465"); /** Expecififcar a porta a ser conectada */
        props.put("mail.smtp.ssl.trust", "*"); /**Validação do Gmail */
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProvedor, senhaprovedor);
            }
        });
        /** troquei a session do getDefaulInstance por getInstance */

        /** Ativa Debug para sessão */
        session.setDebug(true);

        try {

            // verificar como colocar mais de um email
            Address[] toUser = InternetAddress // Destinatário(s)
                    .parse(email);

            Message message = new MimeMessage(session);            
            message.setFrom(new InternetAddress(emailProvedor, "Microex")); // pode colocar ou tirar o nome em vez de deixar somente email

            // Remetente
            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject("Email para efeturar cadastro do Contra Rotulo");// Assunto
            message.setText("Fornecedor, favor entrar no sistema Microex e colocar dados do Rotulo " + nomeContraRotulo);
            /** Método para enviar a mensagem criada */
            Transport.send(message);

            System.out.println("Feito!!!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Recuperar a senha
    public void recuoeraremail(String email) {
        Properties props = new Properties();

        /** Parâmetros de conexão com servidor Gmail */
        props.put("mail.smtp.auth", "true"); /** Autorização */
        props.put("mail.smtp.starttls", "true"); /** Autenticação */
        props.put("mail.smtp.host", "smtp.gmail.com"); /** Servidor do Gmail */
        props.put("mail.smtp.port", "465"); /** Porta */
        props.put("mail.smtp.socketFactory.port", "465"); /** Expecififcar a porta a ser conectada */
        props.put("mail.smtp.ssl.trust", "*"); /**Validação do Gmail */
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProvedor, senhaprovedor);
            }
        });

        /** Ativa Debug para sessão */
        session.setDebug(true);

        try {

            // verificar como colocar mais de um email
            Address[] toUser = InternetAddress // Destinatário(s)
                    .parse(email);

            Message message = new MimeMessage(session);            
            message.setFrom(new InternetAddress(emailProvedor, "Microex")); // pode colocar ou tirar o nome em vez de deixar somente email

            //Mudar a senha do Usuario
            Usuario user = usuarioRepository.findByUsuarioByLogin(email);
            user.setSenha(gerarSenha());
            usuarioRepository.save(user);

            // Remetente
            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject("Email para efeturar cadastro do Contra Rotulo");// Assunto
            message.setText("Sua nova senha é " +user.getSenha());
            /** Método para enviar a mensagem criada */
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //gerar senha nova com 10 caracteres
    private static String gerarSenha(){
		int qtdeMaximaCaracteres = 8;
	    String[] caracteres = { "0", "1", "b", "2", "4", "5", "6", "7", "8",
	                "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
	                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
	                "x", "y", "z"};
	    
		StringBuilder senha = new StringBuilder();

        for (int i = 0; i < qtdeMaximaCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }

        String senhaCriptografada = new BCryptPasswordEncoder().encode(senha.toString());

        return senhaCriptografada;
        
	}

}
