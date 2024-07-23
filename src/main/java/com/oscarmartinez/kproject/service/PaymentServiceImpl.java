package com.oscarmartinez.kproject.service;

import org.apache.commons.mail.EmailException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.*;
import javax.mail.internet.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.ChargeHistory;
import com.oscarmartinez.kproject.entity.ChargeMovement;
import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IChargeHistoryRepository;
import com.oscarmartinez.kproject.repository.IChargeMovementRepository;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IPaymentRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.PaymentBotDTO;
import com.oscarmartinez.kproject.resource.PaymentDTO;
import com.oscarmartinez.kproject.resource.ReportMonthDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.JREmptyDataSource;

@Service
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Autowired
	private IPaymentRepository paymentRepo;

	@Autowired
	private IStudentRepository studentRepo;

	@Autowired
	private IGymRepository gymRepository;

	@Autowired
	private IChargeMovementRepository chargeMovementRepository;

	@Autowired
	private IChargeHistoryRepository chargeHistoryRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public List<Payment> listPayments() {
		return paymentRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public void addPayment(PaymentDTO payment) throws Exception {
		Payment newPayment = new Payment();
		newPayment.setDepositTicket(payment.getDepositTicket());
		newPayment.setMonth(payment.getMonth());
		newPayment.setPaymentDate(payment.getPaymentDate());
		newPayment.setValue(payment.getValue());
		newPayment.setLatePayment(payment.isLatePayment());
		newPayment.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		Student student = studentRepo.findById(payment.getStudentId())
				.orElseThrow(() -> new Exception("Student not exist with id " + payment.getStudentId()));
		newPayment.setStudent(student);
		newPayment.setValid(true);
		newPayment.setInsertedBy("ADMIN");

		Payment p = paymentRepo.save(newPayment);

		validatePaymentAmount(p);
	}

	@Override
	public ResponseEntity<Payment> editPayment(long id, PaymentDTO paymentDetail) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));
		payment.setDepositTicket(paymentDetail.getDepositTicket());
		payment.setMonth(paymentDetail.getMonth());
		payment.setPaymentDate(paymentDetail.getPaymentDate());
		payment.setValue(paymentDetail.getValue());
		payment.setLatePayment(paymentDetail.isLatePayment());
		Student student = studentRepo.findById(paymentDetail.getStudentId())
				.orElseThrow(() -> new Exception("Student not exist with id: " + paymentDetail.getStudentId()));
		payment.setStudent(student);

		paymentRepo.save(payment);

		return ResponseEntity.ok(payment);
	}

	@Override
	public ResponseEntity<HttpStatus> deletePayment(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));

		paymentRepo.delete(payment);
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Payment> getPaymentById(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));

		return ResponseEntity.ok(payment);
	}

	public boolean existOnArray(List<ReportMonthDTO> array, String month) {
		for (ReportMonthDTO report : array) {
			if (report.getMonth().equals(month))
				return true;
		}
		return false;
	}

	public List<ReportMonthDTO> modifyList(List<ReportMonthDTO> array, int value) {
		for (ReportMonthDTO report : array) {
			int tempValue = report.getValue();
			report.setValue(tempValue + value);
		}
		return array;
	}

	@Override
	public List<ReportMonthDTO> getPaymentPerMoth() throws Exception {
		List<Payment> payments = paymentRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		List<ReportMonthDTO> response = new ArrayList<ReportMonthDTO>();
		for (Payment payment : payments) {
			if (existOnArray(response, payment.getMonth())) {
				response = modifyList(response, payment.getValue());
			} else {
				Date today = new Date();
				if (payment.getPaymentDate().getYear() == today.getYear()) {
					ReportMonthDTO report = new ReportMonthDTO();
					report.setMonth(payment.getMonth());
					report.setValue(payment.getValue());
					response.add(report);
				}
			}
		}
		return response;
	}

	public String getMonth(int month) {
		String[] monthNames = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE",
				"OCTUBRE", "NOVIEMBRE", "DICIEMBRE" };
		return monthNames[month - 1];
	}

	public String getCurrentMonth() {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		return getMonth(month);
	}

	public boolean isLate() {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (localDate.getDayOfMonth() > 5) {
			return true;
		}
		return false;
	}

	@Override
	public void registerBotPayment(PaymentBotDTO payment) throws Exception {
		Payment newPayment = new Payment();
		newPayment.setDepositTicket(payment.getDepositTicket());
		newPayment.setMonth(getCurrentMonth());
		newPayment.setPaymentDate(new Date());
		newPayment.setValue(payment.getValue());
		newPayment.setLatePayment(isLate());
		Student student = studentRepo.findByLicense(payment.getStudentLicense());
		newPayment.setStudent(student);
		newPayment.setGym(student.getGym());
		newPayment.setValid(false);
		newPayment.setInsertedBy(payment.getInsertedBy());
		paymentRepo.save(newPayment);
	}

	@Override
	public void validPayment(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));
		payment.setValid(true);

		validatePaymentAmount(payment);

		paymentRepo.save(payment);
	}

	public void validatePaymentAmount(Payment p) throws JRException, IOException, EmailException {

		createChargeHistory(p);

		List<ChargeMovement> chargesDetails = chargeMovementRepository.findByStudent(p.getStudent());

		int availableAmount = p.getValue();

		Optional<ChargeMovement> inscription = chargesDetails.stream()
				.filter(charge -> charge.getType() == ChargeMovement.ChargeType.ADDITIONAL)
				.min(Comparator.comparing(ChargeMovement::getAddedDate));

		if (inscription.isPresent()) {
			ChargeMovement inscriptionValue = inscription.get();
			if (availableAmount == inscriptionValue.getAmount()) {
				int temp = availableAmount;
				availableAmount = temp - inscriptionValue.getAmount();
				chargeMovementRepository.delete(inscriptionValue);
			} else if (availableAmount > inscriptionValue.getAmount()) {
				int temp = availableAmount;
				availableAmount = temp - inscriptionValue.getAmount();
				chargeMovementRepository.delete(inscriptionValue);
			} else {
				System.out.println("No aplica a pago de inscripcion, procede a buscar cuotas.");
			}
		}

		// Obtain oldest cuote
		Optional<ChargeMovement> oldestCharge = chargesDetails.stream()
				.filter(charge -> charge.getType() == ChargeMovement.ChargeType.QUOTE)
				.min(Comparator.comparing(ChargeMovement::getAddedDate));

		while (oldestCharge.isPresent() && availableAmount > 0) {
			ChargeMovement quote = oldestCharge.get();
			if (availableAmount < quote.getAmount()) {
				int newAmount = quote.getAmount() - availableAmount;
				int temp = availableAmount;
				availableAmount = temp - quote.getAmount();
				quote.setAmount(newAmount);
				chargeMovementRepository.save(quote);
			} else if (availableAmount == quote.getAmount()) {
				int temp = availableAmount;
				availableAmount = temp - quote.getAmount();
				chargeMovementRepository.delete(quote);
			} else {
				int temp = availableAmount;
				availableAmount = temp - quote.getAmount();
				chargeMovementRepository.delete(quote);
			}

			// Obtain oldest cuote
			oldestCharge = chargeMovementRepository.findByStudent(p.getStudent()).stream()
					.filter(charge -> charge.getType() == ChargeMovement.ChargeType.QUOTE)
					.min(Comparator.comparing(ChargeMovement::getAddedDate));

		}

		if (!oldestCharge.isPresent() && availableAmount > 0) {
			boolean containLateness = chargesDetails.stream()
					.anyMatch(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.DELINQUENCY);
			if (containLateness) {
				// Obtain oldest past deliquency
				Optional<ChargeMovement> optionalLateness = chargesDetails.stream()
						.filter(charge -> charge.getType() == ChargeMovement.ChargeType.DELINQUENCY)
						.min(Comparator.comparing(ChargeMovement::getAddedDate));
				while (optionalLateness.isPresent() && availableAmount > 0) {
					ChargeMovement lateness = optionalLateness.get();
					if (availableAmount < lateness.getAmount()) {
						int newLateness = lateness.getAmount() - availableAmount;
						int tempVal = availableAmount;
						availableAmount = tempVal - lateness.getAmount();
						lateness.setAmount(newLateness);
						chargeMovementRepository.save(lateness);
					} else {
						int tempVal = availableAmount;
						availableAmount = tempVal - lateness.getAmount();
						chargeMovementRepository.delete(lateness);
					}

					optionalLateness = chargeMovementRepository.findByStudent(p.getStudent()).stream()
							.filter(charge -> charge.getType() == ChargeMovement.ChargeType.DELINQUENCY)
							.min(Comparator.comparing(ChargeMovement::getAddedDate));
				}
			}
		}

		List<ChargeMovement> chargesDetailsAfterProcess = chargeMovementRepository.findByStudent(p.getStudent());
		Student student = p.getStudent();
		if (chargesDetailsAfterProcess.isEmpty()) {
			student.setStatus(Student.Status.UP_TO_DATE);
		}

		studentRepo.save(student);

	}

	public void createChargeHistory(Payment p) throws JRException, IOException, EmailException {
		ChargeHistory chargeHistory = new ChargeHistory();
		chargeHistory.setAddedDate(new Date());
		chargeHistory.setAmount(p.getValue());
		chargeHistory.setDescription("Pago Recibido");
		chargeHistory.setType(ChargeHistory.ChargeTypeHistory.PAYMENT);
		chargeHistory.setGym(p.getGym());
		chargeHistory.setStudent(p.getStudent());

		chargeHistoryRepository.save(chargeHistory);

		generateRecipt(p, "Pago realizado en dojo ken sei kai.");
	}

	public void generateRecipt(Payment p, String description) throws JRException, IOException, EmailException {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("dateRecipt",
				LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		parameters.put("number", Long.toString(p.getId()));
		parameters.put("client", p.getStudent().getTutor());
		parameters.put("description", description);
		parameters.put("reciptValue", "Q" + p.getValue() + ".00");

		InputStream jrxmlInputStreamPayment = new ClassPathResource("Recipt.jrxml").getInputStream();
		JasperDesign designPayment = JRXmlLoader.load(jrxmlInputStreamPayment);
		JasperReport jasperReportPayment = JasperCompileManager.compileReport(designPayment);
		JasperPrint jasperPrintPayment = JasperFillManager.fillReport(jasperReportPayment, parameters,
				new JREmptyDataSource());

		// JasperExportManager.exportReportToPdfFile(jasperPrintPayment, "Recibo_" +
		// p.getId() + ".pdf");

		String pdfFilePath = "Recibo_" + p.getId() + ".pdf";
		JasperExportManager.exportReportToPdfFile(jasperPrintPayment, pdfFilePath);

		sendEmailWithAttachment(p.getStudent().getEmail(), "Recibo de Pago", "Adjunto encontrará el recibo de pago.",
				pdfFilePath);

	}

	public void sendEmailWithAttachment(String to, String subject, String messageText, String filePath)
			throws EmailException {

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("oscarmartinez@galileo.edu", "Nickelback20");
			}
		});

		// Enviar el correo electrónico con archivo adjunto
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("oscarmartinez@galileo.edu"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(messageText);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filePath);
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport.send(message);

			System.out.println("Correo enviado con éxito.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendRecipt(long id) throws Exception {
		Payment payment = paymentRepo.findById(id)
				.orElseThrow(() -> new Exception("Payment does not exist with id: " + id));

		generateRecipt(payment, "Pago realizado en dojo ken sei kai.");
	}

}
