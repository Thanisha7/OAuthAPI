import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class Tests {

    JsonPath js;
    String code;
    String accessToken;
    @Test
    public void getAuthorizationCode() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","/Users/thanisha/Documents/chromedriver");
        WebDriver driver=new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php");
        driver.findElement(By.id("identifierId")).sendKeys("thanisha@testvagrant.com");
        driver.findElement(By.xpath("//span[normalize-space()='Next']")).click();
        driver.findElement(By.name("password")).sendKeys("Thsm14is@");
        driver.findElement(By.id("passwordNext")).click();
        Thread.sleep(5000);
        String currentUrl=driver.getCurrentUrl();
        System.out.println(currentUrl);
        String partialUrl=currentUrl.split("code=")[1];
        code=partialUrl.split("&scope")[0];
        System.out.println("Code: "+code);
        Assert.assertNotNull(code);
        driver.close();
    }
    @Test(dependsOnMethods = "getAuthorizationCode")
    public void getAccessToken()
    {
        String response=given().urlEncodingEnabled(false).queryParams("code",code)
                .queryParams("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .queryParams("redirect_uri","https://rahulshettyacademy.com/getCourse.php")
                .queryParams("grant_type","authorization_code")
                .queryParams("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .post("https://www.googleapis.com/oauth2/v4/token").asString();
        System.out.println(response);
        js=new JsonPath(response);
        accessToken=js.getString("access_token");
        System.out.println("Access Token : "+accessToken);
        Assert.assertNotNull(accessToken);
    }
    @Test(dependsOnMethods = "getAccessToken")
    public void request()
    {
        Response c=given().queryParam("access_token",accessToken).expect().defaultParser(Parser.JSON)
                .when()
                .get("https://rahulshettyacademy.com/getCourse.php").as(Response.class);
        String[] s ={"Selenium Webdriver Java","Cypress","Protractor"};
        ArrayList<String> a=new ArrayList<String>();
        List<WebAutomation> b=c.getCourses().getWebAutomation();
        for (int i=0;i<b.size();i++)
        {
            a.add(b.get(i).getCourseTitle());
        }
        List<String>l=Arrays.asList(s);
        Assert.assertTrue(l.equals(a));

        for (int i=0;i<b.size();i++)
        {
            if (b.get(i).getCourseTitle().equalsIgnoreCase("Selenium Webdriver Java"))
            {
                System.out.println(b.get(i).getPrice());
                Assert.assertEquals(b.get(i).getPrice(),50);
            }
        }
    }
}
