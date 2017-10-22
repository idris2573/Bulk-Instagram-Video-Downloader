import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Idris on 28/08/2017.
 */
public class DownloadInstagramVideos {

    File file;
    WebDriver driver;
    int delay = 1000;

    String userPage = "https://www.instagram.com/megatokebyyets/";
    int scrollAmount = 100;

    FileWriter fw;
    BufferedWriter writer;

    FileWriter fw2;
    BufferedWriter writer2;

    ArrayList<String> instaVidLinks;


    public static void main(String[]args) throws Exception{
        DownloadInstagramVideos downloadInstagramVideos = new DownloadInstagramVideos();
        downloadInstagramVideos.run();
    }


    //---------------------------------GET INSTAGRAM LINKS-----------------------------------//
    void run(){
        try {
            start();
            scrollToBottom();
            getLinks();
            saveFile();
            System.out.println("Complete");
        }catch (Exception e){
            System.out.println("Failed...");
        }
    }

    void start(){
        file = new File("lib/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());


        String downloadFilepath = "F:\\Megatoke\\videos\\instagram";
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(cap);

        driver.get(userPage);
    }

    
    void getLinks() throws Exception{

        instaVidLinks = new ArrayList<>();
        createFile2();

        List<WebElement> elements;
        elements = driver.findElements(By.tagName("a"));
        Thread.sleep(1000);

        for (int i = 0; i < elements.size(); i++) {
            String instaLink = elements.get(i).getAttribute("href");
            if (instaLink.contains("https://www.instagram.com/p/")) {
                instaVidLinks.add(instaLink);
                writer2.append("\"" + instaLink.replace("/?taken-by=megatokebyyets","") + "\"\n");
                System.out.println("link " + i + " : " + instaVidLinks.get(instaVidLinks.size()-1));
            }
        }

        saveFile2();

        System.out.println("\n\n");

        for (int i = 0; i < instaVidLinks.size(); i++) {
            createFile();
            writer.append("\"" + instaVidLinks.get(i).replace("/?taken-by=megatokebyyets","") + "\",");
            System.out.println((i+1) + "\n" + instaVidLinks.get(i));
            getDescription(instaVidLinks.get(i));
            downloadVideo(instaVidLinks.get(i));

            System.out.println();
            saveFile();
        }

    }

    void getDescription(String pageName) throws Exception{
        driver.get(pageName);

        Thread.sleep(delay);

        String[] descriptionText = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[2]/div[1]/ul/li")).getText().split(" ");
        String description = "";


        for (int i = 0; i < descriptionText.length; i++) {
            if (descriptionText[i].contains("@")) {
                description = description + descriptionText[i] + "|";
                System.out.println(description.replace("|",""));
            }
        }

        if(description.endsWith("|")){
            description = description.substring(0,description.length()-1);
        }
            writer.append("\"" + description + "\",");

    }

    void downloadVideo(String pageName) throws Exception{

        Thread.sleep(delay);
        driver.get("https://www.dredown.com/instagram");
        Thread.sleep(delay);

        driver.findElement(By.xpath("//*[@id=\"videourl\"]")).sendKeys(pageName);
        driver.findElement(By.xpath("//*[@id=\"dredown\"]")).click();
        boolean load = false;

        while(!load) {
            try {
                Thread.sleep(2000);
                driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/table/tbody/tr/td[1]/div[2]")).click();
                load = true;

            }catch (Exception e){}

            try{
                Thread.sleep(2000);

                if(driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/div")).getText().contains("Sorry")){
                load = true;}
            }catch (Exception e){}
        }

        String filename = pageName.replace("https://www.instagram.com/p/","").replace("/?taken-by=megatokebyyets","");
        System.out.println("Downloading...");

        writer.append("\"" + filename + "\"\n");


    }

    //---------------------------------FILE WRITER-----------------------------------//

    void createFile()throws IOException {

        File file = new File("files/instagramLinks.csv");
        fw = new FileWriter(file,true);
        writer = new BufferedWriter(fw);
    }

    void saveFile() throws IOException{
        writer.flush();
        writer.close();
    }

    void createFile2()throws IOException {

        File file = new File("files/instagramLinksAlone.txt");
        fw2 = new FileWriter(file);
        writer2 = new BufferedWriter(fw2);
    }

    void saveFile2() throws IOException{
        writer2.flush();
        writer2.close();
    }



}
