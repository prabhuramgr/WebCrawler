package application;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WebExtract {

	public ArrayList<String> webWords = new ArrayList<String>();
	ArrayList<String> wordsNotInJSON = new ArrayList<String>();
	ArrayList<String> jsonElements;
	String directoryLocation;
	static ArrayList<String> negationWords;
	ArrayList<String> correctedWebWords;
	ArrayList<String> alProcessed;
	static ArrayList<String> arylst;

	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		osValidator();
		// getDropDown();
		// TODO Auto-generated method stub
		// we.getWebContent4();
		//getDataFromXML();
	}

	public void smartLinkParsing(String location, String language) {

		jsonElements = new ArrayList<String>();

		String target_dir = location;
		File dir = new File(target_dir);
		File[] files = dir.listFiles();
		System.out.println(files.length);
		for (File f : files) {
			if (f.isFile()) {
				try {
					System.out.println(f.getName());
					Scanner s = new Scanner(f);
					String str = "";
					while (s.hasNextLine()) {
						str += s.nextLine();
					}

					String str_new = str.replace("\"", "");

					String arr[] = null;
					if (str_new.contains(":")) {
						arr = str_new.split(":");
					}
					for (int i = 0; i < arr.length; i++) {
						String strArray[] = arr[i].split(",");
						// System.out.println(strArray[0]);
						String[] indwords = strArray[0].toLowerCase().trim().split("\\s");
						jsonElements.addAll(Arrays.asList(indwords));
					}
					
					
					

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
		try {
			getDataFromXML(language);
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonElements.addAll(arylst);
	}

	public void removeNegationWords() {

		correctedWebWords = new ArrayList(alProcessed);

		for (int i = 0; i < alProcessed.size(); i++) {
			for (int j = 0; j < negationWords.size(); j++) {
				if (alProcessed.get(i).contains(negationWords.get(j))) {
					correctedWebWords.remove(alProcessed.get(i));
				}
			}
		}
	}

	public static void osValidator() {

		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			System.setProperty("webdriver.chrome.driver", "BrowserDriver/chromedriver");
		} else {
			System.setProperty("webdriver.chrome.driver", "BrowserDriver/chromedriver.exe");
		}
	}

	public static void getDropDown(String url) {
		String make = null;
		String location = null;
		WebDriver driver = new ChromeDriver();
		if (url.contains(".de")) {
			driver.navigate().to("https://g-mmember-qa1.copart.de/finder");
			make = driver.findElement(By.xpath("//div[contains(text(),'Her')]/following-sibling::div/select")).getText()
					.replaceAll("[0-9]", "");
			location = driver.findElement(By.xpath("//div[contains(text(),'Sta')]/following-sibling::div/select"))
					.getText().replaceAll("[0-9]", "");
		}
		if (url.contains(".es")) {
			driver.navigate().to("https://g-mmember-qa1.copart.es/finder");
			make = driver.findElement(By.xpath("//div[contains(text(),'Mar')]/following-sibling::div/select")).getText()
					.replaceAll("[0-9]", "");
			location = driver.findElement(By.xpath("//div[contains(text(),'Ubi')]/following-sibling::div/select"))
					.getText().replaceAll("[0-9]", "");
		}
		if (url.contains(".in")) {
			driver.navigate().to("https://g-mmember-qa1.copart.in/finder");
			make = driver.findElement(By.xpath("//div[contains(text(),'Make')]/following-sibling::div/select"))
					.getText().replaceAll("[0-9]", "");
			location = driver.findElement(By.xpath("//div[contains(text(),'Location')]/following-sibling::div/select"))
					.getText().replaceAll("[0-9]", "");
		}

		System.out.println(make);
		System.out.println(location);
		String textContents = make.replace("\n", ",").toLowerCase();
		String locationContents = location.replace("\n", ",").toLowerCase();
		;

		String[] negationList = textContents.split(",");
		String[] locationNegation = locationContents.split(",");
		negationWords = new ArrayList<String>(Arrays.asList(negationList));
		negationWords.addAll(Arrays.asList(locationNegation));

		System.out.println(negationWords);
		// removing jegiche
		negationWords.remove(0);

		driver.close();

	}

	public static void getDataFromXML(String language) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		ArrayList<String> xmlElements = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//data//value");
		String target_dir = "";
		File dir = null;
		if(language.equals("தமிழ்")){
		dir = new File("DoNotTouch/Tamil");}
		if(language.equals("हिंदी")){
		dir = new File("DoNotTouch/Hindi");}

		
		File[] files = dir.listFiles();
		
		for (File file : files) {
			System.out.println(file.getName());
			Document doc = builder.parse(file);
			NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			if (file.isFile()) {
				for (int i = 0; i < list.getLength(); i++) {
					doc = builder.parse(file);
					Node node = list.item(i);
					xmlElements.add(node.getTextContent());
					xmlElements.add(" ");
				}

			}

		}

		// words present in xml
		System.out.println("******************************\nContent from XML\n******************************");
		// System.out.println(xmlElements);

		String listString = "";

		for (String s : xmlElements) {
			listString += s;
		}
		// System.out.println("******************************\nContent\n******************************");
		// System.out.println(listString);
		String listString2 = listString.replaceAll("[<>()?:!.\";=*#{}/।]+", ",");
		String listString3 = listString2.replaceAll("[0-9]", "");
		String temp3 = listString3.replaceAll(" ", ",");
		String listString4 = temp3.replaceAll("^ +| +$|( )+", "");
		String listString5 = listString4.toLowerCase().trim().replaceAll("\\s", "");
		String[] strlst = listString5.split(",");
		arylst = new ArrayList<String>(Arrays.asList(strlst));
		//System.out.println(arylst);
	}

	public void changeLanguage(WebDriver driver,String url, String language) throws InterruptedException {
		driver.navigate().to(url);
		driver.findElement(By.xpath("html/body/header/div/div/p[2]/span/a")).click();
		Thread.sleep(2000);
		Select currdropdown = new Select(driver.findElement(By.xpath(".//*[@id='lang']")));
		currdropdown.selectByVisibleText(language);
		// தமிழ்
		driver.findElement(By.xpath(".//*[@id='confirmlang']")).click();

	}

	public String getWebContent(String url, String dirLocation, String userName, String password, String language)
			throws InterruptedException {

		directoryLocation = dirLocation;

		WebDriver driver = new ChromeDriver();
		driver.navigate().to(url);
		if (driver.getCurrentUrl().contains("/member/signIn") && !url.contains("/member/signIn")) {
			login(driver, userName, password, url);
		}

		if (url.contains("g-member")) {
			Thread.sleep(4000);
			changeLanguage(driver,url, language);
		}
		Thread.sleep(4000);
		if(!driver.getCurrentUrl().equals("url")){
		driver.navigate().to(url);}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// driver.navigate().refresh();
		// driver.navigate().refresh();

		String pageText = driver.findElement(By.xpath("//body")).getText();
		// String dropdown = driver.findElement(By.xpath("//select")).getText();

		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

		String textContents = pageText.replaceAll("\n", ",").replaceAll(" ", ",");
		
		String processedText = textContents.replaceAll("[0-9]", "").replaceAll("[:#().!]", "").replaceAll("\\s", "");;
		// System.out.println("******************************\nContent from

		String lcTextVontents = textContents.toLowerCase().trim();
		String[] aWebWords = lcTextVontents.split(",");
		
		
		ArrayList<String> alWebWords = new ArrayList<String>(Arrays.asList(aWebWords));
		System.out.println("******************************\nContent from Web\n******************************");
		System.out.println(alWebWords);

		String processedContents = processedText.toLowerCase().trim();
		String[] processedWords = processedContents.split(",");
		alProcessed = new ArrayList<String>(Arrays.asList(processedWords));

		// words present in json
		System.out.println("******************************\nContent from JSON\n******************************");
		// to remove topmost element
		// System.out.println(jsonElements);

		ArrayList<String> tempElements = new ArrayList<>();
		tempElements.addAll(alProcessed);
		webWords = alWebWords;

		removeNegationWords();

		HashSet<String> webPage = new HashSet<String>(correctedWebWords);

		HashSet<String> set_json = new HashSet<String>(jsonElements);

		webPage.removeAll(set_json);// removing common words page - json
		System.out.println(
				"*****************************\n Words present on page but not in JSON \n*******************************");
		System.out.println(webPage);

		driver.close();

		return webPage.toString();

	}

	public void login(WebDriver driver, String userName, String password, String url) {

		driver.findElement(By.xpath("//*[@id='root']/div/div/div/div[1]/div[2]/div/div/form/input[1]"))
				.sendKeys(userName);
		driver.findElement(By.xpath("//*[@id='root']/div/div/div/div[1]/div[2]/div/div/form/input[2]"))
				.sendKeys(password);
		driver.findElement(By.xpath("//*[@id='root']/div/div/div/div[1]/div[2]/div/div/form/div[2]/button")).click();
		;
	}

	public String[] getLanguageFilter() {

		try {
			String sOpenerFile = System.getProperty("user.dir") + "/src/dictionary_DE/smartLink_German.txt";
			File fOpenerFile = new File(sOpenerFile);
			BufferedReader br = new BufferedReader(new FileReader(fOpenerFile));
			StringBuffer sb = new StringBuffer();

			String sTmp = "";
			while (sTmp != null) {
				sTmp = br.readLine();
				if (sTmp != null) {
					sb.append(sTmp + "\n");
				}
			}
			String[] lsSmartLink = sb.toString().split(",");
			System.out.println(Arrays.toString(lsSmartLink));
			return lsSmartLink;

		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { " " };
		}

	}

}
