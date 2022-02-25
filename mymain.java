package myReadPhoto2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.smartcardio.CardTerminals.State;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jmrtd.BACKey;
import org.jmrtd.PassportService;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.SODFile;
import org.jmrtd.lds.icao.COMFile;
import org.jmrtd.lds.icao.DG11File;
import org.jmrtd.lds.icao.DG12File;
import org.jmrtd.lds.icao.DG15File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.DG5File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import net.sf.scuba.smartcards.APDUListener;
import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;

public class mymain {

	public static void main(String[] args) throws CardException, CardServiceException, IOException, CertificateException {
		Security.addProvider(new BouncyCastleProvider());

		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list(State.CARD_PRESENT);
		if (terminals.size() == 0) {
			throw new RuntimeException("There is no terminal!");
		}
		CardTerminal terminal = terminals.get(0);
		if (!terminal.isCardPresent()) {
			throw new RuntimeException("There is no card present in this terminal!");
		}

		PassportService service = new PassportService(CardService.getInstance(terminal));
		
		
		service.open();
		service.sendSelectApplet(false);
		// code owner
		
	//	 APDUListener APDUListener = null;//=new APDUListener();

//service.addAPDUListener(APDUListener);

		
//service.addAPDUListener(l);
		/*
		 * String cardNumber = "11IC76084"; String dateOfExpiry = "220425"; String
		 * dateOfBirth = "930514"; //MONTH NUMBER WILL BE ACTUAL Month NUMBER -1
		 * service.doBAC(new BACKey(cardNumber, new Date(1993, 4, 14), new Date(2022, 3,
		 * 25)));//code owner
		 */

		// ilayda su

		String cardNumber =   "M00T21597";
		String dateOfExpiry = "260418";
		String dateOfBirth =  "920914";
		service.doBAC(new BACKey(cardNumber, new Date(1992, 8, 14), new Date(2026, 3, 18)));// ilayda su


		// Me
		/*
		  String cardNumber = "A05K54181"; 
		  String dateOfExpiry = "270918"; 
		  String dateOfBirth = "970708"; 
		  service.doBAC(new BACKey(cardNumber, new Date(1997,6, 8), new Date(2027, 8, 18)));//me
		 */
		
		  /*  //albakir
		  
		  String cardNumber = "A02G88739"; String dateOfExpiry = "270317"; String
		  dateOfBirth = "910403"; service.doBAC(new BACKey(cardNumber, new Date(1991,
		  3, 3), new Date(2027, 2, 17)));//albakir
		 */
		
		System.out.println("service.getATR():"+DatatypeConverter.printHexBinary(service.getATR()).toLowerCase());
		//service.addAPDUListener();
		//service.addPlainTextAPDUListener(null);
		
		
		InputStream	in0 = service.getInputStream(PassportService.EF_COM);
		COMFile com = (COMFile) LDSFileUtil.getLDSFile(PassportService.EF_COM, in0);
		System.out.println("\n\n\n***************Common File (Shows the Data Groups in the CARD)****************");
		System.out.println(com);
		in0.close();

		InputStream in1 = service.getInputStream(PassportService.EF_DG1);
		in1 = service.getInputStream(PassportService.EF_DG1);
		DG1File dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, in1);
		System.out.println("\n\n\n***********MRZ INFO(DG1)***********");
		System.out.println(dg1);
	    System.out.println("DG1 data Parsing...");
		System.out.println("DocumentNumber: " + dg1.getMRZInfo().getDocumentNumber());
		System.out.println("Gender: " + dg1.getMRZInfo().getGender());
		System.out.println("DateOfBirth: " + dg1.getMRZInfo().getDateOfBirth());
		System.out.println("DateOfExpiry: " + dg1.getMRZInfo().getDateOfExpiry());
		System.out.println("DocumentCode: " + dg1.getMRZInfo().getDocumentCode());
		System.out.println("IssuingState: " + dg1.getMRZInfo().getIssuingState());
		System.out.println("Nationality: " + dg1.getMRZInfo().getNationality());
		System.out.println("OptionalData1: " + dg1.getMRZInfo().getOptionalData1());
		System.out.println("OptionalData2: " + dg1.getMRZInfo().getOptionalData2());
		System.out.println("PersonalNumber: " + dg1.getMRZInfo().getPersonalNumber());
		System.out.println("PrimaryIdentifier: " + dg1.getMRZInfo().getPrimaryIdentifier());
		System.out.println("SecondaryIdentifier: " + dg1.getMRZInfo().getSecondaryIdentifier());

