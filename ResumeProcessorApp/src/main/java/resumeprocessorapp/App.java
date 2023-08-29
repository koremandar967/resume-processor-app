package resumeprocessorapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONObject;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import resumeprocessorapp.model.Candidate;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private AmazonDynamoDB amazonDynamoDB;

	private String DYNAMODB_TABLE_NAME = "Candidate";
	private Regions REGION = Regions.AP_SOUTH_1;
	private String UPLOAD_RESUME_URI = System.getenv("UPLOAD_RESUME_URI");
	private String FETCH_RESUME_CONTENT_URI = System.getenv("FETCH_RESUME_CONTENT_URI");

	public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		String uriPath = input.getPath();

		System.out.println("URI Path: " + input.getPath());

		initDynamoDbClient();

		if (uriPath.equals(UPLOAD_RESUME_URI)) {
			response = uploadResume(input);
		} else if (uriPath.equals(FETCH_RESUME_CONTENT_URI)) {
			response = fetchData();
		}

		return response;
	}

	private Candidate readPDF(InputStream pdfInputStream) {

		Candidate candidateInfo = null;

		try {

			PDDocument document;

			document = PDDocument.load(pdfInputStream);
			PDFTextStripper stripper = new PDFTextStripper();
			String pdfContent = stripper.getText(document);

			Pattern namePattern = Pattern.compile("^(.*?)\\s+\\(.*?\\)", Pattern.DOTALL);
			Matcher nameMatcher = namePattern.matcher(pdfContent);

			candidateInfo = new Candidate();

			Random random = new Random();
			int randomNumber = random.nextInt(1000);

			candidateInfo.setCandidateId(randomNumber);

			if (nameMatcher.find()) {
				String name = nameMatcher.group(1).trim();
				candidateInfo.setName(name);
			}

			Pattern experiencePattern = Pattern.compile("Experience\\s+(.*?)\\s+Skills", Pattern.DOTALL);
			Matcher experienceMatcher = experiencePattern.matcher(pdfContent);

			if (experienceMatcher.find()) {
				String experience = experienceMatcher.group(1).trim();
				candidateInfo.setExperience(experience);
			}

			Pattern frontendPattern = Pattern.compile("Front End Skills:(.*?)Back End Skills:", Pattern.DOTALL);
			Matcher frontendMatcher = frontendPattern.matcher(pdfContent);
			if (frontendMatcher.find()) {
				String frontendSkills = frontendMatcher.group(1).trim();
				candidateInfo.setFrontendSkills(frontendSkills);
			}

			Pattern backendPattern = Pattern.compile("Back End Skills:(.*?)Database Skills:", Pattern.DOTALL);
			Matcher backendMatcher = backendPattern.matcher(pdfContent);
			if (backendMatcher.find()) {
				String backendSkills = backendMatcher.group(1).trim();
				candidateInfo.setBackendSkills(backendSkills);
			}

			Pattern databasePattern = Pattern.compile("Database SkEills:(.*?)Frameworks:", Pattern.DOTALL);
			Matcher databaseMatcher = databasePattern.matcher(pdfContent);
			if (databaseMatcher.find()) {
				String databaseSkills = databaseMatcher.group(1).trim();
				candidateInfo.setDatabaseSkills(databaseSkills);
			}

			Pattern frameworksPattern = Pattern.compile("Frameworks:(.*?)Education", Pattern.DOTALL);
			Matcher frameworksMatcher = frameworksPattern.matcher(pdfContent);
			if (frameworksMatcher.find()) {
				String frameworksSkills = frameworksMatcher.group(1).trim();
				candidateInfo.setFrameworks(frameworksSkills);
			}

			Pattern educationPattern = Pattern.compile("Education\\s+(.*?)Relevant Project Experience", Pattern.DOTALL);
			Matcher educationMatcher = educationPattern.matcher(pdfContent);

			if (educationMatcher.find()) {
				String educationSkills = educationMatcher.group(1).trim();
				candidateInfo.setEducation(educationSkills);
			}

			System.out.println("candidateInfo " + candidateInfo.toString());

			document.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return candidateInfo;
	}

	private void initDynamoDbClient() {
		this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
	}

	private void persistData(Candidate candidateInfo) {

		if (candidateInfo != null) {

			Map<String, AttributeValue> attributesMap = new HashMap<>();

			if (candidateInfo.getCandidateId() != null) {
				attributesMap.put("candidateId",
						new AttributeValue().withN(String.valueOf(candidateInfo.getCandidateId())));
			}
			if (candidateInfo.getName() != null && !candidateInfo.getName().isEmpty()) {
				attributesMap.put("name", new AttributeValue(candidateInfo.getName()));
			}
			if (candidateInfo.getEmail() != null && !candidateInfo.getEmail().isEmpty()) {
				attributesMap.put("email", new AttributeValue(candidateInfo.getEmail()));
			}
			if (candidateInfo.getExperience() != null && !candidateInfo.getExperience().isEmpty()) {
				attributesMap.put("experience", new AttributeValue(candidateInfo.getExperience()));
			}
			if (candidateInfo.getDesignation() != null && !candidateInfo.getDesignation().isEmpty()) {
				attributesMap.put("designation", new AttributeValue(candidateInfo.getDesignation()));
			}
			if (candidateInfo.getFrontendSkills() != null && !candidateInfo.getFrontendSkills().isEmpty()) {
				attributesMap.put("frontendSkills", new AttributeValue(candidateInfo.getFrontendSkills()));
			}
			if (candidateInfo.getBackendSkills() != null && !candidateInfo.getBackendSkills().isEmpty()) {
				attributesMap.put("backendSkills", new AttributeValue(candidateInfo.getBackendSkills()));
			}
			if (candidateInfo.getDatabaseSkills() != null && !candidateInfo.getDatabaseSkills().isEmpty()) {
				attributesMap.put("databaseSkills", new AttributeValue(candidateInfo.getDatabaseSkills()));
			}
			if (candidateInfo.getFrameworks() != null && !candidateInfo.getFrameworks().isEmpty()) {
				attributesMap.put("frameworks", new AttributeValue(candidateInfo.getFrameworks()));
			}
			if (candidateInfo.getEducation() != null && !candidateInfo.getEducation().isEmpty()) {
				attributesMap.put("education", new AttributeValue(candidateInfo.getEducation()));
			}

			amazonDynamoDB.putItem(DYNAMODB_TABLE_NAME, attributesMap);
		}

	}

	private APIGatewayProxyResponseEvent fetchData() {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		try {

			ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE_NAME);

			ScanResult scanResult = amazonDynamoDB.scan(scanRequest);

			List<Candidate> candidateInfoList = new ArrayList<>();

			for (Map<String, AttributeValue> item : scanResult.getItems()) {
				// Process each item in the result

				Candidate candidateInfo = new Candidate();

				for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
					String attributeName = entry.getKey();
					AttributeValue attributeValue = entry.getValue();
					if (attributeName.equals("candidateId")) {
						candidateInfo.setCandidateId(Integer.parseInt(attributeValue.getN()));
					} else if (attributeName.equals("name")) {
						candidateInfo.setName(attributeValue.getS());
					} else if (attributeName.equals("email")) {
						candidateInfo.setEmail(attributeValue.getS());
					} else if (attributeName.equals("experience")) {
						candidateInfo.setExperience(attributeValue.getS());
					} else if (attributeName.equals("designation")) {
						candidateInfo.setDesignation(attributeValue.getS());
					} else if (attributeName.equals("frontendSkills")) {
						candidateInfo.setFrontendSkills(attributeValue.getS());
					} else if (attributeName.equals("databaseSkills")) {
						candidateInfo.setDatabaseSkills(attributeValue.getS());
					} else if (attributeName.equals("frameworks")) {
						candidateInfo.setFrameworks(attributeValue.getS());
					} else if (attributeName.equals("education")) {
						candidateInfo.setEducation(attributeValue.getS());
					}
				}

				candidateInfoList.add(candidateInfo);
			}

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(candidateInfoList);

			response.setStatusCode(200);
			Map<String, String> responseBody = new HashMap<String, String>();
			responseBody.put("data", jsonString);
			response.setBody(jsonString);

			Map<String, String> headerMap = new HashMap<String, String>();

			headerMap.put("Access-Control-Allow-Origin", "*");
			headerMap.put("Access-Control-Allow-Credentials", "true");

			response.setHeaders(headerMap);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return response.withBody("{}").withStatusCode(500);
		}

		return response;

	}

	private APIGatewayProxyResponseEvent uploadResume(APIGatewayProxyRequestEvent event) {

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		// Set up contentType
		String contentType = "";
		String fileContentType = "";

		try {
			byte[] bI = Base64.decodeBase64(event.getBody().getBytes());

			// Get the content-type header
			Map<String, String> hps = event.getHeaders();

			iterateUsingEntrySet(hps);

			if (hps != null) {
				contentType = hps.get("Content-Type");
			}

			String[] boundaryArray = contentType.split("=");

			// Transform the boundary to a byte array
			byte[] boundary = boundaryArray[1].getBytes();

			// Create a ByteArrayInputStream
			ByteArrayInputStream content = new ByteArrayInputStream(bI);

			MultipartStream multipartStream = new MultipartStream(content, boundary, bI.length, null);

			// Create a ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			// Find first boundary in the MultipartStream
			boolean nextPart = multipartStream.skipPreamble();

			// Loop through each segment
			while (nextPart) {
				String header = multipartStream.readHeaders();
				fileContentType = getFileContentType(header);
				// Write out the file to our ByteArrayOutputStream
				multipartStream.readBodyData(out);

				// Get the next part, if any
				nextPart = multipartStream.readBoundary();

			}

			// Prepare an InputStream from the ByteArrayOutputStream
			InputStream fis = new ByteArrayInputStream(out.toByteArray());

			Candidate candidateInfo = readPDF(fis);
			persistData(candidateInfo);

			response.setStatusCode(200);
			Map<String, String> responseBody = new HashMap<String, String>();
			responseBody.put("message", "Resume file processed successfully");
			String responseBodyString = new JSONObject(responseBody).toJSONString();
			response.setBody(responseBodyString);

			Map<String, String> headerMap = new HashMap<String, String>();

			headerMap.put("Access-Control-Allow-Origin", "*");
			headerMap.put("Access-Control-Allow-Credentials", "true");

			response.setHeaders(headerMap);

		} catch (IOException e) {

			System.out.println(e.getMessage());
			return response.withBody("{}").withStatusCode(500);
		}

		return response;
	}

	public void iterateUsingEntrySet(Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

	public static String getFileContentType(String headers) {

		String contentType = "";

		if (headers != null && !headers.isEmpty()) {

			String[] headersArray = headers.split(":");

			if (headersArray != null && headersArray.length > 0) {

				for (int i = 0; i < headersArray.length; i++) {
					if (headersArray[i].equals("Content-Type")) {
						contentType = headersArray[i + 1].trim();
					}
				}
			}
		}
		return contentType;
	}

}
