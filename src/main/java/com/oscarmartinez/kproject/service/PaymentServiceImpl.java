package com.oscarmartinez.kproject.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

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

	public void validatePaymentAmount(Payment p) throws JRException, IOException {

		createChargeHistory(p);

		List<ChargeMovement> chargesDetails = chargeMovementRepository.findByStudent(p.getStudent());

		Optional<ChargeMovement> optionalQuote = chargesDetails.stream()
				.filter(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.QUOTE).findFirst();

		if (optionalQuote.isPresent()) {

			ChargeMovement quote = optionalQuote.get();
			if (p.getValue() < quote.getAmount()) {
				int newAmount = quote.getAmount() - p.getValue();
				quote.setAmount(newAmount);
				chargeMovementRepository.save(quote);
			} else if (p.getValue() == quote.getAmount()) {
				chargeMovementRepository.delete(quote);
			} else {
				chargeMovementRepository.delete(quote);
				boolean containLateness = chargesDetails.stream()
						.anyMatch(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.DELINQUENCY);

				int newAmountAvailable = p.getValue() - quote.getAmount();
				if (containLateness) {
					Optional<ChargeMovement> optionalLateness = chargesDetails.stream()
							.filter(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.DELINQUENCY)
							.findFirst();
					if (optionalLateness.isPresent()) {
						ChargeMovement lateness = optionalLateness.get();
						if (newAmountAvailable < lateness.getAmount()) {
							int newLateness = lateness.getAmount() - newAmountAvailable;
							lateness.setAmount(newLateness);
							chargeMovementRepository.save(lateness);
						} else {
							chargeMovementRepository.delete(lateness);
						}
					}
				}
			}
		} else {
			boolean containLateness = chargesDetails.stream()
					.anyMatch(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.DELINQUENCY);
			if (containLateness) {
				Optional<ChargeMovement> optionalLateness = chargesDetails.stream()
						.filter(chargeDetail -> chargeDetail.getType() == ChargeMovement.ChargeType.DELINQUENCY)
						.findFirst();
				if (optionalLateness.isPresent()) {
					ChargeMovement lateness = optionalLateness.get();
					if (p.getValue() < lateness.getAmount()) {
						int newLateness = lateness.getAmount() - p.getValue();
						lateness.setAmount(newLateness);
						chargeMovementRepository.save(lateness);
					} else {
						chargeMovementRepository.delete(lateness);
					}
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

	public void createChargeHistory(Payment p) throws JRException, IOException {
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

	public void generateRecipt(Payment p, String description) throws JRException, IOException {
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("dateRecipt", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		parameters.put("number", Long.toString(p.getId()));
		parameters.put("client", p.getStudent().getTutor());
		parameters.put("description", description);
		parameters.put("reciptValue", "Q" + p.getValue() + ".00");
		
		InputStream jrxmlInputStreamPayment = new ClassPathResource("Recipt.jrxml").getInputStream();
		JasperDesign designPayment = JRXmlLoader.load(jrxmlInputStreamPayment);
		JasperReport jasperReportPayment = JasperCompileManager.compileReport(designPayment);
		JasperPrint jasperPrintPayment = JasperFillManager.fillReport(jasperReportPayment, parameters, new JREmptyDataSource());
		
		JasperExportManager.exportReportToPdfFile(jasperPrintPayment, "Recibo_"+p.getId()+".pdf");

	}

}