		in1.close();

		System.out.println("\n\n\n***********Photo(DG2)***********");
		try (InputStream in2 = service.getInputStream(PassportService.EF_DG2)) {
			DG2File dg2 = new DG2File(in2);
			List<FaceImageInfo> allFaceImageInfos = new ArrayList<FaceImageInfo>();
			List<FaceInfo> faceInfos = dg2.getFaceInfos();
			for (FaceInfo faceInfo : faceInfos) {
				allFaceImageInfos.addAll(faceInfo.getFaceImageInfos());
			}

			InputStream imageInputStream = allFaceImageInfos.get(0).getImageInputStream();
			DataInputStream dataInputStream = new DataInputStream(imageInputStream);
			byte[] imageBytes = new byte[allFaceImageInfos.get(0).getImageLength()];
			dataInputStream.readFully(imageBytes);
			imageInputStream.close();
			in2.close();

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
					"cd \"C:\\eclipse-workspace\\myReadPhoto2\" && \"img.jpg");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);

			}

			FileOutputStream out = new FileOutputStream("img.jpg");
			out.write(imageBytes);
			System.out.println("Photo Info has writed look at img.jpg");

		}

		InputStream in11 = service.getInputStream(PassportService.EF_DG11);
		in11 = service.getInputStream(PassportService.EF_DG11);
		DG11File dg11 = (DG11File) LDSFileUtil.getLDSFile(PassportService.EF_DG11, in11);
		System.out.println("\n\n\n*******additional personal detail.(DG11)**************");
		System.out.println(dg11);
		in11.close();

		InputStream in12 = service.getInputStream(PassportService.EF_DG12);
		in12 = service.getInputStream(PassportService.EF_DG12);
		DG12File dg12 = (DG12File) LDSFileUtil.getLDSFile(PassportService.EF_DG12, in12);
		System.out.println("\n\n\n*******additional document details.(DG12)************");
		System.out.println(dg12);
		in12.close();

		InputStream in15 = service.getInputStream(PassportService.EF_DG15);
		in15 = service.getInputStream(PassportService.EF_DG15);
		DG15File dg15 = (DG15File) LDSFileUtil.getLDSFile(PassportService.EF_DG15, in15);
		System.out.println("\n\n\n*******Active Authentication Public Key.(DG15)********");
		System.out.println(dg15);
		in15.close();

		
		
		
		InputStream efSod = service.getInputStream(PassportService.EF_SOD);
		efSod = service.getInputStream(PassportService.EF_SOD);
		SODFile efSodFile = (SODFile) LDSFileUtil.getLDSFile(PassportService.EF_SOD, efSod);
		System.out.println("\n\n\n*******SOD********");
		System.out.println(efSodFile.toString());
		  X509Certificate cert =efSodFile.getDocSigningCertificate();
	
		  
		
		  
		  
		  
		  
		System.out.println("**************Certificate info....*************\n"+efSodFile.getDocSigningCertificate());
		System.out.println( "***************efSodFile.getIssuerX500Principal()\n***************"+efSodFile.getIssuerX500Principal());
		
		
		System.out.println("Der Encoded Certificate......");
		BASE64Encoder encoder = new BASE64Encoder();		
		  System.out.println(X509Factory.BEGIN_CERT);
		  encoder.encodeBuffer(cert.getEncoded(), System.out);
		  System.out.println(X509Factory.END_CERT);
		  
		  
		  
		efSod.close();
	
		
		
	}

}
