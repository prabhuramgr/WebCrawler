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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebExtract {

	public ArrayList<String> webWords = new ArrayList<String>();
	ArrayList<String> wordsNotInJSON = new ArrayList<String>();
	ArrayList<String> jsonElements;
	String directoryLocation;
	static ArrayList<String> negationWords;
	ArrayList<String> correctedWebWords;
	ArrayList<String> alProcessed;

	public static void main(String[] args) {
		osValidator();
		// getDropDown();
		// TODO Auto-generated method stub
		// we.getWebContent4();

	}

	public void smartLinkParsing(String location) {

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
						jsonElements.add(strArray[0].toLowerCase().trim());
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
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
		String location  = null;
		WebDriver driver = new ChromeDriver();
		if (url.contains(".de")) {
			driver.navigate().to("https://g-mmember-qa1.copart.de/finder");
			make = driver
					.findElement(By
							.xpath("//div[contains(text(),'Her')]/following-sibling::div/select"))
					.getText();
			location = driver
					.findElement(By
							.xpath("//div[contains(text(),'Sta')]/following-sibling::div/select"))
					.getText();
		}
		if (url.contains(".es")) {
			driver.navigate().to("https://g-mmember-qa1.copart.es/finder");
			make = driver
					.findElement(By
							.xpath("//div[contains(text(),'Mar')]/following-sibling::div/select"))
					.getText();
			location = driver
					.findElement(By
							.xpath("//div[contains(text(),'Ubi')]/following-sibling::div/select"))
					.getText();
		}
		if (url.contains(".in")) {
			driver.navigate().to("https://g-mmember-qa1.copart.in/finder");
		}

		
		System.out.println(make);
		System.out.println(location);
		String textContents = make.replace("\n", ",").toLowerCase();
		String locationContents = location.replace("\n", ",").toLowerCase();;
		

		String[] negationList = textContents.split(",");
		String[] locationNegation = locationContents.split(",");
		negationWords = new ArrayList<String>(Arrays.asList(negationList));
		negationWords.addAll(Arrays.asList(locationNegation));
		
		System.out.println(negationWords);
		// removing jegiche
		negationWords.remove(0);

	}

	public String getWebContent(String url, String dirLocation, String userName, String password) {
		directoryLocation = dirLocation;

		WebDriver driver = new ChromeDriver();
		driver.navigate().to(url);
		if (driver.getCurrentUrl().contains("/member/signIn")) {
			login(driver, userName, password, url);
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		driver.navigate().refresh();
		driver.navigate().refresh();

		String pageText = driver.findElement(By.xpath("//body")).getText();
		// String dropdown = driver.findElement(By.xpath("//select")).getText();

		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

		String textContents = pageText.replace("\n", ",");
		String processedText = textContents.replaceAll("[0-9]", "").replaceAll("[:#]", "");
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
