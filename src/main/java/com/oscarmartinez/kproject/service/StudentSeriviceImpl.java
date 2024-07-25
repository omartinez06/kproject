package com.oscarmartinez.kproject.service;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.ChargeHistory;
import com.oscarmartinez.kproject.entity.ChargeMovement;
import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IChargeHistoryRepository;
import com.oscarmartinez.kproject.repository.IChargeMovementRepository;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IKyuRepository;
import com.oscarmartinez.kproject.repository.IScheduleRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.AccountStatusDTO;
import com.oscarmartinez.kproject.resource.AccountStatusReportDTO;
import com.oscarmartinez.kproject.resource.AccountStatusReportInformation;
import com.oscarmartinez.kproject.resource.StudentDTO;
import com.oscarmartinez.kproject.resource.StudentStatusDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class StudentSeriviceImpl implements IStudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentSeriviceImpl.class);

	@Autowired
	private IStudentRepository studentRepository;

	@Autowired
	private IScheduleRepository scheduleRepository;

	@Autowired
	private IKyuRepository kyuRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IGymRepository gymRepository;

	@Autowired
	private IChargeMovementRepository chargeMovementRepository;

	@Autowired
	private IChargeHistoryRepository chargeHistoryRepository;

	@Override
	public List<Student> listStudents() {
		return studentRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	public String getLicenseId() {
		Random r = new Random();
		String license = "";
		boolean exit = true;

		while (exit) {
			StringBuffer sb = new StringBuffer();
			while (sb.length() < 3) {
				// Limitar el valor aleatorio a 16 para obtener solo dígitos hexadecimales
				sb.append(Integer.toHexString(r.nextInt(16)));
			}

			int year = Calendar.getInstance().get(Calendar.YEAR);
			// Convertir la cadena aleatoria a mayúsculas
			license = year + sb.toString().toUpperCase();

			// Verificar si existe un estudiante con el mismo número de licencia
			Student student = studentRepository.findByLicense(license);
			if (student == null) {
				// Salir del bucle si no existe ningún estudiante con ese número de licencia
				exit = false;
			}
		}

		return license;
	}

	@Override
	public void addStudent(StudentDTO student) throws Exception {
		Student newStudent = new Student();
		newStudent.setName(student.getName());
		newStudent.setLastName(student.getLastName());
		newStudent.setEmail(student.getEmail());
		newStudent.setBirth(student.getBirth());
		newStudent.setBloodType(student.getBloodType());
		newStudent.setTutor(student.getTutor());
		newStudent.setQuota(Integer.parseInt(student.getQuota()));
		newStudent.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		newStudent.setLicense(getLicenseId());
		Schedule schedule = scheduleRepository.findById(student.getSchedule())
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + student.getSchedule()));
		newStudent.setSchedule(schedule);
		Kyu kyu = kyuRepository.findById(student.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + student.getKyuId()));
		newStudent.setKyu(kyu);
		newStudent.setApplyLatePayment(student.isApplyLatePayment());
		newStudent.setAddedDate(new Date());
		newStudent.setInscription(Integer.parseInt(student.getInscription()));
		newStudent.setStatus(Student.Status.PENDING);

		Student s = studentRepository.save(newStudent);

		if (s.getQuota() > 0)
			createCharge(s, ChargeMovement.ChargeType.QUOTE, Integer.parseInt(student.getInscription()));
	}

	public void createCharge(Student student, ChargeMovement.ChargeType type, int inscription) {
		// Inscripcion
		if (inscription > 0) {
			ChargeMovement chargeDetail = new ChargeMovement();
			chargeDetail.setAddedDate(new Date());
			chargeDetail.setAmount(inscription);
			chargeDetail.setDescription("Inscripcion Ken Sei Kai");
			chargeDetail.setType(ChargeMovement.ChargeType.ADDITIONAL);
			chargeDetail.setGym(student.getGym());
			chargeDetail.setStudent(student);

			chargeMovementRepository.save(chargeDetail);
			createChargeHistory(chargeDetail);
		}
		// Mensualidad
		int monthQuota = calculateProratedQuota(student);

		if (monthQuota > 0) {
			ChargeMovement chargeDetail = new ChargeMovement();
			chargeDetail.setAddedDate(new Date());
			chargeDetail.setAmount(student.getQuota());

			// Formateador para obtener el mes en formato largo y el año
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"));

			// Formatear la fecha actual
			String formattedDate = LocalDate.now().format(formatter);
			chargeDetail.setDescription("Cuota Mes " + formattedDate);
			chargeDetail.setType(type);
			chargeDetail.setGym(student.getGym());
			chargeDetail.setStudent(student);

			chargeMovementRepository.save(chargeDetail);
			createChargeHistory(chargeDetail);
		}
	}

	public int calculateProratedQuota(Student student) {
		LocalDateTime today = LocalDateTime.now();
		YearMonth currentYearMonth = YearMonth.from(today);
		int totalDaysInMonth = currentYearMonth.lengthOfMonth();
		int daysRemaining = totalDaysInMonth - today.getDayOfMonth() + 1;

		int dailyRate = student.getQuota() / totalDaysInMonth;
		logger.debug("{} - The prorate quota is: {}", "calculateProratedQuota()", dailyRate);
		if (dailyRate > 100)
			return dailyRate * daysRemaining;

		return 0;
	}

	public void createChargeHistory(ChargeMovement charge) {
		ChargeHistory chargeHistory = new ChargeHistory();
		chargeHistory.setAddedDate(new Date());
		chargeHistory.setAmount(charge.getAmount());
		chargeHistory.setDescription(charge.getDescription());
		chargeHistory.setType(ChargeHistory.ChargeTypeHistory.CHARGE);
		chargeHistory.setGym(charge.getGym());
		chargeHistory.setStudent(charge.getStudent());

		chargeHistoryRepository.save(chargeHistory);
	}

	@Override
	public ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		student.setName(studentDetail.getName());
		student.setLastName(studentDetail.getLastName());
		student.setEmail(studentDetail.getEmail());
		student.setBirth(studentDetail.getBirth());
		student.setBloodType(studentDetail.getBloodType());
		student.setTutor(studentDetail.getTutor());
		student.setQuota(Integer.parseInt(studentDetail.getQuota()));
		Schedule schedule = scheduleRepository.findById(studentDetail.getSchedule())
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + studentDetail.getSchedule()));
		student.setSchedule(schedule);
		Kyu kyu = kyuRepository.findById(studentDetail.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + studentDetail.getKyuId()));
		student.setKyu(kyu);
		student.setApplyLatePayment(studentDetail.isApplyLatePayment());
		student.setInscription(Integer.parseInt(studentDetail.getInscription()));

		studentRepository.save(student);

		return ResponseEntity.ok(student);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteStudent(long id) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));

		studentRepository.delete(student);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Student> getStudentById(long id) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		return ResponseEntity.ok(student);
	}

	@Override
	public ResponseEntity<Long> getStudentsQuantity() throws Exception {
		List<Student> students = studentRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		long count = students.size();
		log.debug("Estudiantes: " + count);
		return ResponseEntity.ok(count);
	}

	@Override
	public Student getStudentByLicense(String license) throws Exception {
		return studentRepository.findByLicense(license);
	}

	@Override
	public ResponseEntity<HttpStatus> generateAccountStatus(AccountStatusDTO accountStatusDetail) throws Exception {
		generateReportAccoutStatus(accountStatusDetail);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public void generateReportAccoutStatus(AccountStatusDTO accountStatusInfo) throws Exception {

		Student student = studentRepository.findById(accountStatusInfo.getId())
				.orElseThrow(() -> new Exception("Student not exist with id: " + accountStatusInfo.getId()));

		Collection<AccountStatusReportDTO> collectionAccountStatus = getReportAccountStatus(accountStatusInfo);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("validFrom", new SimpleDateFormat("dd/MM/yyyy").format(accountStatusInfo.getFrom()));
		parameters.put("validUntil", new SimpleDateFormat("dd/MM/yyyy").format(accountStatusInfo.getTo()));
		parameters.put("name", student.getName() + " " + student.getLastName());

		InputStream jrxmlInputStreamAccountStatus = new ClassPathResource("AccountStatus.jrxml").getInputStream();
		JasperDesign designAccountStatus = JRXmlLoader.load(jrxmlInputStreamAccountStatus);
		JasperReport jasperReportAccountStatus = JasperCompileManager.compileReport(designAccountStatus);
		JasperPrint jasperPrintAccountStatus = JasperFillManager.fillReport(jasperReportAccountStatus, parameters,
				new JRBeanCollectionDataSource(collectionAccountStatus));

		/*
		 * JasperExportManager.exportReportToPdfFile(jasperPrintAccountStatus,
		 * "AccountStatus_" + new
		 * SimpleDateFormat("ddMMyyyy").format(accountStatusInfo.getFrom()) + "_" + new
		 * SimpleDateFormat("ddMMyyyy").format(accountStatusInfo.getTo()) + ".pdf");
		 */

		String pdfFilePath = "AccountStatus_" + new SimpleDateFormat("ddMMyyyy").format(accountStatusInfo.getFrom())
				+ "_" + new SimpleDateFormat("ddMMyyyy").format(accountStatusInfo.getTo()) + ".pdf";
		JasperExportManager.exportReportToPdfFile(jasperPrintAccountStatus, pdfFilePath);

		sendEmailWithAttachment(student.getEmail(), "Estado De Cuenta",
				"Adjunto encontrará su estado de cuenta correspondiente del "
						+ new SimpleDateFormat("dd/MM/yyyy").format(accountStatusInfo.getFrom()) + " al "
						+ new SimpleDateFormat("dd/MM/yyyy").format(accountStatusInfo.getTo()) + ".",
				pdfFilePath);

	}

	public Collection<AccountStatusReportDTO> getReportAccountStatus(AccountStatusDTO accountStatusInfo)
			throws Exception {
		Student student = studentRepository.findById(accountStatusInfo.getId())
				.orElseThrow(() -> new Exception("Student not exist with id: " + accountStatusInfo.getId()));

		List<ChargeHistory> chargeHistoryList = chargeHistoryRepository
				.findByAddedDateBetweenAndStudentOrderByAddedDateAsc(accountStatusInfo.getFrom(),
						accountStatusInfo.getTo(), student);

		List<AccountStatusReportInformation> informationList = new ArrayList<>();
		for (ChargeHistory history : chargeHistoryList) {
			AccountStatusReportInformation information = new AccountStatusReportInformation();
			information.setDate(new SimpleDateFormat("dd/MM/yyyy").format(history.getAddedDate()));
			information.setDescription(history.getDescription());
			if (history.getType().equals(ChargeHistory.ChargeTypeHistory.CHARGE)) {
				information.setCharge("Q." + history.getAmount());
			} else {
				information.setPayment("Q." + history.getAmount());
			}
			informationList.add(information);
		}

		AccountStatusReportDTO dto = new AccountStatusReportDTO();
		System.out.println("Tamanio de lista: " + informationList.size());
		dto.setInformation(informationList);

		Collection<AccountStatusReportDTO> collectionResponse = Collections.singletonList(dto);

		return collectionResponse;
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
	public ResponseEntity<StudentStatusDTO> getPendingBalance(String license) throws Exception {
		Student student = studentRepository.findByLicense(license);

		if (student != null) {
			List<ChargeMovement> movements = chargeMovementRepository.findByStudent(student);

			// Utiliza AtomicInteger para mantener un contador mutable
			AtomicInteger totalValue = new AtomicInteger(0);

			// Recorre la lista y actualiza el totalValue
			movements.forEach(movement -> {
				totalValue.addAndGet(movement.getAmount());
			});

			StudentStatusDTO status = new StudentStatusDTO();
			status.setPendingBalance(totalValue.get());

			switch (student.getStatus()) {
			case UP_TO_DATE:
				status.setStatus("AL DIA");
				break;
			case PENDING:
				status.setStatus("PENDIENTE DE PAGO");
				break;
			case DELIQUENT:
				status.setStatus("EN MORA");
				break;
			case LOCKED:
				status.setStatus("BLOQUEADO");
				break;
			default:
				status.setStatus(null);
				break;
			}

			return ResponseEntity.ok(status);
		}

		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<byte[]> getTelegramAccountStatus(String license) throws Exception {
		Student student = studentRepository.findByLicense(license);

		if (student != null) {
			LocalDate today = LocalDate.now();
			LocalDate threeMonthsAgo = today.minusMonths(3);
			Collection<AccountStatusReportDTO> collectionAccountStatus = getTelegramReportAccountStatus(student);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("validFrom", threeMonthsAgo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			parameters.put("validUntil", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			parameters.put("name", student.getName() + " " + student.getLastName());

			InputStream jrxmlInputStreamAccountStatus = new ClassPathResource("AccountStatus.jrxml").getInputStream();
			JasperDesign designAccountStatus = JRXmlLoader.load(jrxmlInputStreamAccountStatus);
			JasperReport jasperReportAccountStatus = JasperCompileManager.compileReport(designAccountStatus);
			JasperPrint jasperPrintAccountStatus = JasperFillManager.fillReport(jasperReportAccountStatus, parameters,
					new JRBeanCollectionDataSource(collectionAccountStatus));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrintAccountStatus, byteArrayOutputStream);

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AccountStatus.pdf");
			headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

			return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	public Collection<AccountStatusReportDTO> getTelegramReportAccountStatus(Student student) throws Exception {

		LocalDate today = LocalDate.now();
		LocalDate threeMonthsAgo = today.minusMonths(3);

		Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date threeMonthsAgoDate = Date.from(threeMonthsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

		List<ChargeHistory> chargeHistoryList = chargeHistoryRepository
				.findByAddedDateBetweenAndStudentOrderByAddedDateAsc(threeMonthsAgoDate, todayDate, student);

		List<AccountStatusReportInformation> informationList = new ArrayList<>();
		for (ChargeHistory history : chargeHistoryList) {
			AccountStatusReportInformation information = new AccountStatusReportInformation();
			information.setDate(new SimpleDateFormat("dd/MM/yyyy").format(history.getAddedDate()));
			information.setDescription(history.getDescription());
			if (history.getType().equals(ChargeHistory.ChargeTypeHistory.CHARGE)) {
				information.setCharge("Q." + history.getAmount());
			} else {
				information.setPayment("Q." + history.getAmount());
			}
			informationList.add(information);
		}

		AccountStatusReportDTO dto = new AccountStatusReportDTO();
		System.out.println("Tamanio de lista: " + informationList.size());
		dto.setInformation(informationList);

		Collection<AccountStatusReportDTO> collectionResponse = Collections.singletonList(dto);

		return collectionResponse;
	}

}
